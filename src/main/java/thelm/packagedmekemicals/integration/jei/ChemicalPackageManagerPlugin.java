package thelm.packagedmekemicals.integration.jei;

import java.util.List;
import java.util.Optional;

import mekanism.api.chemical.ChemicalStack;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.api.IVolumeType;
import thelm.packagedauto.component.PackagedAutoDataComponents;
import thelm.packagedmekemicals.volume.ChemicalVolumeType;

public class ChemicalPackageManagerPlugin implements IRecipeManagerPlugin {

	@Override
	public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
		V ingredient = focus.getTypedValue().getIngredient();
		if(ingredient instanceof ItemStack stack) {
			if(stack.has(PackagedAutoDataComponents.VOLUME_PACKAGE_STACK)) {
				IVolumeStackWrapper vStack = stack.get(PackagedAutoDataComponents.VOLUME_PACKAGE_STACK);
				IVolumeType vType = vStack.getVolumeType();
				if(vType == ChemicalVolumeType.INSTANCE) {
					switch(focus.getRole()) {
					case INPUT: return List.of(ChemicalPackageContentsCategory.TYPE);
					case OUTPUT: return List.of(ChemicalPackageFillingCategory.TYPE);
					default: break;
					}
				}
			}
		}
		if(ingredient instanceof ChemicalStack) {
			switch(focus.getRole()) {
			case INPUT: return List.of(ChemicalPackageFillingCategory.TYPE);
			case OUTPUT: return List.of(ChemicalPackageContentsCategory.TYPE);
			default: break;
			}
		}
		return List.of();
	}

	@Override
	public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
		RecipeType<T> type = recipeCategory.getRecipeType();
		V ingredient = focus.getTypedValue().getIngredient();
		if(ingredient instanceof ItemStack stack) {
			if(stack.has(PackagedAutoDataComponents.VOLUME_PACKAGE_STACK)) {
				IVolumeStackWrapper vStack = stack.get(PackagedAutoDataComponents.VOLUME_PACKAGE_STACK);
				if(vStack.getVolumeType() == ChemicalVolumeType.INSTANCE) {
					if(ChemicalPackageContentsCategory.TYPE.equals(type) || ChemicalPackageFillingCategory.TYPE.equals(type)) {
						return (List<T>)List.of(vStack);
					}
				}
			}
		}
		if(ingredient instanceof ChemicalStack stack) {
			if(ChemicalPackageContentsCategory.TYPE.equals(type) || ChemicalPackageFillingCategory.TYPE.equals(type)) {
				Optional<IVolumeStackWrapper> vStack = ChemicalVolumeType.INSTANCE.wrapStack(stack);
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
