package thelm.packagedmekemicals;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import thelm.packagedmekemicals.client.event.ClientEventHandler;
import thelm.packagedmekemicals.event.CommonEventHandler;

@Mod(PackagedMekemicals.MOD_ID)
public class PackagedMekemicals {

	public static final String MOD_ID = "packagedmekemicals";
	public static PackagedMekemicals core;

	public PackagedMekemicals() {
		core = this;
		CommonEventHandler.getInstance().onConstruct();
		DistExecutor.runWhenOn(Dist.CLIENT, ()->()->{
			ClientEventHandler.getInstance().onConstruct();
		});
	}
}
