package org.uwtech.epix.epixuhc.commands;

import org.uwtech.epix.epixuhc.exceptions.UhcPlayerDoesntExistException;
import org.uwtech.epix.epixuhc.game.GameManager;
import org.uwtech.epix.epixuhc.game.GameState;
import org.uwtech.epix.epixuhc.players.PlayerState;
import org.uwtech.epix.epixuhc.players.UhcPlayer;
import org.uwtech.epix.epixuhc.players.UhcTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandManager {
	public static void setGameState(GameState gameState){
		GameManager.getGameManager().setGameState(gameState);
	}

	public static void setPlayerState(Player player, PlayerState state) {
		try {
			GameManager.getGameManager().getPlayersManager().getUhcPlayer(player).setState(state);
		} catch (UhcPlayerDoesntExistException e) {
		}
	}

	public static void setPvp(boolean state) {
		GameManager.getGameManager().setPvp(state);
	}

	public static void listUhcPlayers() {
		StringBuilder str = new StringBuilder();
		str.append("Current UhcPlayers : ");
		for(UhcPlayer player : GameManager.getGameManager().getPlayersManager().getPlayersList()){
			str.append(player.getName()+" ");
		}
		Bukkit.getLogger().info(str.toString());
	}

	public static void listUhcTeams() {
		StringBuilder str;
		Bukkit.getLogger().info("Current UhcTeams : ");
		
		for(UhcTeam team : GameManager.getGameManager().getPlayersManager().listUhcTeams()){
			str = new StringBuilder();
			str.append("Team "+team.getLeader().getName()+" : ");
			for(UhcPlayer player : team.getMembers()){
				str.append(player.getName()+" ");
			}
			Bukkit.getLogger().info(str.toString());
		}
	}
}
