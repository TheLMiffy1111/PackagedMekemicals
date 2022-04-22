package thelm.packagedmekemicals.util;

import java.util.Optional;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;

public class ChemicalHelper {

	public static final ChemicalHelper INSTANCE = new ChemicalHelper();

	private ChemicalHelper() {}

	public LazyOptional<IGasHandler> getGasHandler(ItemStack itemStack) {
		return itemStack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY);
	}

	public Optional<GasStack> getGasContained(ItemStack container) {
		if(!container.isEmpty()) {
			container = ItemHandlerHelper.copyStackWithSize(container, 1);
			return getGasHandler(container).
					map(handler->handler.extractChemical(Long.MAX_VALUE, Action.SIMULATE)).
					filter(stack->!stack.isEmpty());
		}
		return Optional.empty();
	}

	public LazyOptional<IInfusionHandler> getInfusionHandler(ItemStack itemStack) {
		return itemStack.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY);
	}

	public Optional<InfusionStack> getInfusionContained(ItemStack container) {
		if(!container.isEmpty()) {
			container = ItemHandlerHelper.copyStackWithSize(container, 1);
			return getInfusionHandler(container).
					map(handler->handler.extractChemical(Long.MAX_VALUE, Action.SIMULATE)).
					filter(stack->!stack.isEmpty());
		}
		return Optional.empty();
	}

	public LazyOptional<IPigmentHandler> getPigmentHandler(ItemStack itemStack) {
		return itemStack.getCapability(Capabilities.PIGMENT_HANDLER_CAPABILITY);
	}

	public Optional<PigmentStack> getPigmentContained(ItemStack container) {
		if(!container.isEmpty()) {
			container = ItemHandlerHelper.copyStackWithSize(container, 1);
			return getPigmentHandler(container).
					map(handler->handler.extractChemical(Long.MAX_VALUE, Action.SIMULATE)).
					filter(stack->!stack.isEmpty());
		}
		return Optional.empty();
	}

	public LazyOptional<ISlurryHandler> getSlurryHandler(ItemStack itemStack) {
		return itemStack.getCapability(Capabilities.SLURRY_HANDLER_CAPABILITY);
	}

	public Optional<SlurryStack> getSlurryContained(ItemStack container) {
		if(!container.isEmpty()) {
			container = ItemHandlerHelper.copyStackWithSize(container, 1);
			return getSlurryHandler(container).
					map(handler->handler.extractChemical(Long.MAX_VALUE, Action.SIMULATE)).
					filter(stack->!stack.isEmpty());
		}
		return Optional.empty();
	}

	public boolean hasChemicalHandler(ItemStack itemStack) {
		return getGasHandler(itemStack).isPresent() ||
				getInfusionHandler(itemStack).isPresent() ||
				getPigmentHandler(itemStack).isPresent() ||
				getSlurryHandler(itemStack).isPresent();
	}

	public Optional<? extends ChemicalStack<?>> getChemicalContained(ItemStack container) {
		Optional<? extends ChemicalStack<?>> optional;
		optional = getGasContained(container);
		if(optional.isPresent()) {
			return optional;
		}
		optional = getInfusionContained(container);
		if(optional.isPresent()) {
			return optional;
		}
		optional = getPigmentContained(container);
		if(optional.isPresent()) {
			return optional;
		}
		optional = getSlurryContained(container);
		if(optional.isPresent()) {
			return optional;
		}
		return Optional.empty();
	}
}
