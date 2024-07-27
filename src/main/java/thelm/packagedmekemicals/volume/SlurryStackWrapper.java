package thelm.packagedmekemicals.volume;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.api.IVolumeType;
import thelm.packagedmekemicals.api.IChemicalStackWrapper;

public record SlurryStackWrapper(SlurryStack stack) implements IChemicalStackWrapper {

	public static final SlurryStackWrapper EMPTY = new SlurryStackWrapper(SlurryStack.EMPTY);

	public static final Codec<SlurryStackWrapper> CODEC = SlurryStack.CODEC.xmap(
			SlurryStackWrapper::of, SlurryStackWrapper::getChemical);
	public static final StreamCodec<RegistryFriendlyByteBuf, SlurryStackWrapper> STREAM_CODEC = SlurryStack.STREAM_CODEC.map(
			SlurryStackWrapper::of, SlurryStackWrapper::getChemical);

	public static SlurryStackWrapper of(SlurryStack stack) {
		if(stack.isEmpty()) {
			return EMPTY;
		}
		return new SlurryStackWrapper(stack);
	}

	@Override
	public IVolumeType getVolumeType() {
		return SlurryVolumeType.INSTANCE;
	}

	@Override
	public SlurryStack getChemical() {
		return stack;
	}

	@Override
	public int getAmount() {
		return (int)stack.getAmount();
	}

	@Override
	public IVolumeStackWrapper copy() {
		return new SlurryStackWrapper(stack.copy());
	}

	@Override
	public IVolumeStackWrapper withAmount(int amount) {
		return new SlurryStackWrapper(stack.copyWithAmount(amount));
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public CompoundTag saveAEKey(CompoundTag tag, HolderLookup.Provider registries) {
		tag.putString("#t", "appmek:chemical");
		CompoundTag idTag = new CompoundTag();
		idTag.putString("chemical_type", "slurry");
		idTag.putString("slurry", MekanismAPI.SLURRY_REGISTRY.getKey(stack.getChemical()).toString());
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
		if(obj instanceof SlurryStackWrapper other) {
			return stack.equals(other.stack);
		}
		return false;
	}
}
