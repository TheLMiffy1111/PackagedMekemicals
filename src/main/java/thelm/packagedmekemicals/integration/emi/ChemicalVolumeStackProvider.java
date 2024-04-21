package thelm.packagedmekemicals.integration.emi;

import dev.emi.emi.api.EmiStackProvider;
import dev.emi.emi.api.stack.EmiStackInteraction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import thelm.packagedauto.api.IVolumeStackWrapper;
import thelm.packagedauto.slot.FalseCopyVolumeSlot;
import thelm.packagedmekemicals.api.IChemicalStackWrapper;

public class ChemicalVolumeStackProvider implements EmiStackProvider<Screen> {

	@Override
	public EmiStackInteraction getStackAt(Screen screen, int x, int y) {
		if(screen instanceof AbstractContainerScreen<?> containerScreen &&
				containerScreen.getSlotUnderMouse() instanceof FalseCopyVolumeSlot volumeSlot) {
			IVolumeStackWrapper volumeStack = volumeSlot.volumeInventory.getStackInSlot(volumeSlot.slotIndex);
			if(volumeStack instanceof IChemicalStackWrapper chemicalVolumeStack) {
				return new EmiStackInteraction(PackagedMekemicalsEMIPlugin.getJemiStack(chemicalVolumeStack.getChemical()), null, false);
			}
		}
		return EmiStackInteraction.EMPTY;
	}
}
