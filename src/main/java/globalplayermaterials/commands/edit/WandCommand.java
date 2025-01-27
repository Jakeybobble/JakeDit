package globalplayermaterials.commands.edit;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WandCommand {
	
	// TODO: Set wand name based on how many blocks are selected...
	
	public static String WAND_NAME = "§rJakeDit Wand";
	
	public static int Command(CommandContext<ServerCommandSource> context) {
		try {
			// It makes sense to have a crafting recipe for this item.

			ServerPlayerEntity player = context.getSource().getPlayer();
			ItemStack wandStack = new ItemStack(Items.STONE_SWORD);

			wandStack.set(DataComponentTypes.CUSTOM_NAME, Text.of("JakeDit Wand"));
			wandStack.set(DataComponentTypes.MAX_STACK_SIZE, 1);

			wandStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT
					.with(EntityAttributes.BLOCK_INTERACTION_RANGE,
							new EntityAttributeModifier(Identifier.of("minecraft:block_interaction_range"), 10.0,
									EntityAttributeModifier.Operation.ADD_VALUE),
							AttributeModifierSlot.MAINHAND)
					.with(EntityAttributes.ENTITY_INTERACTION_RANGE,
							new EntityAttributeModifier(Identifier.of("minecraft:entity_interaction_range"), -3.0,
									EntityAttributeModifier.Operation.ADD_VALUE),
							AttributeModifierSlot.MAINHAND));
			
			NbtCompound nbt = new NbtCompound();
			nbt.putBoolean("wand", true);
			wandStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
			
			player.giveItemStack(wandStack);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static boolean isWand(ItemStack itemStack) {
		if (itemStack.isEmpty()) return false;
		NbtComponent nbt = itemStack.get(DataComponentTypes.CUSTOM_DATA);
		
		if(nbt == null) return false;
		
		if(nbt.contains("wand")) return true;
		
		return false;
		
		//return itemStack.get(DataComponentTypes.CUSTOM_DATA).contains("IS_WAND");
	}
	
}
