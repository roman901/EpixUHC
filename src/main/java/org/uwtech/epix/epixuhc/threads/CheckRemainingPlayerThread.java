package org.uwtech.epix.epixuhc.threads;

import org.uwtech.epix.epixuhc.EpixUHC;
import org.uwtech.epix.epixuhc.game.GameManager;
import org.uwtech.epix.epixuhc.game.GameState;
import org.bukkit.Bukkit;


public class CheckRemainingPlayerThread implements Runnable{

	private CheckRemainingPlayerThread task;
	
	public CheckRemainingPlayerThread(){
		task = this;
	}
	
	@Override
	public void run() {
		
		Bukkit.getScheduler().runTask(EpixUHC.getPlugin(), new Runnable(){

			@Override
			public void run() {
				GameManager.getGameManager().getPlayersManager().checkIfRemainingPlayers();
				GameState state = GameManager.getGameManager().getGameState();
				if(state.equals(GameState.PLAYING) || state.equals(GameState.DEATHMATCH))
					Bukkit.getScheduler().runTaskLaterAsynchronously(EpixUHC.getPlugin(),task,40);
				}
				
		});
	}

}
