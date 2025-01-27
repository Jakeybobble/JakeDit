package globalplayermaterials.storage;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

public class PlayerMaterialsStorage {

	public ArrayList<StorageItem> inventory;

	public PlayerMaterialsStorage() {
		this.inventory = new ArrayList<StorageItem>();
	}

	public StorageItem Add(Block block, int count) {

		// TODO: Find out if there is a cooler way of doing all this

		// Loop through storage to see if it exists
		StorageItem firstItem = null;
		for (StorageItem storageItem : inventory) {
			if (storageItem.block == block) {
				firstItem = storageItem;
				break;
			}
		}

		if (firstItem == null) {
			StorageItem storageItem = new StorageItem(block, count);
			this.inventory.add(storageItem);
			return storageItem;
		} else {
			firstItem.setCount(firstItem.count + count);
			return firstItem;
		}

	}

	public void writeNbt(NbtList list, RegistryWrapper.WrapperLookup registries) {

		for (int i = 0; i < inventory.size(); i++) {
			StorageItem storageItem = inventory.get(i);
			Block block = storageItem.block;
			if (storageItem.count > 0) {
				NbtCompound nbt = new NbtCompound();
				nbt.putString("block_id", Registries.BLOCK.getId(block).toString());
				nbt.putInt("count", storageItem.count);

				list.add(nbt);
			}

		}
	}

	/**
	 * Used for checking whether a Block from ItemStack exists
	 * 
	 * @param ItemStack to check
	 * @return Block from block registry or null
	 */
	public static Block getRegistriesBlock(ItemStack itemStack) {
		if (itemStack.getItem() instanceof BlockItem blockItem) {
			return blockItem.getBlock();
		}
		return null;

	}

	/**
	 * Removes blocks from inventory, returns false if there aren't enough blocks
	 * 
	 * @param matchItems - Blocks and counts to remove
	 * @return Success
	 */
	public boolean removeSufficientBlocks(StorageItem[] matchItems) {

		class RemovalEntry {
			StorageItem storageItem;
			int toRemove;

			public RemovalEntry(StorageItem storageItem, int toRemove) {
				this.storageItem = storageItem;
				this.toRemove = toRemove;
			}
		}

		List<RemovalEntry> toRemove = new ArrayList<RemovalEntry>();
		boolean doFail = false;
		for (StorageItem invItem : inventory) {
			for (StorageItem matchItem : matchItems) {
				if (doFail)
					break;
				if (invItem.block == matchItem.block) {
					if (invItem.count >= matchItem.count) {
						toRemove.add(new RemovalEntry(invItem, matchItem.count));
					} else {
						doFail = true;
					}
					break;
				}
			}
		}
		if (doFail || toRemove.size() != matchItems.length)
			return false;

		for (RemovalEntry entry : toRemove) {
			entry.storageItem.count -= entry.toRemove;
		}

		cleanEmpty();

		return true;
	}
	
	public void cleanEmpty() {
		inventory.removeIf(n -> n.count <= 0);
	}
	
	public @Nullable StorageItem getFirst(Block block) {
		for(StorageItem storageItem : inventory) {
			if(storageItem.block == block) return storageItem;
		}
		return null;
	}

	/*
	 * Old code List<StorageItem> storage = inventory.stream() .filter(invItem ->
	 * Arrays.stream(storageItems) .anyMatch(matchItem -> invItem.block ==
	 * matchItem.block && invItem.count >= matchItem.count))
	 * .collect(Collectors.toList());
	 * 
	 * if (storageItems.length != storage.size()) return false;
	 */

}
