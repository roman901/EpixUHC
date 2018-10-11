package org.uwtech.epix.playuhc.threads;

import org.uwtech.epix.playuhc.PlayUhc;
import org.uwtech.epix.playuhc.game.GameManager;
import org.uwtech.epix.playuhc.languages.Lang;
import org.bukkit.Bukkit;

public class Auto20MinBroadcastThread implements Runnable {
	
	boolean broadcast;
	Auto20MinBroadcastThread task;
	
	public Auto20MinBroadcastThread() {
		broadcast = GameManager.getGameManager().getConfiguration().getAuto20MinBroadcast();
		task = this;
	}

	@Override
	public void run() {
		Bukkit.getScheduler().runTask(PlayUhc.getPlugin(), new Runnable(){

			@Override
			public void run() {

				if(broadcast){
					GameManager.getGameManager().broadcastInfoMessage(Lang.DISPLAY_YOUTUBER_MARK);
					Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), task, 24000);
				}
				
			}});
	}

}
