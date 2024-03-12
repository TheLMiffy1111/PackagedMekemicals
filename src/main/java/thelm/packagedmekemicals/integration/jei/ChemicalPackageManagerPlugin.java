package thelm.packagedmekemicals.integration.jei;

import java.util.List;
import java.util.Optional;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.api.IVolumePackageItem;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.api.IVolumeType;
import thelm.packagedmekemicals.volume.GasVolumeType;
import thelm.packagedmekemicals.volume.InfusionVolumeType;
import thelm.packagedmekemicals.volume.PigmentVolumeType;
import thelm.packagedmekemicals.volume.SlurryVolumeType;

public class ChemicalPackageManagerPlugin implements IRecipeManagerPlugin {

	@Override
	public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
		V ingredient = focus.getTypedValue().getIngredient();
		if(ingredient instanceof ItemStack stack) {
			if(stack.getItem() instanceof IVolumePackageItem vPackage) {
				IVolumeType vType = vPackage.getVolumeType(stack);
				if(vType == GasVolumeType.INSTANCE ||
						vType == InfusionVolumeType.INSTANCE ||
						vType == PigmentVolumeType.INSTANCE ||
						vType == SlurryVolumeType.INSTANCE) {
					switch(focus.getRole()) {
					case INPUT: return List.of(ChemicalPackageContentsCategory.TYPE);
					case OUTPUT: return List.of(ChemicalPackageFillingCategory.TYPE);
					default: break;
					}
				}
			}
		}
		if(ingredient instanceof ChemicalStack<?> stack) {
			switch(focus.getRole()) {
			case INPUT: return List.of(ChemicalPackageFillingCategory.TYPE);
			case OUTPUT: return List.of(ChemicalPackageContentsCategory.TYPE);
			default: break;
			}
		}
		return List.of();
	}

	@Override
	public <V> List<ResourceLocation> getRecipeCategoryUids(IFocus<V> focus) {
		return getRecipeTypes(focus).stream().map(RecipeType::getUid).toList();
	}

	@Override
	public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
		RecipeType<T> type = recipeCategory.getRecipeType();
		V ingredient = focus.getTypedValue().getIngredient();
		if(ingredient instanceof ItemStack stack) {
			if(stack.getItem() instanceof IVolumePackageItem vPackage) {
				IVolumeType vType = vPackage.getVolumeType(stack);
				if(vType == GasVolumeType.INSTANCE ||
						vType == InfusionVolumeType.INSTANCE ||
						vType == PigmentVolumeType.INSTANCE ||
						vType == SlurryVolumeType.INSTANCE) {
					if(ChemicalPackageContentsCategory.TYPE.equals(type) || ChemicalPackageFillingCategory.TYPE.equals(type)) {
						return (List<T>)List.of(vPackage.getVolumeStack(stack));
					}
				}
			}
		}
		if(ingredient instanceof ChemicalStack<?> stack) {
			if(ChemicalPackageContentsCategory.TYPE.equals(type) || ChemicalPackageFillingCategory.TYPE.equals(type)) {
				IVolumeType vType = switch(ChemicalType.getTypeFor(stack)) {
				case GAS -> GasVolumeType.INSTANCE;
				case INFUSION -> InfusionVolumeType.INSTANCE;
				case PIGMENT -> PigmentVolumeType.INSTANCE;
				case SLURRY -> SlurryVolumeType.INSTANCE;
				};
				Optional<IVolumeStackWrapper> vStack = vType.wrapStack(stack);
				if(vStack.isPresent()) {
					return (List<T>)List.of(vStack.get());
				}
			}
		}
		return List.of();
	}

	@Override
	public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
		return List.of();
	}
}
