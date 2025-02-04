package globalplayermaterials.jakedit;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class StatePosition {
	public BlockState blockState;
	public BlockPos blockPos;

	public StatePosition(BlockState blockState, BlockPos blockPos) {
		this.blockState = blockState;
		this.blockPos = blockPos;
	}
}
