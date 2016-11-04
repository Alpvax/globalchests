package alpvax.globalchests.inventory;

import java.util.HashMap;
import java.util.Map;

import com.firebase.client.Firebase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GlobalChestsHelper
{
	private static GlobalChestsHelper instance;

	private Firebase root;
	private Firebase inventories;
	private Map<String, InventorySynched> loadedInventories = new HashMap<>();

	GlobalChestsHelper()
	{
		instance = this;
		root = new Firebase("https://globalchests.firebaseio.com/");
		inventories = root.child("inventory");
	}

	public static InventorySynched getInventory(String key)
	{
		InventorySynched inv = instance.loadedInventories.get(key);
		if(inv == null)
		{
			inv = new InventorySynched(key, instance.inventories.child(key));
			instance.loadedInventories.put(key, inv);
		}
		return inv;
	}

	static void removeInventory(String key)
	{
		instance.loadedInventories.remove(key);
	}

	public static InventorySynched newInventory(int size, String name, EntityPlayer owner)
	{
		Firebase ref = instance.inventories.push();
		ref.child("owner").setValue(owner.getGameProfile().getId());
		ref.child("name").setValue(name);
		ref.child("size").setValue(Integer.valueOf(size));
		return getInventory(ref.getKey());
	}

	public static String getInventoryKey(ItemStack item)
	{
		//TODO:Get key
		return null;
	}

	public static String getInventoryName(ItemStack item)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
