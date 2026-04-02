package com.astryxion.astryxions_miners_dream.item;

import com.astryxion.astryxions_miners_dream.AstryxionsMinersDreamMod;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AstryxionsMinersDreamMod.MODID);

    public static final DeferredItem<Item> MINERS_DREAM = ITEMS.register(
            "miners_dream",
            () -> new MinersDreamItem(new Item.Properties().stacksTo(16))
    );

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
