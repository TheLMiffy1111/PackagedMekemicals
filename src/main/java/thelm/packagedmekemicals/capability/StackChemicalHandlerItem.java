package thelm.packagedmekemicals.capability;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.component.PackagedAutoDataComponents;
import thelm.packagedmekemicals.api.IChemicalStackWrapper;
import thelm.packagedmekemicals.volume.ChemicalStackWrapper;

public class StackChemicalHandlerItem implements IChemicalHandler {

	protected ItemStack container;

	public StackChemicalHandlerItem(ItemStack container) {
		this.container = container;
	}

	public ChemicalStack getChemical() {
		IVolumeStackWrapper stack = container.get(PackagedAutoDataComponents.VOLUME_PACKAGE_STACK);
		if(stack instanceof IChemicalStackWrapper chemical) {
			return chemical.getChemical();
		}
		return ChemicalStack.EMPTY;
	}

	public void setChemical(ChemicalStack chemical)  {
		if(chemical != null && !chemical.isEmpty()) {
			DataComponentPatch patch = DataComponentPatch.builder().
					set(PackagedAutoDataComponents.VOLUME_PACKAGE_STACK.get(), ChemicalStackWrapper.of(chemical)).
					build();
			container.applyComponents(patch);
		}
	}

	protected void setContainerToEmpty() {
		container.shrink(1);
	}

	@Override
	public int getChemicalTanks() {
		return 1;
	}

	@Override
	public ChemicalStack getChemicalInTank(int tank) {
		return getChemical();
	}

	@Override
	public void setChemicalInTank(int tank, ChemicalStack stack) {
		setChemical(stack);
	}

	@Override
	public long getChemicalTankCapacity(int tank) {
		return getChemical().getAmount();
	}

	@Override
	public boolean isValid(int tank, ChemicalStack stack) {
		return true;
	}

	@Override
	public ChemicalStack insertChemical(int tank, ChemicalStack resource, Action action)  {
		return resource;
	}

	@Override
	public ChemicalStack extractChemical(int tank, long maxDrain, Action action) {
		ChemicalStack chemicalStack = getChemical();
		if(tank != 0 || maxDrain < chemicalStack.getAmount()) {
			return ChemicalStack.EMPTY;
		}
		if(!chemicalStack.isEmpty()) {
			if(action.execute()) {
				setContainerToEmpty();
			}
			return chemicalStack;
		}
		return ChemicalStack.EMPTY;
	}
}
