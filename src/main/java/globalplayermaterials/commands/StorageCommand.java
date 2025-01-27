package globalplayermaterials.commands;

import com.mojang.brigadier.context.CommandContext;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import globalplayermaterials.storage.PlayerMaterialsStorage;
import globalplayermaterials.storage.StorageItem;
import globalplayermaterials.storage.Storages;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class StorageCommand {

	public static int Command(CommandContext<ServerCommandSource> objectCommandContext) {
		try {
			ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();
			SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X6, player, true) {
				@Override
				public boolean onClick(int index, ClickType type, SlotActionType action, GuiElementInterface element) {
					//this.player.sendMessage(Text.literal(type.toString()), false);

					if (index > this.getVirtualSize() - 1) {
						if(type != ClickType.MOUSE_LEFT_SHIFT) return false;
						// Might be good to have a safeguard which makes sure the item you clicked is the same as the one in the inventory
						// But it probably doesn't matter.
						
						// TODO: Error messages
						
						// Grab item
						int invIndex = (index - this.getVirtualSize() + 9) % PlayerInventory.MAIN_SIZE;
						ItemStack pickedStack = this.player.getInventory().getStack(invIndex);
						
						boolean hasImportantData = pickedStack.hasChangedComponent(DataComponentTypes.CONTAINER);
						
						Block blockFromStack = PlayerMaterialsStorage.getRegistriesBlock(pickedStack);
						if(blockFromStack != null && hasImportantData == false) {
							ItemStack stack = this.player.getInventory().removeStack(invIndex);
							if(stack != ItemStack.EMPTY) {
								PlayerMaterialsStorage storage = Storages.getStorageFromPlayer(player);
								storage.Add(blockFromStack, pickedStack.getCount());
								
								player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 2, 1); // FIXME: Sound doesn't work :-(
								Command(objectCommandContext); // Re-runs this command
							}
						}

					}

					return super.onClick(index, type, action, element);
				}
			};

			gui.setTitle(Text.literal("Your global storage"));

			PlayerMaterialsStorage storage = Storages.getStorageFromPlayer(player);
			// Storage items
			for (int i = 0; i < storage.inventory.size(); i++) {
				if (i >= gui.getVirtualSize())
					break;
				
				StorageItem storageItem = storage.inventory.get(i);
				
				ItemStack displayStack = new ItemStack(storageItem.block, storageItem.count);
				
				int count = storageItem.count;
				
				displayStack.set(DataComponentTypes.CUSTOM_NAME,
						Text.of(displayStack.getItemName().getString() + ": " + count));
				displayStack.set(DataComponentTypes.MAX_STACK_SIZE, count);

				gui.setSlot(i, displayStack);
			}
			
			// TODO: Add info description to all transferable items
			
			// Player items
			// LoreComponent lore = new LoreComponent(List.of(Text.of("§6Shift-click to insert")));
			PlayerInventory inv = player.getInventory();
			for (int i = 0; i < inv.main.size(); i++) {
				ItemStack playerStack = inv.main.get(i);
				//playerStack.set(DataComponentTypes.LORE, lore);
				gui.setSlot((i + 27) % PlayerInventory.MAIN_SIZE + gui.getVirtualSize(), playerStack);
			}

			gui.open();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
