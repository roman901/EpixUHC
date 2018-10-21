package org.uwtech.epix.epixuhc.listeners;

import org.uwtech.epix.epixuhc.exceptions.UhcPlayerDoesntExistException;
import org.uwtech.epix.epixuhc.game.GameManager;
import org.uwtech.epix.epixuhc.players.PlayerState;
import org.uwtech.epix.epixuhc.players.UhcPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener{

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        GameManager gm = GameManager.getGameManager();

        if (e.isCancelled()){
            return;
        }

        UhcPlayer uhcPlayer;
        try {
            uhcPlayer = gm.getPlayersManager().getUhcPlayer(player);
        } catch (UhcPlayerDoesntExistException ex) {
            return;
        }

        // Stop spec chat
        if(!gm.getConfiguration().getCanSendMessagesAfterDeath() && uhcPlayer.getState() == PlayerState.DEAD){
            uhcPlayer.sendMessage(ChatColor.RED + "You are not allowed to send messaged!");
            e.setCancelled(true);
            return;
        }

        // Team chat
        if (!uhcPlayer.isGlobalChat() && uhcPlayer.getState() == PlayerState.PLAYING){
            e.setCancelled(true);
            uhcPlayer.getTeam().sendChatMessageToTeamMembers(
                    ChatColor.WHITE + uhcPlayer.getName() + " : " + e.getMessage()
            );
        }

    }

}