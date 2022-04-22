package thelm.packagedmekemicals.network.packet;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import thelm.packagedmekemicals.menu.ChemicalPackageFillerMenu;

public class SetChemicalAmountPacket {

	private int amount;

	public SetChemicalAmountPacket(int amount) {
		this.amount = amount;
	}

	public static void encode(SetChemicalAmountPacket pkt, FriendlyByteBuf buf) {
		buf.writeInt(pkt.amount);
	}

	public static SetChemicalAmountPacket decode(FriendlyByteBuf buf) {
		return new SetChemicalAmountPacket(buf.readInt());
	}

	public static void handle(SetChemicalAmountPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ServerPlayer player = ctx.get().getSender();
		ctx.get().enqueueWork(()->{
			if(player.containerMenu instanceof ChemicalPackageFillerMenu menu) {
				menu.blockEntity.requiredAmount = pkt.amount;
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
