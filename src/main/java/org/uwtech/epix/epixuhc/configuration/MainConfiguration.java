package org.uwtech.epix.epixuhc.configuration;

import org.uwtech.epix.epixuhc.EpixUHC;
import org.uwtech.epix.epixuhc.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainConfiguration {
	// Config options.
		private int timeBeforePvp;
		private int minimalReadyTeamsPercentageToStart;
		private int minimalReadyTeamsToStart;
		private int minPlayersToStart;
		private int maxPlayersPerTeam;
		private int timeBeforeStartWhenReady;
		private boolean canSpectateAfterDeath;
		private boolean canSendMessagesAfterDeath;
		private String overworldUuid;
		private String netherUuid;
		private boolean pickRandomSeedFromList;
		private List<Long> seeds;
		private boolean pickRandomWorldFromList;
		private List<String> worldsList;
		private boolean spectatingTeleport;
		private boolean enableKitsPermissions;
		private boolean enableCraftsPermissions;
		private boolean enableExtraHalfHearts;
		private int extraHalfHearts;
		private boolean enableGoldDrops;
		private int minGoldDrops;
		private int maxGoldDrops;
		private List<EntityType> affectedGoldDropsMobs;
		private int goldDropPercentage;
		private boolean enableExpDropOnDeath;
		private int expDropOnDeath;
		private boolean enableKillDisconnectedPlayers;
		private int maxDisconnectPlayersTime;
		private long timeToShrink;
		private long timeLimit;
		private boolean enableTimeLimit;
		private boolean banNether;
		private boolean banLevelTwoPotions;
		private boolean alwaysDay;
		private boolean borderIsMoving;
		private boolean regenHeadDropOnPlayerDeath;
		private boolean allowGhastTearsDrops;
		private Sound soundOnPlayerDeath;
		private boolean autoAssignNewPlayerTeam;
		private boolean forceAssignSoloPlayerToTeamWhenStarting;
		private boolean preventPlayerFromLeavingTeam;
		private boolean teamAlwaysReady;
		private long timeBeforeRestartAfterEnd;
		private List<PotionEffect> potionEffectOnStart;
		private boolean canJoinAsSpectator;
		private boolean endGameWhenAllPlayersHaveLeft;
		private boolean debug; // debug: true to skip map loading and load old world
		private int restEveryTicks;
		private int chunksPerTick;
		private int restDuraton;
		private boolean enablePregenerateWorld;

		// fast mode
		private boolean cookedDroppedFood;
		private boolean treesAutoCut;
		private boolean treesApplesOnEveryTreeType;
		private boolean disableFallDamage;
		private boolean enableGenerateVein;
		private Map<Material,GenerateVeinConfiguration> generateVeins;
		private boolean enableBlockLoots;
		private Map<Material,BlockLootConfiguration> blockLoots;
		private boolean enableMobLoots;
		private Map<EntityType,MobLootConfiguration> mobLoots;

		public void load(FileConfiguration cfg){
			minimalReadyTeamsPercentageToStart = cfg.getInt("minimal-ready-teams-percentage-to-start",50);
			minimalReadyTeamsToStart = cfg.getInt("minimal-ready-teams-to-start",2);
			minPlayersToStart = cfg.getInt("min-players-to-start",0);
			maxPlayersPerTeam = cfg.getInt("max-players-per-team",2);
			timeBeforeStartWhenReady = cfg.getInt("time-to-start-when-ready",15);
			canSpectateAfterDeath = cfg.getBoolean("can-spectate-after-death",false);
			canSendMessagesAfterDeath = cfg.getBoolean("can-send-messages-after-death",true);
			timeBeforePvp = cfg.getInt("time-before-pvp",600);
			overworldUuid = cfg.getString("worlds.overworld",null);
			netherUuid = cfg.getString("worlds.nether",null);
			pickRandomSeedFromList = cfg.getBoolean("world-seeds.pick-random-seed-from-list",false);
			pickRandomWorldFromList = cfg.getBoolean("world-list.pick-random-world-from-list",false);
			spectatingTeleport = cfg.getBoolean("spectating-teleport",false);
			enableKitsPermissions = cfg.getBoolean("enable-kits-permissions",false);
			enableCraftsPermissions = cfg.getBoolean("customize-game-behavior.enable-crafts-permissions",false);
			enableExtraHalfHearts = cfg.getBoolean("customize-game-behavior.add-player-extra-half-hearts.enable",false);
			extraHalfHearts = cfg.getInt("customize-game-behavior.add-player-extra-half-hearts.extra-half-hearts",0);
			enableGoldDrops = cfg.getBoolean("customize-game-behavior.add-gold-drops.enable",false);
			minGoldDrops = cfg.getInt("customize-game-behavior.add-gold-drops.min",0);
			maxGoldDrops = cfg.getInt("customize-game-behavior.add-gold-drops.max",0);
			goldDropPercentage = cfg.getInt("customize-game-behavior.add-gold-drops.drop-chance-percentage",0);
			enableExpDropOnDeath = cfg.getBoolean("customize-game-behavior.add-xp-drops-on-player-death.enable",false);
			expDropOnDeath = cfg.getInt("customize-game-behavior.add-xp-drops-on-player-death.quantity",0);
			enableKillDisconnectedPlayers = cfg.getBoolean("kill-disconnected-players-after-delay.enable",false);
			maxDisconnectPlayersTime = cfg.getInt("kill-disconnected-players-after-delay.delay",60);
			timeToShrink = cfg.getLong("border.time-to-shrink",3600);
			enableTimeLimit = cfg.getBoolean("time-limit.enable",false);
			timeLimit = cfg.getLong("time-limit.limit",timeToShrink);
			borderIsMoving = cfg.getBoolean("border.moving",false);
			regenHeadDropOnPlayerDeath = cfg.getBoolean("customize-game-behavior.add-regen-head-drop-on-player-death",true);
			allowGhastTearsDrops = cfg.getBoolean("customize-game-behavior.allow-ghast-tears-drops",true);
			autoAssignNewPlayerTeam = cfg.getBoolean("auto-assign-new-player-team",false);
			forceAssignSoloPlayerToTeamWhenStarting = cfg.getBoolean("force-assign-solo-player-to-team-when-starting",false);
			preventPlayerFromLeavingTeam = cfg.getBoolean("prevent-player-from-leaving-team",false);
			teamAlwaysReady = cfg.getBoolean("team-always-ready",false);
			timeBeforeRestartAfterEnd = cfg.getLong("time-before-restart-after-end",30);
			canJoinAsSpectator = cfg.getBoolean("can-join-as-spectator",false);
			endGameWhenAllPlayersHaveLeft = cfg.getBoolean("countdown-ending-game-when-all-players-have-left",true);
			debug = cfg.getBoolean("debug",false);
			banNether = cfg.getBoolean("customize-game-behavior.ban-nether",false);
			banLevelTwoPotions = cfg.getBoolean("customize-game-behavior.ban-level-2-potions",false);
			alwaysDay = cfg.getBoolean("customize-game-behavior.always-day",true);
			enablePregenerateWorld = cfg.getBoolean("pre-generate-world.enable",false);
			restEveryTicks = cfg.getInt("pre-generate-world.rest-every-ticks",20);
			chunksPerTick = EpixUHC.getPlugin().getConfig().getInt("pre-generate-world.chunks-per-tick",10);
			restDuraton = EpixUHC.getPlugin().getConfig().getInt("pre-generate-world.rest-duration",20);

			// SOund on player death
			String soundDeath = cfg.getString("customize-game-behavior.sound-on-player-death","false");
			try{
				soundOnPlayerDeath = Sound.valueOf(soundDeath);
			}catch(IllegalArgumentException e){
				soundOnPlayerDeath = null;
			}

			// Set remaining time
			if(enableTimeLimit){
				GameManager.getGameManager().setRemainingTime(timeLimit);
			}

			// Potions effects on start
			List<String> potionStrList = cfg.getStringList("potion-effect-on-start");
			List<PotionEffect> potionList = new ArrayList<>();
			if(potionStrList == null){
				potionEffectOnStart = potionList;
			}else{
				for(String potionStr : potionStrList){
					try{
						String[] potionArr = potionStr.split("/");
						int duration = Integer.parseInt(potionArr[1]);
						int amplifier = Integer.parseInt(potionArr[2]);
						PotionEffect effect = new PotionEffect(PotionEffectType.getByName(potionArr[0].toUpperCase()),duration,amplifier);
						potionList.add(effect);
					}catch(Exception e){
						Bukkit.getLogger().warning("[PlayUHC] "+potionStr+" ignored, please check the syntax. It must be formated like POTION_NAME/duration/amplifier");
					}
				}
				potionEffectOnStart = potionList;
			}

			// Mobs gold drops
			List<String> mobsGoldDrop = cfg.getStringList("customize-game-behavior.add-gold-drops.affected-mobs");
			List<EntityType> mobsType = new ArrayList<>();
			if(mobsGoldDrop != null){
				for(String mobTypeString : mobsGoldDrop){
					try{
						EntityType mobType = EntityType.valueOf(mobTypeString);
						mobsType.add(mobType);
					}catch(IllegalArgumentException e){
						Bukkit.getLogger().warning(mobTypeString+" is not a valid mob type");
					}
				}

				affectedGoldDropsMobs = mobsType;
			}

			// Seed list
			List<Long> choosenSeed = cfg.getLongList("world-seeds.list");
			if(choosenSeed == null)
				seeds = new ArrayList<>();
			else
				seeds = choosenSeed;


			// World list
			List<String> worldList = cfg.getStringList("world-list.list");
			worldsList = (worldList == null) ? new ArrayList<>() : worldList;


			// Fast Mode
			cookedDroppedFood = cfg.getBoolean("fast-mode.cooked-dropped-food",false);
			treesAutoCut = cfg.getBoolean("fast-mode.trees.auto-cut",false);
			treesApplesOnEveryTreeType = cfg.getBoolean("fast-mode.trees.apples-on-every-tree-type",false);
			disableFallDamage = cfg.getBoolean("fast-mode.disable-fall-damage",false);

			// Fast Mode, generate-vein
			enableGenerateVein = cfg.getBoolean("fast-mode.generate-vein.enable",false);
			generateVeins = new HashMap<>();
			ConfigurationSection allVeinsSection = cfg.getConfigurationSection("fast-mode.generate-vein.veins");
			if(allVeinsSection != null){
				for(String veinSectionName : allVeinsSection.getKeys(false)){
					ConfigurationSection veinSection = allVeinsSection.getConfigurationSection(veinSectionName);
					GenerateVeinConfiguration veinConfig = new GenerateVeinConfiguration();
					if(veinConfig.parseConfiguration(veinSection)){
						generateVeins.put(veinConfig.getMaterial(),veinConfig);
					}
				}
			}

			// Fast Mode, block-loot
			enableBlockLoots = cfg.getBoolean("fast-mode.block-loot.enable",false);
			blockLoots = new HashMap<>();
			ConfigurationSection allBlockLootsSection = cfg.getConfigurationSection("fast-mode.block-loot.loots");
            if(allBlockLootsSection != null){
                for(String mobLootSectionName : allBlockLootsSection.getKeys(false)){
                    ConfigurationSection mobLootSection = allBlockLootsSection.getConfigurationSection(mobLootSectionName);
                    BlockLootConfiguration mobLootConfig = new BlockLootConfiguration();
                    if(mobLootConfig.parseConfiguration(mobLootSection)){
                        blockLoots.put(mobLootConfig.getMaterial(),mobLootConfig);
                    }
                }
            }

			// Fast Mode, mob-loot
			enableMobLoots = cfg.getBoolean("fast-mode.mob-loot.enable",false);
			mobLoots = new HashMap<>();
			ConfigurationSection allMobLootsSection = cfg.getConfigurationSection("fast-mode.mob-loot.loots");
            if(allMobLootsSection != null){
                for(String mobLootSectionName : allMobLootsSection.getKeys(false)){
                    ConfigurationSection mobLootSection = allMobLootsSection.getConfigurationSection(mobLootSectionName);
                    MobLootConfiguration mobLootConfig = new MobLootConfiguration();
                    if(mobLootConfig.parseConfiguration(mobLootSection)){
                        mobLoots.put(mobLootConfig.getEntityType(),mobLootConfig);
                    }
                }
            }
		}

		public boolean getForceAssignSoloPlayerToTeamWhenStarting() {
			return forceAssignSoloPlayerToTeamWhenStarting;
		}

		public int getMinPlayersToStart() {
			return minPlayersToStart;
		}

		public boolean getEnableGenerateVein() {
			return enableGenerateVein;
		}

		public boolean getEnableBlockLoots() {
			return enableBlockLoots;
		}
		
		public boolean getEnableMobLoots() {
			return enableMobLoots;
		}

		public int getRestEveryTicks() {
			return restEveryTicks;
		}

		public int getChunksPerTick() {
			return chunksPerTick;
		}

		public int getRestDuraton() {
			return restDuraton;
		}

		public boolean getEnablePregenerateWorld() {
			return enablePregenerateWorld;
		}

		public Map<Material, GenerateVeinConfiguration> getGenerateVeins() {
			return generateVeins;
		}

		public Map<Material, BlockLootConfiguration> getBlockLoots() {
			return blockLoots;
		}
		
		public Map<EntityType, MobLootConfiguration> getMobLoots() {
			return mobLoots;
		}

		public boolean getCookedDroppedFood() {
			return cookedDroppedFood;
		}

		public boolean getTreesAutoCut() {
			return treesAutoCut;
		}

		public boolean getTreesApplesOnEveryTreeType() {
			return treesApplesOnEveryTreeType;
		}

		public boolean getDisableFallDamage() {
			return disableFallDamage;
		}

		public Map<Material, GenerateVeinConfiguration> getMoreOres() {
			return generateVeins;
		}

		public boolean getDebug() {
			return debug;
		}

		public boolean getEndGameWhenAllPlayersHaveLeft() {
			return endGameWhenAllPlayersHaveLeft;
		}
		
		public boolean getCanJoinAsSpectator() {
			return canJoinAsSpectator;
		}

		public List<PotionEffect> getPotionEffectOnStart() {
			return potionEffectOnStart;
		}

		public long getTimeBeforeRestartAfterEnd() {
			return timeBeforeRestartAfterEnd;
		}
		
		public boolean getTeamAlwaysReady() {
			return teamAlwaysReady;
		}

		public boolean getAutoAssignNewPlayerTeam() {
			return autoAssignNewPlayerTeam;
		}

		public boolean getPreventPlayerFromLeavingTeam() {
			return preventPlayerFromLeavingTeam;
		}

		public int getMinimalReadyTeamsPercentageToStart() {
			return minimalReadyTeamsPercentageToStart;
		}

		public boolean getPickRandomSeedFromList() {
			return pickRandomSeedFromList;
		}

		public List<Long> getSeeds() {
			return seeds;
		}
		
		public String getOverworldUuid() {
			return overworldUuid;
		}

		public String getNetherUuid() {
			return netherUuid;
		}

		public boolean getSpectatingTeleport() {
			return spectatingTeleport;
		}

		public int getTimeBeforePvp() {
			return timeBeforePvp;
		}

		public boolean getCanSpectateAfterDeath() {
			return canSpectateAfterDeath;
		}

		public boolean getCanSendMessagesAfterDeath() {
			return canSendMessagesAfterDeath;
		}

		public boolean getEnableKitsPermissions() {
			return enableKitsPermissions;
		}
		
		public boolean getEnableCraftsPermissions() {
			return enableCraftsPermissions;
		}
		public boolean getEnableExtraHalfHearts() {
			return enableExtraHalfHearts;
		}

		public int getExtraHalfHearts() {
			return extraHalfHearts;
		}

		public boolean getEnableGoldDrops() {
			return enableGoldDrops;
		}

		public int getMinGoldDrops() {
			return minGoldDrops;
		}

		public int getMaxGoldDrops() {
			return maxGoldDrops;
		}

		public List<EntityType> getAffectedGoldDropsMobs() {
			return affectedGoldDropsMobs;
		}

		public int getGoldDropPercentage() {
			return goldDropPercentage;
		}

		public int getMinimalReadyTeamsToStart() {
			return minimalReadyTeamsToStart;
		}

		public int getMaxPlayersPerTeam() {
			return maxPlayersPerTeam;
		}

		public boolean getEnableExpDropOnDeath() {
			return enableExpDropOnDeath;
		}

		public int getExpDropOnDeath() {
			return expDropOnDeath;
		}

		public boolean getEnableKillDisconnectedPlayers() {
			return enableKillDisconnectedPlayers;
		} 

		public int getMaxDisconnectPlayersTime() {
			return maxDisconnectPlayersTime;
		} 

		public boolean getPickRandomWorldFromList() {
			return pickRandomWorldFromList;
		} 

		public List<String> getWorldsList() {
			return worldsList;
		} 

		public long getTimeToShrink() {
			return timeToShrink;
		} 
		
		public long getTimeLimit() {
			return timeLimit;
		} 
		
		public boolean getEnableTimeLimit(){
				return enableTimeLimit;
		}
		
		public boolean getBanNether() {
			return banNether;
		} 

		public boolean getBanLevelTwoPotions() {
			return banLevelTwoPotions;
		}  
		
		public boolean getAlwaysDay(){
			return alwaysDay;
		}

		public boolean getBorderIsMoving() {
			return borderIsMoving;
		} 

		public int getTimeBeforeStartWhenReady() {
			return timeBeforeStartWhenReady;
		} 

		public boolean getRegenHeadDropOnPlayerDeath() {
			return regenHeadDropOnPlayerDeath;
		} 

		public boolean getAllowGhastTearsDrops() {
			return allowGhastTearsDrops;
		} 

		public Sound getSoundOnPlayerDeath() {
			return soundOnPlayerDeath;
		}

		public void setOverworldUuid(String uuid) {
			overworldUuid = uuid;
		}

		public void setNetherUuid(String uuid) {
			netherUuid = uuid;
		} 
}
