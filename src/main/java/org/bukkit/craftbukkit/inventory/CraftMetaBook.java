package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.meta.BookMeta;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.AbstractList;
import net.minecraft.server.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.IChatBaseComponent;
import net.minecraft.server.NBTTagString;
import org.bukkit.craftbukkit.util.CraftChatMessage;

// Spigot start
import static org.spigotmc.ValidateUtils.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.ChatBaseComponent;
// Spigot end

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaBook extends CraftMetaItem implements BookMeta {
    static final ItemMetaKey BOOK_TITLE = new ItemMetaKey("title");
    static final ItemMetaKey BOOK_AUTHOR = new ItemMetaKey("author");
    static final ItemMetaKey BOOK_PAGES = new ItemMetaKey("pages");
    static final ItemMetaKey RESOLVED = new ItemMetaKey("resolved");
    static final ItemMetaKey GENERATION = new ItemMetaKey("generation");
    static final int MAX_PAGES = 50;
    static final int MAX_PAGE_LENGTH = 320; // 256 limit + 64 characters to allow for psuedo colour codes
    static final int MAX_TITLE_LENGTH = 32;
    private static final boolean OVERRIDE_CHECKS = Boolean.getBoolean("disable.book-limits"); // Paper - Add override

    protected String title;
    protected String author;
    public List<IChatBaseComponent> pages = new ArrayList<IChatBaseComponent>();
    protected Integer generation;

    CraftMetaBook(CraftMetaItem meta) {
        super(meta);

        if (meta instanceof CraftMetaBook) {
            CraftMetaBook bookMeta = (CraftMetaBook) meta;
            this.title = bookMeta.title;
            this.author = bookMeta.author;
            pages.addAll(bookMeta.pages);
            this.generation = bookMeta.generation;
        }
    }

    CraftMetaBook(NBTTagCompound tag) {
        this(tag, true);
    }

    CraftMetaBook(NBTTagCompound tag, boolean handlePages) {
        super(tag);

        if (tag.hasKey(BOOK_TITLE.NBT)) {
            this.title = limit( tag.getString(BOOK_TITLE.NBT), 1024 ); // Spigot
        }

        if (tag.hasKey(BOOK_AUTHOR.NBT)) {
            this.author = limit( tag.getString(BOOK_AUTHOR.NBT), 1024 ); // Spigot
        }

        boolean resolved = false;
        if (tag.hasKey(RESOLVED.NBT)) {
            resolved = tag.getBoolean(RESOLVED.NBT);
        }

        if (tag.hasKey(GENERATION.NBT)) {
            generation = tag.getInt(GENERATION.NBT);
        }

        if (tag.hasKey(BOOK_PAGES.NBT) && handlePages) {
            NBTTagList pages = tag.getList(BOOK_PAGES.NBT, CraftMagicNumbers.NBT.TAG_STRING);

            for (int i = 0; i < Math.min(pages.size(), MAX_PAGES); i++) {
                String page = pages.getString(i);
                if (resolved) {
                    try {
                        this.pages.add(ChatSerializer.a(page));
                        continue;
                    } catch (Exception e) {
                        // Ignore and treat as an old book
                    }
                }
                addPage( limit( page, 2048 ) ); // Spigot
            }
        }
    }

    CraftMetaBook(Map<String, Object> map) {
        super(map);

        setAuthor(SerializableMeta.getString(map, BOOK_AUTHOR.BUKKIT, true));

        setTitle(SerializableMeta.getString(map, BOOK_TITLE.BUKKIT, true));

        Iterable<?> pages = SerializableMeta.getObject(Iterable.class, map, BOOK_PAGES.BUKKIT, true);
        if(pages != null) {
            for (Object page : pages) {
                if (page instanceof String) {
                    addPage((String) page);
                }
            }
        }

        generation = SerializableMeta.getObject(Integer.class, map, GENERATION.BUKKIT, true);
    }

    @Override
    void applyToItem(NBTTagCompound itemData) {
        applyToItem(itemData, true);
    }

    void applyToItem(NBTTagCompound itemData, boolean handlePages) {
        super.applyToItem(itemData);

        if (hasTitle()) {
            itemData.setString(BOOK_TITLE.NBT, this.title);
        }

        if (hasAuthor()) {
            itemData.setString(BOOK_AUTHOR.NBT, this.author);
        }

        if (handlePages) {
            if (hasPages()) {
                NBTTagList list = new NBTTagList();
                for (IChatBaseComponent page : pages) {
                    list.add(new NBTTagString(CraftChatMessage.fromComponent(page)));
                }
                itemData.set(BOOK_PAGES.NBT, list);
            }

            itemData.remove(RESOLVED.NBT);
        }

        if (generation != null) {
            itemData.setInt(GENERATION.NBT, generation);
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isBookEmpty();
    }

    boolean isBookEmpty() {
        return !(hasPages() || hasAuthor() || hasTitle());
    }

    @Override
    boolean applicableTo(Material type) {
        switch (type) {
        case WRITTEN_BOOK:
        case WRITABLE_BOOK:
            return true;
        default:
            return false;
        }
    }

    public boolean hasAuthor() {
        return !Strings.isNullOrEmpty(author);
    }

    public boolean hasTitle() {
        return !Strings.isNullOrEmpty(title);
    }

    public boolean hasPages() {
        return !pages.isEmpty();
    }

    public boolean hasGeneration() {
        return generation != null;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean setTitle(final String title) {
        if (title == null) {
            this.title = null;
            return true;
        } else if (title.length() > MAX_TITLE_LENGTH && !OVERRIDE_CHECKS) { // Paper - Add override
            return false;
        }

        this.title = title;
        return true;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    @Override
    public Generation getGeneration() {
        return (generation == null) ? null : Generation.values()[generation];
    }

    @Override
    public void setGeneration(Generation generation) {
        this.generation = (generation == null) ? null : generation.ordinal();
    }

    public String getPage(final int page) {
        Validate.isTrue(isValidPage(page), "Invalid page number");
        return CraftChatMessage.fromComponent(pages.get(page - 1));
    }

    public void setPage(final int page, final String text) {
        if (!isValidPage(page)) {
            throw new IllegalArgumentException("Invalid page number " + page + "/" + pages.size());
        }

        String newText = text == null ? "" : text.length() > MAX_PAGE_LENGTH && !OVERRIDE_CHECKS ? text.substring(0, MAX_PAGE_LENGTH) : text;
        pages.set(page - 1, CraftChatMessage.fromString(newText, true)[0]);
    }

    public void setPages(final String... pages) {
        this.pages.clear();

        addPage(pages);
    }

    public void addPage(final String... pages) {
        for (String page : pages) {
            if (this.pages.size() >= MAX_PAGES && !OVERRIDE_CHECKS) {
                return;
            }

            if (page == null) {
                page = "";
            } else if (page.length() > MAX_PAGE_LENGTH && !OVERRIDE_CHECKS) { // Paper - Add override
                page = page.substring(0, MAX_PAGE_LENGTH);
            }

            this.pages.add(CraftChatMessage.fromString(page, true)[0]);
        }
    }

    public int getPageCount() {
        return pages.size();
    }

    public List<String> getPages() {
        return pages.stream().map(CraftChatMessage::fromComponent).collect(ImmutableList.toImmutableList());
    }

    public void setPages(List<String> pages) {
        this.pages.clear();
        for (String page : pages) {
            addPage(page);
        }
    }

    private boolean isValidPage(int page) {
        return page > 0 && page <= pages.size();
    }

    @Override
    public CraftMetaBook clone() {
        CraftMetaBook meta = (CraftMetaBook) super.clone();
        meta.pages = new ArrayList<IChatBaseComponent>(pages);
        return meta;
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (hasTitle()) {
            hash = 61 * hash + this.title.hashCode();
        }
        if (hasAuthor()) {
            hash = 61 * hash + 13 * this.author.hashCode();
        }
        if (hasPages()) {
            hash = 61 * hash + 17 * this.pages.hashCode();
        }
        if (hasGeneration()) {
            hash = 61 * hash + 19 * this.generation.hashCode();
        }
        return original != hash ? CraftMetaBook.class.hashCode() ^ hash : hash;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaBook) {
            CraftMetaBook that = (CraftMetaBook) meta;

            return (hasTitle() ? that.hasTitle() && this.title.equals(that.title) : !that.hasTitle())
                    && (hasAuthor() ? that.hasAuthor() && this.author.equals(that.author) : !that.hasAuthor())
                    && (hasPages() ? that.hasPages() && this.pages.equals(that.pages) : !that.hasPages())
                    && (hasGeneration() ? that.hasGeneration() && this.generation.equals(that.generation) : !that.hasGeneration());
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaBook || isBookEmpty());
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);

        if (hasTitle()) {
            builder.put(BOOK_TITLE.BUKKIT, title);
        }

        if (hasAuthor()) {
            builder.put(BOOK_AUTHOR.BUKKIT, author);
        }

        if (hasPages()) {
            List<String> pagesString = new ArrayList<String>();
            for (IChatBaseComponent comp : pages) {
                pagesString.add(CraftChatMessage.fromComponent(comp));
            }
            builder.put(BOOK_PAGES.BUKKIT, pagesString);
        }

        if (generation != null) {
            builder.put(GENERATION.BUKKIT, generation);
        }

        return builder;
    }

    // Spigot start
    private final BookMeta.Spigot spigot = new BookMeta.Spigot() {

        @Override
        public BaseComponent[] getPage(final int page) {
            Validate.isTrue(isValidPage(page), "Invalid page number");
            return ComponentSerializer.parse(IChatBaseComponent.ChatSerializer.a(pages.get(page - 1)));
        }

        @Override
        public void setPage(final int page, final BaseComponent... text) {
            if (!isValidPage(page)) {
                throw new IllegalArgumentException("Invalid page number " + page + "/" + pages.size());
            }

            BaseComponent[] newText = text == null ? new BaseComponent[0] : text;
            CraftMetaBook.this.pages.set(page - 1, IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(newText)));
        }

        @Override
        public void setPages(final BaseComponent[]... pages) {
            CraftMetaBook.this.pages.clear();

            addPage(pages);
        }

        @Override
        public void addPage(final BaseComponent[]... pages) {
            for (BaseComponent[] page : pages) {
                if (CraftMetaBook.this.pages.size() >= MAX_PAGES) {
                    return;
                }

                if (page == null) {
                    page = new BaseComponent[0];
                }

                CraftMetaBook.this.pages.add(IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(page)));
            }
        }

        @Override
        public List<BaseComponent[]> getPages() {
            final List<IChatBaseComponent> copy = ImmutableList.copyOf(CraftMetaBook.this.pages);
            return new AbstractList<BaseComponent[]>() {

                @Override
                public BaseComponent[] get(int index) {
                    return ComponentSerializer.parse(IChatBaseComponent.ChatSerializer.a(copy.get(index)));
                }

                @Override
                public int size() {
                    return copy.size();
                }
            };
        }

        @Override
        public void setPages(List<BaseComponent[]> pages) {
            CraftMetaBook.this.pages.clear();
            for (BaseComponent[] page : pages) {
                addPage(page);
            }
        }
    };

    @Override
    public BookMeta.Spigot spigot() {
        return spigot;
    }
    // Spigot end
}
