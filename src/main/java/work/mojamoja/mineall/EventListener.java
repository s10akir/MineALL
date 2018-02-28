package work.mojamoja.mineall;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static org.bukkit.Material.AIR;

/**
 * @author : akira.shinohara
 * @since : 2018/02/28
 */
public class EventListener implements Listener {
    private Tool tool = new Tool();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        switch (block.getType()) {
            case LOG: {
                ItemStack itemInHand = event.getPlayer().getItemInHand();

                if (tool.getType(itemInHand).equals("AXE")) {
                    mineAll(event);
                }
                break;
            }

            case COAL_ORE:
            case IRON_ORE:
            case GOLD_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
            case EMERALD_ORE:
            case DIAMOND_ORE: {
                ItemStack itemInHand = event.getPlayer().getItemInHand();

                if (tool.getType(itemInHand).equals("PICKAXE")) {
                    // シルクタッチの場合通常ドロップを無効化
                    if (itemInHand.getEnchantmentLevel(Enchantment.SILK_TOUCH) == 1) {
                        event.setDropItems(false);
                    }
                    mineAll(event);
                }
                break;
            }

            default:
        }
        Bukkit.broadcastMessage(event.getBlock().toString());
    }

    /**
     * 同じ種類のブロックを一括破壊しツールにダメージを与えるメソッド
     * @author : akira.shinohara
     * @since : 2018/02/28
     * @param event : BlockBreakEvent
     */
    private void mineAll(BlockBreakEvent event) {
        Block block = event.getBlock();
        ItemStack itemInHand = event.getPlayer().getItemInHand();

        Location location = block.getLocation();
        short durability = itemInHand.getDurability();

        short toolDamage = recursiveMine(location, block.getType(), itemInHand);

        // Unbreakingエンチャントの処理
        short tmp = toolDamage;
        switch (itemInHand.getEnchantmentLevel(Enchantment.DURABILITY)) {
            case 1:
                tmp *= 0.5;
                break;
            case 2:
                tmp *= 0.33;
                break;
            case 3:
                tmp *= 0.25;
                break;

            default:
        }
        if (tmp > 0) {
            toolDamage = tmp;
        }

        // シルクタッチで一括破壊がおこならかった場合のドロップ
        if (toolDamage == 0 && itemInHand.getEnchantmentLevel(Enchantment.SILK_TOUCH) == 1) {
            location.getWorld().dropItemNaturally(location, new ItemStack(event.getBlock().getType()));
        }

        durability += toolDamage;
        // 耐久値の適用
        itemInHand.setDurability(durability);
        // なぜか耐久値が0になってもツールが壊れないので強制的に破棄
        if (itemInHand.getType().getMaxDurability() - itemInHand.getDurability() <= 0) {
            itemInHand.setAmount(0);
        }
    }

    /**
     * 隣接したブロックを再帰的に判定して原木を一括破壊するメソッド
     * @author : akira.shinohara
     * @since : 2018/02/28
     * @param location : 起点ブロックの座標
     */
    private short recursiveMine(Location location, Material material, ItemStack itemInHand) {
        Location newLocation;
        short toolDamage = 0;
        Material blockType = material;
        Vector[] vectors = {new Vector(1, 0, 0),
                            new Vector(-1, 0 , 0),
                            new Vector(0, 1 , 0),
                            new Vector(0, -1 , 0),
                            new Vector(0, 0 , 1),
                            new Vector(0, 0 , -1)
        };

        for (Vector vector: vectors) {
            newLocation = location.clone();
            newLocation.add(vector);
            if (newLocation.getBlock().getType() == material) {
                // シルクタッチの場合ブロックをAIRで置き換えドロップすることで擬似的に再現
                if (itemInHand.getEnchantmentLevel(Enchantment.SILK_TOUCH) == 1) {
                    newLocation.getBlock().setType(AIR);
                    newLocation.getWorld().dropItemNaturally(newLocation, new ItemStack(blockType));
                } else {
                    newLocation.getBlock().breakNaturally(itemInHand);
                }
                toolDamage++;

                toolDamage += recursiveMine(newLocation, blockType, itemInHand);
            }
        }

        return toolDamage;
    }
}
