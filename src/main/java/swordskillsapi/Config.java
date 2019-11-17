package swordskillsapi;

import java.io.File;
import java.util.Arrays;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import swordskillsapi.api.item.WeaponRegistry;

public class Config
{
	public static Configuration config;

	public static void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(new File(event.getModConfigurationDirectory().getAbsolutePath() + "/" + SwordSkillsApi.ID + ".cfg"));
		config.load();
		boolean enableWeaponLog = config.get("Weapon Registry", "Log changes to the WeaponRegistry - recommended to leave enabled until satisfied with the state of the WeaponRegistry", true).getBoolean(true);
		if (!enableWeaponLog) {
			SwordSkillsApi.LOGGER.info("Weapon Registry logging is now disabled");
			SwordSkillsApi.LOGGER.disable();
		}
		config.save();
	}

	public static void postInit() {
		final String origin = "Config:" + SwordSkillsApi.ID;
		Arrays.stream(config.get("Weapon Registry", "[Allowed Swords] Enter items as modid:registered_item_name, each on a separate line between the '<' and '>'", new String[0], "Allow the following items to activate skills requiring a sword").getStringList())
		.forEach(s -> {
			Item item = Config.getItemFromString(s);
			if (item != null) {
				WeaponRegistry.INSTANCE.registerSword(origin, item, true);
			}
		});
		Arrays.stream(config.get("Weapon Registry", "[Allowed Weapons] Enter items as modid:registered_item_name, each on a separate line between the '<' and '>'", new String[0], "Allow the following items to activate weapon skills that do not specifically require a sword").getStringList())
		.forEach(s -> {
			Item item = Config.getItemFromString(s);
			if (item != null) {
				WeaponRegistry.INSTANCE.registerWeapon(origin, item, true);
			}
		});
		Arrays.stream(config.get("Weapon Registry", "[Forbidden Swords] Enter items as modid:registered_item_name, each on a separate line between the '<' and '>'", new String[0], "Forbid the following items from activating skills requiring a sword").getStringList())
		.forEach(s -> {
			Item item = Config.getItemFromString(s);
			if (item != null) {
				WeaponRegistry.INSTANCE.removeSword(origin, item, true);
			}
		});
		Arrays.stream(config.get("Weapon Registry", "[Forbidden Weapons] Enter items as modid:registered_item_name, each on a separate line between the '<' and '>'", new String[0], "Forbid the following items from activating all weapon-based skills").getStringList())
		.forEach(s -> {
			Item item = Config.getItemFromString(s);
			if (item != null) {
				WeaponRegistry.INSTANCE.removeWeapon(origin, item, true);
			}
		});
		config.save();
	}

	private static Item getItemFromString(String s) {
		ResourceLocation location = WeaponRegistry.getResourceLocation(s);
		if (location == null) {
			SwordSkillsApi.LOGGER.warn(String.format("[WeaponRegistry] [Config] Invalid ResourceLocation string %s", s));
			return null;
		}
		Item item = Item.REGISTRY.getObject(location);
		if (item == null) {
			SwordSkillsApi.LOGGER.warn(String.format("[WeaponRegistry] [Config] %s could not be found - the mod may not be installed or it may have been typed incorrectly", s));
		}
		return item;
	}
}
