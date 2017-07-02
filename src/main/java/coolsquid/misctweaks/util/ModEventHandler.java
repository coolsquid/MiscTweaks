package coolsquid.misctweaks.util;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.item.ItemBed;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import coolsquid.misctweaks.MiscTweaks;
import coolsquid.misctweaks.config.ConfigManager;

import squeek.applecore.api.hunger.HealthRegenEvent;
import squeek.applecore.api.hunger.StarvationEvent;

public class ModEventHandler {

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if (!ConfigManager.forcedDifficulty.isEmpty()) {
			if (ConfigManager.forcedDifficulty.equalsIgnoreCase("hardcore")) {
				event.getWorld().getWorldInfo().setGameType(GameType.SURVIVAL);
				event.getWorld().getWorldInfo().setDifficulty(EnumDifficulty.HARD);
				event.getWorld().getWorldInfo().setAllowCommands(false);
				event.getWorld().getWorldInfo().setHardcore(true);
			} else {
				event.getWorld().getWorldInfo()
						.setDifficulty(EnumDifficulty.valueOf(ConfigManager.forcedDifficulty.toUpperCase()));
			}
		}
		if (!ConfigManager.forcedGamemode.isEmpty()) {
			event.getWorld().getWorldInfo().setGameType(GameType.valueOf(ConfigManager.forcedGamemode.toUpperCase()));
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
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onDrawScreen(GuiScreenEvent.DrawScreenEvent event) {
		if (event.getGui() instanceof GuiCreateWorld) {
			GuiCreateWorld gui = (GuiCreateWorld) event.getGui();
			if (!ConfigManager.forcedDifficulty.isEmpty()) {
				if (ConfigManager.forcedDifficulty.equalsIgnoreCase("hardcore")) {
					gui.btnGameMode.displayString = I18n.format("selectWorld.gameMode") + ": "
							+ I18n.format("selectWorld.gameMode.hardcore");
				} else {
					gui.btnGameMode.displayString = I18n.format("selectWorld.gameMode") + ": "
							+ I18n.format("selectWorld.gameMode." + ConfigManager.forcedGamemode.toLowerCase());
				}
				gui.btnGameMode.enabled = false;
			}
			if (ConfigManager.disableCheats) {
				gui.btnAllowCommands.enabled = false;
				gui.btnAllowCommands.displayString = I18n.format("selectWorld.allowCommands", new Object[0]) + ' '
						+ I18n.format("options.off", new Object[0]);
			}
		} else if (event.getGui() instanceof GuiOptions && !ConfigManager.forcedDifficulty.isEmpty()) {
			GuiButton b = ((GuiOptions) event.getGui()).difficultyButton;
			if (b != null) {
				b.enabled = false;
				b.displayString = I18n.format("options.difficulty", new Object[0]) + ": " + I18n
						.format("options.difficulty." + ConfigManager.forcedDifficulty.toLowerCase(), new Object[0]);
			}
		} else if (event.getGui() instanceof GuiMainMenu) {
			if (ConfigManager.removeRealmsButton) {
				((GuiMainMenu) event.getGui()).realmsButton.visible = false;
				((GuiMainMenu) event.getGui()).realmsNotification = null;
				ReflectionHelper.setPrivateValue(GuiButton.class,
						Util.getPrivateValue(GuiMainMenu.class, (GuiMainMenu) event.getGui(), "modButton"), 200,
						"width");
			}
			if (ConfigManager.removeCopyrightText) {
				((GuiMainMenu) event.getGui()).widthCopyright = 0;
				((GuiMainMenu) event.getGui()).widthCopyrightRest = event.getGui().width + 1;
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

	@Method(modid = "AppleCore")
	@SubscribeEvent
	public void onHealthRegen(HealthRegenEvent.Regen event) {
		if (ConfigManager.hungerHealthRegen != 1.0F || ConfigManager.hungerExhaustionRegen != 3.0F) {
			event.deltaHealth = ConfigManager.hungerHealthRegen;
			event.deltaExhaustion = ConfigManager.hungerExhaustionRegen;
		}
	}

	@Method(modid = "AppleCore")
	@SubscribeEvent
	public void onStarve(StarvationEvent.Starve event) {
		if (ConfigManager.hungerStarveDamage != 1.0F) {
			event.starveDamage = ConfigManager.hungerStarveDamage;
		}
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if (event.getModID().equals(MiscTweaks.MODID)) {
			ConfigManager.CONFIG.save();
			ConfigManager.loadConfig();
			BrandingTweaks.updateBranding();
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
		if (event.getItemStack() != null && event.getItemStack().getItem() instanceof ItemBed) {
			event.getToolTip().add("Unusable");
		}
	}
}