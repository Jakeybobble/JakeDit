package globalplayermaterials.storage;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class StorageItem {
	
	public Block block;
	public int count;
	
	public StorageItem(Identifier identifier, int count) {
		this.block = Registries.BLOCK.get(identifier);
		this.count = count;
	}
	
	public StorageItem(Block block, int count) {
		this.block = block;
		this.count = count;
	}
	
	public StorageItem(ItemStack itemStack) {
		this.count = itemStack.getCount();
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public ItemStack toStack() {
		return new ItemStack(this.block, this.count);
	}

}
