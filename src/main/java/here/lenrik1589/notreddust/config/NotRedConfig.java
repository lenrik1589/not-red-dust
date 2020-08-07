//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package here.lenrik1589.notreddust.config;

import com.google.common.collect.Lists;
import io.github.prospector.modmenu.api.ModMenuApi;
import io.github.prospector.modmenu.api.ConfigScreenFactory;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder.TopCellElementBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Material;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.*;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class NotRedConfig implements ModMenuApi {
	private static final Logger LOGGER = LogManager.getLogger("notreddust.config");
	private static final File configFile = new File(FabricLoader.getInstance().getConfigDirectory(),"notreddust.nbt");
	public static final CompoundTag defaultConfig;
	public static CompoundTag configTag;
	static {
		CompoundTag tag = new CompoundTag();
		tag.putInt("Mode", ColorMode.OneColor.ordinal());
		tag.putBoolean("Enabled", true);
		{
			CompoundTag Rainbow = new CompoundTag();
			Rainbow.putBoolean("Folded", true);
			Rainbow.putBoolean("Reversed", false);
			Rainbow.putBoolean("PowerBrightness", false);
			tag.put("Rainbow", Rainbow);
		}{
			CompoundTag OneColor = new CompoundTag();
			OneColor.putBoolean("Folded", true);
			OneColor.putIntArray("Color", new int[]{0, 255, 0, 0});
			tag.put("OneColor", OneColor);
		}{
			CompoundTag Gradient = new CompoundTag();
			Gradient.putInt("Mode", LerpMode.RGB.ordinal());
			Gradient.putBoolean("Folded", true);
			Gradient.putIntArray("Start", new int[]{255, 255, 0, 0});
			Gradient.putIntArray("End", new int[]{255, 0, 255, 0});
			tag.put("Gradient", Gradient);
		}
		defaultConfig = tag;
		try {
			configTag = NbtIo.read(configFile);
		} catch (IOException e) {
			configTag = new CompoundTag().copyFrom(defaultConfig);
			try {
				LOGGER.error("Error occurred while reading config file, falling back to default config:", e);
				NbtIo.write(configTag, configFile);
			} catch (IOException ioException) {
				LOGGER.error("Error occurred while writing default config to file config file:", e);
			}
		}
		if (configTag == null) {
			configTag = defaultConfig;
		}
	}


	public NotRedConfig () {
	}

	public String getModId() {
		return "notreddust";
	}

	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (screen) -> defaultConfigBuilder().build();
	}

	public enum ColorMode implements Serializable {
		Rainbow,
		OneColor,
		Gradient;
		public static ColorMode load (){
			int value = loadInt("Mode");
			return values()[value];
		}
		public static void save(ColorMode value){
			saveInt(value.ordinal(), "Mode");
		}
	}

	public enum LerpMode implements Serializable {
		RGB,
		HSB;
		public static LerpMode load (){
			int value = loadInt("Mode", "Gradient");
			return values()[value];
		}
		public static void save(LerpMode value){
			saveInt(value.ordinal(), "Mode", "Gradient");
		}
	}

	public static ConfigBuilder defaultConfigBuilder () {
//		LOGGER.info(screen.width + "×" + screen.height);
		ConfigBuilder builder = ConfigBuilder.create()
						.setParentScreen(MinecraftClient.getInstance().currentScreen)
						.setTitle(new TranslatableText("notreddust.config.title"));
		builder.setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/redstone_block.png"));
		builder.setGlobalized(true);
		builder.transparentBackground();
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		ConfigCategory testing = builder.getOrCreateCategory(new TranslatableText("notreddust.category.main"));
		ColorMode currentColorMode = ColorMode.load();
		testing.addEntry(
						entryBuilder.startBooleanToggle(new TranslatableText("notreddust.enabled"), loadBoolean("Enabled"))
										.setDefaultValue(true).setSaveConsumer(bool -> saveBoolean(bool, "Enabled"))
										.setTooltip(new TranslatableText("notreddust.enabled.desc"))
										.build());
		testing.addEntry(
						entryBuilder
										.startEnumSelector(
														new TranslatableText("notreddust.mode"),
														ColorMode.class,
														ColorMode.load()
										)
										.setDefaultValue(ColorMode.OneColor)
										.setSaveConsumer(ColorMode::save)
										.setTooltip(new TranslatableText("notreddust.mode.desc"))
										.build()
		);
		SubCategoryBuilder modeSettingsCategory = entryBuilder
						.startSubCategory(new TranslatableText("notreddust.category.mode"))
						.setTooltip(new TranslatableText("notreddust.category.mode.desc"));
		if(currentColorMode == ColorMode.Rainbow){
			modeSettingsCategory.add(
							entryBuilder
											.startBooleanToggle(
															new TranslatableText("notreddust.rainbow.reverse"),
															loadBoolean("Reversed","Rainbow")
											)
											.setDefaultValue(false).setTooltip(new TranslatableText("notreddust.rainbow.reverse.desc"))
											.setSaveConsumer(bool -> saveBoolean(bool, "Reversed", "Rainbow")).build());
			modeSettingsCategory.add(
							entryBuilder
											.startBooleanToggle(
															new TranslatableText("notreddust.rainbow.power_brightness"),
															loadBoolean("PowerBrightness", "Rainbow")
											)
											.setDefaultValue(false).setTooltip(new TranslatableText("notreddust.rainbow.power_brightness.desc"))
											.setSaveConsumer(bool -> saveBoolean(bool, "PowerBrightness", "Rainbow")).build());
		} else if (currentColorMode == ColorMode.OneColor){
			modeSettingsCategory.add(
							entryBuilder
											.startColorField(
															new TranslatableText("notreddust.one.color"),
															loadColor("Color", "OneColor")
											)
											.setDefaultValue(0xff0000).setTooltip(new TranslatableText("notreddust.one.color.desc"))
											.setSaveConsumer(color -> saveColor(color, "Color","OneColor")).build());
		} else {
			modeSettingsCategory.add(
							entryBuilder
											.startEnumSelector(
															new TranslatableText("notreddust.gradient.mode"),
															LerpMode.class,
															LerpMode.load()
											)
											.setDefaultValue(LerpMode.RGB)
											.setSaveConsumer(LerpMode::save)
											.setTooltip(new TranslatableText("notreddust.gradient.mode.desc"))
											.build()
			);
			modeSettingsCategory.add(
							entryBuilder
											.startColorField(
															new TranslatableText("notreddust.gradient.start"),
															loadColor("Start", "Gradient")
											)
											.setDefaultValue(0xff0000).setTooltip(new TranslatableText("notreddust.gradient.start.desc"))
											.setSaveConsumer(color -> saveColor(color, "Start","Gradient")).build());
			modeSettingsCategory.add(
							entryBuilder
											.startColorField(
															new TranslatableText("notreddust.gradient.end"),
															loadColor("End", "Gradient")
											)
											.setDefaultValue(0x00ff00).setTooltip(new TranslatableText("notreddust.gradient.end.desc"))
											.setSaveConsumer(color -> saveColor(color, "End","Gradient")).build());
		}
		testing.addEntry(modeSettingsCategory.build());
		builder.setSavingRunnable(NotRedConfig::save);
		return builder;
	}

	// Whole save ——————————————————————————

	public static void save(){
		LOGGER.info("Saving settings.");
		try {
			NbtIo.write(configTag, configFile);
		} catch (IOException e) {
			LOGGER.error("Error occurred while saving config:", e);
		} finally {
			new RedstoneWireBlock(FabricBlockSettings.of(Material.METAL).hardness(4.0f));
		}
	}

	// boolean stuff ———————————————————————
	public static boolean loadBoolean(String name, String... groups){
		try {
			CompoundTag currentTag = configTag;
			for (String group : groups){
				currentTag = currentTag.getCompound(group);
			}
			return currentTag.getBoolean(name);
		} catch (NullPointerException e){
			LOGGER.error("Empty config or key ${name} is not present:", e);
			CompoundTag currentTag = defaultConfig;
			for (String group : groups){
				currentTag = currentTag.getCompound(group);
			}
			return currentTag.getBoolean(name);
		}
	}

	public static void saveBoolean(boolean state, String name, String... groups){
		CompoundTag currentTag = configTag;
		for (String group : groups){
			currentTag = currentTag.getCompound(group);
		}
		currentTag.putBoolean(name, state);
		if(groups.length > 0) {
			for (int i = groups.length - 1; i >= 0; i--) {
				CompoundTag parentTag = configTag;
				int j;
				for (j = 0; j < i; j++) {
					parentTag = parentTag.getCompound(groups[j]);
				}
				parentTag.put(groups[j], currentTag);
			}
		}
	}

	// int stuff ———————————————————————
	public static int loadInt(String name, String... groups){
		try {
			CompoundTag currentTag = configTag;
			for (String group : groups){
				currentTag = currentTag.getCompound(group);
			}
			return currentTag.getInt(name);
		} catch (NullPointerException e){
			LOGGER.error("Empty config or key ${name} is not present:", e);
			CompoundTag currentTag = defaultConfig;
			for (String group : groups){
				currentTag = currentTag.getCompound(group);
			}
			return currentTag.getInt(name);
		}
	}

	public static void saveInt(int state, String name, String... groups){
		CompoundTag currentTag = configTag;
		for (String group : groups){
			currentTag = currentTag.getCompound(group);
		}
		currentTag.putInt(name, state);
		if(groups.length > 0) {
			for (int i = groups.length - 1; i >= 0; i--) {
				CompoundTag parentTag = configTag;
				int j;
				for (j = 0; j < i; j++) {
					parentTag = parentTag.getCompound(groups[j]);
				}
				parentTag.put(groups[j], currentTag);
			}
		}
	}

	// Color Stuff —————————————————————————

	public static int loadColor(String name, String... groups){
		try {
			CompoundTag currentTag = configTag;
			for (String group : groups){
				currentTag = currentTag.getCompound(group);
			}
			int[] arr = currentTag.getIntArray(name).length == 0 ? new int[4] : currentTag.getIntArray(name);
			return ((arr[0]) << 24) + ((arr[1]) << 16) + ((arr[2]) << 8) + arr[3];
		} catch (NullPointerException e) {
			LOGGER.error("Empty config or key ${name} is not present:", e);
			CompoundTag currentTag = defaultConfig;
			for (String group : groups){
				currentTag = currentTag.getCompound(group);
			}
			int[] arr = currentTag.getIntArray(name).length == 0 ? new int[4] : currentTag.getIntArray(name);
			return ((arr[0]) << 24) + ((arr[1]) << 16) + ((arr[2]) << 8) + arr[3];
		}
	}
	public static void saveColor(int color, String name, String... groups){
		CompoundTag currentTag = configTag;
		for (String group : groups){
			currentTag = currentTag.getCompound(group);
		}
		currentTag.putIntArray(name, new int[]{(color >> 24) & 0xff, (color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff});
		if(groups.length > 0) {
			for (int i = groups.length - 1; i >= 0; i--) {
				CompoundTag parentTag = configTag;
				int j;
				for (j = 0; j < i; j++) {
					parentTag = parentTag.getCompound(groups[j]);
				}
				parentTag.put(groups[j], currentTag);
			}
		}
	}
}
