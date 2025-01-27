package globalplayermaterials.jakedit.shapes;

import net.minecraft.util.math.BlockPos;

public class CuboidShape implements SelectionShape {

	@Override
	public Iterable<BlockPos> iterate(BlockPos p1, BlockPos p2) {
		return BlockPos.iterate(p1, p2);
	}
	
}
