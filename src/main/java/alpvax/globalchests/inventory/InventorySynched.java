package alpvax.globalchests.inventory;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class InventorySynched implements IItemHandlerModifiable
{
	public int numConnections;
	private String name;
	private int size;
	private ItemStack[] items;

	private final String key;
	private final Firebase invRef;

	private ValueEventListener nameListener = new ValueEventListener()
	{
		@Override
		public void onDataChange(DataSnapshot snap)
		{
			name = snap.getValue(String.class);
		}

		@Override
		public void onCancelled(FirebaseError err)
		{}
	};
	private ValueEventListener sizeListener = new ValueEventListener()
	{
		@Override
		public void onDataChange(DataSnapshot snap)
		{
			size = snap.getValue(Integer.class).intValue();
			items = new ItemStack[size];
		}

		@Override
		public void onCancelled(FirebaseError err)
		{}
	};
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
			// TODO Auto-generated method stub

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

	InventorySynched(String key, Firebase ref)
	{
		this.key = key;
		invRef = ref;
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
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack)
	{
		// TODO Auto-generated method stub

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
}