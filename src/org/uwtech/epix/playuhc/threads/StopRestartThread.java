package org.uwtech.epix.playuhc.threads;

import org.uwtech.epix.playuhc.PlayUhc;
import org.uwtech.epix.playuhc.game.GameManager;
import org.uwtech.epix.playuhc.languages.Lang;
import org.bukkit.Bukkit;

public class StopRestartThread implements Runnable{

	long timeBeforeStop;
	
	
	public StopRestartThread(){
		this.timeBeforeStop = GameManager.getGameManager().getConfiguration().getTimeBeforeRestartAfterEnd();
	}
	
	@Override
	public void run() {
		GameManager gm = GameManager.getGameManager();
			
			if(timeBeforeStop == 0){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
			}else{
				if(timeBeforeStop<5 || timeBeforeStop%10 == 0){
					Bukkit.getLogger().info("[PlayUhc] Server will shutdown in "+timeBeforeStop+"s");
					gm.broadcastInfoMessage(Lang.GAME_SHUTDOWN.replace("%time%", ""+timeBeforeStop));
				}
				
				timeBeforeStop--;
				Bukkit.getScheduler().scheduleSyncDelayedTask(PlayUhc.getPlugin(), this,20);
				}
	}

}
