package thelm.packagedmekemicals.volume;

import java.util.Optional;

import com.mojang.serialization.Codec;

import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.ItemCapability;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.api.IVolumeType;
import thelm.packagedmekemicals.capability.StackChemicalHandlerItem;
import thelm.packagedmekemicals.client.ChemicalRenderer;
import thelm.packagedmekemicals.util.ChemicalHelper;

public class ChemicalVolumeType implements IVolumeType {

	public static final ChemicalVolumeType INSTANCE = new ChemicalVolumeType();
	public static final ResourceLocation NAME = ResourceLocation.parse("mekanism:chemical");

	@Override
	public ResourceLocation getName() {
		return NAME;
	}

	@Override
	public Class<?> getTypeClass() {
		return ChemicalStack.class;
	}

	@Override
	public Class<?> getTypeBaseClass() {
		return Chemical.class;
	}

	@Override
	public MutableComponent getDisplayName() {
		return Component.translatable("volume.packagedmekemicals.mekanism.chemical");
	}

	@Override
	public boolean supportsAE() {
		return ModList.get().isLoaded("appmek");
	}

	@Override
	public Optional<?> makeStackFromBase(Object volumeBase, int amount, DataComponentPatch patch) {
		if(volumeBase instanceof Chemical chemical) {
			return Optional.of(new ChemicalStack(chemical, amount));
		}
		else if(volumeBase instanceof ChemicalStack chemicalStack) {
			chemicalStack = chemicalStack.copy();
			chemicalStack.setAmount(amount);
			return Optional.of(chemicalStack);
		}
		return Optional.empty();
	}

	@Override
	public IVolumeStackWrapper getEmptyStackInstance() {
		return ChemicalStackWrapper.EMPTY;
	}

	@Override
	public Optional<IVolumeStackWrapper> wrapStack(Object volumeStack) {
		if(volumeStack instanceof ChemicalStack chemicalStack && ChemicalAttributeValidator.DEFAULT.process(chemicalStack)) {
			return Optional.of(new ChemicalStackWrapper(chemicalStack));
		}
		return Optional.empty();
	}

	@Override
	public Optional<IVolumeStackWrapper> getStackContained(ItemStack container) {
		return ChemicalHelper.INSTANCE.getChemicalContained(container).map(ChemicalStackWrapper::new);
	}

	@Override
	public void setStack(ItemStack stack, IVolumeStackWrapper volumeStack) {
		if(volumeStack instanceof ChemicalStackWrapper chemicalStack) {
			ChemicalHelper.INSTANCE.getChemicalHandler(stack).ifPresent(handler->{
				if(handler instanceof StackChemicalHandlerItem vHandler) {
					vHandler.setChemical(chemicalStack.stack());
				}
			});
		}
	}

	@Override
	public Codec<? extends IVolumeStackWrapper> getStackCodec() {
		return ChemicalStackWrapper.CODEC;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, ? extends IVolumeStackWrapper> getStackStreamCodec() {
		return ChemicalStackWrapper.STREAM_CODEC;
	}

	@Override
	public IChemicalHandler makeItemCapability(ItemStack volumePackage) {
		return new StackChemicalHandlerItem(volumePackage);
	}

	@Override
	public ItemCapability<IChemicalHandler, Void> getItemCapability() {
		return Capabilities.CHEMICAL.item();
	}

	@Override
	public boolean hasBlockCapability(Level level, BlockPos pos, Direction direction) {
		return level.getCapability(Capabilities.CHEMICAL.block(), pos, direction) != null;
	}

	@Override
	public boolean isEmpty(Level level, BlockPos pos, Direction direction) {
		IChemicalHandler handler = level.getCapability(Capabilities.CHEMICAL.block(), pos, direction);
		if(handler != null) {
			if(handler.getChemicalTanks() == 0) {
				return false;
			}
			for(int i = 0; i < handler.getChemicalTanks(); ++i) {
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
		if(resource instanceof ChemicalStackWrapper chemicalStack) {
			IChemicalHandler handler = level.getCapability(Capabilities.CHEMICAL.block(), pos, direction);
			if(handler != null) {
				Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
				ChemicalStack stack = handler.insertChemical(chemicalStack.stack(), action);
				return (int)(chemicalStack.getAmount()-stack.getAmount());
			}
		}
		return 0;
	}

	@Override
	public IVolumeStackWrapper drain(Level level, BlockPos pos, Direction direction, IVolumeStackWrapper resource, boolean simulate) {
		if(resource instanceof ChemicalStackWrapper chemicalStack) {
			IChemicalHandler handler = level.getCapability(Capabilities.CHEMICAL.block(), pos, direction);
			if(handler != null) {
				Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
				return new ChemicalStackWrapper(handler.extractChemical(chemicalStack.stack(), action));
			}
		}
		return ChemicalStackWrapper.EMPTY;
	}

	@Override
	public void render(GuiGraphics graphics, int i, int j, IVolumeStackWrapper stack) {
		if(stack instanceof ChemicalStackWrapper chemicalStack) {
			ChemicalRenderer.INSTANCE.render(graphics, i, j, chemicalStack.stack());
		}
	}
}
