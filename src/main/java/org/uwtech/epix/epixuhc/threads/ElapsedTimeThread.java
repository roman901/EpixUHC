package org.uwtech.epix.epixuhc.threads;

import org.uwtech.epix.epixuhc.EpixUHC;
import org.uwtech.epix.epixuhc.game.GameManager;
import org.uwtech.epix.epixuhc.game.GameState;
import org.bukkit.Bukkit;

public class ElapsedTimeThread implements Runnable{

	private GameManager gm;
	private ElapsedTimeThread task;

	public ElapsedTimeThread() {
		this.gm = GameManager.getGameManager();
		this.task = this;
	}
	
	@Override
	public void run() {

		long time = gm.getElapsedTime() + 1;
		gm.setElapsedTime(time);


		if(!gm.getGameState().equals(GameState.ENDED)){
			Bukkit.getScheduler().runTaskLaterAsynchronously(EpixUHC.getPlugin(), task, 20);
		}
	}
	
}
