package thelm.packagedmekemicals.block;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PackagedMekemicalsBlocks {

	private PackagedMekemicalsBlocks() {}

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("packagedmekemicals");

	public static final DeferredBlock<Block> CHEMICAL_PACKAGE_FILLER = BLOCKS.register("chemical_package_filler", ChemicalPackageFillerBlock::new);
}
