package com.astryxion.astryxions_miners_dream.item;

import com.astryxion.astryxions_miners_dream.AstryxionsMinersDreamMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(
                    ForgeRegistries.ITEMS,
                    AstryxionsMinersDreamMod.MODID
            );

    public static final RegistryObject<Item> MINERS_DREAM =
            ITEMS.register(
                    "miners_dream",
                    () -> new MinersDreamItem(new Item.Properties().stacksTo(16))
            );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
