package thelm.packagedmekemicals.block.entity;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import thelm.packagedauto.api.IVolumePackageItem;
import thelm.packagedauto.block.entity.BaseBlockEntity;
import thelm.packagedauto.block.entity.UnpackagerBlockEntity;
import thelm.packagedauto.energy.EnergyStorage;
import thelm.packagedauto.item.VolumePackageItem;
import thelm.packagedmekemicals.block.ChemicalPackageFillerBlock;
import thelm.packagedmekemicals.inventory.ChemicalPackageFillerItemHandler;
import thelm.packagedmekemicals.menu.ChemicalPackageFillerMenu;
import thelm.packagedmekemicals.util.ChemicalHelper;

public class ChemicalPackageFillerBlockEntity extends BaseBlockEntity {

	public static final BlockEntityType<ChemicalPackageFillerBlockEntity> TYPE_INSTANCE = (BlockEntityType<ChemicalPackageFillerBlockEntity>)BlockEntityType.Builder.
			of(ChemicalPackageFillerBlockEntity::new, ChemicalPackageFillerBlock.INSTANCE).build(null);

	public static int energyCapacity = 5000;
	public static int energyReq = 500;
	public static int energyUsage = 100;

	public boolean firstTick = true;
	public boolean isWorking = false;
	public ChemicalStack<?> currentChemical = GasStack.EMPTY;
	public int requiredAmount = 100;
	public int amount = 0;
	public int remainingProgress = 0;
	public boolean powered = false;
	public boolean activated = false;

	public ChemicalPackageFillerBlockEntity(BlockPos pos, BlockState state) {
		super(TYPE_INSTANCE, pos, state);
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
					energyStorage.receiveEnergy(Math.abs(remainingProgress), false);
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
		ItemStack outputStack = VolumePackageItem.tryMakeVolumePackage(currentChemical);
		return !outputStack.isEmpty() && (slotStack.isEmpty() || slotStack.getItem() == outputStack.getItem() && ItemStack.isSameItemSameTags(slotStack, outputStack) && slotStack.getCount()+1 <= outputStack.getMaxStackSize());
	}

	protected boolean canFinish() {
		return remainingProgress <= 0 && isTemplateValid();
	}

	protected void getChemical() {
		currentChemical = GasStack.EMPTY;
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
				BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(direction));
				if(blockEntity != null) {
					switch(ChemicalType.getTypeFor(currentChemical)) {
					case GAS -> {
						if(blockEntity.getCapability(Capabilities.GAS_HANDLER, direction.getOpposite()).isPresent()) {
							IGasHandler gasHandler = blockEntity.getCapability(Capabilities.GAS_HANDLER, direction.getOpposite()).resolve().get();
							GasStack toDrain = (GasStack)currentChemical.copy();
							toDrain.setAmount(requiredAmount-amount);
							amount += gasHandler.extractChemical(toDrain, Action.EXECUTE).getAmount();
						}
					}
					case INFUSION -> {
						if(blockEntity.getCapability(Capabilities.INFUSION_HANDLER, direction.getOpposite()).isPresent()) {
							IInfusionHandler infusionHandler = blockEntity.getCapability(Capabilities.INFUSION_HANDLER, direction.getOpposite()).resolve().get();
							InfusionStack toDrain = (InfusionStack)currentChemical.copy();
							toDrain.setAmount(requiredAmount-amount);
							amount += infusionHandler.extractChemical(toDrain, Action.EXECUTE).getAmount();
						}
					}
					case PIGMENT -> {
						if(blockEntity.getCapability(Capabilities.PIGMENT_HANDLER, direction.getOpposite()).isPresent()) {
							IPigmentHandler pigmentHandler = blockEntity.getCapability(Capabilities.PIGMENT_HANDLER, direction.getOpposite()).resolve().get();
							PigmentStack toDrain = (PigmentStack)currentChemical.copy();
							toDrain.setAmount(requiredAmount-amount);
							amount += pigmentHandler.extractChemical(toDrain, Action.EXECUTE).getAmount();
						}
					}
					case SLURRY -> {
						if(blockEntity.getCapability(Capabilities.SLURRY_HANDLER, direction.getOpposite()).isPresent()) {
							ISlurryHandler slurryHandler = blockEntity.getCapability(Capabilities.SLURRY_HANDLER, direction.getOpposite()).resolve().get();
							SlurryStack toDrain = (SlurryStack)currentChemical.copy();
							toDrain.setAmount(requiredAmount-amount);
							amount += slurryHandler.extractChemical(toDrain, Action.EXECUTE).getAmount();
						}
					}
					}
				}
			}
		}
		else {
			int energy = energyStorage.extractEnergy(energyUsage, false);
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
		else if(itemHandler.getStackInSlot(1).getItem() instanceof IVolumePackageItem) {
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
			BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(direction));
			if(blockEntity != null && !(blockEntity instanceof UnpackagerBlockEntity)
					&& blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).isPresent()
					&& !blockEntity.getCapability(Capabilities.GAS_HANDLER, direction.getOpposite()).isPresent()
					&& !blockEntity.getCapability(Capabilities.INFUSION_HANDLER, direction.getOpposite()).isPresent()
					&& !blockEntity.getCapability(Capabilities.PIGMENT_HANDLER, direction.getOpposite()).isPresent()
					&& !blockEntity.getCapability(Capabilities.SLURRY_HANDLER, direction.getOpposite()).isPresent()) {
				IItemHandler itemHandler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).resolve().get();
				ItemStack stack = this.itemHandler.getStackInSlot(1);
				if(stack.isEmpty()) {
					return;
				}
				for(int slot = 0; slot < itemHandler.getSlots(); ++slot) {
					ItemStack stackRem = itemHandler.insertItem(slot, stack, false);
					if(stackRem.getCount() < stack.getCount()) {
						stack = stackRem;
					}
					if(stack.isEmpty()) {
						break;
					}
				}
				this.itemHandler.setStackInSlot(1, stack);
			}
		}
	}

	protected void chargeEnergy() {
		int prevStored = energyStorage.getEnergyStored();
		ItemStack energyStack = itemHandler.getStackInSlot(2);
		if(energyStack.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
			int energyRequest = Math.min(energyStorage.getMaxReceive(), energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
			energyStorage.receiveEnergy(energyStack.getCapability(ForgeCapabilities.ENERGY).resolve().get().extractEnergy(energyRequest, false), false);
			if(energyStack.getCount() <= 0) {
				itemHandler.setStackInSlot(2, ItemStack.EMPTY);
			}
		}
	}

	public void updatePowered() {
		if(level.getBestNeighborSignal(worldPosition) > 0 != powered) {
			powered = !powered;
			if(powered) {
				activated = true;
			}
			sync(false);
			setChanged();
		}
	}

	@Override
	public void loadSync(CompoundTag nbt) {
		super.loadSync(nbt);
		isWorking = nbt.getBoolean("Working");
		currentChemical = BoxedChemicalStack.read(nbt.getCompound("Chemical")).getChemicalStack();
		requiredAmount = nbt.getInt("AmountReq");
		amount = nbt.getInt("Amount");
		remainingProgress = nbt.getInt("Progress");
		powered = nbt.getBoolean("Powered");
	}

	@Override
	public CompoundTag saveSync(CompoundTag nbt) {
		super.saveSync(nbt);
		nbt.putBoolean("Working", isWorking);
		nbt.put("Chemical", BoxedChemicalStack.box(currentChemical).write(new CompoundTag()));
		nbt.putInt("AmountReq", requiredAmount);
		nbt.putInt("Amount", amount);
		nbt.putInt("Progress", remainingProgress);
		nbt.putBoolean("Powered", powered);
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
