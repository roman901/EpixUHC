package org.uwtech.epix.epixuhc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.uwtech.epix.epixuhc.game.GameManager;

public class EpixUHC extends JavaPlugin {
	private static EpixUHC pl;

	@Override
	public void onEnable(){
		pl = this;
	
		// Blocks players joins while loading the plugin
		Bukkit.getServer().setWhitelist(true);
		saveDefaultConfig();

		
		Bukkit.getScheduler().runTaskLater(this, () -> {
			new GameManager().loadNewGame();
			// Unlock players joins and rely on UhcPlayerJoinListener
			Bukkit.getServer().setWhitelist(false);
		}, 1);

	}

	public static EpixUHC getPlugin(){
		return pl;
	}

	@Override
	public void onDisable(){
		Bukkit.getLogger().info("Plugin EpixUHC disabled");
	}

}
