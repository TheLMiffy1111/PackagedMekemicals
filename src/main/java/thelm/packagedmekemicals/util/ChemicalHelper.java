package thelm.packagedmekemicals.util;

import java.util.Optional;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.world.item.ItemStack;

public class ChemicalHelper {

	public static final ChemicalHelper INSTANCE = new ChemicalHelper();

	private ChemicalHelper() {}

	public Optional<IChemicalHandler> getChemicalHandler(ItemStack itemStack) {
		return Optional.ofNullable(itemStack.getCapability(Capabilities.CHEMICAL.item()));
	}

	public Optional<ChemicalStack> getChemicalContained(ItemStack container) {
		if(!container.isEmpty()) {
			container = container.copyWithCount(1);
			return getChemicalHandler(container).
					map(handler->handler.extractChemical(Long.MAX_VALUE, Action.SIMULATE)).
					filter(stack->!stack.isEmpty());
		}
		return Optional.empty();
	}

	public boolean hasChemicalHandler(ItemStack itemStack) {
		return getChemicalHandler(itemStack).isPresent();
	}
}
