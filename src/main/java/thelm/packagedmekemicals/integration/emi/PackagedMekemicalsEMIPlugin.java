package thelm.packagedmekemicals.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class PackagedMekemicalsEMIPlugin implements EmiPlugin {

	@Override
	public void register(EmiRegistry registry) {
		registry.addGenericStackProvider(new ChemicalVolumeStackProvider());
	}
}
