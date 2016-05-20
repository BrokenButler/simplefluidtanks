package net.zarathul.simplefluidtanks;

import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.zarathul.simplefluidtanks.blocks.TankBlock;
import net.zarathul.simplefluidtanks.blocks.ValveBlock;
import net.zarathul.simplefluidtanks.items.TankItem;
import net.zarathul.simplefluidtanks.items.ValveItem;
import net.zarathul.simplefluidtanks.items.WrenchItem;

@Mod(modid = SimpleFluidTanks.MOD_ID, name = "SimpleFluidTanks", version = SimpleFluidTanks.VERSION, guiFactory = "net.zarathul.simplefluidtanks.configuration.ConfigGuiFactory")
public class SimpleFluidTanks
{
	@Instance(value = SimpleFluidTanks.MOD_ID)
	public static SimpleFluidTanks instance;

	@SidedProxy(clientSide = "net.zarathul.simplefluidtanks.ClientProxy", serverSide = "net.zarathul.simplefluidtanks.ServerProxy")
	public static CommonProxy proxy;

	// blocks
	public static TankBlock tankBlock;
	public static ValveBlock valveBlock;

	// items
	public static TankItem tankItem;
	public static ValveItem valveItem;
	public static WrenchItem wrenchItem;

	// creative tabs
	public static CreativeTabs creativeTab;

	// event  hub
	public static ClientEventHub clientEventHub;
	public static CommonEventHub commonEventHub;
	
	// logger
	public static Logger log;
	
	// constants
	public static final String MOD_ID = "simplefluidtanks";
	public static final String MOD_READABLE_NAME = "Simple Fluid Tanks";
	public static final String MOD_TAB_NAME = "Simple Mods";
	public static final String VERSION = "@VERSION@";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}
}