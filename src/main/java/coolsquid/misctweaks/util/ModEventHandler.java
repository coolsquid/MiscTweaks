package coolsquid.misctweaks.util;

import java.util.Map.Entry;

import coolsquid.misctweaks.MiscTweaks;
import coolsquid.misctweaks.config.ConfigManager;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.item.ItemBed;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.GameRuleChangeEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.applecore.api.hunger.HealthRegenEvent;

public class ModEventHandler {

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if (event.getModID().equals(MiscTweaks.MODID)) {
			ConfigManager.CONFIG.save();
			ConfigManager.loadConfig();
			MiscTweaks.applyTweaks();
		}
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if (!ConfigManager.forcedDifficulty.isEmpty()) {
			event.getWorld().getWorldInfo()
					.setDifficulty(EnumDifficulty.valueOf(ConfigManager.forcedDifficulty.toUpperCase()));
		}
		if (!ConfigManager.forcedGamemode.isEmpty()) {
			if (ConfigManager.forcedGamemode.equalsIgnoreCase("hardcore")) {
				event.getWorld().getWorldInfo().setGameType(GameType.SURVIVAL);
				event.getWorld().getWorldInfo().setDifficulty(EnumDifficulty.HARD);
				event.getWorld().getWorldInfo().setAllowCommands(false);
				event.getWorld().getWorldInfo().setHardcore(true);
			} else {
				event.getWorld().getWorldInfo()
						.setGameType(GameType.valueOf(ConfigManager.forcedGamemode.toUpperCase()));
			}
		}
		if (ConfigManager.disableCheats) {
			event.getWorld().getWorldInfo().setAllowCommands(false);
		}
	}

	@SubscribeEvent
	public void onCommand(CommandEvent event) {
		if (!ConfigManager.forcedGamemode.isEmpty() && event.getCommand().getName().equals("gamemode")) {
			event.setCanceled(true);
		}
		if (!ConfigManager.forcedDifficulty.isEmpty() && event.getCommand().getName().equals("difficulty")) {
			event.setCanceled(true);
		}
		if (event.getCommand().getName().equals("gamerule")) {
			if (event.getParameters().length > 1) {
				String ruleName = event.getParameters()[0];
				if (ConfigManager.forcedGameRules.contains(ruleName)) {
					event.getSender().getServer().getWorld(0).getGameRules().setOrCreateGameRule(ruleName, ConfigManager.gameRules.get(ruleName));
					event.setCanceled(true);
					event.getSender().sendMessage(new TextComponentString("<MiscTweaks> You are not allowed to change this game rule.").setStyle(new Style().setColor(TextFormatting.DARK_RED)));
				}
			}
		}
	}

	@SubscribeEvent
	public void onPopulate(PopulateChunkEvent.Populate event) {
		if (!ConfigManager.netherLavaPockets && (event.getType() == PopulateChunkEvent.Populate.EventType.NETHER_LAVA
				|| event.getType() == PopulateChunkEvent.Populate.EventType.NETHER_LAVA2)) {
			event.setResult(Result.DENY);
		}
	}

	@Method(modid = "applecore")
	@SubscribeEvent
	public void onHealthRegen(HealthRegenEvent.Regen event) {
		if (ConfigManager.hungerHealthRegen != 1.0F || ConfigManager.hungerExhaustionRegen != 3.0F) {
			event.deltaHealth = ConfigManager.hungerHealthRegen;
			event.deltaExhaustion = ConfigManager.hungerExhaustionRegen;
		}
	}

	@SubscribeEvent
	public void onStarve(LivingAttackEvent event) {
		if (event.getSource() == DamageSource.STARVE && ConfigManager.hungerStarveDamage == 0) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onStarve(LivingHurtEvent event) {
		if (event.getSource() == DamageSource.STARVE && ConfigManager.hungerStarveDamage != 1F) {
			event.setAmount(ConfigManager.hungerStarveDamage);
		}
	}

	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityTNTPrimed && ConfigManager.tntFuseTime != 80) {
			EntityTNTPrimed tnt = (EntityTNTPrimed) event.getEntity();
			if (tnt.getFuse() == 80) {
				tnt.setFuse(ConfigManager.tntFuseTime);
			}
		} else if (event.getEntity() instanceof EntityCreeper
				&& (ConfigManager.creeperFuseTime != 30 || ConfigManager.creeperExplosionRadius != 3)) {
			EntityCreeper creeper = (EntityCreeper) event.getEntity();
			if (creeper.fuseTime == 30) {
				creeper.fuseTime = ConfigManager.creeperFuseTime;
			}
			if (creeper.explosionRadius == 3) {
				creeper.explosionRadius = ConfigManager.creeperExplosionRadius;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingHurt(LivingHurtEvent event) {
		if (event.getSource() == DamageSource.DROWN && ConfigManager.drowningDamage != 1 && event.getAmount() != 1) {
			event.setAmount(ConfigManager.drowningDamage);
		}
	}

	@SubscribeEvent
	public void onSleepCheck(PlayerSleepInBedEvent event) {
		if (ConfigManager.disableSleep) {
			event.getEntityPlayer().sendStatusMessage(new TextComponentString("Sleeping has been disabled"), true);
			event.setResult(SleepResult.OTHER_PROBLEM);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		if (ConfigManager.disableSleep && event.getItemStack() != null
				&& event.getItemStack().getItem() instanceof ItemBed) {
			event.getToolTip().add("Unusable");
		}
	}

	@SubscribeEvent
	public void onSpawnSet(PlayerSetSpawnEvent event) {
		if (ConfigManager.preventPlayerSpawnChange || !event.isForced() && ConfigManager.preventPlayerBedSpawnChange) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onCreateSpawnPosition(WorldEvent.CreateSpawnPosition event) {
		if (event.getWorld().provider.getDimension() == 0) {
			for (Entry<String, String> e : ConfigManager.gameRules.entrySet()) {
				event.getWorld().getGameRules().setOrCreateGameRule(e.getKey(), e.getValue());
			}
			if (ConfigManager.newWorldTime != -1) {
				event.getWorld().setWorldTime(ConfigManager.newWorldTime);
			}
			if (!ConfigManager.newWorldWeather.isEmpty()) {
				if (ConfigManager.newWorldWeather.equals("rain")) {
					event.getWorld().getWorldInfo().setRaining(true);
				} else if (ConfigManager.newWorldWeather.equals("thunder")) {
					event.getWorld().getWorldInfo().setRaining(true);
					event.getWorld().getWorldInfo().setThundering(true);
				} else {
					event.getWorld().getWorldInfo().setRaining(false);
					event.getWorld().getWorldInfo().setThundering(false);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onGameRuleChange(GameRuleChangeEvent event) {
		if (ConfigManager.forcedGameRules.contains(event.getRuleName())) {
			event.getRules().setOrCreateGameRule(event.getRuleName(), ConfigManager.gameRules.get(event.getRuleName()));
		}
	}
}