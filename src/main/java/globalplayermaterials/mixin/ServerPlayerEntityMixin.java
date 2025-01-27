package globalplayermaterials.mixin;

import java.io.File;
import java.io.IOException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import globalplayermaterials.GlobalPlayerMaterials;
import globalplayermaterials.storage.PlayerMaterialsStorage;
import globalplayermaterials.storage.Storages;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin extends PlayerEntity {

	protected ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Override
	public boolean isSpectator() {
		return false;
	}

	@Override
	public boolean isCreative() {
		return false;
	}
	
	@Inject(at = @At("HEAD"), method = "writeCustomDataToNbt")
	public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
		
		MinecraftServer server = this.getServer();
		if (server == null) {
			return;
		}
		
		GlobalPlayerMaterials.LOGGER.info("Saving custom data for " + this.getGameProfile().getName() + ".");
		
		File worldFolder = server.getSavePath(WorldSavePath.ROOT).toFile();
		File customFolder = new File(worldFolder, "custom_playerdata");
		if (!customFolder.exists()) {
			customFolder.mkdirs();
		}
		
		File customFile = new File(customFolder, this.getUuidAsString() + ".dat");
        try {
            NbtCompound customData = new NbtCompound();
            //customData.put("CustomItemStorage", nbt.get("Inventory")); // Example: Save inventory separately
            
            NbtList list = new NbtList();
            
            PlayerMaterialsStorage storage = Storages.getStorageFromPlayer(this);
            if (storage != null) storage.writeNbt(list, getRegistryManager());
            
            customData.put("storage", list);
            
            NbtIo.writeCompressed(customData, customFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
}
