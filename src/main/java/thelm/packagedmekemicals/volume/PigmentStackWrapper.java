package thelm.packagedmekemicals.volume;

import java.util.List;

import com.google.common.collect.Lists;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.pigment.PigmentStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.api.IVolumeType;
import thelm.packagedmekemicals.api.IChemicalStackWrapper;

public record PigmentStackWrapper(PigmentStack stack) implements IChemicalStackWrapper {

	public static final PigmentStackWrapper EMPTY = new PigmentStackWrapper(PigmentStack.EMPTY);

	@Override
	public IVolumeType getVolumeType() {
		return PigmentVolumeType.INSTANCE;
	}

	@Override
	public ChemicalStack<?> getChemical() {
		return stack;
	}

	@Override
	public int getAmount() {
		return (int)stack.getAmount();
	}

	@Override
	public IVolumeStackWrapper copy() {
		return new PigmentStackWrapper(stack.copy());
	}

	@Override
	public void setAmount(int amount) {
		stack.setAmount(amount);
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		return stack.write(tag);
	}

	@Override
	public CompoundTag saveAEKey(CompoundTag tag) {
		tag.putString("#c", "appmek:chemical");
		tag.putByte("t", (byte)2);
		return stack.write(tag);
	}

	@Override
	public Component getDisplayName() {
		return stack.getTextComponent();
	}

	@Override
	public Component getAmountDesc() {
		return new TextComponent(stack.getAmount()+"mB");
	}

	@Override
	public List<Component> getTooltip() {
		return Lists.newArrayList(stack.getTextComponent());
	}

	@Override
	public int hashCode() {
		return stack.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PigmentStackWrapper other) {
			return stack.equals(other.stack);
		}
		return false;
	}
}
