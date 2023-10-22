package thelm.packagedmekemicals.volume;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import mekanism.api.Action;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.api.IVolumeType;
import thelm.packagedmekemicals.capability.StackInfusionHandlerItem;
import thelm.packagedmekemicals.client.ChemicalRenderer;
import thelm.packagedmekemicals.util.ChemicalHelper;

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
	public MutableComponent getDisplayName() {
		return new TranslatableComponent("volume.packagedmekemicals.mekanism.infuse_type");
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
		if(volumeStack instanceof InfusionStack infusionStack && ChemicalAttributeValidator.DEFAULT.process(infusionStack)) {
			return Optional.of(new InfusionStackWrapper(infusionStack));
		}
		return Optional.empty();
	}

	@Override
	public Optional<IVolumeStackWrapper> getStackContained(ItemStack container) {
		return ChemicalHelper.INSTANCE.getInfusionContained(container).map(InfusionStackWrapper::new);
	}

	@Override
	public void setStack(ItemStack stack, IVolumeStackWrapper volumeStack) {
		if(volumeStack instanceof InfusionStackWrapper infusionStack) {
			ChemicalHelper.INSTANCE.getInfusionHandler(stack).ifPresent(handler->{
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
	public IInfusionHandler makeItemCapability(ItemStack volumePackage) {
		return new StackInfusionHandlerItem(volumePackage);
	}

	@Override
	public Capability<IInfusionHandler> getItemCapability() {
		return Capabilities.INFUSION_HANDLER_CAPABILITY;
	}

	@Override
	public boolean hasBlockCapability(ICapabilityProvider capProvider, Direction direction) {
		return capProvider.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY, direction).isPresent();
	}

	@Override
	public boolean isEmpty(ICapabilityProvider capProvider, Direction direction) {
		return capProvider.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY, direction).map(handler->{
			if(handler.getTanks() == 0) {
				return false;
			}
			for(int i = 0; i < handler.getTanks(); ++i) {
				if(!handler.getChemicalInTank(i).isEmpty()) {
					return false;
				}
			}
			return true;
		}).orElse(false);
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
