package alpvax.globalchests.inventory;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSynched extends Container
{
	private final ItemHandlerSynched inventory;
	private final int numRows;

	public ContainerSynched(EntityPlayer player, ItemHandlerSynched itemHandlerSynched)
	{
		inventory = itemHandlerSynched;
		numRows = inventory.getSlots() / 9;
		inventory.openInventory();

		for(int invRow = 0; invRow < this.numRows; invRow++)
		{
			for(int invCol = 0; invCol < 9; invCol++)
			{
				this.addSlotToContainer(new SlotSynchronised(inventory, invCol + invRow * 9, 8 + invCol * 18, 18 + invRow * 18));
			}
		}

		int plyrInvOffset = (this.numRows - 4) * 18;
		for(int plyrRow = 0; plyrRow < 3; plyrRow++)
		{
			for(int plyrCol = 0; plyrCol < 9; plyrCol++)
			{
				this.addSlotToContainer(new Slot(player.inventory, plyrCol + plyrRow * 9 + 9, 8 + plyrCol * 18, 103 + plyrRow * 18 + plyrInvOffset));
			}
		}

		for(int htbar = 0; htbar < 9; htbar++)
		{
			this.addSlotToContainer(new Slot(player.inventory, htbar, 8 + htbar * 18, 161 + plyrInvOffset));
		}
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return (inventory.getPermissions(playerIn) & GlobalChestsHelper.Permissions.VIEW) != 0;
	}

	/**
	 * Take a stack from the specified inventory slot.
	 */
	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(index);

		if(slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if(index < this.numRows * 9)
			{
				if(!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if(!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false))
			{
				return null;
			}

			if(itemstack1.stackSize == 0)
			{
				slot.putStack((ItemStack)null);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(EntityPlayer playerIn)
	{
		super.onContainerClosed(playerIn);
		inventory.syncAll(true);
	}
}
