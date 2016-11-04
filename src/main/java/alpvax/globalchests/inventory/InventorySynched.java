package alpvax.globalchests.inventory;

import java.util.function.Consumer;

import org.apache.logging.log4j.Level;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class InventorySynched implements IItemHandler
{
	public int numConnections;
	private boolean upToDate;
	private String owner;
	private String name;
	private int size;
	private ItemStack[] items;

	private final String key;
	private final Firebase invRef;

	protected class SimpleValueListener implements ValueEventListener
	{
		private Consumer<DataSnapshot> handler;

		protected SimpleValueListener(Consumer<DataSnapshot> updateHandler)
		{
			handler = updateHandler;
		}

		@Override
		public void onCancelled(FirebaseError snap)
		{
			FMLLog.log("GlobalChests", Level.WARN, "Error syncing data for inventory: \"%s\".", key);
			upToDate = false;
		}

		@Override
		public void onDataChange(DataSnapshot snap)
		{
			upToDate = true;
			handler.accept(snap);
		}
	}

	private ValueEventListener ownerListener = new SimpleValueListener(snap -> owner = snap.getValue(String.class));
	private ValueEventListener nameListener = new SimpleValueListener(snap -> name = snap.getValue(String.class));
	private ValueEventListener sizeListener = new SimpleValueListener(snap ->
	{
			size = snap.getValue(Integer.class).intValue();
			items = new ItemStack[size];
	});
	private ChildEventListener itemListener = new ChildEventListener()
	{
		@Override
		public void onChildAdded(DataSnapshot snap, String arg1)
		{
			items[Integer.parseInt(snap.getKey())] = ItemStackSerialiser.fromJSON(snap);
		}

		@Override
		public void onChildChanged(DataSnapshot snap, String arg1)
		{
			items[Integer.parseInt(snap.getKey())] = ItemStackSerialiser.fromJSON(snap);
		}

		@Override
		public void onChildMoved(DataSnapshot snap, String arg1)
		{
			System.err.printf("Item in inventory \"%s\" moved.%nSnapshot Key: %s%nArg1: %s%n", key, snap.getKey(), arg1);//XXX
			//TODO:Modify items array;
		}

		@Override
		public void onChildRemoved(DataSnapshot snap)
		{
			items[Integer.parseInt(snap.getKey())] = null;
		}

		@Override
		public void onCancelled(FirebaseError err)
		{}
	};

	private ValueEventListener syncAllListener = new SimpleValueListener(snap -> {
		owner = snap.child("owner").getValue(String.class);
		name = snap.child("name").getValue(String.class);
		size = snap.getValue(Integer.class).intValue();
		items = new ItemStack[size];
		for(DataSnapshot i : snap.child("items").getChildren())
		{
			items[Integer.parseInt(i.getKey())] = ItemStackSerialiser.fromJSON(i);
		}
	});

	InventorySynched(String key, Firebase ref)
	{
		this.key = key;
		invRef = ref;
		invRef.child("owner").addListenerForSingleValueEvent(ownerListener);
		invRef.child("name").addValueEventListener(nameListener);
		invRef.child("size").addValueEventListener(sizeListener);
		invRef.child("items").addChildEventListener(itemListener);
	}

	void removeListeners()
	{
		invRef.child("name").removeEventListener(nameListener);
		invRef.child("size").removeEventListener(sizeListener);
		invRef.child("items").removeEventListener(itemListener);
	}

	public void setName(String name)
	{
		invRef.child("name").setValue(name);
	}

	public String getName()
	{
		return name;
	}

	public void setSlots(int slots)
	{
		invRef.child("size").setValue(Integer.valueOf(slots));
	}
	
	@Override
	public int getSlots()
	{
		return size;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return items[slot];
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if(stack == null || stack.stackSize == 0)
			return null;

		validateSlotIndex(slot);
		ItemStack existing = getStackInSlot(slot);
		
		int limit = stack.getMaxStackSize();
		
		if(existing != null)
		{
		    if(!ItemHandlerHelper.canItemStacksStack(stack, existing))
		        return stack;
		
		    limit -= existing.stackSize;
		}
		
		if(limit <= 0)
		    return stack;
		
		boolean reachedLimit = stack.stackSize > limit;
		
		if (!simulate)
		{
			if(existing == null)
		    {
				setStack(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
		    }
		    else
		    {
		        existing.stackSize += reachedLimit ? limit : stack.stackSize;
				syncSlot(slot);
		    }
		}
		
		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;
	}

	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		if(amount == 0)
		{
			return null;
		}
		validateSlotIndex(slot);
		ItemStack existing = this.items[slot];

		if(existing == null)
		{
			return null;
		}
		int toExtract = Math.min(amount, existing.getMaxStackSize());

		if(existing.stackSize <= toExtract)
		{
			if(!simulate)
			{
				setStack(slot, null);
			}
			return existing;
		}
		else
		{
			if(!simulate)
			{
				setStack(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - toExtract));
			}

			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}

	public void setStack(int slot, ItemStack stack)
	{
		validateSlotIndex(slot);
		if(ItemStack.areItemStacksEqual(this.items[slot], stack))
		{//Nothing changes
			return;
		}
		if(stack != null)
		{
			ItemStackSerialiser.setValue(invRef.child("items").child(Integer.toString(slot)), stack.serializeNBT());
		}
		else
		{
			invRef.child("items").child(Integer.toString(slot)).removeValue();
		}
	}

	protected void syncSlot(int slot)
	{
		ItemStack stack = items[slot];
		if(stack != null)
		{
			ItemStackSerialiser.setValue(invRef.child("items").child(Integer.toString(slot)), stack.serializeNBT());
		}
		else
		{
			invRef.child("items").child(Integer.toString(slot)).removeValue();
		}
	}

	public void syncAll(boolean forceSync)
	{
		if(upToDate && !forceSync)
		{
			return;
		}
		invRef.addListenerForSingleValueEvent(syncAllListener);
	}

	public void openInventory()
	{
		++numConnections;
	}

	public void closeInventory()
	{
		if(--numConnections <= 0)
		{
			removeListeners();
			GlobalChestsHelper.removeInventory(key);
		}
	}

	protected void validateSlotIndex(int slot)
	{
		if(slot < 0 || slot >= items.length)
			throw new ArrayIndexOutOfBoundsException("Slot " + slot + " not in valid range - [0," + items.length + ")");
	}
}