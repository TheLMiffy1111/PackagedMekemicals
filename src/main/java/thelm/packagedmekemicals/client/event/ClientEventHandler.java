package thelm.packagedmekemicals.client.event;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import thelm.packagedmekemicals.client.screen.ChemicalPackageFillerScreen;
import thelm.packagedmekemicals.menu.ChemicalPackageFillerMenu;

public class ClientEventHandler {

	public static final ClientEventHandler INSTANCE = new ClientEventHandler();

	public static ClientEventHandler getInstance() {
		return INSTANCE;
	}

	public void onConstruct() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event) {
		MenuScreens.register(ChemicalPackageFillerMenu.TYPE_INSTANCE, ChemicalPackageFillerScreen::new);
	}
}
