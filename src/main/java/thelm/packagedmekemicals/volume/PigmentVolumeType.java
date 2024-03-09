package thelm.packagedmekemicals.volume;

import java.util.Optional;

import mekanism.api.Action;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.PigmentStack;
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
import thelm.packagedmekemicals.capability.StackPigmentHandlerItem;
import thelm.packagedmekemicals.client.ChemicalRenderer;
import thelm.packagedmekemicals.util.ChemicalHelper;

public class PigmentVolumeType implements IVolumeType {

	public static final PigmentVolumeType INSTANCE = new PigmentVolumeType();
	public static final ResourceLocation NAME = new ResourceLocation("mekanism:pigment");

	@Override
	public ResourceLocation getName() {
		return NAME;
	}

	@Override
	public Class<?> getTypeClass() {
		return PigmentStack.class;
	}

	@Override
	public MutableComponent getDisplayName() {
		return Component.translatable("volume.packagedmekemicals.mekanism.pigment");
	}

	@Override
	public boolean supportsAE() {
		return ModList.get().isLoaded("appmek");
	}

	@Override
	public IVolumeStackWrapper getEmptyStackInstance() {
		return PigmentStackWrapper.EMPTY;
	}

	@Override
	public Optional<IVolumeStackWrapper> wrapStack(Object volumeStack) {
		if(volumeStack instanceof PigmentStack pigmentStack && ChemicalAttributeValidator.DEFAULT.process(pigmentStack)) {
			return Optional.of(new PigmentStackWrapper(pigmentStack));
		}
		return Optional.empty();
	}

	@Override
	public Optional<IVolumeStackWrapper> getStackContained(ItemStack container) {
		return ChemicalHelper.INSTANCE.getPigmentContained(container).map(PigmentStackWrapper::new);
	}

	@Override
	public void setStack(ItemStack stack, IVolumeStackWrapper volumeStack) {
		if(volumeStack instanceof PigmentStackWrapper pigmentStack) {
			ChemicalHelper.INSTANCE.getPigmentHandler(stack).ifPresent(handler->{
				if(handler instanceof StackPigmentHandlerItem vHandler) {
					vHandler.setPigment(pigmentStack.stack());
				}
			});
		}
	}

	@Override
	public IVolumeStackWrapper loadStack(CompoundTag tag) {
		return new PigmentStackWrapper(PigmentStack.readFromNBT(tag));
	}

	@Override
	public IPigmentHandler makeItemCapability(ItemStack volumePackage) {
		return new StackPigmentHandlerItem(volumePackage);
	}

	@Override
	public ItemCapability<IPigmentHandler, Void> getItemCapability() {
		return Capabilities.PIGMENT.item();
	}

	@Override
	public boolean hasBlockCapability(Level level, BlockPos pos, Direction direction) {
		return level.getCapability(Capabilities.PIGMENT.block(), pos, direction) != null;
	}

	@Override
	public boolean isEmpty(Level level, BlockPos pos, Direction direction) {
		IPigmentHandler handler = level.getCapability(Capabilities.PIGMENT.block(), pos, direction);
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
		if(resource instanceof PigmentStackWrapper pigmentStack) {
			IPigmentHandler handler = level.getCapability(Capabilities.PIGMENT.block(), pos, direction);
			if(handler != null) {
				Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
				PigmentStack stack = handler.insertChemical(pigmentStack.stack(), action);
				return (int)(pigmentStack.getAmount()-stack.getAmount());
			}
		}
		return 0;
	}

	@Override
	public IVolumeStackWrapper drain(Level level, BlockPos pos, Direction direction, IVolumeStackWrapper resource, boolean simulate) {
		if(resource instanceof PigmentStackWrapper pigmentStack) {
			IPigmentHandler handler = level.getCapability(Capabilities.PIGMENT.block(), pos, direction);
			if(handler != null) {
				Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
				return new PigmentStackWrapper(handler.extractChemical(pigmentStack.stack(), action));
			}
		}
		return PigmentStackWrapper.EMPTY;
	}

	@Override
	public void render(GuiGraphics graphics, int i, int j, IVolumeStackWrapper stack) {
		if(stack instanceof PigmentStackWrapper pigmentStack) {
			ChemicalRenderer.INSTANCE.render(graphics, i, j, pigmentStack.stack());
		}
	}
}
