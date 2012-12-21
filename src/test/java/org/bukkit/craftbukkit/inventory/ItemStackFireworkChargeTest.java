package org.bukkit.craftbukkit.inventory;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Joiner;

@RunWith(Parameterized.class)
public class ItemStackFireworkChargeTest extends ItemStackTest {

    @Parameters(name="[{index}]:{" + NAME_PARAMETER + "}")
    public static List<Object[]> data() {
        return StackProvider.compound(operators(), "%s %s", NAME_PARAMETER, Material.FIREWORK_CHARGE);
    }

    @SuppressWarnings("unchecked")
    static List<Object[]> operators() {
        return CompoundOperator.compound(
            Joiner.on('+'),
            NAME_PARAMETER,
            Long.parseLong("10", 2),
            ItemStackLoreEnchantmentTest.operators(),
            Arrays.asList(
                new Object[] {
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            FireworkEffectMeta meta = (FireworkEffectMeta) cleanStack.getItemMeta();
                            meta.setEffect(FireworkEffect.builder().withColor(Color.WHITE).build());
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        }
                    },
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            FireworkEffectMeta meta = (FireworkEffectMeta) cleanStack.getItemMeta();
                            meta.setEffect(FireworkEffect.builder().withColor(Color.BLACK).build());
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        }
                    },
                    "Effect Color 1 vs. Effect Color 2"
                },
                new Object[] {
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            FireworkEffectMeta meta = (FireworkEffectMeta) cleanStack.getItemMeta();
                            meta.setEffect(FireworkEffect.builder().withColor(Color.WHITE).with(Type.CREEPER).build());
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        }
                    },
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            FireworkEffectMeta meta = (FireworkEffectMeta) cleanStack.getItemMeta();
                            meta.setEffect(FireworkEffect.builder().withColor(Color.WHITE).with(Type.BURST).build());
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        }
                    },
                    "Effect type 1 vs. Effect type 2"
                },
                new Object[] {
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            FireworkEffectMeta meta = (FireworkEffectMeta) cleanStack.getItemMeta();
                            meta.setEffect(FireworkEffect.builder().withColor(Color.WHITE).withFade(Color.BLUE).build());
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        }
                    },
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            FireworkEffectMeta meta = (FireworkEffectMeta) cleanStack.getItemMeta();
                            meta.setEffect(FireworkEffect.builder().withColor(Color.WHITE).withFade(Color.RED).build());
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        }
                    },
                    "Effect fade 1 vs. Effect fade 2"
                },
                new Object[] {
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            FireworkEffectMeta meta = (FireworkEffectMeta) cleanStack.getItemMeta();
                            meta.setEffect(FireworkEffect.builder().withColor(Color.WHITE).withFlicker().build());
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        }
                    },
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            FireworkEffectMeta meta = (FireworkEffectMeta) cleanStack.getItemMeta();
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        }
                    },
                    "Effect vs. Null"
                },
                new Object[] {
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            FireworkEffectMeta meta = (FireworkEffectMeta) cleanStack.getItemMeta();
                            meta.setEffect(FireworkEffect.builder().withColor(Color.WHITE).withTrail().build());
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        }
                    },
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            return cleanStack;
                        }
                    },
                    "Effect vs. None"
                }
            )
        );
    }
}
