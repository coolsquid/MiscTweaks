package coolsquid.misctweaks.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import com.google.common.collect.Sets;

import coolsquid.misctweaks.MiscTweaks;
import coolsquid.misctweaks.util.Expression;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigManager {

	public static final Configuration CONFIG = new Configuration(new File("config/MiscTweaks.cfg"), MiscTweaks.VERSION);

	public static final String CATEGORY_GAME_OPTIONS = "game_options";
	public static final String CATEGORY_WORLD = "world";
	public static final String CATEGORY_HUNGER = "hunger";
	public static final String CATEGORY_CLIENT = "client";
	public static final String CATEGORY_MISCELLANEOUS = "miscellaneous";

	public static String defaultDifficulty;
	public static Set<String> allowedDifficulties;
	public static String defaultGamemode;
	public static Set<String> allowedGamemodes;
	public static String defaultWorldType;
	public static Set<String> allowedWorldTypes;
	public static String defaultChunkProviderSettings;
	public static boolean forceChunkProviderSettings;

	public static int generateStructures;
	public static boolean forceDefaultGenerateStructuresOption;
	public static int cheats;
	public static boolean forceDefaultCheatsOption;
	public static int bonusChest;
	public static boolean forceDefaultBonusChestOption;
	
	public static String defaultSeed;
	public static boolean forceSeed;
	
	public static List<Pair<String, String>> defaultServerProperties;

	public static long newWorldTime;
	public static String newWorldWeather;
	public static long respawnTime;

	public static float maxGamma;
	public static int maxRenderDistance;

	public static Map<String, String> gameRules;
	public static Set<String> forcedGameRules;

	public static boolean netherLavaPockets;

	public static float hungerHealthRegen;
	public static float hungerExhaustionRegen;
	public static float hungerStarveDamage;

	public static boolean retainOldBranding;
	public static String[] branding;

	public static boolean removeRealmsButton;
	public static boolean removeCopyrightText;

	public static Set<ElementType> disabledOverlays;

	public static int fireTickRate;
	public static int tntFuseTime;
	public static int creeperFuseTime;
	public static int creeperExplosionRadius;

	public static Map<String, Expression> damageModifiers;

	public static Set<ResourceLocation> infiniteLiquids;
	public static Set<ResourceLocation> finiteLiquids;

	public static boolean disableSleep;
	public static boolean preventPlayerSpawnChange;
	public static boolean preventPlayerBedSpawnChange;
	
	public static HashSet<String> disabledFireSources;
	public static HashSet<String> newFireSources;
	
	public static int chestSize;
	public static int enderChestSize;
	public static int minecartChestSize;

	public static boolean enableConfigGui;

	public static void loadConfig() {
		CONFIG.load();
		
		//moveCategory("game_options", CATEGORY_GAME_OPTIONS);

		migrateOldCrap1();

		defaultDifficulty = CONFIG.getString("defaultDifficulty", CATEGORY_GAME_OPTIONS, "",
				"Sets a default difficulty for new worlds. Allows for hard, normal, easy or peaceful. Leave empty to disable.",
				new String[] { "peaceful", "easy", "normal", "hard" });
		allowedDifficulties = new HashSet<>();
		for (String s : CONFIG.getStringList("allowedDifficulties", CATEGORY_GAME_OPTIONS, new String[0], "Disables all difficulties except those listed. Leave empty to disable.")) {
			if (defaultDifficulty.isEmpty()) {
				defaultDifficulty = s;
			}
			allowedDifficulties.add(s.toUpperCase());
		}
		defaultDifficulty = defaultDifficulty.toUpperCase();
		defaultGamemode = CONFIG.getString("defaultGamemode", CATEGORY_GAME_OPTIONS, "",
				"Forces the specified gamemode. Allows for survival, creative, adventure, spectator, and hardcore. Leave empty to disable.",
				new String[] { "survival", "creative", "adventure", "spectator", "hardcore" });
		allowedGamemodes = new HashSet<>();
		for (String s : CONFIG.getStringList("allowedGamemodes", CATEGORY_GAME_OPTIONS, new String[0], "Disables all game modes except those listed. Leave empty to disable.")) {
			if (defaultGamemode.isEmpty()) {
				defaultGamemode = s;
			}
			allowedGamemodes.add(s.toUpperCase());
		}
		defaultGamemode = defaultGamemode.toUpperCase();
		defaultWorldType = CONFIG.getString("defaultWorldType", CATEGORY_GAME_OPTIONS, "",
				"Sets a default (initially selected) world type. Leave empty to disable.");
		allowedWorldTypes = new HashSet<>();
		for (String s : CONFIG.getStringList("allowedWorldTypes", CATEGORY_GAME_OPTIONS, new String[0], "Disables all world types except those listed. Leave empty to disable.")) {
			if (defaultWorldType.isEmpty()) {
				defaultWorldType = s.toUpperCase();
			}
			allowedWorldTypes.add(s.toUpperCase());
		}
		defaultWorldType = defaultWorldType.toUpperCase();
		defaultChunkProviderSettings = CONFIG.getString("defaultChunkProviderSettings", CATEGORY_GAME_OPTIONS, "",
				"Sets a default chunk provider settings JSON.");
		forceChunkProviderSettings = CONFIG.getBoolean("forceChunkProviderSettings", CATEGORY_GAME_OPTIONS, false, "Prevents the player from changing from the default chunk provider settings.");

		generateStructures = CONFIG.getInt("generateStructures", CATEGORY_GAME_OPTIONS, -1, -1, 1, "Whether structures should, by default, be generated in new worlds. 1 = generate structures, 0 = don't generate structures, -1 = Vanilla behavior.");
		forceDefaultGenerateStructuresOption = CONFIG.getBoolean("forceDefaultGenerateStructuresOption", CATEGORY_GAME_OPTIONS, false,
				"Prevents the player from enabling or disabling the structure generation option when creating a new world. Must be combined with the \"generateStructures\" option.");

		migrateOldCrap3();
		cheats = CONFIG.getInt("cheats", CATEGORY_GAME_OPTIONS, -1, -1, 1, "Whether cheats should, by default, be enabled in new worlds. 1 = enable cheats, 0 = disable cheats, -1 = Vanilla behavior.");
		forceDefaultCheatsOption = CONFIG.getBoolean("forceDefaultCheatsOption", CATEGORY_GAME_OPTIONS, false,
				"Prevents the player from enabling or disabling cheats when creating a new world. Must be combined with the \"cheats\" option.");
		bonusChest = CONFIG.getInt("bonusChest", CATEGORY_GAME_OPTIONS, -1, -1, 1, "Whether bonus chests should, by default, be generated in new worlds. 1 = enable bonus chests, 0 = disable bonus chests, -1 = Vanilla behavior.");
		forceDefaultBonusChestOption = CONFIG.getBoolean("forceDefaultBonusChestOption", CATEGORY_GAME_OPTIONS, false,
				"Prevents the player from enabling or disabling bonus chests when creating a new world. Must be combined with the \"bonusChest\" option.");
		
		defaultSeed = CONFIG.getString("defaultSeed", CATEGORY_GAME_OPTIONS, "", "The default seed of new worlds.");
		forceSeed = CONFIG.getBoolean("forceSeed", CATEGORY_GAME_OPTIONS, false, "Prevents the player from changing the seed of new worlds. Must be combined with the \"defaultSeed\" option.");

		{
			defaultServerProperties = new ArrayList<>();
			Property prop = CONFIG.get(CATEGORY_GAME_OPTIONS, "defaultServerProperties", new String[0]);
			prop.setValidationPattern(Pattern.compile("(\\w+-)*(\\w+)\\s+\\S+.*"));
			prop.setComment("Sets the default value of \"server.properties\" entries. Will be automatically inserted when the server is run for the first time. Format: \\\"key value\\\". Note that some other options, such as defaultWorldType, also affect the \"server.properties\" file. This option will be prioritized if any conflicts occur.");
			for (String a : prop.getStringList()) {
				String[] b = a.split("\\s+");
				defaultServerProperties.add(Pair.of(b[0], a.substring(b.length)));
			}
		}

		migrateOldCrap5();
		maxGamma = (float) CONFIG.getInt("maxGamma", CATEGORY_GAME_OPTIONS, -1, -1, 100, "Sets a maximum brightness level. -1 does nothing.")
				/ 100;
		if (maxGamma < 0) { maxGamma = -1; } // TODO next major version: make the maxGamma option a float
		maxRenderDistance = CONFIG.getInt("maxRenderDistance", CATEGORY_GAME_OPTIONS, 32, -1, 32,
				"Sets a maximum render distance. -1 does nothing. Should be at least 2!");

		newWorldTime = CONFIG.getInt("newWorldTime", CATEGORY_GAME_OPTIONS, -1, -1, 24000, "The starting time of newly created worlds. Can be combined with \"doDaylightCycle false\" in the \"gameRules\" option to indefinitely stay at the specified time.");
		newWorldWeather = CONFIG.getString("newWorldWeather", CATEGORY_GAME_OPTIONS, "", "The starting weather of newly created worlds. Either \"clear\", \"rain\" or \"thunder\". Can be combined with \"doWeatherCycle false\" in the \"gameRules\" option to indefinitely retain the specified weather.");
		respawnTime = CONFIG.getInt("respawnTime", CATEGORY_GAME_OPTIONS, -1, -1, 24000, "Changes the world time to the given value when the player respawns. Only applies if the player is alone in the world and the gamerule \"doDaylightCycle\" is true.");

		{
			gameRules = new HashMap<>();
			forcedGameRules = new HashSet<>();
			Property prop = CONFIG.get(CATEGORY_GAME_OPTIONS, "gameRules", new String[0]);
			prop.setValidationPattern(Pattern.compile("^\\s*\\w+\\s+[\\w\\d]+(\\s+force|)\\s*$"));
			prop.setComment("Sets the default value of any game rule. To prevent players from changing the rule in-game, add \"force\" after the value. Format: naturalRegeneration false force");
			for (String a : prop.getStringList()) {
				String[] b = a.split("\\s+");
				gameRules.put(b[0], b[1]);
				if (b.length > 2) {
					forcedGameRules.add(b[0]);
				}
			}
		}

		netherLavaPockets = CONFIG.getBoolean("netherLavaPockets", CATEGORY_WORLD, true,
				"Set to false to disable the random lava pockets in the Nether.");
		fireTickRate = CONFIG.getInt("fireTickRate", CATEGORY_WORLD, 30, 0, Integer.MAX_VALUE,
				"The number of world ticks for each fire tick. Decrease for fire to spread and burn faster.");
		disabledFireSources = Sets.newHashSet(CONFIG.getStringList("fireSourcesDisabled", CATEGORY_WORLD, new String[0], "A fire source is a block that sustains fire indefinitely. In Vanilla, netherrack and magma blocks are considered fire sources. To disable a fire source, add its block ID to the list."));
		newFireSources = Sets.newHashSet(CONFIG.getStringList("fireSourcesNew", CATEGORY_WORLD, new String[0], "A fire source is a block that sustains fire indefinitely. In Vanilla, netherrack and magma blocks are considered fire sources. To make a block a fire source, add its ID to the list."));
		tntFuseTime = CONFIG.getInt("tntFuseTime", CATEGORY_WORLD, -1, -1, Integer.MAX_VALUE,
				"The fuse time of TNT, in ticks. 80 = Vanilla, -1 = no effect.");
		creeperFuseTime = CONFIG.getInt("creeperFuseTime", CATEGORY_WORLD, -1, -1, Integer.MAX_VALUE,
				"The fuse time of creepers, in ticks. Has to be at least 1, as otherwise the creepers would explode immediately after spawning. 30 = Vanilla, -1 = no effect.");
		creeperExplosionRadius = CONFIG.getInt("creeperExplosionRadius", CATEGORY_WORLD, -1, -1, 64,
				"The approximate radius of creeper explosions. 3 = Vanilla, -1 = no effect.");

		hungerHealthRegen = CONFIG.getFloat("healthRegen", CATEGORY_HUNGER, -1, Float.MIN_VALUE,
				Float.MAX_VALUE, "The amount of health regen from having a full hunger bar. Requires AppleCore.");
		hungerExhaustionRegen = CONFIG.getFloat("exhaustionRegen", CATEGORY_HUNGER, -1, Float.MIN_VALUE,
				Float.MAX_VALUE, "The amount of exhaustion regen from having a full hunger bar. Requires AppleCore.");
		hungerStarveDamage = CONFIG.getFloat("starveDamage", CATEGORY_HUNGER, -1, Float.MIN_VALUE,
				Float.MAX_VALUE, "The amount of damage dealt by starvation.");

		branding = CONFIG.getStringList("branding", CATEGORY_CLIENT, new String[0], "Changes the text in the lower left corner.");
		retainOldBranding = CONFIG.getBoolean("brandingRetainOld", CATEGORY_CLIENT, true,
				"Whether to retain the old branding and append the new one, or to replace the old one completely.");
		removeRealmsButton = CONFIG.getBoolean("removeRealmsButton", CATEGORY_CLIENT, false,
				"Removes the realms button from the main menu.");
		removeCopyrightText = CONFIG.getBoolean("removeCopyrightText", CATEGORY_CLIENT, false,
				"Removes the copyright information from the main menu.");
		
		disabledOverlays = new HashSet<>();
		
		migrateOldCrap2();

		for (String s : CONFIG.getStringList("disabledOverlays", CATEGORY_CLIENT, new String[0], "Disables UI overlays, such as the health bar or the debug screen. List of overlays as of 1.12.2: https://gist.github.com/coolsquid/499cb7a03303f39b7e9d918b617d0b11")) {
			try {
				ElementType overlay = ElementType.valueOf(s.toUpperCase());
				if (overlay != null) {
					disabledOverlays.add(overlay);
				}
			} catch (Exception e) {
				LogManager.getLogger(MiscTweaks.NAME).error("Failed to disable overlay: " + s);
			}
		}

		migrateOldCrap4();
		damageModifiers = new HashMap<>();
		for (String s : CONFIG.getStringList("damageModifiers", CATEGORY_MISCELLANEOUS, new String[0], "Modifies the damage dealt by the specified damage sources. Format: \"damageSourceName = x * sqrt(16) - 5\", where 'x' is the unmodified damage. Supports addition, subtraction, multiplication, division, parentheses, sin, cos, tan and sqrt.")) {
			String[] parts = s.split("=");
			damageModifiers.put(parts[0], Expression.eval(parts[1]));
		}
		
		infiniteLiquids = new HashSet<>();
		for (String s : CONFIG.getStringList("infiniteLiquids", CATEGORY_MISCELLANEOUS, new String[0], "A list of flowing liquids that should automatically become source blocks when adjacent to at least two other source blocks of the same type. Note that the name of the flowing liquid does not necessarily correspond with the name of its static counterpart. For example, lava should be entered as \"flowing_lava\".")) {
			infiniteLiquids.add(new ResourceLocation(s));
		}
		finiteLiquids = new HashSet<>();
		for (String s : CONFIG.getStringList("finiteLiquids", CATEGORY_MISCELLANEOUS, new String[0], "A list of flowing liquids that should not automatically become source blocks when adjacent to at least two other source blocks of the same type. Note that the name of the flowing liquid does not necessarily correspond with the name of its static counterpart. For example, water should be entered as \"flowing_water\".")) {
			finiteLiquids.add(new ResourceLocation(s));
		}

		disableSleep = CONFIG.getBoolean("disableSleep", CATEGORY_MISCELLANEOUS, false,
				"Disables all forms of beds and sleeping bags.");
		preventPlayerSpawnChange = CONFIG.getBoolean("preventPlayerSpawnChange", CATEGORY_MISCELLANEOUS, false,
				"Prevents players from setting new spawn points (with or without beds). This will completely disable custom player spawns, so all players will spawn at the world's spawn point.");
		preventPlayerBedSpawnChange = CONFIG.getBoolean("preventPlayerBedSpawnChange", CATEGORY_MISCELLANEOUS, false,
				"Prevents players from setting new spawn points with beds. This might also affect some other spawn-setting methods.");
		
		chestSize = CONFIG.getInt("chestSize", CATEGORY_WORLD, 27, 9, 54, "Changes the number of slots in normal and trapped chests. Note that >27 doesn't work very well with double chests.");
		enderChestSize = CONFIG.getInt("enderChestSize", CATEGORY_WORLD, 27, 9, 54, "Changes the number of slots in ender chests.");
		minecartChestSize = CONFIG.getInt("minecartChestSize", CATEGORY_WORLD, 27, 9, 54, "Changes the number of slots in minecart chests.");

		Property enableConfigGui = CONFIG.get("general", "enableConfigGui", true);
		enableConfigGui.setComment("Whether to enable the ingame config GUI.");
		enableConfigGui.setShowInGui(false);
		ConfigManager.enableConfigGui = enableConfigGui.getBoolean();

		for (String category : CONFIG.getCategoryNames()) {
			CONFIG.setCategoryLanguageKey(category, "misctweaks.config." + category);
			for (ConfigCategory child : CONFIG.getCategory(category).getChildren()) {
				child.setLanguageKey("misctweaks.config." + child.getQualifiedName());
			}
		}

		if (CONFIG.hasChanged()) {
			CONFIG.save();
		}
	}

	private static void migrateOldCrap5() {
		{
			Property prop = CONFIG.get(CATEGORY_GAME_OPTIONS, "maxGamma", -1);
			if (prop.getInt() == 100) {
				prop.set(-1);
			}
		}
		{
			Property prop = CONFIG.get(CATEGORY_GAME_OPTIONS, "maxRenderDistance", -1);
			if (prop.getInt() == 32) {
				prop.set(-1);
			}
		}
	}

	private static void migrateOldCrap4() {
		if (CONFIG.hasKey(CATEGORY_MISCELLANEOUS, "drowningDamage")) {
			Property prop = CONFIG.get(CATEGORY_MISCELLANEOUS, "drowningDamage", "");
			if (prop.getDouble() != 1.0D) {
				Property prop2 = CONFIG.get(CATEGORY_MISCELLANEOUS, "damageModifiers", new String[0]);
				String[] s = new String[prop2.getStringList().length + 1];
				for (int i = 0; i < prop2.getStringList().length; i++) {
					s[i] = prop2.getStringList()[i];
				}
				s[prop2.getStringList().length] = "drown=" + prop.getDouble();
				prop2.set(s);
			}
			CONFIG.getCategory(CATEGORY_MISCELLANEOUS).remove("drowningDamage");
		}
	}

	private static void migrateOldCrap3() {
		if (CONFIG.hasKey(CATEGORY_GAME_OPTIONS, "disableCheats")) {
			Property prop = CONFIG.get(CATEGORY_GAME_OPTIONS, "disableCheats", "");
			if (prop.getBoolean()) {
				Property prop2 = CONFIG.get(CATEGORY_GAME_OPTIONS, "cheats", -1);
				prop2.set(1);
			}
			CONFIG.getCategory(CATEGORY_GAME_OPTIONS).remove("disableCheats");
		}
		if (CONFIG.hasKey(CATEGORY_GAME_OPTIONS, "disableBonusChest")) {
			Property prop = CONFIG.get(CATEGORY_GAME_OPTIONS, "disableBonusChest", "");
			if (prop.getBoolean()) {
				Property prop2 = CONFIG.get(CATEGORY_GAME_OPTIONS, "bonusChest", -1);
				prop2.set(1);
			}
			CONFIG.getCategory(CATEGORY_GAME_OPTIONS).remove("disableBonusChest");
		}
	}

	/**
	 * Migrates the old "disabled_overlays" option to the new "disabledOverlays" option.
	 * TODO: remove at some point 
	 */
	private static void migrateOldCrap2() {
		for (ElementType overlay : ElementType.values()) {
			if (overlay != ElementType.ALL) {
				if (CONFIG.getBoolean(overlay.name().toLowerCase(), "client.disabled_overlays", false, "LEGACY! Use \"disabledOverlays\" instead.")) {
					disabledOverlays.add(overlay);
					Property prop = CONFIG.get(CATEGORY_CLIENT, "disabledOverlays", new String[0]);
					String[] newValues = new String[prop.getStringList().length + 1];
					for (int i = 0; i < prop.getStringList().length; i++) {
						newValues[i] = prop.getStringList()[i];
					}
					newValues[prop.getStringList().length] = overlay.name().toLowerCase();
					prop.set(newValues);
				}
			}
		}
		CONFIG.getCategory("client.disabled_overlays").clear();
		CONFIG.removeCategory(CONFIG.getCategory("client.disabled_overlays"));
	}

	/**
	 * Migrates the old "forcedX" options to the new "forceX" options.
	 * TODO: remove at some point 
	 */
	private static void migrateOldCrap1() {
		if (CONFIG.hasKey(CATEGORY_GAME_OPTIONS, "forcedDifficulty")) {
			Property prop = CONFIG.get(CATEGORY_GAME_OPTIONS, "forcedDifficulty", "");
			if (prop.getString().isEmpty()) {
				CONFIG.getCategory(CATEGORY_GAME_OPTIONS).remove("forcedDifficulty");
			}
			else {
				if (defaultDifficulty.isEmpty()) {
					Property prop2 = CONFIG.get(CATEGORY_GAME_OPTIONS, "defaultDifficulty", "");
					prop2.set(prop.getString());
					Property prop3 = CONFIG.get(CATEGORY_GAME_OPTIONS, "allowedDifficulties", "");
					prop3.set(new String[] { prop2.getString() });
					CONFIG.getCategory(CATEGORY_GAME_OPTIONS).remove("forcedDifficulty");
				} else {
					prop.setComment("LEGACY! This option no longer works. Use \"defaultDifficulty\" and \"forceDifficulty\" instead.");
				}
			}
		}
		if (CONFIG.hasKey(CATEGORY_GAME_OPTIONS, "forcedGamemode")) {
			Property prop = CONFIG.get(CATEGORY_GAME_OPTIONS, "forcedGamemode", "");
			if (prop.getString().isEmpty()) {
				CONFIG.getCategory(CATEGORY_GAME_OPTIONS).remove("forcedGamemode");
			}
			else {
				if (defaultGamemode.isEmpty()) {
					Property prop2 = CONFIG.get(CATEGORY_GAME_OPTIONS, "defaultGamemode", "");
					prop2.set(prop.getString());
					Property prop3 = CONFIG.get(CATEGORY_GAME_OPTIONS, "allowedGamemodes", "");
					prop3.set(new String[] { prop2.getString() });
					CONFIG.getCategory(CATEGORY_GAME_OPTIONS).remove("forcedGamemode");
				} else {
					prop.setComment("LEGACY! This option no longer works. Use \"defaultGamemode\" and \"forceGamemode\" instead.");
				}
			}
		}
		if (CONFIG.hasKey(CATEGORY_GAME_OPTIONS, "forcedWorldType")) {
			Property prop = CONFIG.get(CATEGORY_GAME_OPTIONS, "forcedWorldType", -1);
			if (prop.getInt() == -1) {
				CONFIG.getCategory(CATEGORY_GAME_OPTIONS).remove("forcedWorldType");
			}
			else {
				if (defaultWorldType.isEmpty()) {
					Property prop2 = CONFIG.get(CATEGORY_GAME_OPTIONS, "defaultWorldType", "");
					prop2.set(WorldType.WORLD_TYPES[prop.getInt()].getName());
					Property prop3 = CONFIG.get(CATEGORY_GAME_OPTIONS, "allowedWorldTypes", "");
					prop3.set(new String[] { prop2.getString() });
					CONFIG.getCategory(CATEGORY_GAME_OPTIONS).remove("forcedWorldType");
				} else {
					prop.setComment("LEGACY! This option no longer works. Use \"defaultWorldType\" and \"forceWorldType\" instead.");
				}
			}
		}
		if (CONFIG.hasKey(CATEGORY_GAME_OPTIONS, "forcedChunkProviderSettings")) {
			Property prop = CONFIG.get(CATEGORY_GAME_OPTIONS, "forcedChunkProviderSettings", "");
			if (prop.getString().isEmpty()) {
				CONFIG.getCategory(CATEGORY_GAME_OPTIONS).remove("forcedChunkProviderSettings");
			}
			else {
				if (defaultChunkProviderSettings.isEmpty()) {
					Property prop2 = CONFIG.get(CATEGORY_GAME_OPTIONS, "defaultChunkProviderSettings", "");
					prop2.set(prop.getString());
					Property prop3 = CONFIG.get(CATEGORY_GAME_OPTIONS, "forceChunkProviderSettings", "");
					prop3.set(true);
					CONFIG.getCategory(CATEGORY_GAME_OPTIONS).remove("forcedChunkProviderSettings");
				} else {
					prop.setComment("LEGACY! This option no longer works. Use \"defaultChunkProviderSettings\" and \"forceChunkProviderSettings\" instead.");
				}
			}
		}
	}
}