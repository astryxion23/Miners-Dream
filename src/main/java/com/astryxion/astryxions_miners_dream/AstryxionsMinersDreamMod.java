package com.astryxion.astryxions_miners_dream;

import com.astryxion.astryxions_miners_dream.item.MinersDreamTunnelScheduler;
import com.astryxion.astryxions_miners_dream.item.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(AstryxionsMinersDreamMod.MODID)
public class AstryxionsMinersDreamMod {

    public static final String MODID = "astryxions_miners_dream";
    private static final Logger LOGGER = LoggerFactory.getLogger(AstryxionsMinersDreamMod.class);

    public AstryxionsMinersDreamMod(IEventBus modEventBus) {
        ModItems.register(modEventBus);
        ModRecipes.registerEvents();
        MinersDreamTunnelScheduler.register();
        modEventBus.addListener(this::onBuildCreativeModeTabContents);

        LOGGER.info("Astryxion's Miner's Dream loaded successfully.");
    }

    private void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            ItemStack stack = new ItemStack(ModItems.MINERS_DREAM.get(), 1);
            event.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
