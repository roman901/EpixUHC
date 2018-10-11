package org.uwtech.epix.playuhc.listeners;

import org.uwtech.epix.playuhc.customitems.Craft;
import org.uwtech.epix.playuhc.customitems.CraftsManager;
import org.uwtech.epix.playuhc.exceptions.UhcPlayerDoesntExistException;
import org.uwtech.epix.playuhc.game.GameManager;
import org.uwtech.epix.playuhc.languages.Lang;
import org.uwtech.epix.playuhc.players.UhcPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCrafting(CraftItemEvent event){
		
		// Banned craft prevention
		if(CraftsManager.isBannedCraft(event.getRecipe().getResult())){
			event.getWhoClicked().sendMessage(ChatColor.RED+ Lang.ITEMS_CRAFT_BANNED);
			event.setCancelled(true);
		}else{

			Craft craft = CraftsManager.getCraft(event.getRecipe().getResult());
			if(craft != null){
				HumanEntity human = event.getWhoClicked();
				if(human instanceof Player){
					Player player = (Player) human;
					
					try {
						UhcPlayer uhcPlayer = GameManager.getGameManager().getPlayersManager().getUhcPlayer(player);
						if(GameManager.getGameManager().getConfiguration().getEnableCraftsPermissions() && !player.hasPermission("playuhc.craft."+craft.getName())){
							uhcPlayer.sendMessage(ChatColor.RED+ Lang.ITEMS_CRAFT_NO_PERMISSION.replace("%craft%",craft.getName()));
							event.setCancelled(true);
						}else{
							if(craft.getLimit() != -1 && (event.isShiftClick() || event.isRightClick())){
								uhcPlayer.sendMessage(ChatColor.RED+ Lang.ITEMS_CRAFT_LEFT_CLICK.replace("%craft%", craft.getName()));
								event.setCancelled(true);
							}else{
								if(!uhcPlayer.addCraftedItem(craft.getName())){
									uhcPlayer.sendMessage(ChatColor.RED+ Lang.ITEMS_CRAFT_LIMIT.replace("%craft%", craft.getName()).replace("%limit%",""+craft.getLimit()));
									event.setCancelled(true);
								}else{
									uhcPlayer.sendMessage(ChatColor.GREEN+ Lang.ITEMS_CRAFT_CRAFTED.replace("%craft%", craft.getName()));
								}
							}
						}
					} catch (UhcPlayerDoesntExistException e) {
						// No craft for offline players
					}
					
				}
				
			}
			
		}
	}
}
