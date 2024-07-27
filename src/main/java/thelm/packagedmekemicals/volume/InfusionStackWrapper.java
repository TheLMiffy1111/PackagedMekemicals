package thelm.packagedmekemicals.volume;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.infuse.InfusionStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.api.IVolumeType;
import thelm.packagedmekemicals.api.IChemicalStackWrapper;

public record InfusionStackWrapper(InfusionStack stack) implements IChemicalStackWrapper {

	public static final InfusionStackWrapper EMPTY = new InfusionStackWrapper(InfusionStack.EMPTY);

	public static final Codec<InfusionStackWrapper> CODEC = InfusionStack.CODEC.xmap(
			InfusionStackWrapper::of, InfusionStackWrapper::getChemical);
	public static final StreamCodec<RegistryFriendlyByteBuf, InfusionStackWrapper> STREAM_CODEC = InfusionStack.STREAM_CODEC.map(
			InfusionStackWrapper::of, InfusionStackWrapper::getChemical);

	public static InfusionStackWrapper of(InfusionStack stack) {
		if(stack.isEmpty()) {
			return EMPTY;
		}
		return new InfusionStackWrapper(stack);
	}

	@Override
	public IVolumeType getVolumeType() {
		return InfusionVolumeType.INSTANCE;
	}

	@Override
	public InfusionStack getChemical() {
		return stack;
	}

	@Override
	public int getAmount() {
		return (int)stack.getAmount();
	}

	@Override
	public IVolumeStackWrapper copy() {
		return new InfusionStackWrapper(stack.copy());
	}

	@Override
	public IVolumeStackWrapper withAmount(int amount) {
		return new InfusionStackWrapper(stack.copyWithAmount(amount));
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public CompoundTag saveAEKey(CompoundTag tag, HolderLookup.Provider registries) {
		tag.putString("#t", "appmek:chemical");
		CompoundTag idTag = new CompoundTag();
		idTag.putString("chemical_type", "infuse_type");
		idTag.putString("infuse_type", MekanismAPI.INFUSE_TYPE_REGISTRY.getKey(stack.getChemical()).toString());
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
		if(obj instanceof InfusionStackWrapper other) {
			return stack.equals(other.stack);
		}
		return false;
	}
}
