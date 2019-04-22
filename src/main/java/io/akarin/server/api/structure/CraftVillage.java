package io.akarin.server.api.structure;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CraftVillage implements Village {
    private final net.minecraft.server.Village village;
    
    public Village getHandle() {
        return (Village) village;
    }

    @Override
    public String toString() {
        return "CraftVillage";
    }
}
