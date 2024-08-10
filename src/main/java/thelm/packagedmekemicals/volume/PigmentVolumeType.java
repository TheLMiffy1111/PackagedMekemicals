package thelm.packagedmekemicals.volume;

import net.minecraft.resources.ResourceLocation;

@Deprecated
public class PigmentVolumeType extends ChemicalVolumeType {

	public static final PigmentVolumeType INSTANCE = new PigmentVolumeType();
	public static final ResourceLocation NAME = ResourceLocation.parse("mekanism:pigment");

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
