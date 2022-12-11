package net.unilock.sleepover.mixin;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.BedBlock.OCCUPIED;
import static net.minecraft.block.BedBlock.PART;

// REDUNDANT CODE:
// - wakeVillager()
// - if (!world.isClient) - likely impossible to avoid
// - if (state.get(BedBlock.PART) != BedPart.HEAD)
// - if ((Boolean) state.get(BedBlock.OCCUPIED)) - probably also impossible to avoid
//
// might as well just override the whole damn method!!

@Mixin(BedBlock.class)
public abstract class BedBlockMixin extends HorizontalFacingBlock {
	public BedBlockMixin(Settings settings) {
		super(settings);
	}

	@Shadow
	protected abstract boolean wakeVillager(World world, BlockPos pos);

	@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
	private void bypassOccupied(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if (!world.isClient) {
			System.out.println("[!] using bed");

			System.out.println("[!] blockstate = " + state);

			if (state.get(PART) != BedPart.HEAD) {
				pos = pos.offset((Direction) state.get(FACING));
				state = world.getBlockState(pos);
				if (!state.isOf(this)) {
					cir.setReturnValue(ActionResult.CONSUME);
				}
			}

			if ((Boolean) state.get(OCCUPIED)) {
				System.out.println("[!] bed is occupied");

				if (wakeVillager(world, pos)) {
					System.out.println("[!] villager = true");
					System.out.println("[!] wake up jackass!!");
				}

				System.out.println("[!] sleeping anyway");
				player.trySleep(pos).ifLeft((reason) -> {
					if (reason.getMessage() != null) {
						player.sendMessage(reason.getMessage(), true);
					}
				});
				System.out.println("[!] hopefully that worked!!");
			}
		}
	}
}
