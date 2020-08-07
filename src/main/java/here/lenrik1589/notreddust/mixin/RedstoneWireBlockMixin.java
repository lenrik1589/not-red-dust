package here.lenrik1589.notreddust.mixin;

import here.lenrik1589.notreddust.config.NotRedConfig;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin {

	private static final Logger LOGGER = LogManager.getLogger("notreddust");

	@Final @Shadow
	private static Vector3f[] field_24466;

	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V")
	private void init(AbstractBlock.Settings settings, CallbackInfo info) {
		if(NotRedConfig.loadBoolean("Enabled")){
			NotRedConfig.ColorMode colorMode = NotRedConfig.ColorMode.load();
			LOGGER.info("Applying color in {} mode.",colorMode.toString());
			if(colorMode == NotRedConfig.ColorMode.Rainbow){
				boolean reversed = NotRedConfig.loadBoolean("Reversed", "Rainbow");
				boolean powerBrightness = NotRedConfig.loadBoolean("PowerBrightness", "Rainbow");
				LOGGER.info(" " +reversed);
				for(int powerLevel = 0; powerLevel < 16; powerLevel++) {
					int color = Color.HSBtoRGB(
									reversed? 1 - powerLevel / 16f : powerLevel / 16f,
									1,
									powerBrightness?(0.55f + (powerLevel + 1) / 16f * 0.45f):1);
					LOGGER.info("{}: {}r {}g {}b", powerLevel, (color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff);
					field_24466[powerLevel] = new Vector3f(((color >> 16) & 0xff) / 256f, ((color >> 8) & 0xff) / 256f, ((color) & 0xff) / 256f);
				}
			} else if (colorMode == NotRedConfig.ColorMode.OneColor){
				int color = NotRedConfig.loadColor("Color","OneColor");
				float[] hsbColor = Color.RGBtoHSB((color >> 16) & 0xff, (color >> 8) & 0xff, (color) & 0xff, new float[3]);
				for(int powerLevel = 0; powerLevel < 16; powerLevel++) {
					color = Color.HSBtoRGB(hsbColor[0], hsbColor[1], (hsbColor[2] *(0.55f + powerLevel / 16f * 0.45f)));
					LOGGER.info("{}: {}r {}g {}b", powerLevel, (color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff);
					field_24466[powerLevel] = new Vector3f(((color >> 16) & 0xff) / 256f, ((color >> 8) & 0xff) / 256f, ((color) & 0xff) / 256f);
				}
			} else {
				NotRedConfig.LerpMode lerpMode = NotRedConfig.LerpMode.load();
				LOGGER.info("Lerping color using {} values",lerpMode);
				int color1 = NotRedConfig.loadColor("Start", "Gradient");
				int color2 = NotRedConfig.loadColor("End", "Gradient");
				float[] hsbColor1 = Color.RGBtoHSB((color1 >> 16) & 0xff, (color1 >> 8) & 0xff, color1 & 0xff, new float[3]);
				float[] hsbColor2 = Color.RGBtoHSB((color2 >> 16) & 0xff, (color2 >> 8) & 0xff, color2 & 0xff, new float[3]);
				for (int powerLevel = 0; powerLevel < 16; powerLevel++) {
					int color;
					if(lerpMode == NotRedConfig.LerpMode.RGB) {
						color = lerpColorAndAlpha(powerLevel / 16d, color1, color2);
					} else {
						float lerpedHue        = lerp(powerLevel / 15d, hsbColor1[0], hsbColor2[0]);
						float lerpedSaturation = lerp(powerLevel / 15d, hsbColor1[1], hsbColor2[1]);
						float lerpedBrightness = lerp(powerLevel / 15d, hsbColor1[2], hsbColor2[2]);
						LOGGER.info("{}: {}h {}s {}b", powerLevel, lerpedHue, lerpedSaturation, lerpedBrightness);
						color = Color.HSBtoRGB(lerpedHue, lerpedSaturation, lerpedBrightness);
					}
					LOGGER.info("{}: {}r {}g {}b", powerLevel, (color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff);
					field_24466[powerLevel] = new Vector3f(((color >> 16) & 0xff) / 256f, ((color >> 8) & 0xff) / 256f, ((color) & 0xff) / 256f);
				}
			}
		} else {
			LOGGER.info("Applying vanilla coloring.");
			for(int powerLevel = 0; powerLevel <= 15; ++powerLevel) {
				float value = powerLevel / 15.0F;
				float R = value * 0.6F + (value > 0.0F ? 0.4F : 0.3F);
				float G = MathHelper.clamp(value * value * 0.7F - 0.5F, 0.0F, 1.0F);
				float B = MathHelper.clamp(value * value * 0.6F - 0.7F, 0.0F, 1.0F);
				LOGGER.info("{}: {}r {}g {}b", powerLevel, R * 0xff, G * 0xff, B * 0xff);
				field_24466[powerLevel] = new Vector3f(R, G, B);
			}
		}
	}

	private int lerp(double delta, int to, int from) {
		return (int)(delta * (double)to + (1.0D - delta) * (double)from);
	}

	private float lerp(double delta, float to, float from) {
		return (float) (delta * (double)to + (1.0D - delta) * (double)from);
	}

	private int lerpColorAndAlpha(double delta, int to, int from){
		return
						((lerp(delta, (to >> 24) & 0xFF, (from >> 24) & 0xFF) << 24) & 0xFF000000) |
						((lerp(delta, (to >> 16) & 0xFF, (from >> 16) & 0xFF) << 16) & 0x00FF0000) |
						((lerp(delta, (to >> 8)  & 0xFF, (from >> 8)  & 0xFF) << 8 ) & 0x0000FF00) |
						((lerp(delta, to         & 0xFF, from         & 0xFF)      ) & 0x000000FF);
	}
}
