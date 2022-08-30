package net.rudahee.metallics_arts.modules.powers.helpers;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.rudahee.metallics_arts.modules.data_player.InvestedCapability;
import net.rudahee.metallics_arts.setup.enums.extras.MetalsNBTData;

import java.util.Random;

public class CopperAndBronzeHelpers {

    public static void BronzeAiEntityManipulation(AxisAlignedBB axisAlignedBB, PlayerEntity player, World world) {
        world.getEntitiesOfClass(MobEntity.class, axisAlignedBB).forEach(entity -> {
            entity.goalSelector.removeGoal(entity.goalSelector.getRunningGoals().findFirst().orElse(null));
            entity.getLookControl().setLookAt(player.position().x, player.position().y, player.position().z);
            entity.getMoveControl().setWantedPosition(player.position().x-0.5F, player.position().y, player.position().z-0.5F, 1.2f);
        });
    }

    public static void BronzeEnhancedAiEntityManipulation(AxisAlignedBB axisAlignedBB, PlayerEntity player, World world) {
        world.getEntitiesOfClass(MobEntity.class, axisAlignedBB).forEach(entity -> {
            entity.goalSelector.removeGoal(entity.goalSelector.getRunningGoals().findFirst().orElse(null));

            entity.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 100, 2, true, true, true));
            entity.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 100, 1, true, true, true));
            entity.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 100, 1, true, true, true));

            entity.getLookControl().setLookAt(player.position().x, player.position().y, player.position().z);
            entity.getMoveControl().setWantedPosition(player.position().x-0.5F, player.position().y, player.position().z-0.5F, 1.6f);
        });
    }


    public static void CopperAiEntityManipulation(AxisAlignedBB axisAlignedBB, PlayerEntity player, World world) {
        world.getEntitiesOfClass(MobEntity.class, axisAlignedBB).forEach(entity -> {
            if (!(entity instanceof WitherEntity) && !(entity instanceof EnderDragonEntity)) {
                entity.goalSelector.removeGoal(entity.goalSelector.getRunningGoals().findFirst().orElse(null));
                entity.goalSelector.addGoal(1, new LookRandomlyGoal(entity));
                if (entity.getTarget() == player) {
                    entity.setAggressive(false);
                    entity.setTarget(null);
                }
            }
        });
    }

    public static void CopperEnhancedAiEntityManipulation(AxisAlignedBB axisAlignedBB, PlayerEntity player, World world) {
        world.getEntitiesOfClass(MobEntity.class, axisAlignedBB).forEach(entity -> {
            if (!(entity instanceof WitherEntity) && !(entity instanceof EnderDragonEntity)) {
                entity.goalSelector.removeGoal(entity.goalSelector.getRunningGoals().findFirst().orElse(null));
                entity.goalSelector.addGoal(1, new LookRandomlyGoal(entity));
                if (entity.getTarget() == player) {
                    entity.setAggressive(false);
                    entity.setTarget(null);
                }
            }
            entity.addEffect(new EffectInstance(Effects.WEAKNESS, 100, 2, true, true, true));
            entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 100, 2, true, true, true));
        });
    }

    public static void DontSpawnPhantoms(PlayerEntity player, AxisAlignedBB axisAlignedBB, World world) {
        world.getEntitiesOfClass(PhantomEntity.class, axisAlignedBB).forEach(entity -> {
            if (entity instanceof PhantomEntity) {
                if (entity.getTarget() == player) {
                    entity.setAggressive(false);
                    entity.setTarget(null);
                }
            }
        });
    }

    public static void SpawnPhamtonsWithFireResistance(PlayerEntity player, World world) {

        PhantomEntity phantom = new PhantomEntity(EntityType.PHANTOM, world);
        phantom.setPos(player.position().x,player.position().y + 4, player.position().z);
        phantom.setTarget(player);
        phantom.setAggressive(true);
        phantom.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 240, 2, true, true, true));
        world.addFreshEntity(phantom);
    }



}
