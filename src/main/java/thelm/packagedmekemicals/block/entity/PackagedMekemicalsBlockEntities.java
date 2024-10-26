package thelm.packagedmekemicals.block.entity;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thelm.packagedmekemicals.block.PackagedMekemicalsBlocks;

public class PackagedMekemicalsBlockEntities {

	private PackagedMekemicalsBlockEntities() {}

	public static <T extends BlockEntity> Supplier<BlockEntityType<T>> of(BlockEntityType.BlockEntitySupplier<? extends T> factory, Supplier<Block>... validBlocks) {
		return ()->new BlockEntityType<>(factory, Arrays.stream(validBlocks).map(Supplier::get).filter(Objects::nonNull).collect(Collectors.toSet()), null);
	}

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "packagedmekemicals");

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChemicalPackageFillerBlockEntity>> CHEMICAL_PACKAGE_FILLER = BLOCK_ENTITIES.register(
			"chemical_package_filler", of(ChemicalPackageFillerBlockEntity::new, PackagedMekemicalsBlocks.CHEMICAL_PACKAGE_FILLER));
}
