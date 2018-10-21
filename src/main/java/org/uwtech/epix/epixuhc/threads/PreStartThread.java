package org.uwtech.epix.epixuhc.threads;

import org.uwtech.epix.epixuhc.EpixUHC;
import org.uwtech.epix.epixuhc.configuration.MainConfiguration;
import org.uwtech.epix.epixuhc.game.GameManager;
import org.uwtech.epix.epixuhc.languages.Lang;
import org.uwtech.epix.epixuhc.players.UhcTeam;
import org.uwtech.epix.epixuhc.sounds.UHCSound;
import org.uwtech.epix.epixuhc.utils.TimeUtils;
import org.bukkit.Bukkit;

import java.util.List;

public class PreStartThread implements Runnable{

	private static PreStartThread instance;
	
	private int timeBeforeStart;
	private int remainingTime;
	private int minPlayers;
	private boolean pause;
	private boolean force;
	private PreStartThread task;
	
	public PreStartThread(){
		
		
		MainConfiguration cfg = GameManager.getGameManager().getConfiguration();
		instance = this;
		this.timeBeforeStart = cfg.getTimeBeforeStartWhenReady();
		this.remainingTime = cfg.getTimeBeforeStartWhenReady();
		this.minPlayers = cfg.getMinPlayersToStart();
		this.pause = false;
		this.force = false;
		this.task = this;
	}
	
	public static String togglePause(){
		instance.pause = !instance.pause;
		return "pause:"+instance.pause+"  "+"force:"+instance.force;
	}
	
	public static String toggleForce(){
		instance.force = !instance.force;
		return "pause:"+instance.pause+"  "+"force:"+instance.force;
	}
	
	@Override
	public void run() {
		
		Bukkit.getScheduler().runTask(EpixUHC.getPlugin(), new Runnable(){

			@Override
			public void run() {
				
				GameManager gm = GameManager.getGameManager();
				List<UhcTeam> teams = gm.getPlayersManager().listUhcTeams();
				double readyTeams = 0;
				double teamsNumber = (double) teams.size();
				for(UhcTeam team : teams){
					if(team.isReadyToStart() && team.isOnline())
						readyTeams+=1;
				}
				
				double percentageReadyTeams = 100*readyTeams/teamsNumber;
				int playersNumber = Bukkit.getOnlinePlayers().size();
				
				if(force || (!pause && (remainingTime < 5 || (playersNumber >= minPlayers && readyTeams >= gm.getConfiguration().getMinimalReadyTeamsToStart() && percentageReadyTeams >= gm.getConfiguration().getMinimalReadyTeamsPercentageToStart())))){
						if(remainingTime == timeBeforeStart+1){
							gm.broadcastInfoMessage(Lang.GAME_ENOUGH_TEAMS_READY);
							gm.broadcastInfoMessage(Lang.GAME_STARTING_IN.replace("%time%", ""+ TimeUtils.getFormattedTime(remainingTime)));
							gm.getPlayersManager().playSoundToAll(UHCSound.CLICK);
						}else if((remainingTime > 0 && remainingTime <= 10) || (remainingTime > 0 && remainingTime%10 == 0)){
							gm.broadcastInfoMessage(Lang.GAME_STARTING_IN.replace("%time%", ""+remainingTime));
							gm.getPlayersManager().playSoundToAll(UHCSound.CLICK);
						}
						
						remainingTime--;
						
						if(remainingTime == -1)
							Bukkit.getScheduler().runTask(EpixUHC.getPlugin(), new Runnable(){

								@Override
								public void run() {
									GameManager.getGameManager().startGame();
								}
							});
						else		
							Bukkit.getScheduler().runTaskLaterAsynchronously(EpixUHC.getPlugin(), task,20);
				}else{
					if(pause == false && remainingTime < timeBeforeStart+1){
						gm.broadcastInfoMessage(Lang.GAME_STARTING_CANCELLED);
					}
					remainingTime = timeBeforeStart+1;
					Bukkit.getScheduler().runTaskLaterAsynchronously(EpixUHC.getPlugin(), task,20);
				}
			}
			
		});
		
	}

}
