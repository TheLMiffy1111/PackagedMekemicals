package thelm.packagedmekemicals.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.items.SlotItemHandler;
import thelm.packagedauto.menu.BaseMenu;
import thelm.packagedauto.menu.factory.PositionalBlockEntityMenuFactory;
import thelm.packagedauto.slot.RemoveOnlySlot;
import thelm.packagedmekemicals.block.entity.ChemicalPackageFillerBlockEntity;

public class ChemicalPackageFillerMenu extends BaseMenu<ChemicalPackageFillerBlockEntity> {

	public static final MenuType<ChemicalPackageFillerMenu> TYPE_INSTANCE = (MenuType<ChemicalPackageFillerMenu>)IForgeMenuType.
			create(new PositionalBlockEntityMenuFactory<>(ChemicalPackageFillerMenu::new));

	public ChemicalPackageFillerMenu(int windowId, Inventory inventory, ChemicalPackageFillerBlockEntity blockEntity) {
		super(TYPE_INSTANCE, windowId, inventory, blockEntity);
		addSlot(new SlotItemHandler(itemHandler, 2, 8, 53));
		addSlot(new SlotItemHandler(itemHandler, 0, 44, 35));
		addSlot(new RemoveOnlySlot(itemHandler, 1, 134, 35));
		setupPlayerInventory();
	}
}
