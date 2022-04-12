package thelm.packagedmekemicals.volume;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import mekanism.api.Action;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.ItemHandlerHelper;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.api.IVolumeType;
import thelm.packagedmekemicals.capability.StackInfusionHandlerItem;
import thelm.packagedmekemicals.client.ChemicalRenderer;

public class InfusionVolumeType implements IVolumeType {

	public static final InfusionVolumeType INSTANCE = new InfusionVolumeType();
	public static final ResourceLocation NAME = new ResourceLocation("mekanism:infuse_type");

	@Override
	public ResourceLocation getName() {
		return NAME;
	}

	@Override
	public Class<?> getTypeClass() {
		return InfusionStack.class;
	}

	@Override
	public boolean supportsAE() {
		return ModList.get().isLoaded("appmek");
	}

	@Override
	public IVolumeStackWrapper getEmptyStackInstance() {
		return InfusionStackWrapper.EMPTY;
	}

	@Override
	public Optional<IVolumeStackWrapper> wrapStack(Object volumeStack) {
		if(volumeStack instanceof InfusionStack infusionStack) {
			return Optional.of(new InfusionStackWrapper(infusionStack));
		}
		return Optional.empty();
	}

	@Override
	public Optional<IVolumeStackWrapper> getStackContained(ItemStack container) {
		if(!container.isEmpty()) {
			container = ItemHandlerHelper.copyStackWithSize(container, 1);
			return container.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY).
					map(handler->handler.extractChemical(Long.MAX_VALUE, Action.SIMULATE)).
					filter(stack->!stack.isEmpty()).
					map(InfusionStackWrapper::new);
		}
		return Optional.empty();
	}

	@Override
	public void setStack(ItemStack stack, IVolumeStackWrapper volumeStack) {
		if(volumeStack instanceof InfusionStackWrapper infusionStack) {
			stack.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY).ifPresent(handler->{
				if(handler instanceof StackInfusionHandlerItem vHandler) {
					vHandler.setInfusion(infusionStack.stack());
				}
			});
		}
	}

	@Override
	public IVolumeStackWrapper loadStack(CompoundTag tag) {
		return new InfusionStackWrapper(InfusionStack.readFromNBT(tag));
	}

	@Override
	public Object makeItemCapability(ItemStack volumePackage) {
		return new StackInfusionHandlerItem(volumePackage);
	}

	@Override
	public Capability getItemCapability() {
		return Capabilities.INFUSION_HANDLER_CAPABILITY;
	}

	@Override
	public boolean hasBlockCapability(ICapabilityProvider capProvider, Direction direction) {
		return capProvider.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY, direction).isPresent();
	}

	@Override
	public int fill(ICapabilityProvider capProvider, Direction direction, IVolumeStackWrapper resource, boolean simulate) {
		if(resource instanceof InfusionStackWrapper infusionStack) {
			Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
			return capProvider.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY, direction).
					map(handler->handler.insertChemical(infusionStack.stack(), action)).
					map(stack->infusionStack.getAmount()-stack.getAmount()).orElse(0L).intValue();
		}
		return 0;
	}

	@Override
	public IVolumeStackWrapper drain(ICapabilityProvider capProvider, Direction direction, IVolumeStackWrapper resource, boolean simulate) {
		if(resource instanceof InfusionStackWrapper infusionStack) {
			Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
			return capProvider.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY, direction).
					map(handler->handler.extractChemical(infusionStack.stack(), action)).
					map(InfusionStackWrapper::new).orElse(InfusionStackWrapper.EMPTY);
		}
		return InfusionStackWrapper.EMPTY;
	}

	@Override
	public void render(PoseStack poseStack, int i, int j, IVolumeStackWrapper stack) {
		if(stack instanceof InfusionStackWrapper infusionStack) {
			ChemicalRenderer.INSTANCE.render(poseStack, i, j, infusionStack.stack());
		}
	}
}
