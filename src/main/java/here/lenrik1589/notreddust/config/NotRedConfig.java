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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NotRedConfig implements ModMenuApi {
	private static final Logger LOGGER = LogManager.getLogger("notreddust.config");
	public NotRedConfig () {
	}

	public String getModId() {
		return "notreddust";
	}

	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (screen) -> defaultConfigBuilder(screen).build();
	}

	enum Mode{
		Rainbow,
		OneColor
	}

	public static ConfigBuilder defaultConfigBuilder (Screen screen) {
//		LOGGER.info(screen.width + "×" + screen.height);
		ConfigBuilder builder = ConfigBuilder.create().setParentScreen(MinecraftClient.getInstance().currentScreen).setTitle(new TranslatableText("title.notreddust.config"));
		builder.setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/redstone_block.png"));
		builder.setGlobalized(true);
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		ConfigCategory testing = builder.getOrCreateCategory(new TranslatableText("category.notreddust.main"));
		testing
						.addEntry(
										entryBuilder
														.startDropdownMenu(
																		new TranslatableText("notreddust.mode"),
																		TopCellElementBuilder.of(
																						Mode.Rainbow,
																						(s) -> {
																							try {
																								return Mode.valueOf(s);
																							} catch (NumberFormatException var2) {
																								return null;
																							}
																						}
																						)
														)
														.setDefaultValue(Mode.Rainbow)
														.setSuggestionMode(false)
														.setSelections(Lists.newArrayList(Mode.Rainbow,Mode.OneColor))
														.setSaveConsumer(LOGGER::info)
														.setTooltip(new TranslatableText("notreddust.mode.desc"))
														.build()
						);//AFK
		testing.setCategoryBackground(new Identifier("minecraft","textures/block/red_stained_glass.png"));
		builder.transparentBackground();
		return builder;
	}
}
