package org.uwtech.epix.playuhc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.uwtech.epix.playuhc.game.GameManager;

public class PlayUhc extends JavaPlugin {
	private static PlayUhc pl;
	private static int version = 0;

	@Override
	public void onEnable(){
		pl = this;
	
		// Blocks players joins while loading the plugin
		Bukkit.getServer().setWhitelist(true);
		saveDefaultConfig();

		loadServerVersion();
		
		Bukkit.getScheduler().runTaskLater(this, () -> {
			new GameManager().loadNewGame();
			// Unlock players joins and rely on UhcPlayerJoinListener
			Bukkit.getServer().setWhitelist(false);
		}, 1);

	}

	private void loadServerVersion(){
		// get minecraft version
		String versionString = Bukkit.getBukkitVersion();
        int maxVersion = 15;

		for (int i = 8; i <= maxVersion; i ++){
			if (versionString.contains("1." + i)){
				version = i;
			}
		}

		if (version == 0) {
			version = 13;
			Bukkit.getLogger().warning("[PlayUHC] Failed to detect server version! " + versionString + "?");
		}else {
			Bukkit.getLogger().info("[PlayUHC] 1." + version + " Server detected!");
		}
	}

	public static int getVersion() {
		return version;
	}
	
	public static PlayUhc getPlugin(){
		return pl;
	}

	@Override
	public void onDisable(){
		Bukkit.getLogger().info("Plugin PlayUHC disabled");
	}

}
