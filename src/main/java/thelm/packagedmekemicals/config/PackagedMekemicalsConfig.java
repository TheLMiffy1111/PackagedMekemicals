package thelm.packagedmekemicals.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class PackagedMekemicalsConfig {

	private PackagedMekemicalsConfig() {};

	private static ForgeConfigSpec serverSpec;

	public static void registerConfig() {
		buildConfig();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
	}

	private static void buildConfig() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		serverSpec = builder.build();
	}

	public static void reloadServerConfig() {

	}
}
