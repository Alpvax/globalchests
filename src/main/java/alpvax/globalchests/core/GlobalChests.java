package alpvax.globalchests.core;

import alpvax.globalchests.block.BlockGlobalChest;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Metadata;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = GlobalChests.MOD_ID, version = GlobalChests.VERSION)
public class GlobalChests
{
	public static final String MOD_ID = "globalchests";
	public static final String VERSION = "@VERSION@";

	/*@SidedProxy(
			clientSide = "alpvax.characteroverhaul.core.proxy.ClientProxy",
			serverSide = "alpvax.characteroverhaul.core.proxy.CommonProxy")
	public static CommonProxy proxy;*/

	@Mod.Instance(MOD_ID)
	public static GlobalChests instance;

	@Metadata(MOD_ID)
	public static ModMetadata meta;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		meta.name = I18n.format("mod." + MOD_ID + ".name");
		meta.description = I18n.format("mod." + MOD_ID + ".description");
		meta.authorList.add("Alpvax");
		meta.autogenerated = false;

		new Blocks();

		//MinecraftForge.EVENT_BUS.register(hooks);
	}

	public static class Blocks
	{
		public static final Block CHEST;

		private static final Block[] blocks = {CHEST = new BlockGlobalChest().setRegistryName("chest")};

		static
		{
			for(Block block : blocks)
			{
				GameRegistry.register(block);
			}
		}
	}
}
