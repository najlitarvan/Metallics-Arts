package net.rudahee.metallics_arts.modules.custom_items.coins;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.rudahee.metallics_arts.data.enums.implementations.MetalTagEnum;
import net.rudahee.metallics_arts.data.player.IInvestedPlayerData;
import net.rudahee.metallics_arts.modules.custom_projectiles.CopperProjectile;
import net.rudahee.metallics_arts.modules.error_handling.exceptions.PlayerException;
import net.rudahee.metallics_arts.utils.CapabilityUtils;

public class CopperCoin extends Item {

    public int damage;

    public int cooldown;

    public CopperCoin(Properties properties, int damage, int cooldown) {
        super(properties);
        this.damage = damage;
        this.cooldown = cooldown;

    }



    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack itemstack = player.getItemInHand(interactionHand);

      player.getCooldowns().addCooldown(this, cooldown);
      // level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));


      if (!level.isClientSide) {
          IInvestedPlayerData playerCapability;

          try {
              playerCapability = CapabilityUtils.getCapability(player);
          } catch (PlayerException ex) {
              ex.printCompleteLog();
              return InteractionResultHolder.fail(itemstack);
          }

          if (playerCapability.isBurning(MetalTagEnum.STEEL)) {

              CopperProjectile coin = new CopperProjectile(level, player, damage);

              coin.setItem(itemstack);
              coin.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0F, 1.0F);
              level.addFreshEntity(coin);

              player.awardStat(Stats.ITEM_USED.get(this));
              if (!player.getAbilities().instabuild) {
                  itemstack.shrink(1);
              }
          } else {
              return InteractionResultHolder.fail(itemstack);
          }

      }

      return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
   }

}
