package thelm.packagedmekemicals.volume;

import java.util.Optional;

import mekanism.api.Action;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfusionStack;
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
		return Component.translatable("volume.packagedmekemicals.mekanism.infuse_type");
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
	public ItemCapability<IInfusionHandler, Void> getItemCapability() {
		return Capabilities.INFUSION.item();
	}

	@Override
	public boolean hasBlockCapability(Level level, BlockPos pos, Direction direction) {
		return level.getCapability(Capabilities.INFUSION.block(), pos, direction) != null;
	}

	@Override
	public boolean isEmpty(Level level, BlockPos pos, Direction direction) {
		IInfusionHandler handler = level.getCapability(Capabilities.INFUSION.block(), pos, direction);
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
		if(resource instanceof InfusionStackWrapper infusionStack) {
			IInfusionHandler handler = level.getCapability(Capabilities.INFUSION.block(), pos, direction);
			if(handler != null) {
				Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
				InfusionStack stack = handler.insertChemical(infusionStack.stack(), action);
				return (int)(infusionStack.getAmount()-stack.getAmount());
			}
		}
		return 0;
	}

	@Override
	public IVolumeStackWrapper drain(Level level, BlockPos pos, Direction direction, IVolumeStackWrapper resource, boolean simulate) {
		if(resource instanceof InfusionStackWrapper infusionStack) {
			IInfusionHandler handler = level.getCapability(Capabilities.INFUSION.block(), pos, direction);
			if(handler != null) {
				Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
				return new InfusionStackWrapper(handler.extractChemical(infusionStack.stack(), action));
			}
		}
		return InfusionStackWrapper.EMPTY;
	}

	@Override
	public void render(GuiGraphics graphics, int i, int j, IVolumeStackWrapper stack) {
		if(stack instanceof InfusionStackWrapper infusionStack) {
			ChemicalRenderer.INSTANCE.render(graphics, i, j, infusionStack.stack());
		}
	}
}
