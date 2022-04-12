package thelm.packagedmekemicals.capability;

import mekanism.api.Action;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfusionStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class StackInfusionHandlerItem implements IInfusionHandler {

	protected ItemStack container;

	public StackInfusionHandlerItem(ItemStack container) {
		this.container = container;
	}

	public InfusionStack getInfusion() {
		CompoundTag tagCompound = container.getTag();
		if(tagCompound == null || !tagCompound.contains("Infusion")) {
			return InfusionStack.EMPTY;
		}
		return InfusionStack.readFromNBT(tagCompound.getCompound("Infusion"));
	}

	public void setInfusion(InfusionStack infusion)  {
		if(infusion != null && !infusion.isEmpty()) {
			if(!container.hasTag()) {
				container.setTag(new CompoundTag());
			}
			CompoundTag infusionTag = new CompoundTag();
			infusion.write(infusionTag);
			container.getTag().put("Infusion", infusionTag);
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
		if(tank != 0 || container.getCount() != 1 || maxDrain < infusionStack.getAmount()) {
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
