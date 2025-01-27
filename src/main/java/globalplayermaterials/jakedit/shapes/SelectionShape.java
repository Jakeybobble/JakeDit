package globalplayermaterials.jakedit.shapes;

import net.minecraft.util.math.BlockPos;

public interface SelectionShape {
	public Iterable<BlockPos> iterate(BlockPos p1, BlockPos p2);
}
