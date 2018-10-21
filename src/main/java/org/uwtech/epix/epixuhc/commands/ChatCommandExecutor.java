package org.uwtech.epix.epixuhc.commands;

import org.uwtech.epix.epixuhc.exceptions.UhcPlayerDoesntExistException;
import org.uwtech.epix.epixuhc.game.GameManager;
import org.uwtech.epix.epixuhc.game.GameState;
import org.uwtech.epix.epixuhc.languages.Lang;
import org.uwtech.epix.epixuhc.players.PlayerState;
import org.uwtech.epix.epixuhc.players.UhcPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommandExecutor implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if(sender instanceof Player){
			Player player = (Player) sender;
			GameManager gm = GameManager.getGameManager();
			UhcPlayer uhcPlayer;
			try {
				uhcPlayer = gm.getPlayersManager().getUhcPlayer(player);
				if(uhcPlayer != null && uhcPlayer.getState().equals(PlayerState.PLAYING) && gm.getGameState().equals(GameState.PLAYING)){
					if(args.length == 0){
						if(uhcPlayer.isGlobalChat()){
							uhcPlayer.setGlobalChat(false);
							uhcPlayer.sendMessage(ChatColor.GREEN+ Lang.COMMAND_CHAT_TEAM);
						}
						else{
							uhcPlayer.setGlobalChat(true);
							uhcPlayer.sendMessage(ChatColor.GREEN+ Lang.COMMAND_CHAT_GLOBAL);
						}
					}else{
						player.sendMessage(ChatColor.GRAY+ Lang.COMMAND_CHAT_HELP);
					}
				}else{
					player.sendMessage(ChatColor.RED+ Lang.COMMAND_CHAT_ERROR);
				}
				

				return true;
			} catch (UhcPlayerDoesntExistException e) {
			}
			
		}
		

		return false;
	}

}
