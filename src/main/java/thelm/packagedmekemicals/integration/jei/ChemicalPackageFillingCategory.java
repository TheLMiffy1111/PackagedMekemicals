package thelm.packagedmekemicals.integration.jei;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.client.jei.ChemicalStackRenderer;
import mekanism.client.jei.MekanismJEI;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.util.MiscHelper;
import thelm.packagedmekemicals.api.IChemicalStackWrapper;
import thelm.packagedmekemicals.block.ChemicalPackageFillerBlock;

public class ChemicalPackageFillingCategory implements IRecipeCategory<IChemicalStackWrapper> {

	public static final RecipeType<IChemicalStackWrapper> TYPE = RecipeType.create("packagedmekemicals", "chemical_package_filling", IChemicalStackWrapper.class);
	public static final Component TITLE = new TranslatableComponent("jei.category.packagedmekemicals.chemical_package_filling");

	private final IDrawable background;
	private final IDrawable icon;

	public ChemicalPackageFillingCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(PackagedMekemicalsJEIPlugin.BACKGROUND, 0, 0, 76, 26);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ChemicalPackageFillerBlock.INSTANCE));
	}

	@Override
	public RecipeType<IChemicalStackWrapper> getRecipeType() {
		return TYPE;
	}

	@Override
	public ResourceLocation getUid() {
		return TYPE.getUid();
	}

	@Override
	public Class<? extends IChemicalStackWrapper> getRecipeClass() {
		return TYPE.getRecipeClass();
	}

	@Override
	public Component getTitle() {
		return TITLE;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, IChemicalStackWrapper recipe, IFocusGroup focuses) {
		ChemicalStack<?> chemical = recipe.getChemical();
		IIngredientType type = MekanismJEI.getIngredientType(ChemicalType.getTypeFor(chemical));
		IRecipeSlotBuilder slot;
		slot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 5);
		slot.setCustomRenderer(type, new ChemicalStackRenderer<>(chemical.getAmount(), 16, 16));
		slot.addIngredient(type, chemical);
		slot = builder.addSlot(RecipeIngredientRole.OUTPUT, 55, 5);
		slot.addItemStack(MiscHelper.INSTANCE.makeVolumePackage(recipe));
	}
}
