package thelm.packagedmekemicals.inventory;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.IItemHandlerModifiable;
import thelm.packagedauto.inventory.BaseItemHandler;
import thelm.packagedmekemicals.block.entity.ChemicalPackageFillerBlockEntity;
import thelm.packagedmekemicals.util.ChemicalHelper;

public class ChemicalPackageFillerItemHandler extends BaseItemHandler<ChemicalPackageFillerBlockEntity> {

	public ChemicalPackageFillerItemHandler(ChemicalPackageFillerBlockEntity blockEntity) {
		super(blockEntity, 3);
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) {
		return switch(index) {
		case 1 -> false;
		case 2 -> stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
		default -> (blockEntity.isWorking ? !getStackInSlot(index).isEmpty() : true) && ChemicalHelper.INSTANCE.hasChemicalHandler(stack);
		};
	}

	@Override
	public IItemHandlerModifiable getWrapperForDirection(Direction side) {
		return wrapperMap.computeIfAbsent(side, s->new ChemicalPackageFillerItemHandlerWrapper(this, s));
	}

	@Override
	public int get(int id) {
		return switch(id) {
		case 0 -> blockEntity.requiredAmount;
		case 1 -> blockEntity.amount;
		case 2 -> blockEntity.remainingProgress;
		case 3 -> blockEntity.isWorking ? 1 : 0;
		default -> 0;
		};
	}

	@Override
	public void set(int id, int value) {
		switch(id) {
		case 0 -> blockEntity.requiredAmount = value;
		case 1 -> blockEntity.amount = value;
		case 2 -> blockEntity.remainingProgress = value;
		case 3 -> blockEntity.isWorking = value != 0;
		}
	}

	@Override
	public int getCount() {
		return 4;
	}
}
