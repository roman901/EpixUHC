package org.uwtech.epix.playuhc.customitems;

import org.uwtech.epix.playuhc.exceptions.UhcPlayerNotOnlineException;
import org.uwtech.epix.playuhc.players.UhcPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemFactory {

    public static ItemStack createPlayerSkull(UhcPlayer player){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);

        try {
            SkullMeta im = (SkullMeta) item.getItemMeta();
            im.setOwningPlayer(player.getPlayer());
            item.setItemMeta(im);
        }catch (UhcPlayerNotOnlineException ex){
            // No custom skull
        }
        return item;
    }

    public static ItemStack createPlayerSkull(Player player){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);

        SkullMeta im = (SkullMeta) item.getItemMeta();
        im.setOwningPlayer(player);
        item.setItemMeta(im);

        return item;
    }
	
}