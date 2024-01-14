package net.unilock.sleepover.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@WrapOperation(method = "method_18404", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private boolean stayOccupiedIfPlayer(World instance, BlockPos pos, BlockState state, int flags, Operation<Boolean> original) {
		if (!instance.getEntitiesByClass(PlayerEntity.class, new Box(pos), LivingEntity::isSleeping).isEmpty()) {
			return true;
		}

		return original.call(instance, pos, state, flags);
	}
}
