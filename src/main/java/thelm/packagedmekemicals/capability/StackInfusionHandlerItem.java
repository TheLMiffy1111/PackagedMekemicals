package thelm.packagedmekemicals.capability;

import mekanism.api.Action;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfusionStack;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.component.PackagedAutoDataComponents;
import thelm.packagedmekemicals.api.IChemicalStackWrapper;
import thelm.packagedmekemicals.volume.InfusionStackWrapper;

public class StackInfusionHandlerItem implements IInfusionHandler {

	protected ItemStack container;

	public StackInfusionHandlerItem(ItemStack container) {
		this.container = container;
	}

	public InfusionStack getInfusion() {
		IVolumeStackWrapper stack = container.get(PackagedAutoDataComponents.VOLUME_PACKAGE_STACK);
		if(stack instanceof IChemicalStackWrapper chemical) {
			if(chemical.getChemical() instanceof InfusionStack infusion) {
				return infusion;
			}
		}
		return InfusionStack.EMPTY;
	}

	public void setInfusion(InfusionStack infusion)  {
		if(infusion != null && !infusion.isEmpty()) {
			DataComponentPatch patch = DataComponentPatch.builder().
					set(PackagedAutoDataComponents.VOLUME_PACKAGE_STACK.get(), InfusionStackWrapper.of(infusion)).
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
	public InfusionStack getChemicalInTank(int tank) {
		return getInfusion();
	}

	@Override
	public void setChemicalInTank(int tank, InfusionStack stack) {
		setInfusion(stack);
	}

	@Override
	public long getTankCapacity(int tank) {
		return getInfusion().getAmount();
	}

	@Override
	public boolean isValid(int tank, InfusionStack stack) {
		return true;
	}

	@Override
	public InfusionStack insertChemical(int tank, InfusionStack resource, Action action)  {
		return resource;
	}

	@Override
	public InfusionStack extractChemical(int tank, long maxDrain, Action action) {
		InfusionStack infusionStack = getInfusion();
		if(tank != 0 || maxDrain < infusionStack.getAmount()) {
			return InfusionStack.EMPTY;
		}
		if(!infusionStack.isEmpty()) {
			if(action.execute()) {
				setContainerToEmpty();
			}
			return infusionStack;
		}
		return InfusionStack.EMPTY;
	}

	@Override
	public InfusionStack getEmptyStack() {
		return InfusionStack.EMPTY;
	}
}
