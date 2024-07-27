package thelm.packagedmekemicals.volume;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.api.IVolumeType;
import thelm.packagedmekemicals.api.IChemicalStackWrapper;

public record GasStackWrapper(GasStack stack) implements IChemicalStackWrapper {

	public static final GasStackWrapper EMPTY = new GasStackWrapper(GasStack.EMPTY);

	public static final Codec<GasStackWrapper> CODEC = GasStack.CODEC.xmap(
			GasStackWrapper::of, GasStackWrapper::getChemical);
	public static final StreamCodec<RegistryFriendlyByteBuf, GasStackWrapper> STREAM_CODEC = GasStack.STREAM_CODEC.map(
			GasStackWrapper::of, GasStackWrapper::getChemical);

	public static GasStackWrapper of(GasStack stack) {
		if(stack.isEmpty()) {
			return EMPTY;
		}
		return new GasStackWrapper(stack);
	}

	@Override
	public IVolumeType getVolumeType() {
		return GasVolumeType.INSTANCE;
	}

	@Override
	public GasStack getChemical() {
		return stack;
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
	public IVolumeStackWrapper withAmount(int amount) {
		return new GasStackWrapper(stack.copyWithAmount(amount));
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public CompoundTag saveAEKey(CompoundTag tag, HolderLookup.Provider registries) {
		tag.putString("#t", "appmek:chemical");
		CompoundTag idTag = new CompoundTag();
		idTag.putString("chemical_type", "gas");
		idTag.putString("gas", MekanismAPI.GAS_REGISTRY.getKey(stack.getChemical()).toString());
		tag.put("id", idTag);
		return tag;
	}

	@Override
	public Component getDisplayName() {
		return stack.getTextComponent();
	}

	@Override
	public Component getAmountDesc() {
		return Component.literal(stack.getAmount()+"mB");
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
		if(obj instanceof GasStackWrapper other) {
			return stack.equals(other.stack);
		}
		return false;
	}
}
