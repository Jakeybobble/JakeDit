package globalplayermaterials.jakedit;

import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public class Exceptions {
	public static final SimpleCommandExceptionType FAILED_EXCEPTION;
	public static final SimpleCommandExceptionType POINTS_NOT_SET;
	// TODO: Collect the rest of missing blocks
	public static final Dynamic3CommandExceptionType MISSING_EXCHANGE_BLOCKS = new Dynamic3CommandExceptionType(
			(missingBlock, countObj, storageCountObj) -> {
				// return Text.stringifiedTranslatable("commands.fill.toobig", new
				// Object[]{maxCount, count});
				String missingBlockStr = Registries.BLOCK.getId((Block) missingBlock).getPath();
				;
				int count = (int) countObj;
				int storageCount = storageCountObj == null ? 0 : (int) storageCountObj;

				String str = String.format(
						"Not enough exchangeable blocks from storage to replace blocks in the way.\n"
								+ "First block in way: %s, count: %s (in storage: %s)",
						missingBlockStr, count, storageCount);
				return Text.of(str);
			});

	public static final Dynamic2CommandExceptionType INSUFFICIENT_STORAGE = new Dynamic2CommandExceptionType((neededCount, inStorageCount) -> {
		String str = String.format("Nope. Not enough blocks. (%s needed, %s in storage)", (int)neededCount, (int)inStorageCount);
		return Text.of(str);
	});

	static {
		FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.of("Failed with unknown error."));
		POINTS_NOT_SET = new SimpleCommandExceptionType(Text.of("You haven't set both points with your wand!"));
	}
}
