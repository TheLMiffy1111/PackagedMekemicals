package thelm.packagedmekemicals.volume;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import mekanism.api.Action;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.PigmentStack;
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
		return new TranslatableComponent("volume.packagedmekemicals.mekanism.pigment");
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
	public Capability<IPigmentHandler> getItemCapability() {
		return Capabilities.PIGMENT_HANDLER_CAPABILITY;
	}

	@Override
	public boolean hasBlockCapability(ICapabilityProvider capProvider, Direction direction) {
		return capProvider.getCapability(Capabilities.PIGMENT_HANDLER_CAPABILITY, direction).isPresent();
	}

	@Override
	public boolean isEmpty(ICapabilityProvider capProvider, Direction direction) {
		return capProvider.getCapability(Capabilities.PIGMENT_HANDLER_CAPABILITY, direction).map(handler->{
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
		if(resource instanceof PigmentStackWrapper pigmentStack) {
			Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
			return capProvider.getCapability(Capabilities.PIGMENT_HANDLER_CAPABILITY, direction).
					map(handler->handler.insertChemical(pigmentStack.stack(), action)).
					map(stack->pigmentStack.getAmount()-stack.getAmount()).orElse(0L).intValue();
		}
		return 0;
	}

	@Override
	public IVolumeStackWrapper drain(ICapabilityProvider capProvider, Direction direction, IVolumeStackWrapper resource, boolean simulate) {
		if(resource instanceof PigmentStackWrapper pigmentStack) {
			Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
			return capProvider.getCapability(Capabilities.PIGMENT_HANDLER_CAPABILITY, direction).
					map(handler->handler.extractChemical(pigmentStack.stack(), action)).
					map(PigmentStackWrapper::new).orElse(PigmentStackWrapper.EMPTY);
		}
		return PigmentStackWrapper.EMPTY;
	}

	@Override
	public void render(PoseStack poseStack, int i, int j, IVolumeStackWrapper stack) {
		if(stack instanceof PigmentStackWrapper pigmentStack) {
			ChemicalRenderer.INSTANCE.render(poseStack, i, j, pigmentStack.stack());
		}
	}
}
