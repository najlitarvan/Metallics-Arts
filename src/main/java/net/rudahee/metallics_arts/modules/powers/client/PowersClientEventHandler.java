package net.rudahee.metallics_arts.modules.powers.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.rudahee.metallics_arts.data.network.ChangeEmotionPacket;
import net.rudahee.metallics_arts.data.network.PullAndPushPacket;
import net.rudahee.metallics_arts.modules.client.ClientUtils;
import net.rudahee.metallics_arts.modules.client.GUI.AllomanticMetalOverlay;
import net.rudahee.metallics_arts.modules.client.GUI.FeruchemyMetalSelector;
import net.rudahee.metallics_arts.modules.client.GUI.MetalSelector;
import net.rudahee.metallics_arts.modules.client.KeyInit;
import net.rudahee.metallics_arts.modules.data_player.IDefaultInvestedPlayerData;
import net.rudahee.metallics_arts.modules.data_player.InvestedCapability;
import net.rudahee.metallics_arts.modules.powers.helpers.IronAndSteelHelpers;
import net.rudahee.metallics_arts.setup.enums.extras.MetalsNBTData;
import net.rudahee.metallics_arts.setup.network.ModNetwork;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PowersClientEventHandler {


    private final Minecraft mc = Minecraft.getInstance();

    private final Set<Entity> metal_entities = new HashSet<>();
    private final Set<PlayerEntity> nearby_allomancers = new HashSet<>();
    private final Set<MetalBlockHelpers> metal_blobs = new HashSet<>();


    private final int tickOffset = 0;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            PlayerEntity player = this.mc.player;

            if (player != null && player instanceof PlayerEntity) {
                player.getCapability(InvestedCapability.PLAYER_CAP).ifPresent(
                    playerCapability -> {
                        if (playerCapability.isInvested()) {

                            this.redoLists(player, playerCapability);

                            /** LEFT CLICK (ATTACK) */

                            if (this.mc.options.keyAttack.isDown()) {
                                RayTraceResult trace = ClientUtils.getMouseOverExtended(20F * 1.5F);

                                /***********************************
                                 * DO CLICK AN ENTITY WITH  - ZINC -
                                 ***********************************/
                                if (playerCapability.isBurning(MetalsNBTData.ZINC)) {
                                    Entity entity;
                                    if ((trace != null) && (trace.getType() == RayTraceResult.Type.ENTITY)) {
                                        entity = ((EntityRayTraceResult) trace).getEntity();
                                        if (entity instanceof CreatureEntity) {
                                            ModNetwork.sendToServer(new ChangeEmotionPacket(entity.getId(), true));
                                        }
                                    }
                                }
                                /***********************************
                                 * DO CLICK AN ENTITY WITH  - IRON -
                                 ***********************************/
                                if (playerCapability.isBurning(MetalsNBTData.IRON)) {
                                    BlockPos blockPosition = ((BlockRayTraceResult) trace).getBlockPos();
                                    if (IronAndSteelHelpers.isBlockStateMetal(this.mc.level.getBlockState(blockPosition))) {
                                        ModNetwork.sendToServer(new PullAndPushPacket(blockPosition, Math.round(IronAndSteelHelpers.PULL * IronAndSteelHelpers.getMultiplier(player))));
                                    }
                                }
                            }

                            /** RIGHT CLICK (USE) */

                            if (this.mc.options.keyUse.isDown()) {
                                RayTraceResult trace = ClientUtils.getMouseOverExtended(20F * 1.5F);

                                /***********************************
                                 * DO CLICK AN ENTITY WITH  - BRASS -
                                 ***********************************/
                                if (playerCapability.isBurning(MetalsNBTData.BRASS)) {
                                    Entity entity;
                                    if ((trace != null) && (trace.getType() == RayTraceResult.Type.ENTITY)) {
                                        entity = ((EntityRayTraceResult) trace).getEntity();
                                        if (entity instanceof CreatureEntity) {
                                            ModNetwork.sendToServer(new ChangeEmotionPacket(entity.getId(), false));
                                        }
                                    }
                                }

                                /***********************************
                                 * DO CLICK AN ENTITY WITH  - STEEL -
                                 ***********************************/

                                if (playerCapability.isBurning(MetalsNBTData.STEEL)) {
                                    Entity entity;
                                    if (trace != null) {
                                        if (trace.getType() == RayTraceResult.Type.ENTITY && IronAndSteelHelpers.isEntityMetal(((EntityRayTraceResult) trace).getEntity())) {
                                            //ModNetwork.sendToServer(new PullAndPushPacket(((EntityRayTraceResult) trace).getEntity().getId(), Math.round(IronAndSteelHelpers.PUSH * IronAndSteelHelpers.getMultiplier(player))));
                                        } if (trace.getType() == RayTraceResult.Type.BLOCK) {
                                            BlockPos blockPos = ((BlockRayTraceResult) trace).getBlockPos();
                                            if (IronAndSteelHelpers.isBlockStateMetal(this.mc.level.getBlockState(blockPos))) {
                                                ModNetwork.sendToServer(new PullAndPushPacket(blockPos, Math.round(IronAndSteelHelpers.PUSH * IronAndSteelHelpers.getMultiplier(player))));
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    });
            }
        }

    }

    private void redoLists(PlayerEntity player, IDefaultInvestedPlayerData playerCapability) {

        if (this.tickOffset == 0) {
            // Populate the metal lists
            this.metal_blobs.clear();
            this.metal_entities.clear();
            if (playerCapability.isBurning(MetalsNBTData.IRON) || playerCapability.isBurning(MetalsNBTData.STEEL)) {
                int max = 32;
                BlockPos negative = player.blockPosition().offset(-max, -max, -max);
                BlockPos positive = player.blockPosition().offset(max, max, max);

                // Add metal entities to metal list
                this.metal_entities.addAll(
                        player.level.getEntitiesOfClass(Entity.class, new AxisAlignedBB(negative, positive), e -> IronAndSteelHelpers.isEntityMetal(e) && !e.equals(player)));

                // Add metal blobs to metal list
                Stream<BlockPos> blocks = BlockPos.betweenClosedStream(negative, positive);
                blocks.filter(bp -> IronAndSteelHelpers.isBlockStateMetal(player.level.getBlockState(bp))).forEach(bp -> {
                    Set<MetalBlockHelpers> matches = this.metal_blobs.stream().filter(mbl -> mbl.isMatch(bp)).collect(Collectors.toSet());
                    switch (matches.size()) {
                        case 0: // new blob
                            this.metal_blobs.add(new MetalBlockHelpers(bp));
                            break;
                        case 1: // add to existing blob
                            matches.stream().findAny().get().add(bp);
                            break;
                        default: // this block serves as a bridge between (possibly many) existing blobs
                            this.metal_blobs.removeAll(matches);
                            MetalBlockHelpers mbb = matches.stream().reduce(null, MetalBlockHelpers::merge);
                            mbb.add(bp);
                            this.metal_blobs.add(mbb);
                            break;

                    }

                });

            }
            // Populate our list of nearby allomancy users
            this.nearby_allomancers.clear();
        }
    }


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS) {
            acceptInput();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent event) {
        AllomanticMetalOverlay.drawMetalOverlay(event.getMatrixStack());
        if (KeyInit.allomancy.isDown()){
            PlayerEntity player = this.mc.player;
            if (this.mc.screen == null){
                if (player==null || !this.mc.isWindowActive()){
                    return;
                }
                player.getCapability(InvestedCapability.PLAYER_CAP).ifPresent(data ->{

                    // EL SUICIDIO ES UNA OPCION
                    int num_powers = data.getAllomanticPowerCount();
                    if (num_powers == 0){
                        return;
                    }
                    else {
                        this.mc.setScreen(new MetalSelector());
                    }
                });
            }
        }
        if (KeyInit.feruchemic.isDown()){
            PlayerEntity player = this.mc.player;
            if (this.mc.screen == null){
                if (player==null || !this.mc.isWindowActive()){
                    return;
                }
                player.getCapability(InvestedCapability.PLAYER_CAP).ifPresent(data ->{

                    // EL SUICIDIO ES UNA OPCION
                    int num_powers = data.getFeruchemicPowerCount();
                    if (num_powers == 0){
                        return;
                    }
                    else {
                        this.mc.setScreen(new FeruchemyMetalSelector());
                    }
                });
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onMouseInput(final InputEvent.MouseInputEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS) {
            acceptInput();
        }
    }

    /**
     * Handles either mouse or button presses for the mod's keybinds
     */
    private void acceptInput() {

    }



    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        PlayerEntity player = this.mc.player;
        if (player == null || !player.isAlive() || this.mc.options.getCameraType().isMirrored()) {
            return;
        }

        player.getCapability(InvestedCapability.PLAYER_CAP).ifPresent(playerCap -> {

            if (!playerCap.isInvested()) {
                return;
            }

            MatrixStack matrixStack = event.getMatrixStack();

            matrixStack.pushPose();

            Vector3d view = this.mc.cameraEntity.getEyePosition(event.getPartialTicks());

            matrixStack.translate(-view.x, -view.y, -view.z);

            // TODO investigate depreciation
            RenderSystem.pushMatrix();
            RenderSystem.multMatrix(matrixStack.last().pose());
            RenderSystem.disableTexture();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.enableBlend();


            double dist = 1;
            double yaw = ((this.mc.player.yRot + 90) * Math.PI) / 180;
            double pitch = ((this.mc.player.xRot + 90) * Math.PI) / 180;

            Vector3d playerVector = view.add(MathHelper.sin((float) pitch) * MathHelper.cos((float) yaw) * dist, MathHelper.cos((float) pitch) * dist - 0.35,
                    MathHelper.sin((float) pitch) * MathHelper.sin((float) yaw) * dist);


            /***********************************
             * DRAW LINES  - STEEL & IRON -
             ***********************************/

            if (playerCap.isBurning(MetalsNBTData.IRON) || playerCap.isBurning(MetalsNBTData.STEEL)) {
                for (Entity entity :this.metal_entities) {
                    ClientUtils.drawMetalLine(playerVector, entity.position(), 2f, 0, 0.6f, 1f);
                }

                for (MetalBlockHelpers mb : this.metal_blobs) {
                    ClientUtils.drawMetalLine(playerVector, mb.getCenter(), MathHelper.clamp(0.3F + mb.size() * 0.4F, 0.5F, 7.5F), 0F, 0.6F, 1F);
                }
            }




            RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableTexture();
            RenderSystem.popMatrix();
            matrixStack.popPose();
        });


    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onSound(PlaySoundEvent event) {

    }
}