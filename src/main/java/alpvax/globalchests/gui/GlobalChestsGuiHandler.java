package alpvax.globalchests.gui;

import alpvax.globalchests.block.entity.TileEntityGlobalChest;
import alpvax.globalchests.inventory.ContainerSynched;
import alpvax.globalchests.inventory.GlobalChestsHelper;
import alpvax.globalchests.inventory.ItemHandlerSynched;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GlobalChestsGuiHandler implements IGuiHandler
{
	public static final int INVENTORY_SETTINGS_ITEM = 0;
	public static final int INVENTORY_SETTINGS_TILE = 1;
	public static final int INVENTORY_ITEM = 2;
	public static final int INVENTORY_TILE = 3;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		ItemHandlerSynched inv = getInventory(ID, player, world, x, y, z);
		boolean settings = inv == null || ID == INVENTORY_SETTINGS_ITEM || ID == INVENTORY_SETTINGS_TILE;
		if(!settings)
		{
			return new ContainerSynched(player, inv);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		ItemHandlerSynched inv = getInventory(ID, player, world, x, y, z);
		boolean settings = inv == null || ID == INVENTORY_SETTINGS_ITEM || ID == INVENTORY_SETTINGS_TILE;
		if(!settings)
		{
			return new GuiInventorySynched(player, inv);
		}
		else
		{
		}
		return null;
	}

	private ItemHandlerSynched getInventory(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == INVENTORY_SETTINGS_ITEM || ID == INVENTORY_ITEM)
		{
			return GlobalChestsHelper.getInventory(GlobalChestsHelper.getInventoryKey(player.getHeldItem(EnumHand.values()[x])));
		}
		if(ID == INVENTORY_SETTINGS_TILE || ID == INVENTORY_TILE)
		{
			TileEntity t = world.getTileEntity(new BlockPos(x, y, z));
			if(!(t instanceof TileEntityGlobalChest))
			{
				return ((TileEntityGlobalChest)t).getInventory();
			}
		}
		return null;
	}
}
