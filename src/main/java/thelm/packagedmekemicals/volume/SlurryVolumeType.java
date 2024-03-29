package thelm.packagedmekemicals.volume;

import java.util.Optional;

import mekanism.api.Action;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;
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
	public Capability<ISlurryHandler> getItemCapability() {
		return Capabilities.SLURRY_HANDLER;
	}

	@Override
	public boolean hasBlockCapability(ICapabilityProvider capProvider, Direction direction) {
		return capProvider.getCapability(Capabilities.SLURRY_HANDLER, direction).isPresent();
	}

	@Override
	public boolean isEmpty(ICapabilityProvider capProvider, Direction direction) {
		return capProvider.getCapability(Capabilities.SLURRY_HANDLER, direction).map(handler->{
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
		if(resource instanceof SlurryStackWrapper slurryStack) {
			Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
			return capProvider.getCapability(Capabilities.SLURRY_HANDLER, direction).
					map(handler->handler.insertChemical(slurryStack.stack(), action)).
					map(stack->slurryStack.getAmount()-stack.getAmount()).orElse(0L).intValue();
		}
		return 0;
	}

	@Override
	public IVolumeStackWrapper drain(ICapabilityProvider capProvider, Direction direction, IVolumeStackWrapper resource, boolean simulate) {
		if(resource instanceof SlurryStackWrapper slurryStack) {
			Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
			return capProvider.getCapability(Capabilities.SLURRY_HANDLER, direction).
					map(handler->handler.extractChemical(slurryStack.stack(), action)).
					map(SlurryStackWrapper::new).orElse(SlurryStackWrapper.EMPTY);
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
