package io.github.darkkronicle.advancedchatcore.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends Entity {

    @Shadow @Final protected MinecraftClient client;

    @Shadow public float nauseaIntensity;

    public MixinClientPlayerEntity(EntityType<?> type, World world) {
        super(type, world);
    }
}
