package coolsquid.misctweaks.util;

import java.util.Map.Entry;

import coolsquid.misctweaks.MiscTweaks;
import coolsquid.misctweaks.config.ConfigManager;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.item.ItemBed;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.BlockEvent.CreateFluidSourceEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
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
	public void onCommand(CommandEvent event) {
		if (event.getSender() instanceof EntityPlayer && event.getSender().getServer() != null && !event.getSender().getServer().isDedicatedServer()) {
			if (!ConfigManager.allowedGamemodes.isEmpty() && event.getCommand().getName().equals("gamemode") && !ConfigManager.allowedGamemodes.contains(event.getParameters()[0])) {
				event.setCanceled(true);
				event.getSender().sendMessage(new TextComponentString("<MiscTweaks> You are not allowed to change the game mode.").setStyle(new Style().setColor(TextFormatting.DARK_RED)));
			}
			if (!ConfigManager.allowedDifficulties.isEmpty() && event.getCommand().getName().equals("difficulty") && !ConfigManager.allowedDifficulties.contains(event.getParameters()[0])) {
				event.setCanceled(true);
				event.getSender().sendMessage(new TextComponentString("<MiscTweaks> You are not allowed to change the difficulty.").setStyle(new Style().setColor(TextFormatting.DARK_RED)));
			}
			if (event.getCommand().getName().equals("gamerule")) {
				if (event.getParameters().length > 1) {
					String ruleName = event.getParameters()[0];
					if (ConfigManager.forcedGameRules.contains(ruleName)) {
						event.setCanceled(true);
						event.getSender().sendMessage(new TextComponentString("<MiscTweaks> You are not allowed to change this game rule.").setStyle(new Style().setColor(TextFormatting.DARK_RED)));
					}
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
		if (event.getEntity() instanceof EntityTNTPrimed && ConfigManager.tntFuseTime != -1) {
			EntityTNTPrimed tnt = (EntityTNTPrimed) event.getEntity();
			if (tnt.getFuse() == 80) {
				tnt.setFuse(ConfigManager.tntFuseTime);
			}
		} else if (event.getEntity() instanceof EntityCreeper) {
			EntityCreeper creeper = (EntityCreeper) event.getEntity();
			if (ConfigManager.creeperFuseTime != -1 && creeper.fuseTime == 30) {
				creeper.fuseTime = ConfigManager.creeperFuseTime;
			}
			if (ConfigManager.creeperExplosionRadius != -1 && creeper.explosionRadius == 3) {
				creeper.explosionRadius = ConfigManager.creeperExplosionRadius;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingHurt(LivingHurtEvent event) {
		Expression e = ConfigManager.damageModifiers.get(event.getSource().damageType);
		if (e != null) {
			event.setAmount((float) e.eval(event.getAmount()));
		}
	}

	@SubscribeEvent
	public void onSleepCheck(PlayerSleepInBedEvent event) {
		if (ConfigManager.disableSleep) {
			event.getEntityPlayer().sendStatusMessage(new TextComponentString("Sleep has been disabled"), true);
			event.setResult(SleepResult.OTHER_PROBLEM);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		if (event.getItemStack() != null
				&& event.getItemStack().getItem() instanceof ItemBed) {
			if (ConfigManager.disableSleep) {
				event.getToolTip().add("Sleep has been disabled");
			} else if (ConfigManager.preventPlayerSpawnChange || ConfigManager.preventPlayerBedSpawnChange) {
				event.getToolTip().add("Sleeping won't change your spawn point");
			}
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
			if (!ConfigManager.defaultDifficulty.isEmpty()) {
				event.getWorld().getWorldInfo().setDifficulty(EnumDifficulty.valueOf(ConfigManager.defaultDifficulty));
			}
		}
	}

	@SubscribeEvent
	public void onCreateSourceBlock(CreateFluidSourceEvent event) {
		ResourceLocation name = event.getState().getBlock().getRegistryName();
		if (ConfigManager.infiniteLiquids.contains(name)) {
			event.setResult(Result.ALLOW);
		} else if (ConfigManager.finiteLiquids.contains(name)) {
			event.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (ConfigManager.respawnTime > -1 && !event.isEndConquered() && !event.player.world.isRemote && event.player.world.playerEntities.size() == 1 && event.player.world.getGameRules().getBoolean("doDaylightCycle")) {
			event.player.world.setWorldTime(ConfigManager.respawnTime);
		}
	}
}