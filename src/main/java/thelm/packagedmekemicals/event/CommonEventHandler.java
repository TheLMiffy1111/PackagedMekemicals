package thelm.packagedmekemicals.event;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import thelm.packagedauto.item.PackageItem;
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
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.register(this);
		PackagedMekemicalsConfig.registerConfig();

		DeferredRegister<Block> blockRegister = DeferredRegister.create(Registries.BLOCK, "packagedmekemicals");
		blockRegister.register(modEventBus);
		blockRegister.register("chemical_package_filler", ()->ChemicalPackageFillerBlock.INSTANCE);

		DeferredRegister<Item> itemRegister = DeferredRegister.create(Registries.ITEM, "packagedmekemicals");
		itemRegister.register(modEventBus);
		itemRegister.register("chemical_package_filler", ()->ChemicalPackageFillerBlock.ITEM_INSTANCE);

		DeferredRegister<BlockEntityType<?>> blockEntityRegister = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "packagedmekemicals");
		blockEntityRegister.register(modEventBus);
		blockEntityRegister.register("chemical_package_filler", ()->ChemicalPackageFillerBlockEntity.TYPE_INSTANCE);

		DeferredRegister<MenuType<?>> menuRegister = DeferredRegister.create(Registries.MENU, "packagedmekemicals");
		menuRegister.register(modEventBus);
		menuRegister.register("chemical_package_filler", ()->ChemicalPackageFillerMenu.TYPE_INSTANCE);

		DeferredRegister<CreativeModeTab> creativeTabRegister = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "packagedmekemicals");
		creativeTabRegister.register(modEventBus);
		creativeTabRegister.register("tab", ()->CreativeModeTab.builder().
				title(Component.translatable("itemGroup.packagedmekemicals")).
				icon(()->new ItemStack(PackageItem.INSTANCE)).
				displayItems((parameters, output)->{
					output.accept(ChemicalPackageFillerBlock.ITEM_INSTANCE);
				}).
				build());
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
