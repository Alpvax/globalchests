package alpvax.globalchests.inventory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class ItemStackSerialiser
{
	public static ItemStack fromJSON(DataSnapshot snap)
	{
		Item item = Item.getByNameOrId(snap.child("id").getValue(String.class));
		int damage = snap.child("Damage").getValue(Integer.class).intValue();
		int stackSize = snap.child("Count").getValue(Integer.class).intValue();
		if(item == null || stackSize < 1)
		{
			return null;
		}
		NBTTagCompound capNBT = null;
		if(snap.hasChild("ForgeCaps"))
		{
			try
			{
				capNBT = CompressedStreamTools.read(new DataInputStream(new ByteArrayInputStream(snap.child("ForgeCaps").getValue(String.class).getBytes())));
			}
			catch(IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ItemStack stack = new ItemStack(item, stackSize, damage >= 0 ? damage : 0, capNBT);

		NBTTagCompound stackTagCompound = null;
		if(snap.hasChild("tag"))
		{
			try
			{
				stackTagCompound = CompressedStreamTools.read(new DataInputStream(new ByteArrayInputStream(snap.child("tag").getValue(String.class).getBytes())));
			}
			catch(IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stack.getItem().updateItemStackNBT(stackTagCompound);
		}
		stack.setTagCompound(stackTagCompound);
		return stack;
	}

	public static void setValue(Firebase ref, NBTTagCompound nbt)
	{
		for(String key : nbt.getKeySet())
		{
			NBTBase tag = nbt.getTag(key);
			ref.child(key).setValue(tag);
		}
	}
}
