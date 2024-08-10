package thelm.packagedmekemicals.volume;

import net.minecraft.resources.ResourceLocation;

@Deprecated
public class SlurryVolumeType extends ChemicalVolumeType {

	public static final SlurryVolumeType INSTANCE = new SlurryVolumeType();
	public static final ResourceLocation NAME = ResourceLocation.parse("mekanism:slurry");

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
