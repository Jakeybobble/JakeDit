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
		
		HashMap<BlockPos, Block> existingBlocks = new HashMap<BlockPos, Block>();
		HashMap<Block, Integer> existingBlocksCount = new HashMap<Block, Integer>();
		
		// Check for existing blocks within region to populate existingBlocks and
		// existingBlocksCount
		Iterator<BlockPos> iteratorExisting = selectionShape.iterate(p1, p2).iterator();
		int skipCount = 0;
		
		while (iteratorExisting.hasNext()) {
			BlockPos blockPos = iteratorExisting.next();
			BlockState existingBlockState = serverWorld.getBlockState(blockPos);
			if (existingBlockState.isAir())
				continue;
			skipCount++;
			Block existingBlock = existingBlockState.getBlock();
			existingBlocks.put(blockPos.toImmutable(), existingBlock);
			existingBlocksCount.put(existingBlock, existingBlocksCount.getOrDefault(existingBlock, 0) + 1);
		}
		
		// Make sure player has all blocks to exchange
		for (Map.Entry<Block, Integer> entry : existingBlocksCount.entrySet()) {
			if (entry.getKey() == block)
				continue;
			StorageItem first = storage.getFirst(entry.getKey());

			if (first == null || first.count < entry.getValue()) {
				throw(Exceptions.MISSING_EXCHANGE_BLOCKS.create(entry.getKey(), entry.getValue(), first == null ? 0 : first.count));
			}

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
			if (existingBlocks.containsKey(blockPos))
				continue;
			undoList.add(new StatePosition(serverWorld.getBlockState(blockPos), blockPos));
			serverWorld.setBlockState(blockPos, blockState, 2);
			setBlockCount++;
		}
		
		prof.addUndo(new Undo(undoList));
		player.sendMessage(Text.of("Changed " + setBlockCount + " " + (setBlockCount == 1 ? "Block" : "Blocks") + "."));
		
	}
}
