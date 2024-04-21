package thelm.packagedmekemicals.integration.jei;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.slot.FalseCopyVolumeSlot;
import thelm.packagedmekemicals.api.IChemicalStackWrapper;

public class ChemicalVolumeGuiHandler implements IGuiContainerHandler<AbstractContainerScreen<?>> {

	@Override
	public Object getIngredientUnderMouse(AbstractContainerScreen<?> containerScreen, double mouseX, double mouseY) {
		if(containerScreen.getSlotUnderMouse() instanceof FalseCopyVolumeSlot volumeSlot) {
			IVolumeStackWrapper volumeStack = volumeSlot.volumeInventory.getStackInSlot(volumeSlot.slotIndex);
			if(volumeStack instanceof IChemicalStackWrapper chemicalVolumeStack) {
				return chemicalVolumeStack.getChemical();
			}
		}
		return null;
	}
}
