package com.astryxion.astryxions_miners_dream;

import com.astryxion.astryxions_miners_dream.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensures the Miner's Dream shaped recipe exists if the datapack JSON failed to load (e.g. strict parse).
 */
public final class ModRecipes {

    public static final ResourceLocation MINERS_DREAM_RECIPE_ID =
            ResourceLocation.fromNamespaceAndPath(AstryxionsMinersDreamMod.MODID, "miners_dream");

    private ModRecipes() {}

    public static void registerEvents() {
        NeoForge.EVENT_BUS.addListener(ServerStartedEvent.class, event -> injectMinersDreamIfMissing(event.getServer()));
        NeoForge.EVENT_BUS.addListener(OnDatapackSyncEvent.class, event -> {
            if (event.getPlayer() == null) {
                injectMinersDreamIfMissing(event.getPlayerList().getServer());
            }
        });
    }

    private static void injectMinersDreamIfMissing(MinecraftServer server) {
        RecipeManager manager = server.getRecipeManager();
        if (manager.byKey(MINERS_DREAM_RECIPE_ID).isPresent()) {
            return;
        }
        if (manager.byKey(ResourceLocation.withDefaultNamespace("oak_planks")).isEmpty()) {
            return;
        }
        Map<Character, Ingredient> key = new HashMap<>();
        key.put('C', Ingredient.of(Items.CACTUS));
        key.put('R', Ingredient.of(Items.REDSTONE_BLOCK));
        key.put('G', Ingredient.of(Items.GUNPOWDER));
        ShapedRecipePattern pattern = ShapedRecipePattern.of(key, "CCC", "RRR", "GGG");
        ShapedRecipe recipe = new ShapedRecipe(
                "",
                CraftingBookCategory.EQUIPMENT,
                pattern,
                new ItemStack(ModItems.MINERS_DREAM.get(), 1)
        );
        ArrayList<RecipeHolder<?>> merged = new ArrayList<>(manager.getRecipes());
        merged.add(new RecipeHolder<>(MINERS_DREAM_RECIPE_ID, recipe));
        manager.replaceRecipes(merged);
    }
}
