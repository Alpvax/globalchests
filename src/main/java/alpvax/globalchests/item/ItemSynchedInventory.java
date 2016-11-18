package alpvax.globalchests.item;

import static alpvax.globalchests.inventory.GlobalChestsHelper.INTERFACE_OWNER_LEAST;
import static alpvax.globalchests.inventory.GlobalChestsHelper.INTERFACE_OWNER_MOST;
import static alpvax.globalchests.inventory.GlobalChestsHelper.INVENTORY_KEY;

import java.util.UUID;

import alpvax.globalchests.core.GlobalChests;
import alpvax.globalchests.gui.GlobalChestsGuiHandler;
import alpvax.globalchests.inventory.GlobalChestsHelper;
import alpvax.globalchests.inventory.ItemHandlerSynched;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemSynchedInventory extends Item
{
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if(!world.isRemote)
		{
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt == null)
			{
				nbt = new NBTTagCompound();
				stack.setTagCompound(nbt);
			}
			if(!nbt.hasKey(INTERFACE_OWNER_MOST, NBT.TAG_LONG) || !nbt.hasKey(INTERFACE_OWNER_LEAST, NBT.TAG_LONG))
			{
				UUID owner = player.getGameProfile().getId();
				nbt.setLong(INTERFACE_OWNER_MOST, owner.getMostSignificantBits());
				nbt.setLong(INTERFACE_OWNER_LEAST, owner.getLeastSignificantBits());
			}
			String key = GlobalChestsHelper.getInventoryKey(stack);
			if(player.isSneaking() || key == null)
			{
				nbt.setString(INVENTORY_KEY, GlobalChestsHelper.newInventory(27, "Test Inventory", player).getKey());//XXX
				player.openGui(GlobalChests.instance, GlobalChestsGuiHandler.INVENTORY_ITEM, world, hand.ordinal(), 0, 0);
				//TODO:player.openGui(GlobalChests.instance, GlobalChestsGuiHandler.INVENTORY_SETTINGS_ITEM, world, hand.ordinal(), 0, 0);
			}
			else
			{
				ItemHandlerSynched inv = GlobalChestsHelper.getInventory(key);
				if((inv.getPermissions(player) & GlobalChestsHelper.Permissions.VIEW) != 0)
				{
					player.openGui(GlobalChests.instance, GlobalChestsGuiHandler.INVENTORY_ITEM, world, hand.ordinal(), 0, 0);
				}
			}
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}
}
