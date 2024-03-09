package thelm.packagedmekemicals.event;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import thelm.packagedauto.block.entity.BaseBlockEntity;
import thelm.packagedauto.item.PackageItem;
import thelm.packagedauto.util.ApiImpl;
import thelm.packagedmekemicals.block.ChemicalPackageFillerBlock;
import thelm.packagedmekemicals.block.entity.ChemicalPackageFillerBlockEntity;
import thelm.packagedmekemicals.config.PackagedMekemicalsConfig;
import thelm.packagedmekemicals.menu.ChemicalPackageFillerMenu;
import thelm.packagedmekemicals.packet.SetChemicalAmountPacket;
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
	}

	@SubscribeEvent
	public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ChemicalPackageFillerBlockEntity.TYPE_INSTANCE, BaseBlockEntity::getItemHandler);

		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ChemicalPackageFillerBlockEntity.TYPE_INSTANCE, BaseBlockEntity::getEnergyStorage);
	}

	@SubscribeEvent
	public void onRegisterPayloadHandler(RegisterPayloadHandlerEvent event) {
		IPayloadRegistrar registrar = event.registrar("packagedmekemicals");
		registrar.play(SetChemicalAmountPacket.ID, SetChemicalAmountPacket::read, builder->builder.client(SetChemicalAmountPacket::handle));
	}

	@SubscribeEvent
	public void onModConfig(ModConfigEvent event) {
		switch(event.getConfig().getType()) {
		case SERVER -> PackagedMekemicalsConfig.reloadServerConfig();
		default -> {}
		}
	}
}
