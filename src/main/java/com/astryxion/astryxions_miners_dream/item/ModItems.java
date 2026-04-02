package com.astryxion.astryxions_miners_dream.item;

import com.astryxion.astryxions_miners_dream.AstryxionsMinersDreamMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final Item MINERS_DREAM = register(
            "miners_dream",
            new MinersDreamItem(new Item.Properties().stacksTo(16))
    );

    private static Item register(String name, Item item) {
        return Registry.register(
                BuiltInRegistries.ITEM,
                new ResourceLocation(AstryxionsMinersDreamMod.MODID, name),
                item
        );
    }

    public static void register() {
    }
}
