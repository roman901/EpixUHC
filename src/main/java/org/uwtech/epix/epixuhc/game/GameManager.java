package org.uwtech.epix.epixuhc.game;

import org.uwtech.epix.epixuhc.EpixUHC;
import org.uwtech.epix.epixuhc.commands.ChatCommandExecutor;
import org.uwtech.epix.epixuhc.commands.TeleportCommandExecutor;
import org.uwtech.epix.epixuhc.commands.UhcCommandExecutor;
import org.uwtech.epix.epixuhc.configuration.MainConfiguration;
import org.uwtech.epix.epixuhc.customitems.CraftsManager;
import org.uwtech.epix.epixuhc.customitems.KitsManager;
import org.uwtech.epix.epixuhc.exceptions.UhcPlayerNotOnlineException;
import org.uwtech.epix.epixuhc.languages.Lang;
import org.uwtech.epix.epixuhc.listeners.*;
import org.uwtech.epix.epixuhc.maploader.MapLoader;
import org.uwtech.epix.epixuhc.players.PlayersManager;
import org.uwtech.epix.epixuhc.players.UhcPlayer;
import org.uwtech.epix.epixuhc.schematics.Lobby;
import org.uwtech.epix.epixuhc.sounds.SoundManager;
import org.uwtech.epix.epixuhc.sounds.UHCSound;
import org.uwtech.epix.epixuhc.threads.*;
import org.uwtech.epix.epixuhc.utils.TimeUtils;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class GameManager {
	private GameState gameState;
	private Lobby lobby;
	private PlayersManager playerManager;
	private MapLoader mapLoader;
	private UhcWorldBorder worldBorder;
	private SoundManager soundManager;
	private boolean pvp;
	private boolean gameIsEnding;

	private long remainingTime;
	private long elapsedTime = 0;

	private MainConfiguration configuration;

	private static GameManager uhcGM = null;

	public GameManager() {
		uhcGM = this;
	}


	public MainConfiguration getConfiguration() {
		return configuration;
	}

	public UhcWorldBorder getWorldBorder() {
		return worldBorder;
	}

	public SoundManager getSoundManager() {
		return soundManager;
	}

	public static GameManager getGameManager(){
		return uhcGM;
	}

	public synchronized GameState getGameState(){
		return gameState;
	}



	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public PlayersManager getPlayersManager(){
		return playerManager;
	}

	public MapLoader getMapLoader(){
		return mapLoader;
	}

	public Lobby getLobby() {
		return lobby;
	}

	public boolean getGameIsEnding() {
		return gameIsEnding;
	}

	public synchronized long getRemainingTime(){
		return remainingTime;
	}

	public synchronized long getElapsedTime(){
		return elapsedTime;
	}


	public String getFormatedRemainingTime() {
		return TimeUtils.getFormattedTime(getRemainingTime());
	}

	public synchronized void setRemainingTime(long time){
		remainingTime = time;
	}

	public synchronized void setElapsedTime(long time){
		elapsedTime = time;
	}

	public boolean getPvp() {
		return pvp;
	}
	public void setPvp(boolean state) {
		pvp = state;
	}

	public void loadNewGame() {
		deleteOldPlayersFiles();
		gameState = GameState.LOADING;
		soundManager = new SoundManager();
		loadConfig();

		worldBorder = new UhcWorldBorder();
		playerManager = new PlayersManager();

		registerListeners();

		mapLoader = new MapLoader();
		if(getConfiguration().getDebug()){
			mapLoader.loadOldWorld(configuration.getOverworldUuid(),Environment.NORMAL);
			mapLoader.loadOldWorld(configuration.getNetherUuid(),Environment.NETHER);
		}else{
			mapLoader.deleteLastWorld(configuration.getOverworldUuid());
			mapLoader.deleteLastWorld(configuration.getNetherUuid());
			mapLoader.createNewWorld(Environment.NORMAL);
			mapLoader.createNewWorld(Environment.NETHER);
		}

		if(getConfiguration().getEnablePregenerateWorld() && !getConfiguration().getDebug())
			mapLoader.generateChunks(Environment.NORMAL);
		else
			GameManager.getGameManager().startWaitingPlayers();
	}

	private void deleteOldPlayersFiles() {
		if(Bukkit.getServer().getWorlds().size()>0){
			// Deleting old players files
			File playerdata = new File(Bukkit.getServer().getWorlds().get(0).getName()+"/playerdata");
			if(playerdata.exists() && playerdata.isDirectory()){
				for(File playerFile : playerdata.listFiles()){
					playerFile.delete();
				}
			}

			// Deleting old players stats
			File stats = new File(Bukkit.getServer().getWorlds().get(0).getName()+"/stats");
			if(stats.exists() && stats.isDirectory()){
				for(File statFile : stats.listFiles()){
					statFile.delete();
				}
			}
		}
	}

	public void startWaitingPlayers() {
		loadWorlds();
		registerCommands();
		gameState = GameState.WAITING;
		Bukkit.getLogger().info(Lang.DISPLAY_MESSAGE_PREFIX+" Players are now allowed to join");
		Bukkit.getScheduler().scheduleSyncDelayedTask(EpixUHC.getPlugin(), new PreStartThread(),0);
	}

	public void startGame() {
		setGameState(GameState.STARTING);
		broadcastInfoMessage(Lang.GAME_STARTING);
		broadcastInfoMessage(Lang.GAME_PLEASE_WAIT_TELEPORTING);
        Bukkit.getWorld(configuration.getOverworldUuid()).setTime(0);
		getPlayersManager().randomTeleportTeams();
		gameIsEnding = false;
	}

	public void startWatchingEndOfGame() {
		gameState = GameState.PLAYING;

		World overworld = Bukkit.getWorld(configuration.getOverworldUuid());
		overworld.setGameRule(GameRule.DO_MOB_SPAWNING, true);

		getLobby().destroyBoundingBox();
		getPlayersManager().startWatchPlayerPlayingThread();
		Bukkit.getScheduler().runTaskAsynchronously(EpixUHC.getPlugin(), new ElapsedTimeThread());
		Bukkit.getScheduler().runTaskAsynchronously(EpixUHC.getPlugin(), new EnablePVPThread());
		if(getConfiguration().getEnableTimeLimit())
			Bukkit.getScheduler().runTaskAsynchronously(EpixUHC.getPlugin(), new TimeBeforeEndThread());
		worldBorder.startBorderThread();
	}

	public void broadcastTitle(String title, String subtitle) {
		for(UhcPlayer player : getPlayersManager().getPlayersList()){
            try {
                player.getPlayer().sendTitle(title, subtitle);
            } catch (UhcPlayerNotOnlineException e) {
            }
        }
	}
	public void broadcastMessage(String message) {
		for(UhcPlayer player : getPlayersManager().getPlayersList()){
			player.sendMessage(message);
		}
	}

	public void broadcastInfoMessage(String message) {
		broadcastMessage(ChatColor.GREEN+ Lang.DISPLAY_MESSAGE_PREFIX+" "+ChatColor.WHITE+message);
	}

	private void loadConfig() {
		new Lang();

		FileConfiguration cfg = EpixUHC.getPlugin().getConfig();
		configuration = new MainConfiguration();
		configuration.load(cfg);

		// Load kits
		KitsManager.loadKits();
		CraftsManager.loadCrafts();
		CraftsManager.loadBannedCrafts();
	}

	private void registerListeners() {
		// Registers Listeners
			List<Listener> listeners = new ArrayList<>();
			listeners.add(new PlayerConnectionListener());
			listeners.add(new PlayerChatListener());
			listeners.add(new PlayerDamageListener());
			listeners.add(new ItemsListener());
			listeners.add(new PortalListener());
			listeners.add(new PlayerDeathListener());
			listeners.add(new EntityDeathListener());
			listeners.add(new CraftListener());
			listeners.add(new PingListener());
			listeners.add(new BlockListener());
			for(Listener listener : listeners){
				Bukkit.getServer().getPluginManager().registerEvents(listener, EpixUHC.getPlugin());
			}
	}

	private void loadWorlds() {
		World overworld = Bukkit.getWorld(configuration.getOverworldUuid());
		overworld.save();
		overworld.setGameRule(GameRule.NATURAL_REGENERATION, false);
		overworld.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
		overworld.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
		overworld.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
		overworld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		overworld.setTime(6000);
		overworld.setDifficulty(Difficulty.HARD);

		World nether = Bukkit.getWorld(configuration.getNetherUuid());
		nether.save();
		nether.setGameRule(GameRule.NATURAL_REGENERATION, false);
		nether.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
		nether.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
		nether.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
		nether.setDifficulty(Difficulty.HARD);

		lobby = new Lobby(new Location(overworld, 0, 200, 0), Material.BARRIER);
		lobby.build();
		lobby.loadLobbyChunks();

		worldBorder.setUpBukkitBorder();

		pvp = false;
	}

	private void registerCommands(){
		// Registers CommandExecutor
        EpixUHC.getPlugin().getCommand("epixuhc").setExecutor(new UhcCommandExecutor());
        EpixUHC.getPlugin().getCommand("chat").setExecutor(new ChatCommandExecutor());
        EpixUHC.getPlugin().getCommand("teleport").setExecutor(new TeleportCommandExecutor());
	}

	public void endGame() {
		if(gameState.equals(GameState.PLAYING)){
			setGameState(GameState.ENDED);
			pvp = false;
			gameIsEnding = true;
			broadcastInfoMessage(Lang.GAME_FINISHED);
			getPlayersManager().playSoundToAll(UHCSound.ENDER_DRAGON_GROWL, 1, 2);
			getPlayersManager().setAllPlayersEndGame();
			Bukkit.getScheduler().scheduleSyncDelayedTask(EpixUHC.getPlugin(), new StopRestartThread(),20);
		}
	}

	public void startEndGameThread() {
		if(!gameIsEnding && (gameState.equals(GameState.DEATHMATCH) || gameState.equals(GameState.PLAYING))){
			gameIsEnding = true;
			EndThread.start();
		}
	}

	public void stopEndGameThread(){
		if(gameIsEnding && (gameState.equals(GameState.DEATHMATCH) || gameState.equals(GameState.PLAYING))){
			gameIsEnding = false;
			EndThread.stop();
		}
	}

}