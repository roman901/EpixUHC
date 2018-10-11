package org.uwtech.epix.playuhc.players;

import org.uwtech.epix.playuhc.PlayUhc;
import org.uwtech.epix.playuhc.configuration.MainConfiguration;
import org.uwtech.epix.playuhc.customitems.KitsManager;
import org.uwtech.epix.playuhc.customitems.UhcItems;
import org.uwtech.epix.playuhc.events.UhcWinEvent;
import org.uwtech.epix.playuhc.exceptions.UhcPlayerDoesntExistException;
import org.uwtech.epix.playuhc.exceptions.UhcPlayerJoinException;
import org.uwtech.epix.playuhc.exceptions.UhcPlayerNotOnlineException;
import org.uwtech.epix.playuhc.exceptions.UhcTeamException;
import org.uwtech.epix.playuhc.game.GameManager;
import org.uwtech.epix.playuhc.game.GameState;
import org.uwtech.epix.playuhc.languages.Lang;
import org.uwtech.epix.playuhc.sounds.UhcSound;
import org.uwtech.epix.playuhc.threads.CheckRemainingPlayerThread;
import org.uwtech.epix.playuhc.threads.TeleportPlayersThread;
import org.uwtech.epix.playuhc.threads.TimeBeforeSendBungeeThread;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class PlayersManager {
	List<UhcPlayer> players;
	
	public PlayersManager(){
		players = Collections.synchronizedList(new ArrayList<UhcPlayer>());
	}
	
	public boolean isPlayerAllowedToJoin(Player player) throws UhcPlayerJoinException {
		
		
		GameManager uhcGM = GameManager.getGameManager();
		UhcPlayer uhcPlayer;
		switch(uhcGM.getGameState()){
			case LOADING:
				throw new UhcPlayerJoinException(GameManager.getGameManager().getMapLoader().getLoadingState()+"% "+ Lang.KICK_LOADING);
				
			case WAITING:
				return true;
				
			case STARTING:
				try{
					uhcPlayer = getUhcPlayer(player);
					if(uhcPlayer != null && ( uhcPlayer.getState().equals(PlayerState.PLAYING)))
						return true;
					else
						throw new UhcPlayerJoinException(Lang.KICK_STARTING);
				} catch (UhcPlayerDoesntExistException e) {
					throw new UhcPlayerJoinException(Lang.KICK_STARTING);
				}
			case DEATHMATCH:
			case PLAYING:
					try{
						uhcPlayer = getUhcPlayer(player);
						boolean canSpectate = uhcGM.getConfiguration().getCanSpectateAfterDeath();
						if(uhcPlayer != null && ( uhcPlayer.getState().equals(PlayerState.PLAYING) || (canSpectate && uhcPlayer.getState().equals(PlayerState.DEAD))))
							return true;
						else
							throw new UhcPlayerJoinException(Lang.KICK_PLAYING);
					} catch (UhcPlayerDoesntExistException e) {
						if(player.hasPermission("playuhc.join-override") || uhcGM.getConfiguration().getCanJoinAsSpectator() && uhcGM.getConfiguration().getCanSpectateAfterDeath()){
							UhcPlayer spectator = newUhcPlayer(player.getName());
							spectator.setState(PlayerState.DEAD);
							return true;
						}
						throw new UhcPlayerJoinException(Lang.KICK_PLAYING);
					}
					
			case ENDED:
				if(player.hasPermission("playuhc.join-override")){
					return true;
				}
				throw new UhcPlayerJoinException(Lang.KICK_ENDED);
			
		}
		
		

		return false;
	}
	
	public UhcPlayer getUhcPlayer(Player player) throws UhcPlayerDoesntExistException {
		return getUhcPlayer(player.getName());
	}
	
	public UhcPlayer getUhcPlayer(String name) throws UhcPlayerDoesntExistException {
		for(UhcPlayer uhcPlayer : getPlayersList()){
			if(uhcPlayer.getName().equals(name))
				return uhcPlayer;
		}
		
		throw new UhcPlayerDoesntExistException(name);
	}
	
	public synchronized UhcPlayer newUhcPlayer(String playerName){
		UhcPlayer newPlayer = new UhcPlayer(playerName);
		getPlayersList().add(newPlayer);
		return newPlayer;
	}
	
	public synchronized List<UhcPlayer> getPlayersList(){
		return players;
	}

	public void playerJoinsTheGame(Player player) {
		UhcPlayer uhcPlayer;
		try {
			uhcPlayer = getUhcPlayer(player);
		} catch (UhcPlayerDoesntExistException e) {
			uhcPlayer = newUhcPlayer(player.getName());
		}
			
		GameManager gm = GameManager.getGameManager();
			
		switch(uhcPlayer.getState()){
			case WAITING:
				setPlayerWaitsAtLobby(uhcPlayer);

				if(gm.getConfiguration().getAutoAssignNewPlayerTeam()){
					autoAssignPlayerToTeam(uhcPlayer);
				}
				player.sendMessage(ChatColor.GREEN+ Lang.DISPLAY_MESSAGE_PREFIX+" "+ChatColor.WHITE+ Lang.PLAYERS_WELCOME_NEW);
				break;
			case PLAYING:
				setPlayerStartPlaying(uhcPlayer);
				if(!uhcPlayer.getHasBeenTeleportedToLocation()){
					if(uhcPlayer.getStartingLocation() == null){
						World world = gm.getLobby().getLoc().getWorld();
						double maxDistance = 0.9 *  gm.getWorldBorder().getStartSize();
						uhcPlayer.getTeam().setStartingLocation(newRandomLocation(world, maxDistance));
					}
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 1), false);
					player.teleport(uhcPlayer.getStartingLocation());
					uhcPlayer.setHasBeenTeleportedToLocation(true);
					player.removePotionEffect(PotionEffectType.BLINDNESS);
				}
				player.sendMessage(ChatColor.GREEN+ Lang.DISPLAY_MESSAGE_PREFIX+" "+ChatColor.WHITE+ Lang.PLAYERS_WELCOME_BACK_IN_GAME);
				break;
			case DEAD:
				setPlayerSpectateAtLobby(uhcPlayer);
				break;
		}
	}
	
	private void autoAssignPlayerToTeam(UhcPlayer uhcPlayer) {
		GameManager gm = GameManager.getGameManager();
		
		for(UhcTeam team : listUhcTeams()){
			if(team != uhcPlayer.getTeam() && team.getMembers().size() < gm.getConfiguration().getMaxPlayersPerTeam()){
				try {
					team.join(uhcPlayer);
				} catch (UhcPlayerNotOnlineException | UhcTeamException e) {
				}
				break;
			}
		}
	}

	public void setPlayerWaitsAtLobby(UhcPlayer uhcPlayer){
		uhcPlayer.setState(PlayerState.WAITING);
		GameManager gm = GameManager.getGameManager();
		Player player;
		try {
			player = uhcPlayer.getPlayer();
			player.teleport(gm.getLobby().getLoc());
			clearPlayerInventory(player);
			player.setGameMode(GameMode.ADVENTURE);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 99999999, 0), false);
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999999, 0), false);
			player.setHealth(20);
			player.setExhaustion(20);
			player.setFoodLevel(20);
			player.setExp(0);
			UhcItems.giveLobbyItemTo(player);
			UhcItems.giveKitSelectionTo(player);
			UhcItems.giveCraftBookTo(player);
		} catch (UhcPlayerNotOnlineException e) {
			// Do nothing beacause WAITING is a safe state
		}
		
	}
	
	public void setPlayerStartPlaying(UhcPlayer uhcPlayer){
		
		Player player;
		MainConfiguration cfg = GameManager.getGameManager().getConfiguration();
		
		if(!uhcPlayer.getHasBeenTeleportedToLocation()){
			uhcPlayer.setState(PlayerState.PLAYING);
			uhcPlayer.setUpScoreboard();
			uhcPlayer.selectDefaultGlobalChat();
			Bukkit.getScheduler().runTaskLater(PlayUhc.getPlugin(), new UpdateScoreboardThread(uhcPlayer), 10);
			
			try {
				player = uhcPlayer.getPlayer();
				clearPlayerInventory(player);
				player.setFireTicks(0);

				for(PotionEffect effect : player.getActivePotionEffects())
				{
				    player.removePotionEffect(effect.getType());
				}
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 1), false);
				player.setGameMode(GameMode.SURVIVAL);
				if(cfg.getPlayingCompass())
					UhcItems.giveCompassPlayingTo(player);
				if(cfg.getEnableExtraHalfHearts()){
					player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20+((double) cfg.getExtraHalfHearts()));
					player.setHealth(20+((double) cfg.getExtraHalfHearts()));
				}
				UhcItems.giveCraftBookTo(player);
				KitsManager.giveKitTo(player);
			} catch (UhcPlayerNotOnlineException e) {
				// Nothing done
			}
		}
		
	}
	
	private void clearPlayerInventory(Player player) {
		player.getInventory().clear();
		
		//clear player armor
		ItemStack[] emptyArmor = new ItemStack[4];
		for(int i=0 ; i<emptyArmor.length ; i++){
			emptyArmor[i] = new ItemStack(Material.AIR);
		}
		player.getInventory().setArmorContents(emptyArmor);
		
	}

	public void setPlayerSpectateAtLobby(UhcPlayer uhcPlayer) {
		uhcPlayer.setState(PlayerState.DEAD);
		uhcPlayer.sendMessage(ChatColor.GREEN+ Lang.DISPLAY_MESSAGE_PREFIX
				+" "
				+ChatColor.WHITE+ Lang.PLAYERS_WELCOME_BACK_SPECTATING);
		if(GameManager.getGameManager().getConfiguration().getSpectatingTeleport())
			uhcPlayer.sendMessage(ChatColor.GREEN+ Lang.DISPLAY_MESSAGE_PREFIX
					+" "
					+ChatColor.WHITE+ Lang.COMMAND_SPECTATING_HELP);
		Player player;
		try {
			player = uhcPlayer.getPlayer();player.getEquipment().clear();
			clearPlayerInventory(player);
			player.setGameMode(GameMode.SPECTATOR);
			for(PotionEffect effect : player.getActivePotionEffects())
			{
			    player.removePotionEffect(effect.getType());
			}
			player.teleport(GameManager.getGameManager().getLobby().getLoc());
		} catch (UhcPlayerNotOnlineException e) {
			// Do nothing beacause DEAD is a safe state
		}
	}
	

	public void setAllPlayersEndGame() {

		GameManager gm = GameManager.getGameManager();
		MainConfiguration cfg = gm.getConfiguration();
		
		List<UhcPlayer> winners = getWinners();
		for(UhcPlayer player : winners){
			gm.broadcastInfoMessage(Lang.PLAYERS_WON.replace("%player%", ChatColor.GOLD+player.getName()));
		}
		
		// send to bungee

		if(cfg.getEnableBungeeSupport() && cfg.getTimeBeforeSendBungeeAfterEnd() >= 0){
			for(UhcPlayer player : getPlayersList()){
				Bukkit.getScheduler().runTaskAsynchronously(PlayUhc.getPlugin(), new TimeBeforeSendBungeeThread(player, cfg.getTimeBeforeSendBungeeAfterEnd()));
			}
		}
		
		UhcWinEvent event = new UhcWinEvent(new HashSet<UhcPlayer>(winners));
		Bukkit.getServer().getPluginManager().callEvent(event);

		for(UhcPlayer player : getPlayersList()){
			player.setState(PlayerState.DEAD);
			try{
				Player bukkitPlayer = player.getPlayer();
				bukkitPlayer.setGameMode(GameMode.SPECTATOR);
			}catch(UhcPlayerNotOnlineException e){
				// Do nothing for offline players
			}
		}
	}
	
	private List<UhcPlayer> getWinners(){
		List<UhcPlayer> winners = new ArrayList<>();
		for(UhcPlayer player : getPlayersList()){
			try{
				Player connected = player.getPlayer();
				if(connected.isOnline() && player.getState().equals(PlayerState.PLAYING))
					winners.add(player);
			}catch(UhcPlayerNotOnlineException e){
				// not adding the player to winner list
			}
		}
		return winners;
	}
	
	public List<UhcTeam> listUhcTeams(){
		List<UhcTeam> teams = new ArrayList<>();
		for(UhcPlayer player : getPlayersList()){
			UhcTeam team = player.getTeam();
			if(!teams.contains(team))
				teams.add(team);
		}
		return teams;
	}

	public void randomTeleportTeams() {
		GameManager gm = GameManager.getGameManager();
		World world = gm.getLobby().getLoc().getWorld();
		double maxDistance = 0.9 * gm.getWorldBorder().getStartSize();
		double minInterSquareDistance = 0.3*maxDistance*0.3*maxDistance;
		List<Location> locations = new ArrayList<>();
		
		
		// Fore solo players to join teams
		if(gm.getConfiguration().getForceAssignSoloPlayerToTeamWhenStarting()){
			for(UhcPlayer uhcPlayer : getPlayersList()){
				if(uhcPlayer.getTeam().getMembers().size() == 1){
					autoAssignPlayerToTeam(uhcPlayer);
				}
			}
		}
		
		// Try to find the best randoms teleport spots
		for(UhcTeam team : listUhcTeams()){
			Location newLoc = new Location(world,0,100,0);
			int failedAttempts = 0;
			boolean safeLocation = false;
			while(!safeLocation && failedAttempts < 30){
				newLoc = newRandomLocation(world, maxDistance);
				Biome biome = world.getBiome(newLoc.getBlockX(), newLoc.getBlockZ());
				if(biome.equals(Biome.DEEP_OCEAN) || biome.equals(Biome.OCEAN)){
					failedAttempts++;
				}else{
					//Supposition :
					safeLocation = true;
					
					for(Location l : locations){
						if(newLoc.distanceSquared(l) < minInterSquareDistance){
							failedAttempts++;
							safeLocation = false;
						}
					}
					
				}
			}
			
			newLoc = world.getHighestBlockAt(newLoc).getLocation().clone();
			Material groundMaterial = newLoc.getBlock().getType();
			
			if(groundMaterial.equals(Material.LAVA) || groundMaterial.equals(Material.WATER))
				newLoc = findSafeLocationAround(newLoc.clone());
			newLoc = newLoc.add(new Vector(0,25,0));
			
			locations.add(newLoc);
			team.setStartingLocation(newLoc);
		}
		
		long delayTeleportByTeam = 0; 
		
		
		for(UhcTeam team : listUhcTeams()){

			for(UhcPlayer uhcPlayer : team.getMembers()){
				gm.getPlayersManager().setPlayerStartPlaying(uhcPlayer);
			}
			
			Bukkit.getScheduler().runTaskLater(PlayUhc.getPlugin(), new TeleportPlayersThread(team), delayTeleportByTeam);
			Bukkit.getLogger().info("[PlayUHC] Teleporting a team in "+delayTeleportByTeam+" ticks");
			delayTeleportByTeam += 20; // ticks
		}
		
		Bukkit.getScheduler().runTaskLater(PlayUhc.getPlugin(), () -> GameManager.getGameManager().startWatchingEndOfGame(), delayTeleportByTeam + 20);
		
	}
	
	private Location newRandomLocation(World world, double maxDistance){
		Random r = new Random();
		double x = 2*maxDistance*r.nextDouble()-maxDistance;
		double z = 2*maxDistance*r.nextDouble()-maxDistance;
		return new Location(world,x,250,z);
	}
	
	private Location getGroundLocation(Location loc){
		World w = loc.getWorld();
		return w.getHighestBlockAt(loc).getLocation().clone();
	}
	
	private Location findSafeLocationAround(Location loc){
			Location save = loc.clone();
			Material material;
			Location betterLocation;
			for(int i = -35 ; i <= 35 ; i +=3){
				for(int j = -35 ; j <= 35 ; j+=3){
					betterLocation = getGroundLocation(loc.add(new Vector(i,0,j)));
					material = betterLocation.getBlock().getType();
					if(!material.equals(Material.LAVA) && !material.equals(Material.WATER))
						return betterLocation;
				}
			}
			
			return save;
	}

	public void strikeLightning(UhcPlayer uhcPlayer) {
		try {
			Location loc = uhcPlayer.getPlayer().getLocation();
			loc.getWorld().strikeLightningEffect(loc);
			loc.getWorld().getBlockAt(loc).setType(Material.AIR);
		} catch (UhcPlayerNotOnlineException e) {
			Location loc = GameManager.getGameManager().getLobby().getLoc();
			loc.getWorld().strikeLightningEffect(loc);
			loc.getWorld().getBlockAt(loc).setType(Material.AIR);
		}
		// Extinguish fire
	}

	public void playSoundToAll(UhcSound sound) {
		for(UhcPlayer player : getPlayersList()){
			playsoundTo(player, sound);
		}
	}

	public void playSoundToAll(UhcSound sound, float v, float v1){
		for(UhcPlayer player : getPlayersList()){
			playsoundTo(player, sound,v,v1);
		}
	}

	public void playsoundTo(UhcPlayer player, UhcSound sound) {
		playsoundTo(player,sound,1,1);
	}

	public void playsoundTo(UhcPlayer player, UhcSound sound, float v, float v1) {
		try {
			Player p = player.getPlayer();
			GameManager.getGameManager().getSoundManager().playSoundTo(p,sound,v,v1);
		} catch (UhcPlayerNotOnlineException e) {
			// No sound played
		}
	}

	public void checkIfRemainingPlayers() {
		
		int playingPlayers = 0;
		int playingPlayersOnline = 0;
		int playingTeams = 0;
		int playingTeamsOnline = 0;
		
		for(UhcTeam team : listUhcTeams()){
			
			int teamIsOnline = 0;
			int teamIsPlaying = 0;
			
			for(UhcPlayer player : team.getMembers()){
				if(player.getState().equals(PlayerState.PLAYING)){
					playingPlayers++;
					teamIsPlaying = 1;
					try{
						player.getPlayer();
						playingPlayersOnline++;
						teamIsOnline = 1;
					}catch(UhcPlayerNotOnlineException e){
						// Player isn't online
					}
				}
			}
			
			playingTeamsOnline += teamIsOnline;
			playingTeams += teamIsPlaying;
		}
		
		GameManager gm = GameManager.getGameManager();
		if(gm.getConfiguration().getEnableTimeLimit() && gm.getRemainingTime() <= 0 && gm.getGameState().equals(GameState.PLAYING)){
			gm.endGame();
		} else if(playingPlayers == 0){
			gm.endGame();
		}else if(playingPlayers>0 && playingPlayersOnline == 0){
			// Check if all playing players have left the game
			if(gm.getConfiguration().getEndGameWhenAllPlayersHaveLeft()){
				gm.startEndGameThread();
			}
		}else if(playingPlayers>0 && playingPlayersOnline > 0 && playingTeamsOnline == 1 && playingTeams == 1){
			// Check if one playing team remains
			gm.endGame();
		}else if(playingPlayers>0 && playingPlayersOnline > 0 && playingTeamsOnline == 1 && playingTeams > 1){
			// Check if one playing team remains
			if(gm.getConfiguration().getEndGameWhenAllPlayersHaveLeft()){
				gm.startEndGameThread();
			}
		}else if(gm.getGameIsEnding()){
			gm.stopEndGameThread();
		}
		
	}

	public void startWatchPlayerPlayingThread() {
		
		for(Player player : Bukkit.getOnlinePlayers()){
			player.removePotionEffect(PotionEffectType.BLINDNESS);
		}
		
		Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), new CheckRemainingPlayerThread() , 40);
		
		
	}
	
	public void sendPlayerToBungeeServer(Player player, String message) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(GameManager.getGameManager().getConfiguration().getServerBungee());
		player.sendMessage(message);
		player.sendPluginMessage(PlayUhc.getPlugin(), "BungeeCord", out.toByteArray());
	}

	public void playSoundPlayerDeath() {
		Sound sound = GameManager.getGameManager().getConfiguration().getSoundOnPlayerDeath();
		if(sound != null){
			for(Player player : Bukkit.getOnlinePlayers()){
				player.playSound(player.getLocation(), sound, 1, 1);
			}
		}
	}

	public Set<UhcPlayer> getPlayingPlayer() {
		Set<UhcPlayer> playingPlayers = new HashSet<UhcPlayer>();
		for(UhcPlayer p : getPlayersList()){
			if(p.getState().equals(PlayerState.PLAYING) && p.isOnline()){
				playingPlayers.add(p);
			}
		}
		return playingPlayers;
	}

	
	
	
	
}
