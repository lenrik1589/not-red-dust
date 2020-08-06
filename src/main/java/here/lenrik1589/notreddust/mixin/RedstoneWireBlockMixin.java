package here.lenrik1589.notreddust.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.util.math.Vector3f;
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

	@Final
	@Shadow
	private static Vector3f[] field_24466;

	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V")
	private void init(AbstractBlock.Settings settings, CallbackInfo info) {
		for(int i = 0; i < 16; i++) {
			int color = Color.HSBtoRGB(i / 16f, 1, 1);
			LOGGER.info(((color >> 16) & 0xff) + " " + ((color >> 8) & 0xff) + " " + ((color) & 0xff));
			field_24466[i] = new Vector3f(((color >> 16) & 0xff) / 256f, ((color >> 8) & 0xff) / 256f, ((color) & 0xff) / 256f);
		}
	}
}
