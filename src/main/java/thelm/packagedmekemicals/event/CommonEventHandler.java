package thelm.packagedmekemicals.event;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import thelm.packagedauto.util.ApiImpl;
import thelm.packagedmekemicals.block.ChemicalPackageFillerBlock;
import thelm.packagedmekemicals.block.entity.ChemicalPackageFillerBlockEntity;
import thelm.packagedmekemicals.config.PackagedMekemicalsConfig;
import thelm.packagedmekemicals.menu.ChemicalPackageFillerMenu;
import thelm.packagedmekemicals.network.PacketHandler;
import thelm.packagedmekemicals.volume.GasVolumeType;
import thelm.packagedmekemicals.volume.InfusionVolumeType;
import thelm.packagedmekemicals.volume.PigmentVolumeType;
import thelm.packagedmekemicals.volume.SlurryVolumeType;

public class CommonEventHandler {

	public static final CommonEventHandler INSTANCE = new CommonEventHandler();

	public static CommonEventHandler getInstance() {
		return INSTANCE;
	}

	public void onConstruct() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		PackagedMekemicalsConfig.registerConfig();
	}

	@SubscribeEvent
	public void onBlockRegister(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();
		registry.register(ChemicalPackageFillerBlock.INSTANCE);
	}

	@SubscribeEvent
	public void onItemRegister(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		registry.register(ChemicalPackageFillerBlock.ITEM_INSTANCE);
	}

	@SubscribeEvent
	public void onBlockEntityRegister(RegistryEvent.Register<BlockEntityType<?>> event) {
		IForgeRegistry<BlockEntityType<?>> registry = event.getRegistry();
		registry.register(ChemicalPackageFillerBlockEntity.TYPE_INSTANCE);
	}

	@SubscribeEvent
	public void onMenuTypeRegister(RegistryEvent.Register<MenuType<?>> event) {
		IForgeRegistry<MenuType<?>> registry = event.getRegistry();
		registry.register(ChemicalPackageFillerMenu.TYPE_INSTANCE);
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event) {
		ApiImpl.INSTANCE.registerVolumeType(GasVolumeType.INSTANCE);
		ApiImpl.INSTANCE.registerVolumeType(InfusionVolumeType.INSTANCE);
		ApiImpl.INSTANCE.registerVolumeType(PigmentVolumeType.INSTANCE);
		ApiImpl.INSTANCE.registerVolumeType(SlurryVolumeType.INSTANCE);

		PacketHandler.registerPackets();
	}

	@SubscribeEvent
	public void onModConfig(ModConfigEvent event) {
		switch(event.getConfig().getType()) {
		case SERVER -> PackagedMekemicalsConfig.reloadServerConfig();
		default -> {}
		}
	}
}
