package thelm.packagedmekemicals.volume;

import java.util.List;

import com.google.common.collect.Lists;

import mekanism.api.chemical.gas.GasStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.api.IVolumeType;

public record GasStackWrapper(GasStack stack) implements IVolumeStackWrapper {

	public static final GasStackWrapper EMPTY = new GasStackWrapper(GasStack.EMPTY);

	@Override
	public IVolumeType getVolumeType() {
		return GasVolumeType.INSTANCE;
	}

	@Override
	public int getAmount() {
		return (int)stack.getAmount();
	}

	@Override
	public IVolumeStackWrapper copy() {
		return new GasStackWrapper(stack.copy());
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
		tag.putByte("t", (byte)0);
		return stack.write(tag);
	}

	@Override
	public Component getDisplayName() {
		return stack.getTextComponent();
	}

	@Override
	public String getAmountDesc() {
		return stack.getAmount()+"mB";
	}

	@Override
	public List<Component> getTooltip() {
		return Lists.newArrayList(stack.getTextComponent(), new TextComponent(getAmountDesc()));
	}

	@Override
	public int hashCode() {
		return stack.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof GasStackWrapper other) {
			return stack.equals(other.stack);
		}
		return false;
	}
}
