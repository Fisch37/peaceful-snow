package de.fisch37.peacefulsnow.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HostileEntity.class)
public abstract class HostileEntityMixin {
    @Inject(method = "canSpawnInDark", at = @At("TAIL"), cancellable = true)
    private static void canSpawnInDarkInject(
            EntityType<? extends HostileEntity> type, ServerWorldAccess world,
            SpawnReason spawnReason, BlockPos pos, Random random,
            CallbackInfoReturnable<Boolean> cir
    ) {
        // This looks simple, but figuring out where to inject took me 5 hours
        if (
                cir.getReturnValue()
                && spawnReason == SpawnReason.NATURAL
                && world.getLightLevel(LightType.SKY, pos) > world.getDimension().monsterSpawnBlockLightLimit()
                && isSnowing(world, pos)
        ) cir.setReturnValue(false);
    }

    @Unique
    private static boolean isSnowing(ServerWorldAccess worldAccess, BlockPos pos) {
        var world = worldAccess.toServerWorld();
        return world.isRaining()
                && world.getBiome(pos).value().isCold(pos, pos.getY());
    }
}
