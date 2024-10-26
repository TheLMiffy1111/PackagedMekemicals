package thelm.packagedmekemicals.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import thelm.packagedmekemicals.block.PackagedMekemicalsBlocks;

public class PackagedMekemicalsItems {

	private PackagedMekemicalsItems() {}

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("packagedmekemicals");

	public static final DeferredItem<?> CHEMICAL_PACKAGE_FILLER = ITEMS.registerSimpleBlockItem(PackagedMekemicalsBlocks.CHEMICAL_PACKAGE_FILLER);
}
