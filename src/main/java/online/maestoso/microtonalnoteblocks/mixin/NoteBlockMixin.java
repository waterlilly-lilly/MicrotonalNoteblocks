package online.maestoso.microtonalnoteblocks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import online.maestoso.microtonalnoteblocks.MicrotonalNoteblocks;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.minecraft.block.NoteBlock.*;

@Mixin(NoteBlock.class)
public class NoteBlockMixin extends Block {
	private static final IntProperty SCALE = IntProperty.of("scale", 0, 24);
	private static final IntProperty NOTE = IntProperty.of("note", 0, 48);
	public NoteBlockMixin(Settings settings) {
		super(settings);
	}
	@ModifyArg(method = "<init>", at= @At(value = "INVOKE", target = "Lnet/minecraft/block/NoteBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"))
	public BlockState microtonalnoteblocks$setDefaultState(BlockState state) {
		return this.stateManager.getDefaultState()
				.with(INSTRUMENT, NoteBlockInstrument.HARP)
				.with(NOTE, 0)
				.with(SCALE, 12)
				.with(POWERED, false);
	}
	@Inject(method = "appendProperties", at = @At("TAIL"))
	public void microtonalnoteblocks$injectAddProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
		builder.add(SCALE);
	}
	@ModifyVariable(method = "onSyncedBlockEvent", at = @At("STORE"))
	private float microtonalnoteblocks$adjustPitch(float f, @Local BlockState state, @Local(ordinal = 2) int i) {
		int scale = state.get(SCALE);
		return (float)Math.pow(2.0, (double)(i - scale) / (double) scale);
	}
	@ModifyVariable(method = "onUse", at = @At("STORE"), argsOnly = true)
	private BlockState microtonalnoteblocks$resetTuneAtMax(BlockState state) {
		if(state.get(NOTE) == state.get(SCALE) * 2 + 1) {
			return state.with(NOTE, 0);
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
