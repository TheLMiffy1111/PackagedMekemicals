package thelm.packagedmekemicals.volume;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import mekanism.api.Action;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
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
import thelm.packagedmekemicals.capability.StackGasHandlerItem;
import thelm.packagedmekemicals.client.ChemicalRenderer;
import thelm.packagedmekemicals.util.ChemicalHelper;

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
	public MutableComponent getDisplayName() {
		return new TranslatableComponent("volume.packagedmekemicals.mekanism.gas");
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
		if(volumeStack instanceof GasStack gasStack && ChemicalAttributeValidator.DEFAULT.process(gasStack)) {
			return Optional.of(new GasStackWrapper(gasStack));
		}
		return Optional.empty();
	}

	@Override
	public Optional<IVolumeStackWrapper> getStackContained(ItemStack container) {
		return ChemicalHelper.INSTANCE.getGasContained(container).map(GasStackWrapper::new);
	}

	@Override
	public void setStack(ItemStack stack, IVolumeStackWrapper volumeStack) {
		if(volumeStack instanceof GasStackWrapper gasStack) {
			ChemicalHelper.INSTANCE.getGasHandler(stack).ifPresent(handler->{
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
	public IGasHandler makeItemCapability(ItemStack volumePackage) {
		return new StackGasHandlerItem(volumePackage);
	}

	@Override
	public Capability<IGasHandler> getItemCapability() {
		return Capabilities.GAS_HANDLER_CAPABILITY;
	}

	@Override
	public boolean hasBlockCapability(ICapabilityProvider capProvider, Direction direction) {
		return capProvider.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, direction).isPresent();
	}

	@Override
	public boolean isEmpty(ICapabilityProvider capProvider, Direction direction) {
		return capProvider.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, direction).map(handler->{
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
