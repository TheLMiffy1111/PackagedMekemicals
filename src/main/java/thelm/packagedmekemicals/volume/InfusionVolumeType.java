package thelm.packagedmekemicals.volume;

import net.minecraft.resources.ResourceLocation;

@Deprecated
public class InfusionVolumeType extends ChemicalVolumeType {

	public static final InfusionVolumeType INSTANCE = new InfusionVolumeType();
	public static final ResourceLocation NAME = ResourceLocation.parse("mekanism:infuse_type");

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
