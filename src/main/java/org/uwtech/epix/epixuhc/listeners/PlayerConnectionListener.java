package org.uwtech.epix.epixuhc.listeners;

import org.uwtech.epix.epixuhc.EpixUHC;
import org.uwtech.epix.epixuhc.exceptions.UhcPlayerDoesntExistException;
import org.uwtech.epix.epixuhc.exceptions.UhcPlayerJoinException;
import org.uwtech.epix.epixuhc.exceptions.UhcTeamException;
import org.uwtech.epix.epixuhc.game.GameManager;
import org.uwtech.epix.epixuhc.game.GameState;
import org.uwtech.epix.epixuhc.players.PlayerState;
import org.uwtech.epix.epixuhc.players.UhcPlayer;
import org.uwtech.epix.epixuhc.threads.KillDisconnectedPlayerThread;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerConnectionListener implements Listener{
	
	private List<String> kickedPlayersWhileJoining;
	
	public PlayerConnectionListener(){
		kickedPlayersWhileJoining = Collections.synchronizedList(new ArrayList<String>());
	}
	
	private List<String> getKickedPlayersWhileJoining() {
		return kickedPlayersWhileJoining;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event){
		GameManager gm = GameManager.getGameManager();
		
		try {
			boolean allowedToJoin = gm.getPlayersManager().isPlayerAllowedToJoin(event.getPlayer());
			if(allowedToJoin){
				event.setResult(Result.ALLOWED);
			}else{
				throw new UhcPlayerJoinException("An unexpected error as occured.");
			}
		}catch(final UhcPlayerJoinException e){
			event.setKickMessage(e.getMessage());
			event.setResult(Result.KICK_OTHER);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerJoin(final PlayerJoinEvent event){
		Bukkit.getScheduler().runTaskLater(EpixUHC.getPlugin(), () -> GameManager.getGameManager().getPlayersManager().playerJoinsTheGame(event.getPlayer()), 1);

	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerDisconnect(PlayerQuitEvent event){
			if(getKickedPlayersWhileJoining().contains(event.getPlayer().getName())){
				getKickedPlayersWhileJoining().remove(event.getPlayer().getName());
				return;
			}
			GameManager gm = GameManager.getGameManager();
			if(gm.getGameState().equals(GameState.WAITING) || gm.getGameState().equals(GameState.STARTING)){
				UhcPlayer uhcPlayer = null;
				try {
					uhcPlayer = gm.getPlayersManager().getUhcPlayer(event.getPlayer());
					if(gm.getGameState().equals(GameState.STARTING)){
						gm.getPlayersManager().setPlayerSpectateAtLobby(uhcPlayer);
						gm.broadcastInfoMessage(uhcPlayer.getName()+" has left while the game was starting and has been killed.");
						gm.getPlayersManager().strikeLightning(uhcPlayer);
					}
					uhcPlayer.getTeam().leave(uhcPlayer);
				}catch (UhcPlayerDoesntExistException | UhcTeamException e) {
				}

				if(uhcPlayer != null)
					gm.getPlayersManager().getPlayersList().remove(uhcPlayer);
			}
			
			if(gm.getGameState().equals(GameState.PLAYING) || gm.getGameState().equals(GameState.DEATHMATCH)){
				try {
					UhcPlayer uhcPlayer = gm.getPlayersManager().getUhcPlayer(event.getPlayer());
					if(gm.getConfiguration().getEnableKillDisconnectedPlayers() && uhcPlayer.getState().equals(PlayerState.PLAYING)){
						Bukkit.getScheduler().runTaskLaterAsynchronously(EpixUHC.getPlugin(), new KillDisconnectedPlayerThread(event.getPlayer().getName()),1);
					}
					gm.getPlayersManager().checkIfRemainingPlayers();
				}catch (UhcPlayerDoesntExistException e) {
				}
			}
	}
}
