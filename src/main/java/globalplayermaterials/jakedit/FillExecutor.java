package globalplayermaterials.jakedit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import globalplayermaterials.jakedit.shapes.SelectionShape;
import globalplayermaterials.storage.PlayerMaterialsStorage;
import globalplayermaterials.storage.StorageItem;
import globalplayermaterials.storage.Storages;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class FillExecutor {
	
	// TODO: Don't run command if player is inside
	// TODO: Undo memory
	// TODO: Only set within loaded chunks!
	
	// Execute with single block
	public static void execute(ServerPlayerEntity player, Block block, ServerWorld serverWorld ,SelectionShape selectionShape) throws CommandSyntaxException {
		PlayerEditProfile prof = PlayerEditProfile.get(player.getUuid());
		BlockState blockState = block.getDefaultState();
		PlayerMaterialsStorage storage = Storages.getStorageFromPlayer(player);
		
		BlockPos p1 = prof.p1;
		BlockPos p2 = prof.p2;
		
		if(p1 == null || p2 == null) {
			throw Exceptions.POINTS_NOT_SET.create();
		}
		
		int volume = prof.getSelectionVolume();
		
		Iterator<BlockPos> iteratorExisting = selectionShape.iterate(p1, p2).iterator();
		int skipCount = 0;
		
		while (iteratorExisting.hasNext()) {
			BlockPos blockPos = iteratorExisting.next();
			BlockState existingBlockState = serverWorld.getBlockState(blockPos);
			if (existingBlockState.isAir())
				continue;
			skipCount++;
		}
		
		// Remove blocks from storage
		if (!storage.removeSufficientBlocks(new StorageItem[] { new StorageItem(block, volume - skipCount) })) {
			StorageItem first = storage.getFirst(block);
			throw(Exceptions.INSUFFICIENT_STORAGE.create(volume - skipCount, first.count));
		}

		// Place the blocks in the world
		Iterator<BlockPos> iterator = BlockPos.iterate(p1, p2).iterator();
		
		List<StatePosition> undoList = new ArrayList<StatePosition>();

		int setBlockCount = 0;
		while (iterator.hasNext()) {
			BlockPos blockPos = iterator.next();
			BlockState worldBlockState = serverWorld.getBlockState(blockPos);
			if(!worldBlockState.isAir() || worldBlockState.getBlock() == block) {
				continue;
			}
			undoList.add(new StatePosition(worldBlockState, blockPos));
			serverWorld.setBlockState(blockPos, blockState, 2);
			setBlockCount++;
		}
		
		prof.addUndo(new Undo(undoList));
		player.sendMessage(Text.of("Changed " + setBlockCount + " " + (setBlockCount == 1 ? "Block" : "Blocks") + "."));
		
	}
}
