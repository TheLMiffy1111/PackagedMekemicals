package thelm.packagedmekemicals.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thelm.packagedmekemicals.block.ChemicalPackageFillerBlock;

@JeiPlugin
public class PackagedMekemicalsJEIPlugin implements IModPlugin {

	public static final ResourceLocation UID = new ResourceLocation("packagedmekemicals:jei");
	public static final ResourceLocation BACKGROUND = new ResourceLocation("packagedmekemicals:textures/gui/jei.png");

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
		registration.addRecipeCategories(
				new ChemicalPackageFillingCategory(guiHelper),
				new ChemicalPackageContentsCategory(guiHelper));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(ChemicalPackageFillerBlock.INSTANCE), ChemicalPackageFillingCategory.TYPE);
	}

	@Override
	public void registerAdvanced(IAdvancedRegistration registration) {
		registration.addRecipeManagerPlugin(new ChemicalPackageManagerPlugin());
	}
}
