package org.uwtech.epix.playuhc.schematics;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Lobby {
	private Location loc;
	private Material block;
	private boolean built;
	private static int width, length, height;
	
	public Lobby(Location loc, Material block){
		this.loc = loc;
		this.block = block;
		this.built = false;
		loc.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

		width = 10;
		length = 10;
		height = 3; 
	}


	public void build(){
		if(!built){
            int x = loc.getBlockX(), y=loc.getBlockY(), z=loc.getBlockZ();
            World world = loc.getWorld();
            for(int i = -width; i <= width; i++){
                for(int j = -height; j <= height; j++){
                    for(int k = -length ; k <= length ; k++){
                        if(    i == -10
                            || i == 10
                            || j == -3
                            || j == 3
                            || k == -10
                            || k == 10
                          ){
                            world.getBlockAt(x+i,y+j,z+k).setType(block);
                        }else{
                            world.getBlockAt(x+i,y+j,z+k).setType(Material.AIR);
                        }
                    }
                }
            }

            built = true;
        }
	}
	
	public void destroyBoundingBox(){
		if(built){
			int x = loc.getBlockX(), y=loc.getBlockY(), z=loc.getBlockZ();
			World world = loc.getWorld();
			for(int i = -width; i <= width; i++){
				for(int j = -height; j <= height; j++){
					for(int k = -length ; k <= length ; k++){
						Block b = world.getBlockAt(x+i,y+j,z+k);
						if(!b.getType().equals(Material.AIR))
							world.getBlockAt(x+i,y+j,z+k).setType(Material.AIR);
					}
				}
			}
		}
	}
	
	public void loadLobbyChunks(){
		World world = getLoc().getWorld();
		world.loadChunk(getLoc().getChunk());
	}

	public Location getLoc() {
		return loc;
	}

}
