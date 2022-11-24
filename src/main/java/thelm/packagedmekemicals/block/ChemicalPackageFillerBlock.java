package thelm.packagedmekemicals.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import thelm.packagedauto.block.BaseBlock;
import thelm.packagedauto.block.entity.BaseBlockEntity;
import thelm.packagedmekemicals.PackagedMekemicals;
import thelm.packagedmekemicals.block.entity.ChemicalPackageFillerBlockEntity;

public class ChemicalPackageFillerBlock extends BaseBlock {

	public static final ChemicalPackageFillerBlock INSTANCE = new ChemicalPackageFillerBlock();
	public static final Item ITEM_INSTANCE = new BlockItem(INSTANCE, new Item.Properties().tab(PackagedMekemicals.CREATIVE_TAB));

	protected ChemicalPackageFillerBlock() {
		super(BlockBehaviour.Properties.of(Material.METAL).strength(15F, 25F).sound(SoundType.METAL));
	}

	@Override
	public ChemicalPackageFillerBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ChemicalPackageFillerBlockEntity.TYPE_INSTANCE.create(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return BaseBlockEntity::tick;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		level.getBlockEntity(pos, ChemicalPackageFillerBlockEntity.TYPE_INSTANCE).ifPresent(ChemicalPackageFillerBlockEntity::updatePowered);
	}
}
