package org.uwtech.epix.playuhc.threads;

import org.uwtech.epix.playuhc.PlayUhc;
import org.uwtech.epix.playuhc.game.GameManager;
import org.uwtech.epix.playuhc.languages.Lang;
import org.bukkit.Bukkit;

public class EndThread implements Runnable{

	private static EndThread instance;
	
	public static void start(){
		if(instance != null && instance.run){
			instance.run = false;
		}
		instance = new EndThread();
		Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), instance, 20);
	}
	
	public static void stop(){
		if(instance != null && instance.run){
			instance.run = false;
			GameManager.getGameManager().broadcastInfoMessage(Lang.GAME_END_STOPPED);
			Bukkit.getLogger().info(Lang.DISPLAY_MESSAGE_PREFIX+" "+ Lang.GAME_END_STOPPED);
		}
	}
	
	private int timeBeforeEnd;
	private boolean run;
	
	public EndThread(){
		this.timeBeforeEnd = 61;
		this.run = true;
	}
	
	@Override
	public void run() {
		
		if(run){
			Bukkit.getScheduler().runTask(PlayUhc.getPlugin(), new Runnable() {
				
				@Override
				public void run(){ 
					GameManager gm = GameManager.getGameManager();
						
					if(timeBeforeEnd <= 0){
						gm.endGame();
					}else{
						if(timeBeforeEnd > 0 && (timeBeforeEnd%10 == 0 || timeBeforeEnd <= 5)){
							Bukkit.getLogger().info(Lang.DISPLAY_MESSAGE_PREFIX+" "+ Lang.PLAYERS_ALL_HAVE_LEFT+" "+timeBeforeEnd);
							gm.broadcastInfoMessage(Lang.PLAYERS_ALL_HAVE_LEFT+" "+timeBeforeEnd);
						}
						timeBeforeEnd--;
						Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), EndThread.this,20);
					}
				}
			});
		}
		
	}

}
