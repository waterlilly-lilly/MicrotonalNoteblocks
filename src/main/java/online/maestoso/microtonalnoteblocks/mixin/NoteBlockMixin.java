package online.maestoso.microtonalnoteblocks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import online.maestoso.microtonalnoteblocks.MicrotonalNoteblocks;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static online.maestoso.microtonalnoteblocks.MicrotonalNoteblocks.*;

@Mixin(NoteBlock.class)
public class NoteBlockMixin extends Block {
	@Shadow
	@Final
	public static IntProperty NOTE;
	/*@Inject(method = "appendProperties", at = @At("TAIL"))
	public void microtonalnoteblocks$injectAddProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
		builder.add(SCALE);
		builder.add(OCTAVE);
	}*/
	public NoteBlockMixin(Settings settings) {
		super(settings);
	}
	@ModifyVariable(method = "onSyncedBlockEvent", at = @At("STORE"))
	private float microtonalnoteblocks$adjustPitch(float f, @Local BlockState state, @Local(ordinal = 2) int i) {
		int scale = state.get(SCALE);
		int note = i + 24 * state.get(OCTAVE);
		return (float)Math.pow(2.0, (double)(note - scale) / (double) scale);
	}
	@ModifyVariable(method = "onUse", at = @At("STORE"), argsOnly = true)
	private BlockState microtonalnoteblocks$resetTuneAtMax(BlockState state) {
		if(state.get(NOTE) == 0 && state.get(OCTAVE) == 1) {
			state = state
					.with(NOTE, 0)
					.with(OCTAVE, 0);
		}
		if(state.get(SCALE) > 12 && state.get(NOTE) == 24 && state.get(OCTAVE) == 0) {
			state = state.with(OCTAVE, 1).with(NOTE, 0);
		}

		return state;
	}
	@Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;cycle(Lnet/minecraft/state/property/Property;)Ljava/lang/Object;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void microtonalnoteblocks$adjustScale(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if(player.getMainHandStack().isOf(MicrotonalNoteblocks.TUNING_FORK)) {
			if(state.get(SCALE) != 24) {
				state = state.cycle(SCALE);
			} else state = state.with(SCALE, 1);
			world.setBlockState(pos, state, 3);
			player.sendMessage(Text.translatable("item.microtonalnoteblocks.tuning_fork.change_tune", state.get(SCALE)), true);
			cir.setReturnValue(ActionResult.CONSUME);
		}
	}
}
