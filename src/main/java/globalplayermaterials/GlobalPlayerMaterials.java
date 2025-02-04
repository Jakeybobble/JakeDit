package globalplayermaterials;

import static net.minecraft.server.command.CommandManager.literal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.arguments.StringArgumentType;

import globalplayermaterials.commands.StorageCommand;
import globalplayermaterials.commands.TestCommand;
import globalplayermaterials.commands.edit.SetCommand;
import globalplayermaterials.commands.edit.UndoCommand;
import globalplayermaterials.commands.edit.WandCommand;
import globalplayermaterials.commands.suggestionproviders.StorageSuggestionProvider;
import globalplayermaterials.jakedit.PlayerEditProfile;
import globalplayermaterials.storage.Storages;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

// TODO: Rename and refactor project to "JakeDit"
public class GlobalPlayerMaterials implements ModInitializer {
	public static final String MOD_ID = "globalplayermaterials";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Starting up!");

		registerEvents();
		registerCommands();

		Storages.init();
	}
	
	// TODO: Copy command
	// TODO: Paste command
	// TODO: Walls command
	// TODO: Frame command
	// TODO: Hollow set command
	// TODO: Undo command
	// TODO: Pillar + direction command
	// TODO: Settings UI command (to disable messages)
	
	private void registerCommands() {
		
		// TODO: Move registering to each class
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(literal("storage").executes(StorageCommand::Command));
			dispatcher.register(literal("inv").executes(StorageCommand::Command));

			dispatcher.register(literal("test").executes(TestCommand::Command));

			dispatcher.register(CommandManager.literal("set")
					.then(CommandManager.argument("block", StringArgumentType.string())
							.suggests(new StorageSuggestionProvider()).executes(SetCommand::Command)));

			dispatcher.register(literal("wand").executes(WandCommand::Command));
			
			dispatcher.register(literal("undo").executes(UndoCommand::Command));

		});
	}

	private void registerEvents() {
		
		// Left click
		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {

			ItemStack handItem = player.getMainHandStack();
			if (WandCommand.isWand(handItem)) {

				if (player instanceof ServerPlayerEntity serverPlayer) {
					PlayerEditProfile profile = PlayerEditProfile.get(serverPlayer.getUuid());
					profile.wandLeftClick(serverPlayer, pos);
					profile.updateWandName(handItem);
				}

			}

			return ActionResult.PASS;
		});
		
		// Right click
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			
			ItemStack handItem = player.getMainHandStack();
			if (hand == Hand.MAIN_HAND && WandCommand.isWand(handItem)) {
				PlayerEditProfile profile = PlayerEditProfile.get(player.getUuid());
				profile.wandRightClick(player, hitResult.getBlockPos());
				profile.updateWandName(handItem);
				return ActionResult.SUCCESS;
			}

			return ActionResult.PASS;
		});
		
		// Anti-block break
		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
			if (WandCommand.isWand(player.getMainHandStack())) {
				return false;
			}
			return true;
		});

	}
}