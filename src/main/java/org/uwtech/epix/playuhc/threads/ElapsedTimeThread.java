package org.uwtech.epix.playuhc.threads;

import org.uwtech.epix.playuhc.PlayUhc;
import org.uwtech.epix.playuhc.game.GameManager;
import org.uwtech.epix.playuhc.game.GameState;
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
			Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), task, 20);
		}
	}
	
}
