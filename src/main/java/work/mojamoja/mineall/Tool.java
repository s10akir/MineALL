package work.mojamoja.mineall;

import org.bukkit.inventory.ItemStack;

/**
 * @author : akira.shinohara
 * @since : 2018/02/28
 */
public class Tool {
    public String getType(ItemStack tool) {
        switch (tool.getType()) {
            case WOOD_AXE:
            case STONE_AXE:
            case IRON_AXE:
            case DIAMOND_AXE:
                return "AXE";

            case WOOD_SPADE:
            case STONE_SPADE:
            case IRON_SPADE:
            case DIAMOND_SPADE:
                return "SPADE";

            case WOOD_PICKAXE:
            case STONE_PICKAXE:
            case IRON_PICKAXE:
            case DIAMOND_PICKAXE:
                return "PICKAXE";

            default:
                return "";
        }
    }
}
