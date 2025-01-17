package net.rudahee.metallics_arts.data.custom_recipes.vials;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;
import net.rudahee.metallics_arts.data.enums.implementations.MetalTagEnum;
import net.rudahee.metallics_arts.setup.registries.ModItemsRegister;
import net.rudahee.metallics_arts.setup.registries.ModRecipeTypesRegister;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that control the large vial recipe. It's a custom recipe, so extends CustomRecipe.
 *
 * @author SteelCode Team
 * @since 1.5.1
 *
 * @see CustomRecipe
 * @see ItemStack
 * @see Ingredient
 * @see RecipeSerializer
 */
public class SmallVialItemRecipe extends CustomRecipe {

    private ItemStack finalResult = ItemStack.EMPTY;

    private static final Ingredient INGREDIENT_VIAL = Ingredient.of(ModItemsRegister.SMALL_VIAL.get());

    private static final List<Ingredient> INGREDIENT_NUGGET = new ArrayList<Ingredient>() {{
        for (Item metal: ModItemsRegister.ITEM_GEMS_NUGGET.values()) {
            add(Ingredient.of(metal.asItem()));
        }
        for (Item metal: ModItemsRegister.ITEM_METAL_NUGGET.values()) {
            if (!ModItemsRegister.ITEM_METAL_NUGGET.get("lead").getDescriptionId().equals(metal.getDescriptionId())
                    && !ModItemsRegister.ITEM_METAL_NUGGET.get("silver").getDescriptionId().equals(metal.getDescriptionId())
                    && !ModItemsRegister.ITEM_METAL_NUGGET.get("nickel").getDescriptionId().equals(metal.getDescriptionId())) {
                add(Ingredient.of(metal.asItem()));
            }
            add(Ingredient.of(Items.IRON_NUGGET.asItem()));
            add(Ingredient.of(Items.GOLD_NUGGET.asItem()));
        }
    }};

    /**
     * Constructor that receive the path of json recipe.
     *
     * @param location of the path.
     */
    public SmallVialItemRecipe(ResourceLocation location) {
        super(location);
    }

    public ItemStack auxIngredient = null;

    /**
     * Method in which the ingredients of the recipe are evaluated if they are correct and coincide with this one.
     * <p>
     * In this case, it is verified that the pips are correct, and that the quantity is correct.
     * If everything matches, it returns 'true' because the recipe exists and is correct.
     *
     * @param inventory the inventory in which the crafting is taking place.
     * @param level level in which crafting is taking place.
     *
     * @return boolean
     */
    @Override
    public boolean matches(@NotNull CraftingContainer inventory, @NotNull Level level) {
        boolean[] ingredients = {false, false};
        int cantMaxPep = 5;
        ItemStack actualIngredient;
        boolean hasVial = false;


        int[] metalsEnVial = new int[MetalTagEnum.values().length];
        Arrays.fill(metalsEnVial,0);

        int[] cantStorage = new int[MetalTagEnum.values().length];
        Arrays.fill(cantStorage,0);

        boolean[] addMetal = new boolean[MetalTagEnum.values().length];
        Arrays.fill(addMetal,false);

        for (MetalTagEnum metal : MetalTagEnum.values()) {
            cantStorage[metal.getIndex()] = (metal.getMaxAllomanticTicksStorage()/2)/cantMaxPep;
        }
        for(int i = 0; i < inventory.getContainerSize(); i++) {
            actualIngredient = inventory.getItem(i);
            if (actualIngredient != null && !actualIngredient.isEmpty()) {
                if (INGREDIENT_VIAL.test(inventory.getItem(i))) {
                    if (hasVial) {
                        return false;
                    } else {
                        hasVial = true;
                    }
                    if (actualIngredient.hasTag()){
                        for (MetalTagEnum metal : MetalTagEnum.values()) {
                            if (actualIngredient.getTag().contains(metal.getGemNameLower())){
                                metalsEnVial[metal.getIndex()] = actualIngredient.getTag().getInt(metal.getNameLower());
                            }
                        }
                    }
                    ingredients[0] = true;
                }
                auxIngredient = actualIngredient;

                if (INGREDIENT_NUGGET.stream().anyMatch(
                        ing -> ing.getItems()[0].getItem().getDescriptionId().equals(auxIngredient.getItem().getDescriptionId()))) {
                    for (MetalTagEnum metal : MetalTagEnum.values()) {
                        if ((actualIngredient.getItem().getDescriptionId()).equals("item.minecraft."+metal.getNameLower()+"_nugget")
                                ||(actualIngredient.getItem().getDescriptionId()).equals("item.metallics_arts."+metal.getNameLower()+"_nugget")){
                            if (addMetal[metal.getIndex()]){
                                return false;
                            }
                            if(metalsEnVial[metal.getIndex()] >= metal.getMaxAllomanticTicksStorage()/2){
                                return false;
                            }
                            addMetal[metal.getIndex()]=true;
                            ingredients[1] = true;
                        }
                    }
                }
            }
        }

        if (ingredients[0] && ingredients[1]){
            this.finalResult = new ItemStack(ModItemsRegister.SMALL_VIAL.get(),1);
            CompoundTag compoundNBT = new CompoundTag();
            for (MetalTagEnum metal : MetalTagEnum.values()){
                if (addMetal[metal.getIndex()]){
                    compoundNBT.putInt(metal.getNameLower(),metalsEnVial[metal.getIndex()]+cantStorage[metal.getIndex()]);
                }else{
                    compoundNBT.putInt(metal.getNameLower(),metalsEnVial[metal.getIndex()]);
                }
            }
            compoundNBT.putFloat("CustomModelData", 1);
            this.finalResult.setTag(compoundNBT);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Method that return a copy of the final result item of matches method.
     *
     * @param inventory the inventory in which the crafting is taking place.
     *
     * @return ItemStack
     */
    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer inventory) { //getCraftingResult
        return this.finalResult.copy();
    }

    /**
     * Method that define if its special recipe or not. In this case will be always true.
     *
     * @return boolean (always true)
     */
    @Override
    public boolean isSpecial() {
        return true;
    }

    /**
     * Method that return a result item by recipe.
     *
     * @return ItemStack
     */
    @Override
    public @NotNull ItemStack getResultItem() {
        return this.finalResult;
    }

    /**
     * This method evaluates if it is possible to craft the object in the different dimensions of the game.
     * <p>
     * Receive 2 parameters, but they don't use by nothing. The player always can craft vials, dimension no matters.
     *
     * @param num1 don't matter, because don't have any use.
     * @param num2 don't matter, because don't have any use.
     *
     * @return boolean (Always true)
     */
    @Override
    public boolean canCraftInDimensions(int num1, int num2) {
        return true;
    }

    /**
     * This method its getter for serializer. So only return a SmallVialItemRecipeSerializer.
     *
     * @return RecipeSerializer
     */
    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeTypesRegister.SMALL_VIAL_ITEM_RECIPE_SERIALIZER.get();
    }

    /**
     * Static class that controls Custom vial recipe serializer. Extend SimpleRecipeSerializer<LargeVialItemRecipe>
     *
     * @author SteelCode Team
     * @since 1.5.1
     *
     * @see SimpleRecipeSerializer
     * @see SmallVialItemRecipe
     */
    public static class Serializer extends SimpleRecipeSerializer<SmallVialItemRecipe> {

        /**
         * Constructor of the class. The only thing that constructor does its pass the Recipe class to superclass.
         */
        public Serializer() {
            super(SmallVialItemRecipe::new);
        }
    }
}
