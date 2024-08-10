package thelm.packagedmekemicals.volume;

import net.minecraft.resources.ResourceLocation;

@Deprecated
public class GasVolumeType extends ChemicalVolumeType {

	public static final GasVolumeType INSTANCE = new GasVolumeType();
	public static final ResourceLocation NAME = ResourceLocation.parse("mekanism:gas");

	@Override
	public ResourceLocation getName() {
		return NAME;
	}

	@Override
	public Class<?> getTypeClass() {
		class DummyStack {}
		return DummyStack.class;
	}
}
