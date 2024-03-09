package thelm.packagedmekemicals.volume;

import java.util.Optional;

import mekanism.api.Action;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.ItemCapability;
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
		return Component.translatable("volume.packagedmekemicals.mekanism.gas");
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
	public ItemCapability<IGasHandler, Void> getItemCapability() {
		return Capabilities.GAS.item();
	}

	@Override
	public boolean hasBlockCapability(Level level, BlockPos pos, Direction direction) {
		return level.getCapability(Capabilities.GAS.block(), pos, direction) != null;
	}

	@Override
	public boolean isEmpty(Level level, BlockPos pos, Direction direction) {
		IGasHandler handler = level.getCapability(Capabilities.GAS.block(), pos, direction);
		if(handler != null) {
			if(handler.getTanks() == 0) {
				return false;
			}
			for(int i = 0; i < handler.getTanks(); ++i) {
				if(!handler.getChemicalInTank(i).isEmpty()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int fill(Level level, BlockPos pos, Direction direction, IVolumeStackWrapper resource, boolean simulate) {
		if(resource instanceof GasStackWrapper gasStack) {
			IGasHandler handler = level.getCapability(Capabilities.GAS.block(), pos, direction);
			if(handler != null) {
				Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
				GasStack stack = handler.insertChemical(gasStack.stack(), action);
				return (int)(gasStack.getAmount()-stack.getAmount());
			}
		}
		return 0;
	}

	@Override
	public IVolumeStackWrapper drain(Level level, BlockPos pos, Direction direction, IVolumeStackWrapper resource, boolean simulate) {
		if(resource instanceof GasStackWrapper gasStack) {
			IGasHandler handler = level.getCapability(Capabilities.GAS.block(), pos, direction);
			if(handler != null) {
				Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
				return new GasStackWrapper(handler.extractChemical(gasStack.stack(), action));
			}
		}
		return GasStackWrapper.EMPTY;
	}

	@Override
	public void render(GuiGraphics graphics, int i, int j, IVolumeStackWrapper stack) {
		if(stack instanceof GasStackWrapper gasStack) {
			ChemicalRenderer.INSTANCE.render(graphics, i, j, gasStack.stack());
		}
	}
}
