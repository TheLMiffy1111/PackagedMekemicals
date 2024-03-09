package thelm.packagedmekemicals.volume;

import java.util.Optional;

import mekanism.api.Action;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.SlurryStack;
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
import thelm.packagedmekemicals.capability.StackSlurryHandlerItem;
import thelm.packagedmekemicals.client.ChemicalRenderer;
import thelm.packagedmekemicals.util.ChemicalHelper;

public class SlurryVolumeType implements IVolumeType {

	public static final SlurryVolumeType INSTANCE = new SlurryVolumeType();
	public static final ResourceLocation NAME = new ResourceLocation("mekanism:slurry");

	@Override
	public ResourceLocation getName() {
		return NAME;
	}

	@Override
	public Class<?> getTypeClass() {
		return SlurryStack.class;
	}

	@Override
	public MutableComponent getDisplayName() {
		return Component.translatable("volume.packagedmekemicals.mekanism.slurry");
	}

	@Override
	public boolean supportsAE() {
		return ModList.get().isLoaded("appmek");
	}

	@Override
	public IVolumeStackWrapper getEmptyStackInstance() {
		return SlurryStackWrapper.EMPTY;
	}

	@Override
	public Optional<IVolumeStackWrapper> wrapStack(Object volumeStack) {
		if(volumeStack instanceof SlurryStack slurryStack && ChemicalAttributeValidator.DEFAULT.process(slurryStack)) {
			return Optional.of(new SlurryStackWrapper(slurryStack));
		}
		return Optional.empty();
	}

	@Override
	public Optional<IVolumeStackWrapper> getStackContained(ItemStack container) {
		return ChemicalHelper.INSTANCE.getSlurryContained(container).map(SlurryStackWrapper::new);
	}

	@Override
	public void setStack(ItemStack stack, IVolumeStackWrapper volumeStack) {
		if(volumeStack instanceof SlurryStackWrapper slurryStack) {
			ChemicalHelper.INSTANCE.getSlurryHandler(stack).ifPresent(handler->{
				if(handler instanceof StackSlurryHandlerItem vHandler) {
					vHandler.setSlurry(slurryStack.stack());
				}
			});
		}
	}

	@Override
	public IVolumeStackWrapper loadStack(CompoundTag tag) {
		return new SlurryStackWrapper(SlurryStack.readFromNBT(tag));
	}

	@Override
	public ISlurryHandler makeItemCapability(ItemStack volumePackage) {
		return new StackSlurryHandlerItem(volumePackage);
	}

	@Override
	public ItemCapability<ISlurryHandler, Void> getItemCapability() {
		return Capabilities.SLURRY.item();
	}

	@Override
	public boolean hasBlockCapability(Level level, BlockPos pos, Direction direction) {
		return level.getCapability(Capabilities.SLURRY.block(), pos, direction) != null;
	}

	@Override
	public boolean isEmpty(Level level, BlockPos pos, Direction direction) {
		ISlurryHandler handler = level.getCapability(Capabilities.SLURRY.block(), pos, direction);
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
		if(resource instanceof SlurryStackWrapper slurryStack) {
			ISlurryHandler handler = level.getCapability(Capabilities.SLURRY.block(), pos, direction);
			if(handler != null) {
				Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
				SlurryStack stack = handler.insertChemical(slurryStack.stack(), action);
				return (int)(slurryStack.getAmount()-stack.getAmount());
			}
		}
		return 0;
	}

	@Override
	public IVolumeStackWrapper drain(Level level, BlockPos pos, Direction direction, IVolumeStackWrapper resource, boolean simulate) {
		if(resource instanceof SlurryStackWrapper slurryStack) {
			ISlurryHandler handler = level.getCapability(Capabilities.SLURRY.block(), pos, direction);
			if(handler != null) {
				Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
				return new SlurryStackWrapper(handler.extractChemical(slurryStack.stack(), action));
			}
		}
		return SlurryStackWrapper.EMPTY;
	}

	@Override
	public void render(GuiGraphics graphics, int i, int j, IVolumeStackWrapper stack) {
		if(stack instanceof SlurryStackWrapper slurryStack) {
			ChemicalRenderer.INSTANCE.render(graphics, i, j, slurryStack.stack());
		}
	}
}
