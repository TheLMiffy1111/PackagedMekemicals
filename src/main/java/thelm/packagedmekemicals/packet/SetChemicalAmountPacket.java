package thelm.packagedmekemicals.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thelm.packagedmekemicals.menu.ChemicalPackageFillerMenu;

public record SetChemicalAmountPacket(int amount) implements CustomPacketPayload {

	public static final Type<SetChemicalAmountPacket> TYPE = new Type<>(ResourceLocation.parse("packagedmekemicals:set_chemical_amount"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SetChemicalAmountPacket> STREAM_CODEC = ByteBufCodecs.INT.
			map(SetChemicalAmountPacket::new, SetChemicalAmountPacket::amount).cast();

	@Override
	public Type<SetChemicalAmountPacket> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		if(ctx.player() instanceof ServerPlayer player) {
			ctx.enqueueWork(()->{
				if(player.containerMenu instanceof ChemicalPackageFillerMenu menu) {
					menu.blockEntity.requiredAmount = amount;
				}
			});
		}
	}
}
