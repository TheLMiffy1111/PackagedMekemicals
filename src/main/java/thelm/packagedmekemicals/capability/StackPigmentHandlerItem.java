package thelm.packagedmekemicals.capability;

import mekanism.api.Action;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.PigmentStack;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.component.PackagedAutoDataComponents;
import thelm.packagedmekemicals.api.IChemicalStackWrapper;
import thelm.packagedmekemicals.volume.PigmentStackWrapper;

public class StackPigmentHandlerItem implements IPigmentHandler {

	protected ItemStack container;

	public StackPigmentHandlerItem(ItemStack container) {
		this.container = container;
	}

	public PigmentStack getPigment() {
		IVolumeStackWrapper stack = container.get(PackagedAutoDataComponents.VOLUME_PACKAGE_STACK);
		if(stack instanceof IChemicalStackWrapper chemical) {
			if(chemical.getChemical() instanceof PigmentStack pigment) {
				return pigment;
			}
		}
		return PigmentStack.EMPTY;
	}

	public void setPigment(PigmentStack pigment)  {
		if(pigment != null && !pigment.isEmpty()) {
			DataComponentPatch patch = DataComponentPatch.builder().
					set(PackagedAutoDataComponents.VOLUME_PACKAGE_STACK.get(), PigmentStackWrapper.of(pigment)).
					build();
			container.applyComponents(patch);
		}
	}

	protected void setContainerToEmpty() {
		container.shrink(1);
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public PigmentStack getChemicalInTank(int tank) {
		return getPigment();
	}

	@Override
	public void setChemicalInTank(int tank, PigmentStack stack) {
		setPigment(stack);
	}

	@Override
	public long getTankCapacity(int tank) {
		return getPigment().getAmount();
	}

	@Override
	public boolean isValid(int tank, PigmentStack stack) {
		return true;
	}

	@Override
	public PigmentStack insertChemical(int tank, PigmentStack resource, Action action)  {
		return resource;
	}

	@Override
	public PigmentStack extractChemical(int tank, long maxDrain, Action action) {
		PigmentStack pigmentStack = getPigment();
		if(tank != 0 || maxDrain < pigmentStack.getAmount()) {
			return PigmentStack.EMPTY;
		}
		if(!pigmentStack.isEmpty()) {
			if(action.execute()) {
				setContainerToEmpty();
			}
			return pigmentStack;
		}
		return PigmentStack.EMPTY;
	}

	@Override
	public PigmentStack getEmptyStack() {
		return PigmentStack.EMPTY;
	}
}
