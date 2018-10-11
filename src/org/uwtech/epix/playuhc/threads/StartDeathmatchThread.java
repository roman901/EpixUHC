package org.uwtech.epix.playuhc.threads;

import org.uwtech.epix.playuhc.PlayUhc;
import org.uwtech.epix.playuhc.game.GameManager;
import org.uwtech.epix.playuhc.languages.Lang;
import org.uwtech.epix.playuhc.listeners.WaitForDeathmatchListener;
import org.uwtech.epix.playuhc.sounds.UhcSound;
import org.bukkit.Bukkit;

public class StartDeathmatchThread implements Runnable{

	private int timeBeforePVP;
	private StartDeathmatchThread task;
	private WaitForDeathmatchListener listener;
	
	
	public StartDeathmatchThread(){
		this.timeBeforePVP = 31;
		this.task = this;
		this.listener = new WaitForDeathmatchListener();
		GameManager.getGameManager().getPlayersManager().setAllPlayersStartDeathmatch();
		Bukkit.getPluginManager().registerEvents(listener, PlayUhc.getPlugin());
	}
	
	@Override
	public void run() {
		Bukkit.getScheduler().runTask(PlayUhc.getPlugin(), new Runnable(){

			@Override
			public void run() {

				timeBeforePVP --;
				
				if(timeBeforePVP == 0){
					listener.unregister();
					GameManager.getGameManager().setPvp(true);
					GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_ENABLED);
					GameManager.getGameManager().getPlayersManager().playSoundToAll(UhcSound.WITHER_SPAWN);
				}else{
					
					if(timeBeforePVP <= 5 || (timeBeforePVP >= 5 && timeBeforePVP%5 == 0)){
						GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_START_IN+" "+timeBeforePVP+"s");
						GameManager.getGameManager().getPlayersManager().playSoundToAll(UhcSound.CLICK);
					}
					
					if(timeBeforePVP > 0){
						Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), task,20);
					}
				}
				
			}});
	}
}
