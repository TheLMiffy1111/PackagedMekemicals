package thelm.packagedmekemicals.block.entity;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import thelm.packagedauto.block.PackagedAutoBlocks;
import thelm.packagedauto.block.entity.BaseBlockEntity;
import thelm.packagedauto.component.PackagedAutoDataComponents;
import thelm.packagedauto.energy.EnergyStorage;
import thelm.packagedauto.item.VolumePackageItem;
import thelm.packagedauto.util.MiscHelper;
import thelm.packagedmekemicals.inventory.ChemicalPackageFillerItemHandler;
import thelm.packagedmekemicals.menu.ChemicalPackageFillerMenu;
import thelm.packagedmekemicals.util.ChemicalHelper;

public class ChemicalPackageFillerBlockEntity extends BaseBlockEntity {

	public static int energyCapacity = 5000;
	public static int energyReq = 500;
	public static int energyUsage = 100;

	public boolean firstTick = true;
	public boolean isWorking = false;
	public ChemicalStack currentChemical = ChemicalStack.EMPTY;
	public int requiredAmount = 100;
	public int amount = 0;
	public int remainingProgress = 0;
	public boolean powered = false;
	public boolean activated = false;

	public ChemicalPackageFillerBlockEntity(BlockPos pos, BlockState state) {
		super(PackagedMekemicalsBlockEntities.CHEMICAL_PACKAGE_FILLER.get(), pos, state);
		setItemHandler(new ChemicalPackageFillerItemHandler(this));
		setEnergyStorage(new EnergyStorage(this, energyCapacity));
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("block.packagedmekemicals.chemical_package_filler");
	}

	@Override
	public void tick() {
		if(firstTick) {
			firstTick = false;
			updatePowered();
		}
		if(!level.isClientSide) {
			if(isWorking) {
				tickProcess();
				if(remainingProgress <= 0 && isTemplateValid()) {
					finishProcess();
					if(!itemHandler.getStackInSlot(1).isEmpty()) {
						ejectItem();
					}
					if(!canStart()) {
						endProcess();
					}
					else {
						startProcess();
					}
				}
			}
			else if(activated) {
				if(canStart()) {
					startProcess();
					tickProcess();
					activated = false;
					isWorking = true;
				}
			}
			chargeEnergy();
			if(level.getGameTime() % 8 == 0) {
				if(!itemHandler.getStackInSlot(1).isEmpty()) {
					ejectItem();
				}
			}
			energyStorage.updateIfChanged();
		}
	}

	public boolean isTemplateValid() {
		if(currentChemical.isEmpty()) {
			getChemical();
		}
		if(currentChemical.isEmpty()) {
			return false;
		}
		return true;
	}

	public boolean canStart() {
		getChemical();
		if(currentChemical.isEmpty()) {
			return false;
		}
		if(!isTemplateValid()) {
			return false;
		}
		ItemStack slotStack = itemHandler.getStackInSlot(1);
		ItemStack outputStack = MiscHelper.INSTANCE.tryMakeVolumePackage(currentChemical);
		return !outputStack.isEmpty() && (slotStack.isEmpty() || ItemStack.isSameItemSameComponents(slotStack, outputStack) && slotStack.getCount()+1 <= outputStack.getMaxStackSize());
	}

	protected boolean canFinish() {
		return remainingProgress <= 0 && isTemplateValid();
	}

	protected void getChemical() {
		currentChemical = ChemicalStack.EMPTY;
		ItemStack template = itemHandler.getStackInSlot(0);
		if(template.isEmpty()) {
			return;
		}
		ChemicalHelper.INSTANCE.getChemicalContained(template).filter(s->!s.isEmpty()).ifPresent(s->{
			(currentChemical = s.copy()).setAmount(requiredAmount);
		});
	}

	protected void tickProcess() {
		if(amount < requiredAmount) {
			for(Direction direction : Direction.values()) {
				BlockPos offsetPos = worldPosition.relative(direction);
				IChemicalHandler chemicalHandler = level.getCapability(Capabilities.CHEMICAL.block(), offsetPos, direction.getOpposite());
				if(chemicalHandler != null) {
					ChemicalStack toDrain = currentChemical.copy();
					toDrain.setAmount(requiredAmount-amount);
					amount += chemicalHandler.extractChemical(toDrain, Action.EXECUTE).getAmount();
				}
			}
		}
		if(amount >= requiredAmount) {
			int energy = energyStorage.extractEnergy(Math.min(energyUsage, remainingProgress), false);
			remainingProgress -= energy;
		}
	}

	protected void finishProcess() {
		if(currentChemical.isEmpty()) {
			getChemical();
		}
		if(currentChemical.isEmpty()) {
			endProcess();
			return;
		}
		if(itemHandler.getStackInSlot(1).isEmpty()) {
			itemHandler.setStackInSlot(1, VolumePackageItem.tryMakeVolumePackage(currentChemical));
		}
		else if(itemHandler.getStackInSlot(1).has(PackagedAutoDataComponents.VOLUME_PACKAGE_STACK)) {
			itemHandler.getStackInSlot(1).grow(1);
		}
		endProcess();
	}

	public void startProcess() {
		remainingProgress = energyReq;
		amount = 0;
		setChanged();
	}

	public void endProcess() {
		remainingProgress = 0;
		amount = 0;
		isWorking = false;
		setChanged();
	}

	protected void ejectItem() {
		for(Direction direction : Direction.values()) {
			BlockPos offsetPos = worldPosition.relative(direction);
			Block block = level.getBlockState(offsetPos).getBlock();
			IItemHandler itemHandler = level.getCapability(Capabilities.ITEM.block(), offsetPos, direction.getOpposite());
			IChemicalHandler chemicalHandler = level.getCapability(Capabilities.CHEMICAL.block(), offsetPos, direction.getOpposite());
			if(block != PackagedAutoBlocks.UNPACKAGER.get() && itemHandler != null && chemicalHandler == null) {
				ItemStack stack = this.itemHandler.getStackInSlot(1);
				if(!stack.isEmpty()) {
					ItemStack stackRem = ItemHandlerHelper.insertItem(itemHandler, stack, false);
					this.itemHandler.setStackInSlot(1, stackRem);
				}
			}
		}
	}

	protected void chargeEnergy() {
		ItemStack energyStack = itemHandler.getStackInSlot(2);
		IEnergyStorage itemEnergyStorage = energyStack.getCapability(Capabilities.ENERGY.item());
		if(itemEnergyStorage != null) {
			int energyRequest = Math.min(energyStorage.getMaxReceive(), energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
			energyStorage.receiveEnergy(itemEnergyStorage.extractEnergy(energyRequest, false), false);
			if(energyStack.getCount() <= 0) {
				itemHandler.setStackInSlot(2, ItemStack.EMPTY);
			}
		}
	}

	public void updatePowered() {
		if(level.getBestNeighborSignal(worldPosition) > 0 != powered) {
			powered = !powered;
			if(powered && !isWorking) {
				activated = true;
			}
			setChanged();
		}
	}

	@Override
	public int getComparatorSignal() {
		if(isWorking) {
			return 1;
		}
		if(!itemHandler.getStackInSlot(1).isEmpty()) {
			return 15;
		}
		return 0;
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.loadAdditional(nbt, registries);
		isWorking = nbt.getBoolean("working");
		amount = nbt.getInt("amount");
		remainingProgress = nbt.getInt("progress");
		powered = nbt.getBoolean("powered");
	}

	@Override
	public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.saveAdditional(nbt, registries);
		nbt.putBoolean("working", isWorking);
		nbt.putInt("amount", amount);
		nbt.putInt("progress", remainingProgress);
		nbt.putBoolean("powered", powered);
	}

	@Override
	public void loadSync(CompoundTag nbt, HolderLookup.Provider registries) {
		super.loadSync(nbt, registries);
		currentChemical = ChemicalStack.parseOptional(registries, nbt.getCompound("chemical"));
		requiredAmount = nbt.getInt("amount_req");
	}

	@Override
	public CompoundTag saveSync(CompoundTag nbt, HolderLookup.Provider registries) {
		super.saveSync(nbt, registries);
		nbt.put("chemical", currentChemical.saveOptional(registries));
		nbt.putInt("amount_req", requiredAmount);
		return nbt;
	}

	@Override
	public void setChanged() {
		if(isWorking && !isTemplateValid()) {
			endProcess();
		}
		super.setChanged();
	}

	public int getScaledEnergy(int scale) {
		if(energyStorage.getMaxEnergyStored() <= 0) {
			return 0;
		}
		return Math.min(scale * energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored(), scale);
	}

	public int getScaledProgress(int scale) {
		if(remainingProgress <= 0 || energyReq <= 0) {
			return 0;
		}
		return scale * (energyReq-remainingProgress) / energyReq;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		sync(false);
		return new ChemicalPackageFillerMenu(windowId, inventory, this);
	}
}
