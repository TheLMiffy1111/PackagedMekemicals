package thelm.packagedmekemicals.network;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import thelm.packagedmekemicals.network.packet.SetChemicalAmountPacket;

public class PacketHandler {

	public static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation("packagedmekemicals", PROTOCOL_VERSION),
			()->PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static void registerPackets() {
		int id = 0;
		INSTANCE.registerMessage(id++, SetChemicalAmountPacket.class,
				SetChemicalAmountPacket::encode, SetChemicalAmountPacket::decode,
				SetChemicalAmountPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
	}
}
