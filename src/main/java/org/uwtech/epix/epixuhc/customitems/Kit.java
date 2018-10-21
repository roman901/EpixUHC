package org.uwtech.epix.epixuhc.customitems;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Kit {
	protected String key;
	protected String name;
	protected ItemStack symbol;
	protected List<ItemStack> items;
	public String getKey() {
		return key;
	}
	public String getName() {
		return name;
	}
}
