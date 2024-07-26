package thelm.packagedmekemicals.network.packet;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import thelm.packagedmekemicals.menu.ChemicalPackageFillerMenu;

public record SetChemicalAmountPacket(int amount) {

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(amount);
	}

	public static SetChemicalAmountPacket decode(FriendlyByteBuf buf) {
		return new SetChemicalAmountPacket(buf.readInt());
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayer player = ctx.get().getSender();
		ctx.get().enqueueWork(()->{
			if(player.containerMenu instanceof ChemicalPackageFillerMenu menu) {
				menu.blockEntity.requiredAmount = amount;
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
