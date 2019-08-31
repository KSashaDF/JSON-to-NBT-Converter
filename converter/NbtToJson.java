package converter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.server.v1_13_R2.*;

import java.util.List;
import java.util.Map;

/**
 * Converter utility for converting NBT to JSON (GSON).
 *
 * @see JsonToNbt
 */
public final class NbtToJson {
	
	public static JsonElement toJson(NBTBase nbtElement) {
		return toJson(nbtElement, ConversionMode.RAW);
	}
	
	/**
	 * Converts an NBT tag to a JSON element.
	 *
	 * @param nbtElement NBT tag to convert.
	 * @param mode The conversion mode.
	 * @return The JSON element equivalent. (imperfect in certain cases)
	 */
	@SuppressWarnings("unchecked")
	public static JsonElement toJson(NBTBase nbtElement, ConversionMode mode) {
		
		// Numbers
		if (nbtElement instanceof NBTNumber) {
			NBTNumber nbtNumber = (NBTNumber) nbtElement;
			
			switch (mode) {
				case JSON: {
					if (nbtNumber instanceof NBTTagByte) {
						NBTTagByte nbtByte = (NBTTagByte) nbtNumber;
						byte value = nbtByte.asByte();
						
						switch (value) {
							case 0: return new JsonPrimitive(false);
							case 1: return new JsonPrimitive(true);
							
							default: // Continue
						}
					}
					
					// Else, continue
				}
				
				case RAW: {
					return new JsonPrimitive(nbtNumber.j());
				}
			}
			
		// String
		} else if (nbtElement instanceof NBTTagString) {
			NBTTagString nbtString = (NBTTagString) nbtElement;
			return new JsonPrimitive(nbtString.asString());
			
		// Lists
		} else if (nbtElement instanceof NBTList) {
			NBTList nbtList = (NBTList) nbtElement;
			JsonArray jsonArray = new JsonArray();
			
			for (NBTBase nbtBase : (List<NBTBase>) nbtList) {
				jsonArray.add(toJson(nbtBase, mode));
			}
			
			return jsonArray;
			
		// Compound tag
		} else if (nbtElement instanceof NBTTagCompound) {
			NBTTagCompound nbtCompound = (NBTTagCompound) nbtElement;
			JsonObject jsonObject = new JsonObject();
			
			for (Map.Entry<String, NBTBase> nbtEntry : nbtCompound.map.entrySet()) {
				jsonObject.add(nbtEntry.getKey(), toJson(nbtEntry.getValue(), mode));
			}
			
			return jsonObject;
			
		// Nbt termination tag. Should not be encountered.
		} else if (nbtElement instanceof NBTTagEnd) {
			throw new AssertionError();
		}
		
		// Impossible unless a new NBT class is made.
		throw new UnsupportedOperationException();
	}
	
	/**
	 * The NBT to JSON conversion mode.
	 */
	public enum ConversionMode {
		
		/**
		 * The NBT will be converted to JSON 'as is'.
		 */
		RAW,
		/**
		 * The NBT will be converted to JSOn with the assumption that it
		 * was previously JSON, and therefore certain assumptions and
		 * conversions will be made.
		 *
		 * Conversions:
		 *
		 * 0b -> false
		 * 1b -> true
		 */
		JSON
	}
}
