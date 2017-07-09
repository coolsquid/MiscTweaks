package coolsquid.misctweaks.config;

import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.google.common.collect.ImmutableSet;

public class ConfigManager {

	public static final Configuration CONFIG = new Configuration(new File("./config/MiscTweaks.cfg"));

	public static String forcedDifficulty = "";
	public static String forcedGamemode = "";
	public static int forcedWorldType = -1;
	public static boolean disableCheats = false;
	public static boolean disableBonusChest = false;
	public static float maxGamma = 1;
	public static int maxRenderDistance = 32;

	public static boolean netherLavaPockets = true;

	public static float hungerHealthRegen = 1.0F;
	public static float hungerExhaustionRegen = 3.0F;
	public static float hungerStarveDamage = 1.0F;

	public static boolean retainOldBranding = true;
	public static String[] branding = {};

	public static boolean removeRealmsButton = false;
	public static boolean removeCopyrightText = false;

	public static Set<ElementType> disabledOverlays;

	public static int fireTickRate;
	public static int tntFuseTime;
	public static int creeperFuseTime;
	public static int creeperExplosionRadius;

	public static float drowningDamage = 1.0F;

	public static boolean disableSleep = false;

	public static boolean enableConfigGui;

	public static void loadConfig() {
		CONFIG.load();

		forcedDifficulty = CONFIG.getString("forcedDifficulty", "game_options", forcedDifficulty,
				"Forces the specified difficulty. Allows for hard, normal, easy or peaceful. Leave empty to disable.",
				Pattern.compile("(?i)(|peaceful|easy|normal|hard)"));
		forcedGamemode = CONFIG.getString("forcedGamemode", "game_options", forcedGamemode,
				"Forces the specified gamemode. Allows for survival, creative, adventure, spectator, and hardcore. Leave empty to disable.",
				Pattern.compile("(?i)(|survival|creative|adventure|spectator|hardcore)"));
		forcedWorldType = CONFIG.getInt("forcedWorldType", "game_options", -1, -1, WorldType.WORLD_TYPES.length - 1,
				"Forces a certain world type. 0 is default, 1 is superflat, 2 is large biomes, etc. Set to -1 to disable.");
		disableCheats = CONFIG.getBoolean("disableCheats", "game_options", disableCheats,
				"Forces cheats to be disabled.");
		disableBonusChest = CONFIG.getBoolean("disableBonusChest", "game_options", disableBonusChest,
				"Forces the bonus chest to be disabled.");
		maxGamma = (float) CONFIG.getInt("maxGamma", "game_options", 100, 0, 100, "Sets a maximum brightness level.")
				/ 100;
		maxRenderDistance = CONFIG.getInt("maxRenderDistance", "game_options", 32, 2, 32,
				"Sets a maximum render distance.");

		netherLavaPockets = CONFIG.getBoolean("netherLavaPockets", "world", netherLavaPockets,
				"Set to false to disable the random lava pockets in the Nether.");
		fireTickRate = CONFIG.getInt("fireTickRate", "world", 30, 0, Integer.MAX_VALUE,
				"The number of world ticks for each fire tick. Decrease for fire to spread and burn faster.");
		tntFuseTime = CONFIG.getInt("tntFuseTime", "world", 80, 0, Integer.MAX_VALUE,
				"The fuse time of TNT, in ticks.");
		creeperFuseTime = CONFIG.getInt("creeperFuseTime", "world", 30, 1, Integer.MAX_VALUE,
				"The fuse time of creepers, in ticks. Has to be at least 1, as otherwise the creepers would explode immediately after spawning.");
		creeperExplosionRadius = CONFIG.getInt("creeperExplosionRadius", "world", 3, 0, 64,
				"The approximate radius of creeper explosions.");

		hungerHealthRegen = CONFIG.getFloat("healthRegen", "hunger", hungerHealthRegen, Float.MIN_VALUE,
				Float.MAX_VALUE, "The amount of health regen from having a full hunger bar.");
		hungerExhaustionRegen = CONFIG.getFloat("exhaustionRegen", "hunger", hungerExhaustionRegen, Float.MIN_VALUE,
				Float.MAX_VALUE, "The amount of exhaustion regen from having a full hunger bar.");
		hungerStarveDamage = CONFIG.getFloat("starveDamage", "hunger", hungerStarveDamage, Float.MIN_VALUE,
				Float.MAX_VALUE, "The amount of damage dealt by starvation.");

		branding = CONFIG.getStringList("branding", "client", branding, "Changes the text in the lower left corner.");
		retainOldBranding = CONFIG.getBoolean("brandingRetainOld", "client", retainOldBranding,
				"Whether to retain the old branding and append the new one, or to replace the old one completely.");
		removeRealmsButton = CONFIG.getBoolean("removeRealmsButton", "client", removeRealmsButton,
				"Removes the realms button from the main menu.");
		removeCopyrightText = CONFIG.getBoolean("removeCopyrightText", "client", removeCopyrightText,
				"Removes the copyright information from the main menu.");

		ImmutableSet.Builder<ElementType> overlays = ImmutableSet.builder();
		for (String overlay : CONFIG.getStringList("disabledOverlays", "client", new String[0],
				"Disables the listed overlays.")) {
			overlays.add(ElementType.valueOf(overlay.toUpperCase()));
		}
		disabledOverlays = overlays.build();

		drowningDamage = CONFIG.getFloat("drowningDamage", "miscellaneous", drowningDamage, Float.MIN_VALUE,
				Float.MAX_VALUE, "The amount of damage dealt by drowning.");

		disableSleep = CONFIG.getBoolean("disableSleep", "miscellaneous", disableSleep,
				"Disables all forms of beds and sleeping bags.");

		Property enableConfigGui = CONFIG.get("general", "enableConfigGui", true);
		enableConfigGui.setComment("Whether to enable the ingame config GUI.");
		enableConfigGui.setShowInGui(false);
		ConfigManager.enableConfigGui = enableConfigGui.getBoolean();

		if (CONFIG.hasChanged()) {
			CONFIG.save();
		}
	}
}