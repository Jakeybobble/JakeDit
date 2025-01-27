package globalplayermaterials.commands;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.block.Block;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class InsertCommand {
	public static int Command(CommandContext<ServerCommandSource> context) {
		try {
			Block argItem = BlockStateArgumentType.getBlockState(context, "item").getBlockState().getBlock();
			ServerPlayerEntity player = context.getSource().getPlayer();
			player.sendMessage(Text.literal("Inserting: " + argItem.getName()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
