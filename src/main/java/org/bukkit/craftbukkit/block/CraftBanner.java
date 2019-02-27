package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.EnumColor;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.TileEntityBanner;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;

public class CraftBanner extends CraftBlockEntityState<TileEntityBanner> implements Banner {

    private DyeColor base;
    private List<Pattern> patterns;

    public CraftBanner(final Block block) {
        super(block, TileEntityBanner.class);
    }

    public CraftBanner(final Material material, final TileEntityBanner te) {
        super(material, te);
    }

    @Override
    public void load(TileEntityBanner banner) {
        super.load(banner);

        if (banner.color != null) {
            base = DyeColor.getByWoolData((byte) banner.color.getColorIndex());
        }
        patterns = new ArrayList<Pattern>();

        if (banner.patterns != null) {
            for (int i = 0; i < banner.patterns.size(); i++) {
                NBTTagCompound p = (NBTTagCompound) banner.patterns.get(i);
                patterns.add(new Pattern(DyeColor.getByWoolData((byte) p.getInt("Color")), PatternType.getByIdentifier(p.getString("Pattern"))));
            }
        }
    }

    @Override
    public DyeColor getBaseColor() {
        return this.base;
    }

    @Override
    public void setBaseColor(DyeColor color) {
        Preconditions.checkArgument(color != null, "color");
        this.base = color;
    }

    @Override
    public List<Pattern> getPatterns() {
        return new ArrayList<Pattern>(patterns);
    }

    @Override
    public void setPatterns(List<Pattern> patterns) {
        this.patterns = new ArrayList<Pattern>(patterns);
    }

    @Override
    public void addPattern(Pattern pattern) {
        this.patterns.add(pattern);
    }

    @Override
    public Pattern getPattern(int i) {
        return this.patterns.get(i);
    }

    @Override
    public Pattern removePattern(int i) {
        return this.patterns.remove(i);
    }

    @Override
    public void setPattern(int i, Pattern pattern) {
        this.patterns.set(i, pattern);
    }

    @Override
    public int numberOfPatterns() {
        return patterns.size();
    }

    @Override
    public void applyTo(TileEntityBanner banner) {
        super.applyTo(banner);

        banner.color = EnumColor.fromColorIndex(base.getWoolData());

        NBTTagList newPatterns = new NBTTagList();

        for (Pattern p : patterns) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInt("Color", p.getColor().getWoolData());
            compound.setString("Pattern", p.getPattern().getIdentifier());
            newPatterns.add(compound);
        }
        banner.patterns = newPatterns;
    }
}
