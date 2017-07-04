package coolsquid.misctweaks.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GammaSlider extends GuiSlider {

	public GammaSlider(int id, int x, int y) {
		super(new Responder(), id, x, y, I18n.format(Options.GAMMA.getEnumString()) + ": ", Options.GAMMA.getValueMin(),
				Options.GAMMA.getValueMax(), Minecraft.getMinecraft().gameSettings.getOptionFloatValue(Options.GAMMA),
				new Formatter());
	}

	public static class Responder implements GuiResponder {

		@Override
		public void setEntryValue(int id, boolean value) {

		}

		@Override
		public void setEntryValue(int id, float value) {
			Minecraft.getMinecraft().gameSettings.setOptionFloatValue(Options.GAMMA, value);
		}

		@Override
		public void setEntryValue(int id, String value) {

		}
	}

	public static class Formatter implements FormatHelper {

		@Override
		public String getText(int id, String name, float value) {
			if (value == 0.0F) {
				return name + I18n.format("options.gamma.min");
			} else if (value == 1.0F) {
				return name + I18n.format("options.gamma.max");
			} else {
				return name + "+" + (int) (value * 100) + "%";
			}
		}
	}
}