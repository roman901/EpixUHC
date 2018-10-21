package org.uwtech.epix.epixuhc.threads;

import org.uwtech.epix.epixuhc.EpixUHC;
import org.uwtech.epix.epixuhc.game.GameManager;
import org.uwtech.epix.epixuhc.game.GameState;
import org.uwtech.epix.epixuhc.sounds.UHCSound;
import org.bukkit.Bukkit;

public class TimeBeforeEndThread implements Runnable{

	long remainingTime;
	GameManager gm;
	TimeBeforeEndThread task;
	
	
	public TimeBeforeEndThread() {
		this.gm = GameManager.getGameManager();
		this.remainingTime = gm.getRemainingTime();
		task = this;
	}
	
	@Override
	public void run() {
		
		remainingTime--;
		gm.setRemainingTime(remainingTime);
		
		if(remainingTime >= 0 && remainingTime <= 60 && (remainingTime%10 == 0 || remainingTime <= 10)){
			gm.getPlayersManager().playSoundToAll(UHCSound.CLICK);
		}
		
		if(remainingTime > 0 && (gm.getGameState().equals(GameState.PLAYING) || gm.getGameState().equals(GameState.DEATHMATCH)))
			Bukkit.getScheduler().runTaskLaterAsynchronously(EpixUHC.getPlugin(), task, 20);
	}
	
}
