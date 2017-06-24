package coolsquid.misctweaks.util;

import java.lang.reflect.Field;

import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;

public class Util {

	public static <E, V> V getPrivateValue(Class<E> type, E instance, String field) {
		Field f;
		try {
			f = type.getDeclaredField(field);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new UnableToFindFieldException(new String[] {field}, e);
		}
		f.setAccessible(true);
		try {
			return (V) f.get(instance);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new UnableToAccessFieldException(new String[] {field}, e);
		}
	}
}