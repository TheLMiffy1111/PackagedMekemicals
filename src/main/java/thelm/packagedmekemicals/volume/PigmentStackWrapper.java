package thelm.packagedmekemicals.volume;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.pigment.PigmentStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.api.IVolumeType;
import thelm.packagedmekemicals.api.IChemicalStackWrapper;

public record PigmentStackWrapper(PigmentStack stack) implements IChemicalStackWrapper {

	public static final PigmentStackWrapper EMPTY = new PigmentStackWrapper(PigmentStack.EMPTY);

	public static final Codec<PigmentStackWrapper> CODEC = PigmentStack.CODEC.xmap(
			PigmentStackWrapper::of, PigmentStackWrapper::getChemical);
	public static final StreamCodec<RegistryFriendlyByteBuf, PigmentStackWrapper> STREAM_CODEC = PigmentStack.STREAM_CODEC.map(
			PigmentStackWrapper::of, PigmentStackWrapper::getChemical);

	public static PigmentStackWrapper of(PigmentStack stack) {
		if(stack.isEmpty()) {
			return EMPTY;
		}
		return new PigmentStackWrapper(stack);
	}

	@Override
	public IVolumeType getVolumeType() {
		return PigmentVolumeType.INSTANCE;
	}

	@Override
	public PigmentStack getChemical() {
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
	public IVolumeStackWrapper withAmount(int amount) {
		return new PigmentStackWrapper(stack.copyWithAmount(amount));
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public CompoundTag saveAEKey(CompoundTag tag, HolderLookup.Provider registries) {
		tag.putString("#t", "appmek:chemical");
		CompoundTag idTag = new CompoundTag();
		idTag.putString("chemical_type", "pigment");
		idTag.putString("pigment", MekanismAPI.PIGMENT_REGISTRY.getKey(stack.getChemical()).toString());
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
		if(obj instanceof PigmentStackWrapper other) {
			return stack.equals(other.stack);
		}
		return false;
	}
}
