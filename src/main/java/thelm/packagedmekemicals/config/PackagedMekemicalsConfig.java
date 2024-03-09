package thelm.packagedmekemicals.config;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import thelm.packagedmekemicals.block.entity.ChemicalPackageFillerBlockEntity;

public class PackagedMekemicalsConfig {

	private PackagedMekemicalsConfig() {}

	private static ModConfigSpec serverSpec;

	public static ModConfigSpec.IntValue chemicalPackageFillerEnergyCapacity;
	public static ModConfigSpec.IntValue chemicalPackageFillerEnergyReq;
	public static ModConfigSpec.IntValue chemicalPackageFillerEnergyUsage;

	public static void registerConfig() {
		buildConfig();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
	}

	private static void buildConfig() {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

		builder.push("chemical_package_filler");
		builder.comment("How much FE the Chemical Package Filler should hold.");
		chemicalPackageFillerEnergyCapacity = builder.defineInRange("energy_capacity", 5000, 0, Integer.MAX_VALUE);
		builder.comment("How much total FE the Chemical Package Filler should use per operation.");
		chemicalPackageFillerEnergyReq = builder.defineInRange("energy_req", 500, 0, Integer.MAX_VALUE);
		builder.comment("How much FE/t maximum the Chemical Package Filler can use.");
		chemicalPackageFillerEnergyUsage = builder.defineInRange("energy_usage", 100, 0, Integer.MAX_VALUE);
		builder.pop();

		serverSpec = builder.build();
	}

	public static void reloadServerConfig() {
		ChemicalPackageFillerBlockEntity.energyCapacity = chemicalPackageFillerEnergyCapacity.get();
		ChemicalPackageFillerBlockEntity.energyReq = chemicalPackageFillerEnergyReq.get();
		ChemicalPackageFillerBlockEntity.energyUsage = chemicalPackageFillerEnergyUsage.get();
	}
}
