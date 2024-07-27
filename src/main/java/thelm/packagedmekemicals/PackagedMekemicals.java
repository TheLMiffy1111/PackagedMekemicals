package thelm.packagedmekemicals;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import thelm.packagedmekemicals.event.CommonEventHandler;

@Mod(PackagedMekemicals.MOD_ID)
public class PackagedMekemicals {

	public static final String MOD_ID = "packagedmekemicals";

	public PackagedMekemicals(IEventBus modEventBus) {
		CommonEventHandler.getInstance().onConstruct(modEventBus);
	}
}
