package org.uwtech.epix.epixuhc.threads;

import org.uwtech.epix.epixuhc.EpixUHC;
import org.uwtech.epix.epixuhc.game.GameManager;
import org.uwtech.epix.epixuhc.languages.Lang;
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
					Bukkit.getLogger().info("[EpixUHC] Server will shutdown in "+timeBeforeStop+"s");
					gm.broadcastInfoMessage(Lang.GAME_SHUTDOWN.replace("%time%", ""+timeBeforeStop));
				}
				
				timeBeforeStop--;
				Bukkit.getScheduler().scheduleSyncDelayedTask(EpixUHC.getPlugin(), this,20);
				}
	}

}
