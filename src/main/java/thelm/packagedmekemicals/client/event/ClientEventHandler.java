package thelm.packagedmekemicals.client.event;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import thelm.packagedmekemicals.client.screen.ChemicalPackageFillerScreen;
import thelm.packagedmekemicals.menu.PackagedMekemicalsMenus;

public class ClientEventHandler {

	public static final ClientEventHandler INSTANCE = new ClientEventHandler();

	public static ClientEventHandler getInstance() {
		return INSTANCE;
	}

	public void onConstruct(IEventBus modEventBus) {
		modEventBus.register(this);
	}

	@SubscribeEvent
	public void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
		event.register(PackagedMekemicalsMenus.CHEMICAL_PACKAGE_FILLER.get(), ChemicalPackageFillerScreen::new);
	}
}
