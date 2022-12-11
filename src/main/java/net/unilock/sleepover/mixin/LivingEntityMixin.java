package net.unilock.sleepover.mixin;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

// REDUNDANT CODE:
// - posBed = pos
// - worldBed = world
// - ^^^ defined after setVars()
// - ijava.util.Objects.requireNonNull(world); - (idk what this does)
// - posBed.filter(worldBed::isChunkLoaded).ifPresent((pos) -> {...}) - i guess this checks
// 																	    whether the block is
// 																	    chunkloaded?
// - BlockState blockState = this.world.getBlockState(pos) - can't avoid
// - if (blockState.getBlock() instanceof BedBlock) - can't avoid

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	Optional<BlockPos> posBed;
	World worldBed;

	@Shadow
	public abstract Optional<BlockPos> getSleepingPosition();

	@Inject(method = "wakeUp", at = @At("HEAD"))
	private void setVars(CallbackInfo ci) {
		posBed = this.getSleepingPosition();
		worldBed = this.world;
	}

	@Inject(method = "wakeUp", at = @At("TAIL"))
	private void stayOccupiedIfPlayer(CallbackInfo ci) {
		System.out.println("[?] posBed = " + posBed);
		System.out.println("[?] world = " + worldBed);

		java.util.Objects.requireNonNull(world);

		System.out.println("[?] is block @ posBed chunkloaded?");
		posBed.filter(worldBed::isChunkLoaded).ifPresent((pos) -> {
			System.out.println("[?] yes!!");

			BlockState blockState = this.world.getBlockState(pos);
			System.out.println("[?] blockState = " + blockState);

			System.out.println("[?] is block @ posBed a bed? (hopefully!)");
			if (blockState.getBlock() instanceof BedBlock) {
				System.out.println("[?] yes!!");

				System.out.println("[?] is there a sleeping player @ posBed?");
				List<PlayerEntity> list = world.getEntitiesByClass(PlayerEntity.class, new Box(pos), LivingEntity::isSleeping);
				if (!list.isEmpty()) {
					System.out.println("[?] yes!!");

					System.out.println("[?] set occupied = true");
					this.world.setBlockState(pos, (BlockState) blockState.with(BedBlock.OCCUPIED, true), Block.NOTIFY_ALL);
				} else {
					System.out.println("[?] no!?");
				}
			} else {
				System.out.println("[?] no!?");
			}
		});
	}
}
