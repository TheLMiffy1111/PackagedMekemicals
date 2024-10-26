package thelm.packagedmekemicals.event;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import thelm.packagedauto.block.entity.BaseBlockEntity;
import thelm.packagedauto.util.ApiImpl;
import thelm.packagedmekemicals.block.PackagedMekemicalsBlocks;
import thelm.packagedmekemicals.block.entity.PackagedMekemicalsBlockEntities;
import thelm.packagedmekemicals.config.PackagedMekemicalsConfig;
import thelm.packagedmekemicals.creativetab.PackagedMekemicalsCreativeTabs;
import thelm.packagedmekemicals.item.PackagedMekemicalsItems;
import thelm.packagedmekemicals.menu.PackagedMekemicalsMenus;
import thelm.packagedmekemicals.packet.SetChemicalAmountPacket;
import thelm.packagedmekemicals.volume.ChemicalVolumeType;
import thelm.packagedmekemicals.volume.GasVolumeType;
import thelm.packagedmekemicals.volume.InfusionVolumeType;
import thelm.packagedmekemicals.volume.PigmentVolumeType;
import thelm.packagedmekemicals.volume.SlurryVolumeType;

public class CommonEventHandler {

	public static final CommonEventHandler INSTANCE = new CommonEventHandler();

	public static CommonEventHandler getInstance() {
		return INSTANCE;
	}

	public void onConstruct(IEventBus modEventBus) {
		modEventBus.register(this);
		PackagedMekemicalsConfig.registerConfig();

		PackagedMekemicalsBlocks.BLOCKS.register(modEventBus);
		PackagedMekemicalsItems.ITEMS.register(modEventBus);
		PackagedMekemicalsBlockEntities.BLOCK_ENTITIES.register(modEventBus);
		PackagedMekemicalsMenus.MENUS.register(modEventBus);
		PackagedMekemicalsCreativeTabs.CREATIVE_TABS.register(modEventBus);
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event) {
		ApiImpl.INSTANCE.registerVolumeType(ChemicalVolumeType.INSTANCE);

		ApiImpl.INSTANCE.registerVolumeType(GasVolumeType.INSTANCE);
		ApiImpl.INSTANCE.registerVolumeType(InfusionVolumeType.INSTANCE);
		ApiImpl.INSTANCE.registerVolumeType(PigmentVolumeType.INSTANCE);
		ApiImpl.INSTANCE.registerVolumeType(SlurryVolumeType.INSTANCE);
	}

	@SubscribeEvent
	public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, PackagedMekemicalsBlockEntities.CHEMICAL_PACKAGE_FILLER.get(), BaseBlockEntity::getItemHandler);

		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, PackagedMekemicalsBlockEntities.CHEMICAL_PACKAGE_FILLER.get(), BaseBlockEntity::getEnergyStorage);
	}

	@SubscribeEvent
	public void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("packagedmekemicals");
		registrar.playToServer(SetChemicalAmountPacket.TYPE, SetChemicalAmountPacket.STREAM_CODEC, SetChemicalAmountPacket::handle);
	}

	@SubscribeEvent
	public void onModConfigLoading(ModConfigEvent.Loading event) {
		switch(event.getConfig().getType()) {
		case SERVER -> PackagedMekemicalsConfig.reloadServerConfig();
		default -> {}
		}
	}

	@SubscribeEvent
	public void onModConfigReloading(ModConfigEvent.Reloading event) {
		switch(event.getConfig().getType()) {
		case SERVER -> PackagedMekemicalsConfig.reloadServerConfig();
		default -> {}
		}
	}
}
