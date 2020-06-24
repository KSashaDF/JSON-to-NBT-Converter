package converter;

import com.google.gson.*;
import net.minecraft.server.v1_15_R1.*;

import java.util.Map;

/**
 * Converter utility for converting JSON (GSON) to NBT.
 *
 * @see NbtToJson
 */
public final class JsonToNbt {
	
	/**
	 * Converts a JSON element to an NBT tag.
	 *
	 * @param jsonElement Element to convert.
	 * @return The NBT tag equivalent. (imperfect in certain cases)
	 */
	public static NBTBase toNbt(JsonElement jsonElement) {
		
		// JSON Primitive
		if (jsonElement instanceof JsonPrimitive) {
			JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonElement;
			
			if (jsonPrimitive.isBoolean()) {
				boolean value = jsonPrimitive.getAsBoolean();
				
				if (value) {
					return NBTTagByte.a(true);
				} else {
					return NBTTagByte.a(false);
				}
				
			} else if (jsonPrimitive.isNumber()) {
				Number number = jsonPrimitive.getAsNumber();
				
				if (number instanceof Byte) {
					return NBTTagByte.a(number.byteValue());
				} else if (number instanceof Short) {
					return NBTTagShort.a(number.shortValue());
				} else if (number instanceof Integer) {
					return NBTTagInt.a(number.intValue());
				} else if (number instanceof Long) {
					return NBTTagLong.a(number.longValue());
				} else if (number instanceof Float) {
					return NBTTagFloat.a(number.floatValue());
				} else if (number instanceof Double) {
					return NBTTagDouble.a(number.doubleValue());
				}
				
			} else if (jsonPrimitive.isString()) {
				return NBTTagString.a(jsonPrimitive.getAsString());
			}
			
		// JSON Array
		} else if (jsonElement instanceof JsonArray) {
			JsonArray jsonArray = (JsonArray) jsonElement;
			NBTTagList nbtList = new NBTTagList();
			
			for (JsonElement element : jsonArray) {
				nbtList.add(toNbt(element));
			}
			
			return nbtList;
			
		// JSON Object
		} else if (jsonElement instanceof JsonObject) {
			JsonObject jsonObject = (JsonObject) jsonElement;
			NBTTagCompound nbtCompound = new NBTTagCompound();
			
			for (Map.Entry<String, JsonElement> jsonEntry : jsonObject.entrySet()) {
				nbtCompound.set(jsonEntry.getKey(), toNbt(jsonEntry.getValue()));
			}
			
			return nbtCompound;
			
		// Null - Not fully supported
		} else if (jsonElement instanceof JsonNull) {
			return new NBTTagCompound();
		}
		
		// Something has gone wrong, throw an error.
		throw new AssertionError();
	}
}
