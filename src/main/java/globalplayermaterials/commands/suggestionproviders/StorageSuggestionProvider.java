package globalplayermaterials.commands.suggestionproviders;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import globalplayermaterials.storage.PlayerMaterialsStorage;
import globalplayermaterials.storage.StorageItem;
import globalplayermaterials.storage.Storages;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class StorageSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
	
	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
		ServerPlayerEntity player = source.getPlayer();
		
		PlayerMaterialsStorage storage = Storages.getStorageFromPlayer(player);
		for(StorageItem storageItem : storage.inventory) {
			String id = Registries.BLOCK.getId(storageItem.block).getPath();
			builder.suggest(id);
		}
		
		
		return builder.buildFuture();
	}
}
