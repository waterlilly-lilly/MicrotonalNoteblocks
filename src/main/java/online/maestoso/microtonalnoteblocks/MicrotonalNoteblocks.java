package online.maestoso.microtonalnoteblocks;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import virtuoel.statement.api.StateRefresher;

public class MicrotonalNoteblocks implements ModInitializer {
	public static final IntProperty SCALE = IntProperty.of("scale", 1, 24);
	public static final IntProperty OCTAVE = IntProperty.of("octave", 0, 1);
	public static Item TUNING_FORK = new Item(new QuiltItemSettings().maxCount(1));
	public void onInitialize(ModContainer mod) {
		StateRefresher.INSTANCE.addBlockProperty(Blocks.NOTE_BLOCK, SCALE, 12);
		StateRefresher.INSTANCE.addBlockProperty(Blocks.NOTE_BLOCK, OCTAVE, 0);
		StateRefresher.INSTANCE.reorderBlockStates();
		Registry.register(Registries.ITEM, new Identifier("microtonalnoteblocks:tuning_fork"), TUNING_FORK);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS_AND_UTILITIES).register(content -> content.addItem(TUNING_FORK));
	}
}
