package globalplayermaterials.commands.edit;

import java.util.HashMap;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import globalplayermaterials.jakedit.PlayerEditProfile;
import globalplayermaterials.jakedit.StatePosition;
import globalplayermaterials.jakedit.Undo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class UndoCommand {
	public static int Command(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		
		ServerPlayerEntity player = context.getSource().getPlayer();
		PlayerEditProfile profile = PlayerEditProfile.get(player.getUuid());
		ServerWorld serverWorld = context.getSource().getWorld();
		
		Undo peek = profile.undoStack.peek();
		HashMap<Block, Integer> blocksToRefund = new HashMap<Block, Integer>(); // Only add to this if block is placed
		
		// Edge case: It would be possible to /set a chest and undo it to remove it forever
		
		int blocksUndone = 0;
		for(StatePosition statePosition : peek.blocks) {
			BlockState worldBlockState = serverWorld.getBlockState(statePosition.blockPos);
			
			if(worldBlockState.equals(statePosition.blockState)) continue;
			Block block = statePosition.blockState.getBlock();
			blocksToRefund.put(block, blocksToRefund.getOrDefault(block, 0) + 1);
			
			serverWorld.setBlockState(statePosition.blockPos, statePosition.blockState);
			blocksUndone++;
			
		}
		
		// TODO: Refund blocks
		
		profile.undoStack.pop();
		
		player.sendMessage(Text.of("Undid " + blocksUndone + " blocks."));
		return 0;
	}
}
