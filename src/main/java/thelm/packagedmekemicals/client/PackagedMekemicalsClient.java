package thelm.packagedmekemicals.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import thelm.packagedmekemicals.PackagedMekemicals;
import thelm.packagedmekemicals.client.event.ClientEventHandler;

@Mod(value = PackagedMekemicals.MOD_ID, dist = Dist.CLIENT)
public class PackagedMekemicalsClient {

	public PackagedMekemicalsClient(IEventBus modEventBus) {
		ClientEventHandler.getInstance().onConstruct(modEventBus);
	}
}
