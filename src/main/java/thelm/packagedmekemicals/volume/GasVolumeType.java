package thelm.packagedmekemicals.volume;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
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
import thelm.packagedmekemicals.capability.StackGasHandlerItem;
import thelm.packagedmekemicals.client.ChemicalRenderer;

public class GasVolumeType implements IVolumeType {

	public static final GasVolumeType INSTANCE = new GasVolumeType();
	public static final ResourceLocation NAME = new ResourceLocation("mekanism:gas");

	@Override
	public ResourceLocation getName() {
		return NAME;
	}

	@Override
	public Class<?> getTypeClass() {
		return GasStack.class;
	}

	@Override
	public boolean supportsAE() {
		return ModList.get().isLoaded("appmek");
	}

	@Override
	public IVolumeStackWrapper getEmptyStackInstance() {
		return GasStackWrapper.EMPTY;
	}

	@Override
	public Optional<IVolumeStackWrapper> wrapStack(Object volumeStack) {
		if(volumeStack instanceof GasStack gasStack) {
			return Optional.of(new GasStackWrapper(gasStack));
		}
		return Optional.empty();
	}

	@Override
	public Optional<IVolumeStackWrapper> getStackContained(ItemStack container) {
		if(!container.isEmpty()) {
			container = ItemHandlerHelper.copyStackWithSize(container, 1);
			return container.getCapability(Capabilities.GAS_HANDLER_CAPABILITY).
					map(handler->handler.extractChemical(Long.MAX_VALUE, Action.SIMULATE)).
					filter(stack->!stack.isEmpty()).
					map(GasStackWrapper::new);
		}
		return Optional.empty();
	}

	@Override
	public void setStack(ItemStack stack, IVolumeStackWrapper volumeStack) {
		if(volumeStack instanceof GasStackWrapper gasStack) {
			stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY).ifPresent(handler->{
				if(handler instanceof StackGasHandlerItem vHandler) {
					vHandler.setGas(gasStack.stack());
				}
			});
		}
	}

	@Override
	public IVolumeStackWrapper loadStack(CompoundTag tag) {
		return new GasStackWrapper(GasStack.readFromNBT(tag));
	}

	@Override
	public Object makeItemCapability(ItemStack volumePackage) {
		return new StackGasHandlerItem(volumePackage);
	}

	@Override
	public Capability getItemCapability() {
		return Capabilities.GAS_HANDLER_CAPABILITY;
	}

	@Override
	public boolean hasBlockCapability(ICapabilityProvider capProvider, Direction direction) {
		return capProvider.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, direction).isPresent();
	}

	@Override
	public int fill(ICapabilityProvider capProvider, Direction direction, IVolumeStackWrapper resource, boolean simulate) {
		if(resource instanceof GasStackWrapper gasStack) {
			Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
			return capProvider.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, direction).
					map(handler->handler.insertChemical(gasStack.stack(), action)).
					map(stack->gasStack.getAmount()-stack.getAmount()).orElse(0L).intValue();
		}
		return 0;
	}

	@Override
	public IVolumeStackWrapper drain(ICapabilityProvider capProvider, Direction direction, IVolumeStackWrapper resource, boolean simulate) {
		if(resource instanceof GasStackWrapper gasStack) {
			Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
			return capProvider.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, direction).
					map(handler->handler.extractChemical(gasStack.stack(), action)).
					map(GasStackWrapper::new).orElse(GasStackWrapper.EMPTY);
		}
		return GasStackWrapper.EMPTY;
	}

	@Override
	public void render(PoseStack poseStack, int i, int j, IVolumeStackWrapper stack) {
		if(stack instanceof GasStackWrapper gasStack) {
			ChemicalRenderer.INSTANCE.render(poseStack, i, j, gasStack.stack());
		}
	}
}
