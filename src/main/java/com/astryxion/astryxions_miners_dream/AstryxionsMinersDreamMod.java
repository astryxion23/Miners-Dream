package com.astryxion.astryxions_miners_dream;

import com.astryxion.astryxions_miners_dream.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstryxionsMinersDreamMod implements ModInitializer {

    public static final String MODID = "astryxions_miners_dream";
    private static final Logger LOGGER = LoggerFactory.getLogger(AstryxionsMinersDreamMod.class);

    @Override
    public void onInitialize() {
        ModItems.register();
        ModRecipes.registerEvents();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            ItemStack stack = new ItemStack(ModItems.MINERS_DREAM);
            entries.getDisplayStacks().add(stack);
            entries.getSearchTabStacks().add(stack);
        });

        LOGGER.info("Astryxion's Miner's Dream loaded successfully.");
    }
}
