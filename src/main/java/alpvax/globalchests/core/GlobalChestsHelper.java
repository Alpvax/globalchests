package alpvax.globalchests.core;

import java.util.Map;

import net.minecraft.item.ItemStack;

public class GlobalChestsHelper
{
	public class Permission
	{
		private static final byte DEPOSIT = 0b1;
		private static final byte WITHDRAW = 0b10;
		private static final byte VIEW = 0b100;
	}

	private static GlobalChestsHelper instance;
	
	GlobalChestsHelper()
	{
		instance = this;
	}

	private class InventoryData
	{
		private String key;
		private String name;
		private String owner;
		private Map<String, Permission> permissions;
	}

	private InventoryData getData(String key)
	{
		//TODO:Firebase get
		return null;
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
