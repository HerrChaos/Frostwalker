package chaos.frost.mixin;

import chaos.frost.block.ModBlocks;
import chaos.frost.block.custom.frostedMagma;
import net.minecraft.block.*;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FrostWalkerEnchantment.class)
public class FrostWalkerRefactor {

	@Inject(method = "getMaxLevel", at = @At("RETURN"), cancellable = true)
	private void onGetMaxLevel(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(4);
	}

	@Inject(method = "freezeWater", at = @At("HEAD"), cancellable = true)
	private static void init(LivingEntity entity, World world, BlockPos blockPos, int level, CallbackInfo info) {
        /*
		BlockState blockState = Blocks.FROSTED_ICE.getDefaultState();
		BlockState blockState4 = ModBlocks.FROSTED_MAGMA.getDefaultState();
		int i = Math.min(16, 2 + level);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int j = -1;
		if (playerchecker.isPlayerInBoat(entity)) {
			j = 0;
		}

        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-i, j, -i), blockPos.add(i, j, i))) {
            if (blockPos2.isWithinDistance(entity.getPos(), i)) {
                mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
                BlockState blockState2 = world.getBlockState(mutable);
                if (blockState2.isAir()) {
                    BlockState blockState3 = world.getBlockState(blockPos2);
                    if (level > 3 && (blockState3 == frostedMagma.getMeltedState() || blockState3.getBlock() == ModBlocks.FROSTED_MAGMA) && blockState.canPlaceAt(world, blockPos2) && world.canPlace(blockState, blockPos2, ShapeContext.absent())) {
                        world.setBlockState(blockPos2, blockState4);
                        world.scheduleBlockTick(blockPos2, ModBlocks.FROSTED_MAGMA, MathHelper.nextInt(entity.getRandom(), 60, 120));
                    }
                    if (( blockState3 == FrostedIceBlock.getMeltedState() || blockState3.getBlock() == Blocks.FROSTED_ICE || (blockState3.getFluidState().isIn(FluidTags.WATER) && (blockState3.getBlock() == Blocks.KELP || blockState3.getBlock() == Blocks.SEAGRASS))) && blockState.canPlaceAt(world, blockPos2) && world.canPlace(blockState, blockPos2, ShapeContext.absent())) {
                        world.setBlockState(blockPos2, blockState);
                        world.scheduleBlockTick(blockPos2, Blocks.FROSTED_ICE, MathHelper.nextInt(entity.getRandom(), 60, 120));
                    }
                }
            }
        }
         */

        BlockState FrostedIceState = Blocks.FROSTED_ICE.getDefaultState();
        BlockState FrostedMagmaState = ModBlocks.FROSTED_MAGMA.getDefaultState();
        int radius = Math.min(100, 2 + level);
        BlockPos startingPos = blockPos.add(0, -1, 0);

        for (BlockPos currentPos : BlockPos.iterate(startingPos.add(-radius, -radius, -radius), startingPos.add(radius, radius, radius))) {
            BlockPos abovePos = currentPos.up();
            if (shouldPlaceWaterBlock(startingPos, currentPos, radius) && world.isAir(abovePos)) {
                Block currantBlock = world.getBlockState(currentPos).getBlock();
                if ((currantBlock == Blocks.LAVA || currantBlock == ModBlocks.FROSTED_MAGMA) && level > 3) {
                    world.setBlockState(currentPos, FrostedMagmaState);
                    world.scheduleBlockTick(currentPos, ModBlocks.FROSTED_MAGMA, MathHelper.nextInt(entity.getRandom(), 60, 120));
                }

                if (currantBlock == Blocks.WATER || currantBlock == Blocks.FROSTED_ICE || (world.getBlockState(currentPos).getFluidState().isIn(FluidTags.WATER)
                        && (currantBlock == Blocks.KELP || currantBlock == Blocks.SEAGRASS))) {
                    world.setBlockState(currentPos, FrostedIceState);
                    world.scheduleBlockTick(currentPos, Blocks.FROSTED_ICE, MathHelper.nextInt(entity.getRandom(), 60, 120));
                }
            }
        }
        info.cancel();
    }

    private static boolean shouldPlaceWaterBlock(BlockPos centerPos, BlockPos currentPos, int radius) {
        int dx = currentPos.getX() - centerPos.getX();
        int dy = currentPos.getY() - centerPos.getY();
        int dz = currentPos.getZ() - centerPos.getZ();
        return dx * dx + dy * dy + dz * dz <= radius * radius;
    }
}