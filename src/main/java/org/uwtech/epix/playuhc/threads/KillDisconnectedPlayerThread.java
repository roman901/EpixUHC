package org.uwtech.epix.playuhc.threads;

import org.uwtech.epix.playuhc.PlayUhc;
import org.uwtech.epix.playuhc.exceptions.UhcPlayerDoesntExistException;
import org.uwtech.epix.playuhc.game.GameManager;
import org.uwtech.epix.playuhc.game.GameState;
import org.uwtech.epix.playuhc.languages.Lang;
import org.uwtech.epix.playuhc.players.PlayerState;
import org.uwtech.epix.playuhc.players.PlayersManager;
import org.uwtech.epix.playuhc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KillDisconnectedPlayerThread implements Runnable {
	
	String name;
	int timeLeft;
	KillDisconnectedPlayerThread task;
	
	public KillDisconnectedPlayerThread(String playerName) {
		name = playerName;
		timeLeft = GameManager.getGameManager().getConfiguration().getMaxDisconnectPlayersTime();
		task = this;
	}

	@Override
	public void run() {
		if(GameManager.getGameManager().getGameState().equals(GameState.PLAYING)){
			Bukkit.getScheduler().runTask(PlayUhc.getPlugin(), new Runnable(){

					@Override
					public void run() {
						Player player = Bukkit.getPlayer(name);
						if(player == null){
							if(timeLeft <= 0){
								UhcPlayer uhcPlayer;
								GameManager gm = GameManager.getGameManager();
								PlayersManager pm = gm.getPlayersManager();
								try {
									uhcPlayer = pm.getUhcPlayer(name);
									gm.broadcastInfoMessage(Lang.PLAYERS_ELIMINATED.replace("%player%", name));
									uhcPlayer.setState(PlayerState.DEAD);
									pm.strikeLightning(uhcPlayer);
									pm.playSoundPlayerDeath();
									pm.checkIfRemainingPlayers();
								} catch (UhcPlayerDoesntExistException e) {
								}
							}else{
								timeLeft-=5;
								Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), task, 100);
							}
						}
						
					}});
		}
		
	}

}
