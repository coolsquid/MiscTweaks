package coolsquid.misctweaks.config;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import coolsquid.misctweaks.MiscTweaks;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ConfigManager {

	public static final Configuration CONFIG = new Configuration(new File("config/MiscTweaks.cfg"));

	public static String forcedDifficulty = "";
	public static String forcedGamemode = "";
	public static int forcedWorldType = -1;
	public static String forcedChunkProviderSettings = "";
	public static int defaultWorldType = -1;
	public static String defaultChunkProviderSettings = "";
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
	
	public static boolean blocksFallInstantly = false;

	public static float drowningDamage = 1.0F;

	public static boolean disableSleep = false;
	public static boolean preventPlayerSpawnChange = false;
	public static boolean preventPlayerBedSpawnChange = false;
	
	public static HashSet<String> disabledFireSources;
	public static HashSet<String> newFireSources;
	
	public static int chestSize = 27;

	public static boolean enableConfigGui;

	public static void loadConfig() {
		CONFIG.load();

		forcedDifficulty = CONFIG.getString("forcedDifficulty", "game_options", forcedDifficulty,
				"Forces the specified difficulty. Allows for hard, normal, easy or peaceful. Leave empty to disable.",
				new String[] { "peaceful", "easy", "normal", "hard" });
		forcedGamemode = CONFIG.getString("forcedGamemode", "game_options", forcedGamemode,
				"Forces the specified gamemode. Allows for survival, creative, adventure, spectator, and hardcore. Leave empty to disable.",
				new String[] { "survival", "creative", "adventure", "spectator", "hardcore" });
		forcedWorldType = CONFIG.getInt("forcedWorldType", "game_options", -1, -1, WorldType.WORLD_TYPES.length - 1,
				"Forces a certain world type. 0 is default, 1 is superflat, 2 is large biomes, etc. Set to -1 to disable.");
		forcedChunkProviderSettings = CONFIG.getString("forcedChunkProviderSettings", "game_options", "",
				"Forces a certain chunk provider settings JSON.");
		defaultWorldType = CONFIG.getInt("defaultWorldType", "game_options", -1, -1, WorldType.WORLD_TYPES.length - 1,
				"Sets a default (initially selected) world type. 0 is default, 1 is superflat, 2 is large biomes, etc. Set to -1 to disable.");
		defaultChunkProviderSettings = CONFIG.getString("defaultChunkProviderSettings", "game_options", "",
				"Sets a default chunk provider settings JSON.");
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
		disabledFireSources = Sets.newHashSet(CONFIG.getStringList("fireSourcesDisabled", "world", new String[0], "A fire source is a block that sustains fire indefinitely. In Vanilla, netherrack and magma blocks are considered fire sources. To disable a fire source, add its block ID to the list."));
		newFireSources = Sets.newHashSet(CONFIG.getStringList("fireSourcesNew", "world", new String[0], "A fire source is a block that sustains fire indefinitely. In Vanilla, netherrack and magma blocks are considered fire sources. To make a block a fire source, add its ID to the list."));
		tntFuseTime = CONFIG.getInt("tntFuseTime", "world", 80, 0, Integer.MAX_VALUE,
				"The fuse time of TNT, in ticks.");
		creeperFuseTime = CONFIG.getInt("creeperFuseTime", "world", 30, 1, Integer.MAX_VALUE,
				"The fuse time of creepers, in ticks. Has to be at least 1, as otherwise the creepers would explode immediately after spawning.");
		creeperExplosionRadius = CONFIG.getInt("creeperExplosionRadius", "world", 3, 0, 64,
				"The approximate radius of creeper explosions.");
		blocksFallInstantly = CONFIG.getBoolean("blocksFallInstantly", "world", false, "If true, blocks like sand and gravel will instantly teleport to the bottom when falling.");

		hungerHealthRegen = CONFIG.getFloat("healthRegen", "hunger", hungerHealthRegen, Float.MIN_VALUE,
				Float.MAX_VALUE, "The amount of health regen from having a full hunger bar. Requires AppleCore.");
		hungerExhaustionRegen = CONFIG.getFloat("exhaustionRegen", "hunger", hungerExhaustionRegen, Float.MIN_VALUE,
				Float.MAX_VALUE, "The amount of exhaustion regen from having a full hunger bar. Requires AppleCore.");
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
		for (ElementType overlay : ElementType.values()) {
			if (overlay != ElementType.ALL && CONFIG.getBoolean(overlay.name().toLowerCase(),
					"client.disabled_overlays", false, "Set to true to disable the overlay.")) {
				overlays.add(overlay);
			}
		}
		disabledOverlays = overlays.build();

		drowningDamage = CONFIG.getFloat("drowningDamage", "miscellaneous", drowningDamage, Float.MIN_VALUE,
				Float.MAX_VALUE, "The amount of damage dealt by drowning.");

		disableSleep = CONFIG.getBoolean("disableSleep", "miscellaneous", disableSleep,
				"Disables all forms of beds and sleeping bags.");
		preventPlayerSpawnChange = CONFIG.getBoolean("preventPlayerSpawnChange", "miscellaneous", false,
				"Prevents players from setting new spawn points (with or without beds). This will completely disable custom player spawns, so all players will spawn at the world's spawn point.");
		preventPlayerBedSpawnChange = CONFIG.getBoolean("preventPlayerBedSpawnChange", "miscellaneous", false,
				"Prevents players from setting new spawn points with beds. This might also affect some other spawn-setting methods.");
		
		chestSize = CONFIG.getInt("chestSize", "world", 27, 9, 54, "Changes the number of slots in normal and trapped chests. Note that >27 doesn't work very well with double chests.");

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
		// Hidden test for the new event handler.
		{
			for (Object e : HANDLERS) {
				MinecraftForge.EVENT_BUS.unregister(e);
			}
			if (CONFIG.getBoolean("explodingArrows", "secret", false, "")) {
				register(ProjectileImpactEvent.Arrow.class, (e) -> {
					Vec3d pos = e.getRayTraceResult().hitVec;
					e.getArrow().world.newExplosion(e.getArrow(), pos.x, pos.y, pos.z, 3, true, true);
					e.getArrow().setDead();
					if (e.getRayTraceResult().entityHit != null) {
						e.getRayTraceResult().entityHit.setFire(20);
					}
				});
			}
			// The options are hidden, so don't save them
			CONFIG.load();
		}
	}

	private static final ArrayList<Object> HANDLERS = new ArrayList<>();

	/** Experimental event handler. */
	private static <E extends Event> void register(Class<E> c, EventHandler<E> handler) {
		Object o = new Object() {

			@SubscribeEvent
			public void onEvent(Event event) {
				handler.onEvent((E) event);
			}
		};
		HANDLERS.add(o);
		try {
			Method method = EventBus.class.getDeclaredMethod("register", Class.class, Object.class, Method.class,
					ModContainer.class);
			method.setAccessible(true);
			Method targetMethod = o.getClass().getMethod("onEvent", Event.class);
			method.invoke(MinecraftForge.EVENT_BUS, c, o, targetMethod, Loader.instance().activeModContainer());
			Map<Object, ModContainer> listenerOwners = ReflectionHelper.getPrivateValue(EventBus.class,
					MinecraftForge.EVENT_BUS, "listenerOwners");
			listenerOwners.put(o, Loader.instance().getIndexedModList().get(MiscTweaks.MODID));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@FunctionalInterface
	public static interface EventHandler<E extends Event> {

		void onEvent(E event);
	}
}