package globalplayermaterials.jakedit;

import java.util.List;

import net.minecraft.block.BlockState;

public class Undo {
	
	public List<BlockState> blocks;
	
	public Undo(List<BlockState> blocks) {
		this.blocks = blocks;
	}
	
}
