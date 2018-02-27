package work.mojamoja.mineall;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * @author : akira.shinohara
 * @since : 2018/02/28
 */
public class EventListener implements Listener {
    private Tool tool = new Tool();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        switch (block.getType().toString()) {
            case "LOG": {
                ItemStack itemInHand = event.getPlayer().getItemInHand();

                if (tool.getType(itemInHand).equals("AXE")) {
                    Location location = block.getLocation();
                    short durability = itemInHand.getDurability();

                    short toolDamage = cutLog(location);

                    // Unbreakingエンチャントの処理
                    switch (itemInHand.getEnchantmentLevel(Enchantment.DURABILITY)) {
                        case 1:
                            toolDamage *= 0.5;
                            break;
                        case 2:
                            toolDamage *= 0.33;
                            break;
                        case 3:
                            toolDamage *= 0.25;
                            break;

                        default:
                    }

                    durability += toolDamage;
                    // 耐久値の適用
                    itemInHand.setDurability(durability);
                    // なぜか耐久値が0になってもツールが壊れないので強制的に破棄
                    if (itemInHand.getType().getMaxDurability() - itemInHand.getDurability() <= 0) {
                        itemInHand.setAmount(0);
                    }
                }
                break;
            }
            default:
        }
    }

    /**
     * 隣接したブロックを再帰的に判定して原木を一括破壊するメソッド
     * @author : akira.shinohara
     * @since : 2018/02/28
     * @param location : 起点ブロックの座標
     */
    private short cutLog(Location location) {
        Location newLocation;
        short toolDamage = 0;
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

            if (newLocation.getBlock().getType() == Material.LOG) {
                newLocation.getBlock().breakNaturally();
                toolDamage++;

                toolDamage += cutLog(newLocation);
            }
        }

        return toolDamage;
    }
}
