package thelm.packagedmekemicals.capability;

import mekanism.api.Action;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.component.PackagedAutoDataComponents;
import thelm.packagedmekemicals.api.IChemicalStackWrapper;
import thelm.packagedmekemicals.volume.SlurryStackWrapper;

public class StackSlurryHandlerItem implements ISlurryHandler {

	protected ItemStack container;

	public StackSlurryHandlerItem(ItemStack container) {
		this.container = container;
	}

	public SlurryStack getSlurry() {
		IVolumeStackWrapper stack = container.get(PackagedAutoDataComponents.VOLUME_PACKAGE_STACK);
		if(stack instanceof IChemicalStackWrapper chemical) {
			if(chemical.getChemical() instanceof SlurryStack slurry) {
				return slurry;
			}
		}
		return SlurryStack.EMPTY;
	}

	public void setSlurry(SlurryStack slurry)  {
		if(slurry != null && !slurry.isEmpty()) {
			DataComponentPatch patch = DataComponentPatch.builder().
					set(PackagedAutoDataComponents.VOLUME_PACKAGE_STACK.get(), SlurryStackWrapper.of(slurry)).
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
	public SlurryStack getChemicalInTank(int tank) {
		return getSlurry();
	}

	@Override
	public void setChemicalInTank(int tank, SlurryStack stack) {
		setSlurry(stack);
	}

	@Override
	public long getTankCapacity(int tank) {
		return getSlurry().getAmount();
	}

	@Override
	public boolean isValid(int tank, SlurryStack stack) {
		return true;
	}

	@Override
	public SlurryStack insertChemical(int tank, SlurryStack resource, Action action)  {
		return resource;
	}

	@Override
	public SlurryStack extractChemical(int tank, long maxDrain, Action action) {
		SlurryStack slurryStack = getSlurry();
		if(tank != 0 || maxDrain < slurryStack.getAmount()) {
			return SlurryStack.EMPTY;
		}
		if(!slurryStack.isEmpty()) {
			if(action.execute()) {
				setContainerToEmpty();
			}
			return slurryStack;
		}
		return SlurryStack.EMPTY;
	}

	@Override
	public SlurryStack getEmptyStack() {
		return SlurryStack.EMPTY;
	}
}
