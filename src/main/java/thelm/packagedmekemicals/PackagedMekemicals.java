package thelm.packagedmekemicals;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import thelm.packagedmekemicals.block.ChemicalPackageFillerBlock;
import thelm.packagedmekemicals.client.event.ClientEventHandler;
import thelm.packagedmekemicals.event.CommonEventHandler;

@Mod(PackagedMekemicals.MOD_ID)
public class PackagedMekemicals {

	public static final String MOD_ID = "packagedmekemicals";
	public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab("packagedmekemicals") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ChemicalPackageFillerBlock.ITEM_INSTANCE);
		}
	};
	public static PackagedMekemicals core;

	public PackagedMekemicals() {
		core = this;
		CommonEventHandler.getInstance().onConstruct();
		DistExecutor.runWhenOn(Dist.CLIENT, ()->()->{
			ClientEventHandler.getInstance().onConstruct();
		});
	}
}
