package thelm.packagedmekemicals.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import thelm.packagedmekemicals.block.entity.ChemicalPackageFillerBlockEntity;

public class PackagedMekemicalsConfig {

	private PackagedMekemicalsConfig() {};

	private static ForgeConfigSpec serverSpec;

	public static ForgeConfigSpec.IntValue chemicalPackageFillerEnergyCapacity;
	public static ForgeConfigSpec.IntValue chemicalPackageFillerEnergyReq;
	public static ForgeConfigSpec.IntValue chemicalPackageFillerEnergyUsage;

	public static void registerConfig() {
		buildConfig();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
	}

	private static void buildConfig() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

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
