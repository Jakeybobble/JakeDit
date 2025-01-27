package globalplayermaterials.storage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import globalplayermaterials.GlobalPlayerMaterials;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;

public class Storages {
	public static HashMap<UUID, PlayerMaterialsStorage> storages; // Maybe to-do: Concurrency?
	
	public static void init() {
		storages = new HashMap<UUID, PlayerMaterialsStorage>();
	}
	
	public static PlayerMaterialsStorage getStorageFromPlayer(PlayerEntity player) {
		return getStorageFromUUID(player.getUuid(), player.getServer());
	}
	
	public static PlayerMaterialsStorage getStorageFromUUID(UUID uuid, MinecraftServer server) {
		fillIfNull(uuid, server);
		
		PlayerMaterialsStorage storage = storages.get(uuid);
		if(storage == null) {
			GlobalPlayerMaterials.LOGGER.info("No storage was found.");
		}
		return storage;
	}
	
	private static void fillIfNull(UUID uuid, MinecraftServer server) {
		if(!storages.containsKey(uuid)) {
			GlobalPlayerMaterials.LOGGER.info("Storage was not found. Reading from NBT.");
			
			// Load
			File worldFolder = server.getSavePath(WorldSavePath.ROOT).toFile();
			File customFile = new File(new File(worldFolder, "custom_playerdata"), uuid.toString() + ".dat");
			if(customFile.exists()) {
				GlobalPlayerMaterials.LOGGER.info("Found storage file.");
				try {
					NbtCompound customData = NbtIo.readCompressed(customFile.toPath(), NbtSizeTracker.ofUnlimitedBytes());
					NbtList list = customData.getList("storage", 10);
					
					PlayerMaterialsStorage storage = new PlayerMaterialsStorage();
					
					for (int i = 0; i < list.size(); i++) {
						NbtCompound nbtCompound = list.getCompound(i);
						
						int count = nbtCompound.getInt("count");
						Identifier identifier = Identifier.of(nbtCompound.getString("block_id"));
						
						if(count == 0) continue;
						storage.inventory.add(new StorageItem(identifier, count));
						
					}
					
					storages.put(uuid, storage);
					
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
}
