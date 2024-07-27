package thelm.packagedmekemicals.capability;

import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.component.PackagedAutoDataComponents;
import thelm.packagedmekemicals.api.IChemicalStackWrapper;
import thelm.packagedmekemicals.volume.GasStackWrapper;

public class StackGasHandlerItem implements IGasHandler {

	protected ItemStack container;

	public StackGasHandlerItem(ItemStack container) {
		this.container = container;
	}

	public GasStack getGas() {
		IVolumeStackWrapper stack = container.get(PackagedAutoDataComponents.VOLUME_PACKAGE_STACK);
		if(stack instanceof IChemicalStackWrapper chemical) {
			if(chemical.getChemical() instanceof GasStack gas) {
				return gas;
			}
		}
		return GasStack.EMPTY;
	}

	public void setGas(GasStack gas)  {
		if(gas != null && !gas.isEmpty()) {
			DataComponentPatch patch = DataComponentPatch.builder().
					set(PackagedAutoDataComponents.VOLUME_PACKAGE_STACK.get(), GasStackWrapper.of(gas)).
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
	public GasStack getChemicalInTank(int tank) {
		return getGas();
	}

	@Override
	public void setChemicalInTank(int tank, GasStack stack) {
		setGas(stack);
	}

	@Override
	public long getTankCapacity(int tank) {
		return getGas().getAmount();
	}

	@Override
	public boolean isValid(int tank, GasStack stack) {
		return true;
	}

	@Override
	public GasStack insertChemical(int tank, GasStack resource, Action action)  {
		return resource;
	}

	@Override
	public GasStack extractChemical(int tank, long maxDrain, Action action) {
		GasStack gasStack = getGas();
		if(tank != 0 || maxDrain < gasStack.getAmount()) {
			return GasStack.EMPTY;
		}
		if(!gasStack.isEmpty()) {
			if(action.execute()) {
				setContainerToEmpty();
			}
			return gasStack;
		}
		return GasStack.EMPTY;
	}

	@Override
	public GasStack getEmptyStack() {
		return GasStack.EMPTY;
	}
}
