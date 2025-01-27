package globalplayermaterials.jakedit;

import java.util.HashMap;
import java.util.Stack;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import globalplayermaterials.commands.edit.WandCommand;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class PlayerEditProfile {
	
	public static final int MAX_CHANGEABLE = 1000; // TODO: Find a good and balanced value for this
	private static final int MAX_UNDO = 3;
	
	private static HashMap<UUID, PlayerEditProfile> profiles = new HashMap<UUID, PlayerEditProfile>();
	
	public PlayerEditProfile(UUID uuid) {
		profiles.put(uuid, this);
	}
	
	public static PlayerEditProfile get(UUID uuid) {
		if (!profiles.containsKey(uuid)) {
			profiles.put(uuid, new PlayerEditProfile(uuid));
		}
		return profiles.get(uuid);
	}
	
	public Stack<Undo> undoStack = new Stack<Undo>();
	
	@Nullable
	public BlockPos p1;
	@Nullable
	public BlockPos p2;
	
	public void wandLeftClick(PlayerEntity player, BlockPos pos) {
		BlockPos prev = p1 != null ? p1.mutableCopy() : null;
		sendPositionMessage(player, "§3Point 1", prev, pos);
		p1 = pos;
	}
	
	public void wandRightClick(PlayerEntity player, BlockPos pos) {
		BlockPos prev = p2 != null ? p2.mutableCopy() : null;
		sendPositionMessage(player, "§dPoint 2", prev, pos);
		p2 = pos;
	}
	
	public int getSelectionVolume() {
		if(p1 != null && p2 != null) {
			return (Math.abs(p1.getX() - p2.getX()) + 1) * (Math.abs(p1.getY() - p2.getY()) + 1) * (Math.abs(p1.getZ() - p2.getZ()) + 1);
		}
		return 0;
	}
	
	private void sendPositionMessage(PlayerEntity player, String name, BlockPos prev, BlockPos to) {
		if (prev != null) {
			String str = String.format("%s: §8%s, %s, %s §7-> (§f%s, %s, %s§7)", name, prev.getX(), prev.getY(), prev.getZ(), to.getX(), to.getY(), to.getZ());
			player.sendMessage(Text.of(str), false);
		} else {
			String str = String.format("%s: §7-> (§f%s, %s, %s§7)", name, to.getX(), to.getY(), to.getZ());
			player.sendMessage(Text.of(str), false);
		}
	}
	
	public void updateWandName(ItemStack wandStack) {
		if(p1 == null || p2 == null) return;
		int width = Math.abs(p1.getX() - p2.getX()) + 1;
		int height = Math.abs(p1.getY() - p2.getY()) + 1;
		int depth = Math.abs(p1.getZ() - p2.getZ()) + 1;
		int volume = width * height * depth;
		String str = String.format("%s - §c%s§7x§a%s§7x§3%s §7(§f%s§7)", WandCommand.WAND_NAME, width, height, depth, volume <= MAX_CHANGEABLE ? volume : "§cToo many");
		wandStack.set(DataComponentTypes.CUSTOM_NAME, Text.of(str));
	}
	
	public void addUndo(Undo undo) {
		if(undoStack.size() > MAX_UNDO) {
			undoStack.removeFirst();
		}
		undoStack.push(undo);
	}
	
	
}
