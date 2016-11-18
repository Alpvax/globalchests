package alpvax.globalchests.core;

import alpvax.globalchests.inventory.GlobalChestsHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class GlobalInvResetRecipe implements IRecipe
{
	private ItemStack getInputStack(InventoryCrafting inv)
	{
		ItemStack itemstack = null;
        for(int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack itemstack1 = inv.getStackInSlot(i);
            if(itemstack1 != null)
            {
            	NBTTagCompound nbt = itemstack1.getTagCompound();
				if(itemstack != null || !(nbt.hasKey(GlobalChestsHelper.INVENTORY_KEY, NBT.TAG_STRING) || nbt.hasKey(GlobalChestsHelper.INTERFACE_OWNER_MOST, NBT.TAG_LONG) || nbt.hasKey(GlobalChestsHelper.INTERFACE_OWNER_LEAST, NBT.TAG_LONG)))
                {
					return null;
                }
                itemstack = itemstack1;
            }
		}
		return itemstack;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
	{
		return getInputStack(inv) != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		ItemStack stack = getInputStack(inv);
		if(stack != null)
		{
			ItemStack output = stack.copy();
			NBTTagCompound tag = output.getTagCompound();
			tag.removeTag(GlobalChestsHelper.INVENTORY_KEY);
			tag.removeTag(GlobalChestsHelper.INTERFACE_OWNER_MOST);
			tag.removeTag(GlobalChestsHelper.INTERFACE_OWNER_LEAST);
			return output;
		}
		return null;
	}

	@Override
	public int getRecipeSize()
	{
		return 1;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv)
	{
		return new ItemStack[inv.getSizeInventory()];
	}

}
