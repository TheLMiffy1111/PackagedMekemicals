package thelm.packagedmekemicals.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import thelm.packagedmekemicals.menu.ChemicalPackageFillerMenu;

public record SetChemicalAmountPacket(int amount) implements CustomPacketPayload {

	public static final ResourceLocation ID = new ResourceLocation("packagedmekemicals:set_chemical_amount");

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeInt(amount);
	}

	public static SetChemicalAmountPacket read(FriendlyByteBuf buf) {
		return new SetChemicalAmountPacket(buf.readInt());
	}

	public void handle(PlayPayloadContext ctx) {
		if(ctx.player().orElse(null) instanceof ServerPlayer player) {
			ctx.workHandler().execute(()->{
				if(player.containerMenu instanceof ChemicalPackageFillerMenu menu) {
					menu.blockEntity.requiredAmount = amount;
				}
			});
		}
	}
}
