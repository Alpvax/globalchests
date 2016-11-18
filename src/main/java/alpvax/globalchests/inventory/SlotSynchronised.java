package alpvax.globalchests.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.SlotItemHandler;

public class SlotSynchronised extends SlotItemHandler
{
	protected ItemHandlerSynched synchedInventory;

	public SlotSynchronised(ItemHandlerSynched itemHandler, int index, int xPosition, int yPosition)
	{
		super(itemHandler, index, xPosition, yPosition);
		synchedInventory = itemHandler;
	}

	public void onSlotChanged()
	{
		super.onSlotChanged();
		synchedInventory.syncSlot(getSlotIndex());
	}


	@Override
	public boolean canTakeStack(EntityPlayer playerIn)
	{
		return (synchedInventory.getPermissions(playerIn) & GlobalChestsHelper.Permissions.WITHDRAW) != 0 && super.canTakeStack(playerIn);
	}
}
