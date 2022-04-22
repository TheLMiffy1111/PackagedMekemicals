package thelm.packagedmekemicals.inventory;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.inventory.SidedItemHandlerWrapper;

public class ChemicalPackageFillerItemHandlerWrapper extends SidedItemHandlerWrapper<ChemicalPackageFillerItemHandler> {

	public static final int[] SLOTS = {1};

	public ChemicalPackageFillerItemHandlerWrapper(ChemicalPackageFillerItemHandler itemHandler, Direction direction) {
		super(itemHandler, direction);
	}

	@Override
	public int[] getSlotsForDirection(Direction direction) {
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, Direction direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, Direction direction) {
		return slot == 1;
	}
}
