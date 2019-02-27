package org.bukkit.craftbukkit.inventory;

import com.google.common.primitives.Primitives;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagByte;
import net.minecraft.server.NBTTagByteArray;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagDouble;
import net.minecraft.server.NBTTagFloat;
import net.minecraft.server.NBTTagInt;
import net.minecraft.server.NBTTagIntArray;
import net.minecraft.server.NBTTagLong;
import net.minecraft.server.NBTTagLongArray;
import net.minecraft.server.NBTTagShort;
import net.minecraft.server.NBTTagString;
import org.apache.commons.lang3.Validate;
import org.bukkit.craftbukkit.inventory.tags.CraftCustomItemTagContainer;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;

/**
 * This class represents a registry that contains the used adapters for.
 */
public final class CraftCustomTagTypeRegistry {

    private final Function<Class, CustomTagAdapter> CREATE_ADAPTER = this::createAdapter;

    private class CustomTagAdapter<T, Z extends NBTBase> {

        private final Function<T, Z> builder;
        private final Function<Z, T> extractor;

        private final Class<T> primitiveType;
        private final Class<Z> nbtBaseType;

        public CustomTagAdapter(Class<T> primitiveType, Class<Z> nbtBaseType, Function<T, Z> builder, Function<Z, T> extractor) {
            this.primitiveType = primitiveType;
            this.nbtBaseType = nbtBaseType;
            this.builder = builder;
            this.extractor = extractor;
        }

        /**
         * This method will extract the value stored in the tag, according to
         * the expected primitive type.
         *
         * @param base the base to extract from
         * @return the value stored inside of the tag
         * @throws ClassCastException if the passed base is not an instanced of
         * the defined base type and therefore is not applicable to the
         * extractor function
         */
        T extract(NBTBase base) {
            Validate.isInstanceOf(nbtBaseType, base, "The provided NBTBase was of the type %s. Expected type %s", base.getClass().getSimpleName(), nbtBaseType.getSimpleName());
            return this.extractor.apply(nbtBaseType.cast(base));
        }

        /**
         * Builds a tag instance wrapping around the provided value object.
         *
         * @param value the value to store inside the created tag
         * @return the new tag instance
         * @throws ClassCastException if the passed value object is not of the
         * defined primitive type and therefore is not applicable to the builder
         * function
         */
        Z build(Object value) {
            Validate.isInstanceOf(primitiveType, value, "The provided value was of the type %s. Expected type %s", value.getClass().getSimpleName(), primitiveType.getSimpleName());
            return this.builder.apply(primitiveType.cast(value));
        }

        /**
         * Returns if the tag instance matches the adapters one.
         *
         * @param base the base to check
         * @return if the tag was an instance of the set type
         */
        boolean isInstance(NBTBase base) {
            return this.nbtBaseType.isInstance(base);
        }
    }

    private final Map<Class, CustomTagAdapter> adapters = new HashMap<>();

    /**
     * Creates a suitable adapter instance for the primitive class type
     *
     * @param type the type to create an adapter for
     * @param <T> the generic type of that class
     * @return the created adapter instance
     * @throws IllegalArgumentException if no suitable tag type adapter for this
     * type was found
     */
    private <T> CustomTagAdapter createAdapter(Class<T> type) {
        if (!Primitives.isWrapperType(type)) {
            type = Primitives.wrap(type); //Make sure we will always "switch" over the wrapper types
        }

        /*
            Primitives
         */
        if (Objects.equals(Byte.class, type)) {
            return createAdapter(Byte.class, NBTTagByte.class, NBTTagByte::new, NBTTagByte::asByte);
        }
        if (Objects.equals(Short.class, type)) {
            return createAdapter(Short.class, NBTTagShort.class, NBTTagShort::new, NBTTagShort::asShort);
        }
        if (Objects.equals(Integer.class, type)) {
            return createAdapter(Integer.class, NBTTagInt.class, NBTTagInt::new, NBTTagInt::asInt);
        }
        if (Objects.equals(Long.class, type)) {
            return createAdapter(Long.class, NBTTagLong.class, NBTTagLong::new, NBTTagLong::asLong);
        }
        if (Objects.equals(Float.class, type)) {
            return createAdapter(Float.class, NBTTagFloat.class, NBTTagFloat::new, NBTTagFloat::asFloat);
        }
        if (Objects.equals(Double.class, type)) {
            return createAdapter(Double.class, NBTTagDouble.class, NBTTagDouble::new, NBTTagDouble::asDouble);
        }

        /*
            String
         */
        if (Objects.equals(String.class, type)) {
            return createAdapter(String.class, NBTTagString.class, NBTTagString::new, NBTTagString::asString);
        }

        /*
            Primitive Arrays
         */
        if (Objects.equals(byte[].class, type)) {
            return createAdapter(byte[].class, NBTTagByteArray.class, array -> new NBTTagByteArray(Arrays.copyOf(array, array.length)), n -> Arrays.copyOf(n.c(), n.size()));
        }
        if (Objects.equals(int[].class, type)) {
            return createAdapter(int[].class, NBTTagIntArray.class, array -> new NBTTagIntArray(Arrays.copyOf(array, array.length)), n -> Arrays.copyOf(n.d(), n.size()));
        }
        if (Objects.equals(long[].class, type)) {
            return createAdapter(long[].class, NBTTagLongArray.class, array -> new NBTTagLongArray(Arrays.copyOf(array, array.length)), n -> Arrays.copyOf(n.d(), n.size()));
        }

        /*
            Note that this will map the interface CustomItemTagContainer directly to the CraftBukkit implementation
            Passing any other instance of this form to the tag type registry will throw a ClassCastException as defined in CustomTagAdapter#build
         */
        if (Objects.equals(CustomItemTagContainer.class, type)) {
            return createAdapter(CraftCustomItemTagContainer.class, NBTTagCompound.class, CraftCustomItemTagContainer::toTagCompound, tag -> {
                CraftCustomItemTagContainer container = new CraftCustomItemTagContainer(this);
                for (String key : tag.getKeys()) {
                    container.put(key, tag.get(key));
                }
                return container;
            });
        }

        throw new IllegalArgumentException("Could not find a valid CustomTagAdapter implementation for the requested type " + type.getSimpleName());
    }

    private <T, Z extends NBTBase> CustomTagAdapter<T, Z> createAdapter(Class<T> primitiveType, Class<Z> nbtBaseType, Function<T, Z> builder, Function<Z, T> extractor) {
        return new CustomTagAdapter<>(primitiveType, nbtBaseType, builder, extractor);
    }

    /**
     * Wraps the passed value into a tag instance.
     *
     * @param type the type of the passed value
     * @param value the value to be stored in the tag
     * @param <T> the generic type of the value
     * @return the created tag instance
     * @throws IllegalArgumentException if no suitable tag type adapter for this
     * type was found
     */
    public <T> NBTBase wrap(Class<T> type, T value) {
        return this.adapters.computeIfAbsent(type, CREATE_ADAPTER).build(value);
    }

    /**
     * Returns if the tag instance matches the provided primitive type.
     *
     * @param type the type of the primitive value
     * @param base the base instance to check
     * @param <T> the generic type of the type
     * @return if the base stores values of the primitive type passed
     * @throws IllegalArgumentException if no suitable tag type adapter for this
     * type was found
     */
    public <T> boolean isInstanceOf(Class<T> type, NBTBase base) {
        return this.adapters.computeIfAbsent(type, CREATE_ADAPTER).isInstance(base);
    }

    /**
     * Extracts the value out of the provided tag.
     *
     * @param type the type of the value to extract
     * @param tag the tag to extract the value from
     * @param <T> the generic type of the value stored inside the tag
     * @return the extracted value
     * @throws IllegalArgumentException if the passed base is not an instanced
     * of the defined base type and therefore is not applicable to the extractor
     * function
     * @throws IllegalArgumentException if the found object is not of type
     * passed
     * @throws IllegalArgumentException if no suitable tag type adapter for this
     * type was found
     */
    public <T> T extract(Class<T> type, NBTBase tag) throws ClassCastException, IllegalArgumentException {
        CustomTagAdapter adapter = this.adapters.computeIfAbsent(type, CREATE_ADAPTER);
        Validate.isTrue(adapter.isInstance(tag), "`The found tag instance cannot store %s as it is a %s", type.getSimpleName(), tag.getClass().getSimpleName());

        Object foundValue = adapter.extract(tag);
        Validate.isInstanceOf(type, foundValue, "The found object is of the type %s. Expected type %s", foundValue.getClass().getSimpleName(), type.getSimpleName());
        return type.cast(foundValue);
    }
}
