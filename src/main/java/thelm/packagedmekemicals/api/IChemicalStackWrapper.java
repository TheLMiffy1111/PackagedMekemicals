package thelm.packagedmekemicals.api;

import mekanism.api.chemical.ChemicalStack;
import thelm.packagedauto.api.IVolumeStackWrapper;

public interface IChemicalStackWrapper extends IVolumeStackWrapper {

	ChemicalStack<?> getChemical();
}
