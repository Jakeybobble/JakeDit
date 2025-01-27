package globalplayermaterials.commands.edit;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import globalplayermaterials.jakedit.FillExecutor;
import globalplayermaterials.jakedit.shapes.SelectionShapes;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class SetCommand {

	// TODO: Write out testing procedure

	public static int Command(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			String blockName = StringArgumentType.getString(context, "block");
			Block block = Registries.BLOCK.get(Identifier.of("minecraft:" + blockName));

			ServerPlayerEntity player = context.getSource().getPlayer();
			ServerWorld serverWorld = context.getSource().getWorld();
			
			FillExecutor.execute(player, block, serverWorld, SelectionShapes.CUBOID_SHAPE);
			
		return 0;
	}
}
