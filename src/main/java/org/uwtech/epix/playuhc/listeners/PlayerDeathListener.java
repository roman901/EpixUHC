package org.uwtech.epix.playuhc.listeners;

import org.uwtech.epix.playuhc.PlayUhc;
import org.uwtech.epix.playuhc.configuration.MainConfiguration;
import org.uwtech.epix.playuhc.customitems.UhcItems;
import org.uwtech.epix.playuhc.events.UHCPlayerKillEvent;
import org.uwtech.epix.playuhc.exceptions.UhcPlayerDoesntExistException;
import org.uwtech.epix.playuhc.game.GameManager;
import org.uwtech.epix.playuhc.languages.Lang;
import org.uwtech.epix.playuhc.players.PlayerState;
import org.uwtech.epix.playuhc.players.PlayersManager;
import org.uwtech.epix.playuhc.players.UhcPlayer;
import org.uwtech.epix.playuhc.threads.TimeBeforeSendBungeeThread;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerDeathListener implements Listener {

	public PlayerDeathListener(){
		GameManager gm = GameManager.getGameManager();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		GameManager gm = GameManager.getGameManager();
		PlayersManager pm = gm.getPlayersManager();
		MainConfiguration cfg = gm.getConfiguration();
		UhcPlayer uhcPlayer;
		try {
			uhcPlayer = pm.getUhcPlayer(player);
			if(uhcPlayer.getState().equals(PlayerState.PLAYING)){
				
				// kill event
				Player killer = player.getKiller();
				UHCPlayerKillEvent killEvent;
				if(killer != null){
					killEvent = new UHCPlayerKillEvent(uhcPlayer, pm.getUhcPlayer(player.getKiller()));
					Bukkit.getServer().getPluginManager().callEvent(killEvent);
				}
				
				
				// eliminations
				gm.broadcastInfoMessage(Lang.PLAYERS_ELIMINATED.replace("%player%", player.getName()));
				if(cfg.getRegenHeadDropOnPlayerDeath()){
					UhcItems.spawnRegenHead(player);
				}
				if(cfg.getEnableExpDropOnDeath()){
					UhcItems.spawnExtraXp(player.getLocation(), cfg.getExpDropOnDeath());
				}
				uhcPlayer.setState(PlayerState.DEAD);
				pm.strikeLightning(uhcPlayer);
				pm.playSoundPlayerDeath();
				if(!cfg.getCanSpectateAfterDeath()){
					player.kickPlayer(Lang.DISPLAY_MESSAGE_PREFIX+" "+ Lang.KICK_DEAD);
				}
				if(cfg.getEnableBungeeSupport() && cfg.getTimeBeforeSendBungeeAfterDeath() >= 0){
					Bukkit.getScheduler().runTaskAsynchronously(PlayUhc.getPlugin(), new TimeBeforeSendBungeeThread(uhcPlayer, cfg.getTimeBeforeSendBungeeAfterDeath()));
				}
				pm.checkIfRemainingPlayers();
			}else{
				player.kickPlayer("Don't cheat !");
			}
		} catch (UhcPlayerDoesntExistException e) {
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		GameManager gm = GameManager.getGameManager();
		final PlayersManager pm = gm.getPlayersManager();
		final UhcPlayer uhcPlayer;
		try {
			uhcPlayer = pm.getUhcPlayer(player);

			if(uhcPlayer.getState().equals(PlayerState.DEAD)){
				
				Bukkit.getScheduler().runTaskLater(PlayUhc.getPlugin(), new Runnable(){

					@Override
					public void run() {
						pm.setPlayerSpectateAtLobby(uhcPlayer);
					}}, 1);
			}
		} catch (UhcPlayerDoesntExistException e) {
		}
	}
	
}
