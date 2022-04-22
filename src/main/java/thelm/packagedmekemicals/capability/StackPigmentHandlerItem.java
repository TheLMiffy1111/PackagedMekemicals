package thelm.packagedmekemicals.capability;

import mekanism.api.Action;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.PigmentStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class StackPigmentHandlerItem implements IPigmentHandler {

	protected ItemStack container;

	public StackPigmentHandlerItem(ItemStack container) {
		this.container = container;
	}

	public PigmentStack getPigment() {
		CompoundTag tagCompound = container.getTag();
		if(tagCompound == null || !tagCompound.contains("Pigment")) {
			return PigmentStack.EMPTY;
		}
		return PigmentStack.readFromNBT(tagCompound.getCompound("Pigment"));
	}

	public void setPigment(PigmentStack pigment)  {
		if(pigment != null && !pigment.isEmpty()) {
			if(!container.hasTag()) {
				container.setTag(new CompoundTag());
			}
			CompoundTag pigmentTag = new CompoundTag();
			pigment.write(pigmentTag);
			container.getTag().put("Pigment", pigmentTag);
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
