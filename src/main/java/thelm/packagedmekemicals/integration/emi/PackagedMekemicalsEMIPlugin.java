package thelm.packagedmekemicals.integration.emi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.jemi.JemiUtil;
import net.minecraftforge.fml.ModList;

@EmiEntrypoint
public class PackagedMekemicalsEMIPlugin implements EmiPlugin {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void register(EmiRegistry registry) {
		if(ModList.get().isLoaded("jei")) {
			registry.addGenericStackProvider(new ChemicalVolumeStackProvider());
		}
	}

	public static EmiStack getJemiStack(Object ingredient) {
		try {
			return JemiUtil.getStack(ingredient);
		}
		catch(Throwable e) {
			LOGGER.error("Unable to create JEMI stack.", e);
		}
		return EmiStack.EMPTY;
	}
}
