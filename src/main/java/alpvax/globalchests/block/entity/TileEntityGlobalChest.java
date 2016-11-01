package alpvax.globalchests.block.entity;

import alpvax.globalchests.core.GlobalChests;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;

public class TileEntityGlobalChest extends TileEntityLockable implements ITickable
{
	public static final String INVENTORY_KEY = "GlobalInventoryID";

	public float lidAngle;
	/** The angle of the ender chest lid last tick */
	public float prevLidAngle;
	public int numPlayersUsing;
	private int ticksSinceSync;

	/**
	 * Like the old updateEntity(), except more generic.
	 */
	public void update()
	{
		if(++this.ticksSinceSync % 20 * 4 == 0)
		{
			this.worldObj.addBlockEvent(this.pos, GlobalChests.Blocks.CHEST, 1, this.numPlayersUsing);
		}

		this.prevLidAngle = this.lidAngle;
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();

		if(this.numPlayersUsing > 0 && this.lidAngle == 0.0F)
		{
			double d0 = (double)i + 0.5D;
			double d1 = (double)k + 0.5D;
			this.worldObj.playSound((EntityPlayer)null, d0, (double)j + 0.5D, d1, SoundEvents.BLOCK_ENDERCHEST_OPEN, SoundCategory.BLOCKS, 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if(this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F)
		{
			float f2 = this.lidAngle;

			if(this.numPlayersUsing > 0)
			{
				this.lidAngle += 0.1F;
			}
			else
			{
				this.lidAngle -= 0.1F;
			}

			if(this.lidAngle > 1.0F)
			{
				this.lidAngle = 1.0F;
			}

			if(this.lidAngle < 0.5F && f2 >= 0.5F)
			{
				double d3 = (double)i + 0.5D;
				double d2 = (double)k + 0.5D;
				this.worldObj.playSound((EntityPlayer)null, d3, (double)j + 0.5D, d2, SoundEvents.BLOCK_ENDERCHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}

			if(this.lidAngle < 0.0F)
			{
				this.lidAngle = 0.0F;
			}
		}
	}

	public boolean receiveClientEvent(int id, int type)
	{
		if(id == 1)
		{
			this.numPlayersUsing = type;
			return true;
		}
		else
		{
			return super.receiveClientEvent(id, type);
		}
	}

	/**
	 * invalidates a tile entity
	 */
	public void invalidate()
	{
		this.updateContainingBlockInfo();
		super.invalidate();
	}

	public void openChest()
	{
		++this.numPlayersUsing;
		this.worldObj.addBlockEvent(this.pos, GlobalChests.Blocks.CHEST, 1, this.numPlayersUsing);
	}

	public void closeChest()
	{
		--this.numPlayersUsing;
		this.worldObj.addBlockEvent(this.pos, GlobalChests.Blocks.CHEST, 1, this.numPlayersUsing);
	}

	public boolean canBeUsed(EntityPlayer player)
	{
		return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}

	public void writeToStack(ItemStack stack)
	{

	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGuiID()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCustomName()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSizeInventory()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getInventoryStackLimit()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getField(int id)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getFieldCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear()
	{
		// TODO Auto-generated method stub

	}
}
