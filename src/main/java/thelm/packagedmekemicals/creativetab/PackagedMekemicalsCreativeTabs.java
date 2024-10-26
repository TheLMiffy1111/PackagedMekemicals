package thelm.packagedmekemicals.creativetab;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thelm.packagedmekemicals.item.PackagedMekemicalsItems;

public class PackagedMekemicalsCreativeTabs {

	private PackagedMekemicalsCreativeTabs() {}

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "packagedmekemicals");

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_TABS.register(
			"tab", ()->CreativeModeTab.builder().
			title(Component.translatable("itemGroup.packagedmekemicals")).
			icon(PackagedMekemicalsItems.CHEMICAL_PACKAGE_FILLER::toStack).
			displayItems((parameters, output)->{
				output.accept(PackagedMekemicalsItems.CHEMICAL_PACKAGE_FILLER);
			}).build());
}
