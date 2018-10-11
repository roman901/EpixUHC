package org.uwtech.epix.playuhc.threads;

import org.uwtech.epix.playuhc.PlayUhc;
import org.uwtech.epix.playuhc.game.GameManager;
import org.uwtech.epix.playuhc.game.GameState;
import org.uwtech.epix.playuhc.languages.Lang;
import org.uwtech.epix.playuhc.sounds.UhcSound;
import org.bukkit.Bukkit;

public class EnablePVPThread implements Runnable{

	int timeBeforePVP;
	EnablePVPThread task;
	GameManager gm;
	
	public EnablePVPThread(){
		timeBeforePVP = GameManager.getGameManager().getConfiguration().getTimeBeforePvp();;
		task = this;
		gm = GameManager.getGameManager();
	}
	
	@Override
	public void run() {
		Bukkit.getScheduler().runTask(PlayUhc.getPlugin(), new Runnable(){

			@Override
			public void run() {
				
				if(gm.getGameState().equals(GameState.PLAYING)){

					if(timeBeforePVP == 0){
						GameManager.getGameManager().setPvp(true);
						GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_ENABLED);
						GameManager.getGameManager().getPlayersManager().playSoundToAll(UhcSound.WITHER_SPAWN);
					}else{
						
						if(timeBeforePVP <= 10 || timeBeforePVP%60 == 0){
							if(timeBeforePVP%60 == 0)
								GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_START_IN+" "+(timeBeforePVP/60)+"m");
							else
								GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_START_IN+" "+timeBeforePVP+"s");
							
							GameManager.getGameManager().getPlayersManager().playSoundToAll(UhcSound.CLICK);
						}
						
						if(timeBeforePVP >= 20){
							timeBeforePVP -= 10;
							Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), task,200);
						}else{
							timeBeforePVP --;
							Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), task,20);
						}
					}
				}

				
			}});
	}
}
