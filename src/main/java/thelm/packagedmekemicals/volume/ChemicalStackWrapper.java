package thelm.packagedmekemicals.volume;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.api.IVolumeType;
import thelm.packagedmekemicals.api.IChemicalStackWrapper;

public record ChemicalStackWrapper(ChemicalStack stack) implements IChemicalStackWrapper {

	public static final ChemicalStackWrapper EMPTY = new ChemicalStackWrapper(ChemicalStack.EMPTY);

	public static final Codec<ChemicalStackWrapper> CODEC = ChemicalStack.CODEC.xmap(
			ChemicalStackWrapper::of, ChemicalStackWrapper::getChemical);
	public static final StreamCodec<RegistryFriendlyByteBuf, ChemicalStackWrapper> STREAM_CODEC = ChemicalStack.STREAM_CODEC.map(
			ChemicalStackWrapper::of, ChemicalStackWrapper::getChemical);

	public static ChemicalStackWrapper of(ChemicalStack stack) {
		if(stack.isEmpty()) {
			return EMPTY;
		}
		return new ChemicalStackWrapper(stack);
	}

	@Override
	public IVolumeType getVolumeType() {
		return ChemicalVolumeType.INSTANCE;
	}

	@Override
	public ChemicalStack getChemical() {
		return stack;
	}

	@Override
	public int getAmount() {
		return (int)stack.getAmount();
	}

	@Override
	public IVolumeStackWrapper copy() {
		return new ChemicalStackWrapper(stack.copy());
	}

	@Override
	public IVolumeStackWrapper withAmount(int amount) {
		return new ChemicalStackWrapper(stack.copyWithAmount(amount));
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public CompoundTag saveAEKey(CompoundTag tag, HolderLookup.Provider registries) {
		tag.putString("#t", "appmek:chemical");
		tag.putString("id", MekanismAPI.CHEMICAL_REGISTRY.getKey(stack.getChemical()).toString());
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
		if(obj instanceof ChemicalStackWrapper other) {
			return stack.equals(other.stack);
		}
		return false;
	}
}
