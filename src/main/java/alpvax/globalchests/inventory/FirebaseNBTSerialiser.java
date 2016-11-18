package alpvax.globalchests.inventory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.GenericTypeIndicator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Bytes;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fml.common.FMLLog;

@SuppressWarnings("unused")
public class FirebaseNBTSerialiser<T extends NBTBase>
{
	private static final Map<Integer, FirebaseNBTSerialiser<?>> serialisers = Maps.newHashMap();

	private final int type;
	private final Function<T, Object> fromNBT;
	private final Function<DataSnapshot, T> toNBT;

	private FirebaseNBTSerialiser(int nbtType, Function<T, Object> fromNBT, Function<DataSnapshot, T> toNBT)
	{
		type = nbtType;
		this.fromNBT = fromNBT;
		this.toNBT = toNBT;
		serialisers.put(Integer.valueOf(type), this);
	}

	public Map<String, Object> addToMap(T nbt)
	{
		Map<String, Object> map = Maps.newHashMap();
		map.put("NBTTagType", Integer.valueOf(type));
		map.put("data", fromNBT.apply(nbt));
		return map;
	}

	public T readFromFirebase(DataSnapshot snap)
	{
		int i = snap.child("NBTTagType").getValue(Integer.class).intValue();
		if(i != type)
		{
			FMLLog.warning("NBT type mismatch! Attempted to deserialise NBT type %d with deserialiser of type %d", i, type);
		}
		return toNBT.apply(snap.child("data"));
	}

	protected int getType()
	{
		return type;
	}

	private static final FirebaseNBTSerialiser<NBTTagByte> BYTE = new FirebaseNBTSerialiser<>(1, NBTTagByte::getInt, snap -> new NBTTagByte(snap.getValue(Integer.class).byteValue()));
	private static final FirebaseNBTSerialiser<NBTTagShort> SHORT = new FirebaseNBTSerialiser<>(2, NBTTagShort::getInt, snap -> new NBTTagShort(snap.getValue(Integer.class).shortValue()));
	private static final FirebaseNBTSerialiser<NBTTagInt> INT = new FirebaseNBTSerialiser<>(3, NBTTagInt::getInt, snap -> new NBTTagInt(snap.getValue(Integer.class).intValue()));
	private static final FirebaseNBTSerialiser<NBTTagLong> LONG = new FirebaseNBTSerialiser<>(4, NBTTagLong::getLong, snap -> new NBTTagLong(snap.getValue(Long.class).longValue()));
	private static final FirebaseNBTSerialiser<NBTTagFloat> FLOAT = new FirebaseNBTSerialiser<>(5, NBTTagFloat::getFloat, snap -> new NBTTagFloat(snap.getValue(Float.class).floatValue()));
	private static final FirebaseNBTSerialiser<NBTTagDouble> DOUBLE = new FirebaseNBTSerialiser<>(6, NBTTagDouble::getDouble, snap -> new NBTTagDouble(snap.getValue(Double.class).doubleValue()));
	private static final FirebaseNBTSerialiser<NBTTagByteArray> BYTEARRAY = new FirebaseNBTSerialiser<>(7, tag -> Lists.newArrayList(tag.getByteArray()), snap -> new NBTTagByteArray(Bytes.toArray(snap.getValue(new GenericTypeIndicator<List<Byte>>()
	{}))));
	private static final FirebaseNBTSerialiser<NBTTagString> STRING = new FirebaseNBTSerialiser<>(8, NBTTagString::getString, snap -> new NBTTagString(snap.getValue(String.class)));
	private static final FirebaseNBTSerialiser<NBTTagList> LIST = new FirebaseNBTSerialiser<>(9, tag -> IntStream.range(0, tag.tagCount()).mapToObj(Integer::valueOf).collect(Collectors.toMap(i -> i.toString(), i -> getMapFromNBT(tag.get(i)))), snap -> {
		NBTTagList list = new NBTTagList();
		for(DataSnapshot child : snap.getChildren())
		{
			list.appendTag(getNBT(child));
		}
		return list;
	});
	private static final FirebaseNBTSerialiser<NBTTagCompound> COMPOUND = new FirebaseNBTSerialiser<>(10, tag -> tag.getKeySet().stream().collect(Collectors.toMap(k -> k, k -> getMapFromNBT(tag.getTag(k)))), snap -> {
		NBTTagCompound nbt = new NBTTagCompound();
		for(DataSnapshot child : snap.getChildren())
		{
			nbt.setTag(child.getKey(), getNBT(child));
		}
		return nbt;
	});
	private static final FirebaseNBTSerialiser<NBTTagIntArray> INTARRAY = new FirebaseNBTSerialiser<>(11, tag -> Lists.newArrayList(tag.getIntArray()), snap -> new NBTTagIntArray(snap.getValue(new GenericTypeIndicator<List<Integer>>()
	{}).stream().mapToInt(Integer::intValue).toArray()));

	private static FirebaseNBTSerialiser<?> getSerialiserFor(Integer nbtType)
	{
		return serialisers.get(nbtType);
	}

	public static <T extends NBTBase> Map<String, Object> getMapFromNBT(T nbt)
	{
		if(nbt == null)
		{
			return null;
		}
		@SuppressWarnings("unchecked")
		FirebaseNBTSerialiser<T> serialiser = (FirebaseNBTSerialiser<T>)getSerialiserFor(Integer.valueOf(nbt.getId()));
		return serialiser.addToMap(nbt);
	}

	public static <T extends NBTBase> void saveNBT(Firebase ref, T nbt)
	{
		if(nbt == null)
		{
			return;
		}
		ref.setValue(getMapFromNBT(nbt));
	}
	
	public static <T extends NBTBase> T getNBT(DataSnapshot snap)
	{
		@SuppressWarnings("unchecked")
		FirebaseNBTSerialiser<T> serialiser = (FirebaseNBTSerialiser<T>)getSerialiserFor(snap.child("NBTTagType").getValue(Integer.class));
		return serialiser.readFromFirebase(snap);
	}
}
