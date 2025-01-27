package globalplayermaterials.commands;

import com.mojang.brigadier.context.CommandContext;

import globalplayermaterials.GlobalPlayerMaterials;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class TestCommand {
	
	public static int Command(CommandContext<ServerCommandSource> objectCommandContext) {
		GlobalPlayerMaterials.LOGGER.info("Running test command.");
		try {
			ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();
			/*
			PlayerMaterialsStorage storage = Storages.getStorageFromPlayer(player);
			ItemStack newItemStack = new ItemStack(Items.COBBLESTONE);
			newItemStack.setCount(16);
			ItemStack appended = storage.Add(newItemStack);
			
			player.sendMessage(Text.literal("New amount: " + appended.getCount()), false);
			*/
			
			BlockPos p1 = new BlockPos(0,0,0);
			BlockPos p2 = new BlockPos(0,0,0);
			player.sendMessage(Text.of(p1 == p2 ? "True" : "False" )); // False :-(
			player.sendMessage(Text.of(p1.equals(p2) ? "True" : "False" )); // True (-:
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
