package net.rudahee.metallics_arts.data.client_providers;

import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.rudahee.metallics_arts.setup.enums.extras.MetalMindData;
import net.rudahee.metallics_arts.setup.registries.ModBlock;
import net.rudahee.metallics_arts.setup.registries.ModItems;

import java.util.Arrays;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> recipesConsumer) {
        ModItems.ITEM_METAL_INGOT.forEach((name, item) -> {
            ShapelessRecipeBuilder.shapeless(item.getItem(), 9)
                    .requires(ModBlock.BLOCK_METAL_BLOCKS.get(name))
                    .unlockedBy("has_item", has(item))
                    .save(recipesConsumer, new ResourceLocation(item.getDescriptionId() + "_to_" + ModBlock.BLOCK_METAL_BLOCKS.get(name).getDescriptionId()));
        });

        ModBlock.BLOCK_METAL_BLOCKS.forEach((name, block) -> {
            ShapedRecipeBuilder.shaped(block)
                    .define('#', ModItems.ITEM_METAL_INGOT.get(name))
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .unlockedBy("has_block", has(block))
                    .save(recipesConsumer, new ResourceLocation(block.getDescriptionId() + "_to_" + ModItems.ITEM_METAL_INGOT.get(name).getDescriptionId()));
        });

        ModItems.ITEM_GEMS_BASE.forEach((name, item) -> {
            ShapelessRecipeBuilder.shapeless(item.getItem(), 9)
                    .requires(ModBlock.BLOCK_GEMS_BLOCKS.get(name))
                    .unlockedBy("has_item", has(item))
                    .save(recipesConsumer, new ResourceLocation(item.getDescriptionId() + "_to_" + ModBlock.BLOCK_GEMS_BLOCKS.get(name).getDescriptionId()));
        });

        ModBlock.BLOCK_GEMS_BLOCKS.forEach((name, block) -> {
            ShapedRecipeBuilder.shaped(block)
                    .define('#', ModItems.ITEM_GEMS_BASE.get(name))
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .unlockedBy("has_block", has(block))
                    .save(recipesConsumer, new ResourceLocation(block.getDescriptionId() + "_to_" + ModItems.ITEM_GEMS_BASE.get(name).getDescriptionId()));
        });

        ModItems.ITEM_METAL_INGOT.forEach((name, item) -> {
            ShapelessRecipeBuilder.shapeless(ModItems.ITEM_METAL_NUGGET.get(name), 9)
                    .requires(item)
                    .unlockedBy("has_block", has(item))
                    .save(recipesConsumer, new ResourceLocation(item.getDescriptionId() + "_to_" + ModItems.ITEM_METAL_NUGGET.get(name).getDescriptionId()));
        });

        ModItems.ITEM_METAL_NUGGET.forEach((name, item) -> {
            ShapedRecipeBuilder.shaped(ModItems.ITEM_METAL_INGOT.get(name))
                    .define('#', ModItems.ITEM_METAL_NUGGET.get(name))
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .unlockedBy("has_block", has(item))
                    .save(recipesConsumer, new ResourceLocation(item.getDescriptionId() + "_to_" + ModItems.ITEM_METAL_INGOT.get(name).getDescriptionId()));
        });

        ModItems.ITEM_GEMS_BASE.forEach((name, item) -> {
            ShapelessRecipeBuilder.shapeless(ModItems.ITEM_GEMS_NUGGET.get(name), 9)
                    .requires(item)
                    .unlockedBy("has_block", has(item))
                    .save(recipesConsumer, new ResourceLocation(item.getDescriptionId() + "_to_" + ModItems.ITEM_GEMS_NUGGET.get(name).getDescriptionId()));
        });

        ModItems.ITEM_GEMS_NUGGET.forEach((name, item) -> {
            ShapedRecipeBuilder.shaped(ModItems.ITEM_GEMS_BASE.get(name))
                    .define('#', ModItems.ITEM_GEMS_NUGGET.get(name))
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .unlockedBy("has_block", has(item))
                    .save(recipesConsumer, new ResourceLocation(item.getDescriptionId() + "_to_" + ModItems.ITEM_GEMS_BASE.get(name).getDescriptionId()));
        });

        ModBlock.BLOCK_METAL_ORES.forEach((name,block) -> {
            CookingRecipeBuilder.cooking(Ingredient.of(block),ModItems.ITEM_METAL_INGOT.get(name),0.5f,100, IRecipeSerializer.SMELTING_RECIPE)
                    .unlockedBy("has_block",has(block))
                    .save(recipesConsumer, new ResourceLocation(block.getDescriptionId() + "_to_"+ ModItems.ITEM_METAL_INGOT.get(name).getDescriptionId() + "_furnace"));
        });

        ModBlock.BLOCK_METAL_ORES.forEach((name,block) -> {
            CookingRecipeBuilder.blasting(Ingredient.of(block),ModItems.ITEM_METAL_INGOT.get(name),0.5f,100)
                    .unlockedBy("has_block",has(block))
                    .save(recipesConsumer, new ResourceLocation(block.getDescriptionId() + "_to_"+ ModItems.ITEM_METAL_INGOT.get(name).getDescriptionId() + "_blasting"));

        });

        Arrays.asList(MetalMindData.values()).forEach(object -> {
            Item item1,item2;
            if (object.isGems()){
                item1 = ModBlock.BLOCK_GEMS_BLOCKS.get(object.getFirstMetal()).asItem();
                item2 = ModBlock.BLOCK_GEMS_BLOCKS.get(object.getSecondMetal()).asItem();
            }else if (object.isVanilla()){
                if (object.getSecondMetal()=="iron")
                    item1 = Items.IRON_BLOCK;
                else
                    item1 = Items.GOLD_BLOCK;
                item2 = ModBlock.BLOCK_METAL_BLOCKS.get(object.getFirstMetal()).asItem();
            }else{
               item1 = ModBlock.BLOCK_METAL_BLOCKS.get(object.getFirstMetal()).asItem();
               item2 = ModBlock.BLOCK_METAL_BLOCKS.get(object.getSecondMetal()).asItem();
            }

            ShapedRecipeBuilder.shaped(object.getBand())
                    .define('#',item1)
                    .define('x',item2)
                    .pattern("xxx")
                    .pattern("# x")
                    .pattern("###")
                    .unlockedBy("has_item",has(object.getBand()))
                    .save(recipesConsumer,new ResourceLocation("alomantic_arts_band_"+object.getFirstMetal()+"_"+object.getSecondMetal()));

            ShapedRecipeBuilder.shaped(object.getBand())
                    .define('#',item2)
                    .define('x',item1)
                    .pattern("xxx")
                    .pattern("# x")
                    .pattern("###")
                    .unlockedBy("has_item",has(object.getBand()))
                    .save(recipesConsumer,new ResourceLocation("alomantic_arts_band_"+object.getFirstMetal()+"_"+object.getSecondMetal()+"2"));


/*
            ShapedRecipeBuilder.shaped(object.getRing())
                    .define('#',ModItems.ITEM_METAL_INGOT.get(object.getFirstMetal()))
                    .define('x',ModItems.ITEM_METAL_INGOT.get(object.getSecondMetal()))
                    .pattern("xxx")
                    .pattern("# x")
                    .pattern("###")
                    .unlockedBy("has_item",has(object.getBand()))
                    .save(recipesConsumer,new ResourceLocation("alomantic_arts_ring_"+object.getFirstMetal()+"_"+object.getSecondMetal()));

            ShapedRecipeBuilder.shaped(object.getRing())
                    .define('x',ModItems.ITEM_METAL_INGOT.get(object.getFirstMetal()))
                    .define('#',ModItems.ITEM_METAL_INGOT.get(object.getSecondMetal()))
                    .pattern("xxx")
                    .pattern("# x")
                    .pattern("###")
                    .unlockedBy("has_item",has(object.getRing()))
                    .save(recipesConsumer,new ResourceLocation("alomantic_arts_ring_"+object.getFirstMetal()+"_"+object.getSecondMetal()+"2"));
*/

        });






        /*
        ModItems.ITEM_GEMS_NUGGET.forEach((name, item) -> {
            ShapedRecipeBuilder.shaped(ModItems.ITEM_GEMS_BASE.get(name))
                    .define('#', ModItems.ITEM_GEMS_NUGGET.get(name))
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .unlockedBy("has_block", has(item))
                    .save(recipesConsumer, new ResourceLocation(item.getDescriptionId() + "_to_" + ModItems.ITEM_GEMS_BASE.get(name).getDescriptionId()));
        });
         */


/*
        ShapedRecipeBuilder.shaped(ModItems.BandAluminumDuralumin.get())
                .define('#',ModBlock.BLOCK_METAL_BLOCKS.get("aluminum").asItem())
                .define('x',ModBlock.BLOCK_METAL_BLOCKS.get("duralumin").asItem())
                .pattern("xxx").pattern("# x").pattern("###")
                .unlockedBy("has_item",has(ModItems.BandAluminumDuralumin.get()))
                .save(recipesConsumer,new ResourceLocation("alomantic_arts_band_aluminum_duralumin"));

        ShapedRecipeBuilder.shaped(ModItems.BandAluminumDuralumin.get())
                .define('x',ModBlock.BLOCK_METAL_BLOCKS.get("aluminum").asItem())
                .define('#',ModBlock.BLOCK_METAL_BLOCKS.get("duralumin").asItem())
                .pattern("xxx").pattern("# x").pattern("###")
                .unlockedBy("has_item",has(ModItems.BandAluminumDuralumin.get()))
                .save(recipesConsumer,new ResourceLocation("alomantic_arts_band_aluminum_duralumin2"));


*/

       //BrewingRecipeRegistry.addRecipe(Ingredient.of(() -> ModItems.VIAL.get() ),Ingredient.of(ModItems.ITEM_METAL_NUGGET.get("steel").asItem()),ModItems.VIAL.get().getDefaultInstance());

    }


}