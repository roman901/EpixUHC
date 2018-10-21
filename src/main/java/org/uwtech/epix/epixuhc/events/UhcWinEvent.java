package org.uwtech.epix.epixuhc.events;

import org.uwtech.epix.epixuhc.players.UhcPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

public class UhcWinEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private Set<UhcPlayer> winners;
	
	public UhcWinEvent(Set<UhcPlayer> winners){
		this.winners = winners;
	}

	public Set<UhcPlayer> getWinners(){
		return winners;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
