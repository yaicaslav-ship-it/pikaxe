package com.example.pickthrough.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    /**
     * После обычного определения цели прицела: если мы целимся в игрока
     * и держим кирку, ищем блок за ним (луч игнорирует сущности).
     */
    @Inject(method = "updateCrosshairTarget", at = @At("RETURN"))
    private void pickthrough$ignorePlayers(float tickDelta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) return;

        if (!(client.crosshairTarget instanceof EntityHitResult entityHit)) return;
        if (!(entityHit.getEntity() instanceof PlayerEntity)) return;
        if (!player.getMainHandStack().isIn(ItemTags.PICKAXES)) return;

        HitResult blockHit = player.raycast(player.getBlockInteractionRange(), tickDelta, false);
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            client.crosshairTarget = blockHit;
        }
    }
}
