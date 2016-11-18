package alpvax.globalchests.inventory;

import java.util.Map;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.google.common.collect.Maps;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemStackSerialiser
{
	public static ItemStack fromJSON(DataSnapshot snap)
	{
		return fromJSONComplete(snap);
	}

	public static void saveStack(Firebase ref, ItemStack stack)
	{
		saveStackComplete(ref, stack);
	}


	public static ItemStack fromJSONSimple(DataSnapshot snap)
	{
		return ItemStack.loadItemStackFromNBT(FirebaseNBTSerialiser.getNBT(snap));
	}

	public static void saveStackSimple(Firebase ref, ItemStack stack)
	{
		FirebaseNBTSerialiser.saveNBT(ref, stack.serializeNBT());
	}

	public static ItemStack fromJSONComplete(DataSnapshot snap)
	{
		Item item = Item.getByNameOrId(snap.child("id").getValue(String.class));
		int damage = snap.child("Damage").getValue(Integer.class).intValue();
		int stackSize = snap.child("Count").getValue(Integer.class).intValue();
		if(item == null || stackSize < 1)
		{
			return null;
		}
		NBTTagCompound capNBT = snap.hasChild("ForgeCaps") ? FirebaseNBTSerialiser.getNBT(snap.child("ForgeCaps")) : null;

		ItemStack stack = new ItemStack(item, stackSize, damage >= 0 ? damage : 0, capNBT);

		NBTTagCompound stackTagCompound = null;
		if(snap.hasChild("tag"))
		{
			stackTagCompound = FirebaseNBTSerialiser.getNBT(snap.child("tag"));
			stack.getItem().updateItemStackNBT(stackTagCompound);
		}
		stack.setTagCompound(stackTagCompound);
		return stack;
	}

	public static void saveStackComplete(Firebase ref, ItemStack stack)
	{
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", stack.getItem().getRegistryName().toString());
		map.put("Damage", stack.getItemDamage());
		map.put("Count", stack.stackSize);
		if(stack.hasTagCompound())
		{
			map.put("tag", FirebaseNBTSerialiser.getMapFromNBT(stack.getTagCompound()));
		}
		//Serialise capabilities
		NBTTagCompound nbt = stack.serializeNBT();
		if(nbt.hasKey("ForgeCaps"))
		{
			map.put("ForgeCaps", FirebaseNBTSerialiser.getMapFromNBT(nbt.getTag("ForgeCaps")));
		}
		ref.setValue(map);
	}
}
