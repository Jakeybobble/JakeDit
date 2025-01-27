package globalplayermaterials.commands;

import com.mojang.brigadier.context.CommandContext;

import globalplayermaterials.GlobalPlayerMaterials;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class TestCommand {
	
	public static int Command(CommandContext<ServerCommandSource> objectCommandContext) {
		GlobalPlayerMaterials.LOGGER.info("Running test command.");
		try {
			//ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
