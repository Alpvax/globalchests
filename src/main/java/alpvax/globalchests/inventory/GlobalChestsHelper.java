package alpvax.globalchests.inventory;

import java.util.HashMap;
import java.util.Map;

import com.firebase.client.Firebase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GlobalChestsHelper
{
	public static class Permissions
	{
		public static final int VIEW = 0b1;
		public static final int DEPOSIT = 0b10;
		public static final int WITHDRAW = 0b100;
		public static final int MODIFY_PERMISSIONS = 0b1000;
	}

	private static GlobalChestsHelper instance = new GlobalChestsHelper();

	private Firebase root;
	private Firebase inventories;
	private Map<String, ItemHandlerSynched> loadedInventories = new HashMap<>();

	private GlobalChestsHelper()
	{
		root = new Firebase("https://globalchests.firebaseio.com/");
		inventories = root.child("inventory");
	}

	public static ItemHandlerSynched getInventory(String key)
	{
		ItemHandlerSynched inv = instance.loadedInventories.get(key);
		if(inv == null)
		{
			inv = new ItemHandlerSynched(key, instance.inventories.child(key));
			instance.loadedInventories.put(key, inv);
		}
		return inv;
	}

	static void removeInventory(String key)
	{
		instance.loadedInventories.remove(key);
	}

	public static ItemHandlerSynched newInventory(int size, String name, EntityPlayer owner)
	{
		Firebase ref = instance.inventories.push();
		ref.child("owner").setValue(owner.getGameProfile().getId());
		ref.child("name").setValue(name);
		ref.child("size").setValue(Integer.valueOf(size));
		return getInventory(ref.getKey());
	}

	public static String getInventoryKey(ItemStack stack)
	{
		if(stack == null)
		{
			return null;
		}
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null && nbt.hasKey(INVENTORY_KEY))
		{
			return nbt.getString(INVENTORY_KEY);
		}
		return null;
	}

	/** NBT key for the inventory id */
	public static final String INVENTORY_KEY = "GlobalInventoryID";
	/** NBT key for most sig bits of UUID of owner of this item/tileentity */
	public static final String INTERFACE_OWNER_MOST = "OwnerMost";
	/** NBT key for least sig bits of UUID of owner of this item/tileentity */
	public static final String INTERFACE_OWNER_LEAST = "OwnerLeast";
}
