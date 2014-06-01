package net.zarathul.simplefluidtanks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.Configuration;
import net.zarathul.simplefluidtanks.blocks.TankBlock;
import net.zarathul.simplefluidtanks.blocks.ValveBlock;
import net.zarathul.simplefluidtanks.items.WrenchItem;
import net.zarathul.simplefluidtanks.rendering.TankBlockRenderer;
import net.zarathul.simplefluidtanks.rendering.TankItemRenderer;
import net.zarathul.simplefluidtanks.rendering.ValveItemRenderer;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = SimpleFluidTanks.MOD_ID, name = "SimpleFluidTanks", version = SimpleFluidTanks.VERSION)
@NetworkMod(serverSideRequired = true, clientSideRequired = true)
public class SimpleFluidTanks
{
	@Instance(value = "SimpleFluidTanks")
	public static SimpleFluidTanks instance;

	@SidedProxy(clientSide = "net.zarathul.simplefluidtanks.ClientProxy", serverSide = "net.zarathul.simplefluidtanks.ServerProxy")
	public static CommonProxy proxy;
	
	// blocks
	public static TankBlock tankBlock;
	public static ValveBlock valveBlock;
	
	// items
	public static WrenchItem wrenchItem;
	
	// custom renderers
	@SideOnly(Side.CLIENT)
	public static TankBlockRenderer tankBlockRenderer;
	@SideOnly(Side.CLIENT)
	public static TankItemRenderer tankItemRenderer;
	@SideOnly(Side.CLIENT)
	public static ValveItemRenderer valveItemRenderer;
	
	public static int tankBlockId;
	public static int valveBlockId;
	public static int wrenchItemId;
	public static float tankBlockHardness;
	public static float tankBlockResistance;
	public static float valveBlockHardness;
	public static float valveBlockResistance;
	
	public static int bucketsPerTank;
	
	public static CreativeTabs creativeTab;
	
	// constants
	public static final String MOD_ID = "simplefluidtanks";
	public static final String VERSION = "1.0.0.9";
	
	// - registry
	public static final String REGISTRY_TANKBLOCK_NAME = "tankBlock";
	public static final String REGISTRY_TANKBLOCK_KEY = MOD_ID + REGISTRY_TANKBLOCK_NAME;
	
	public static final String REGISTRY_TANKITEM_NAME = "tankItem";
	
	public static final String REGISTRY_TANKBLOCK_ENTITY_NAME = "tankBlockEntity";
	public static final String REGISTRY_TANKBLOCK_ENTITY_KEY = MOD_ID + REGISTRY_TANKBLOCK_ENTITY_NAME;
	
	public static final String REGISTRY_VALVEBLOCK_NAME = "valveBlock";
	public static final String REGISTRY_VALVEBLOCK_KEY = MOD_ID + REGISTRY_VALVEBLOCK_NAME;
	
	public static final String REGISTRY_VALVEITEM_NAME = "valveItem";
	
	public static final String REGISTRY_VALVEBLOCK_ENTITY_NAME = "valveBlockEntity";
	public static final String REGISTRY_VALVEBLOCK_ENTITY_KEY = MOD_ID + REGISTRY_VALVEBLOCK_ENTITY_NAME;
	
	public static final String REGISTRY_WRENCHITEM_NAME = "wrench";
	public static final String REGISTRY_WRENCHITEM_KEY = SimpleFluidTanks.MOD_ID + ":" + REGISTRY_WRENCHITEM_NAME;
	
	public static String REGISTRY_THERMAL_EXPANSION_MOD_ID = "ThermalExpansion";
	public static String REGISTRY_THERMAL_EXPANSION_HARDENED_GLASS = "glassHardened";
	public static String REGISTRY_THERMAL_EXPANSION_BRONZE_INGOT = "ingotBronze";
	
	// - config settings
	public static final int CONFIG_DEFAULT_TANKBLOCK_ID = 2529;
	public static final int CONFIG_DEFAULT_VALVEBLOCK_ID = 2530;
	public static final int CONFIG_DEFAULT_WRENCHITEM_ID = 26000;
	public static final int CONFIG_DEFAULT_BUCKETS_PER_TANK = 16;
	public static final float CONFIG_DEFAULT_BLOCK_HARDNESS = 50;
	public static final float CONFIG_DEFAULT_BLOCK_RESISTANCE = 1000;
	public static final String CONFIG_CATEGORY_MAIN = "simple fluid tanks";
	public static final String CONFIG_TANKBLOCK_ID_KEY = "TankblockId";
	public static final String CONFIG_TANKBLOCK_ID_COMMENT = "The tanks block id.";
	public static final String CONFIG_VALVEBLOCK_ID_KEY = "ValveblockId";
	public static final String CONFIG_VALVEBLOCK_ID_COMMENT = "The valves block id.";
	public static final String CONFIG_WRENCHITEM_ID_KEY = "WrenchItemId";
	public static final String CONFIG_WRENCHITEM_ID_COMMENT = "The wrench item id.";
	public static final String CONFIG_BUCKETS_PER_TANK_KEY = "BucketsPerTank";
	public static final String CONFIG_BUCKETS_PER_TANK_COMMENT = "The amount of fluid that can be stored per tank (measured in buckets).";
	public static final String CATEGORY_BLOCKS = "blocks";
	public static final String CATEGORY_BLOCKS_COMMENT = "Here you can adjust some of the blocks properties.";
	public static final String CATEGORY_BLOCKS_TANKBLOCK = CATEGORY_BLOCKS + Configuration.CATEGORY_SPLITTER + "tankBlock";
	public static final String CATEGORY_BLOCKS_VALVEBLOCK = CATEGORY_BLOCKS + Configuration.CATEGORY_SPLITTER + "valveBlock";
	public static final String BLOCK_HARDNESS_COMMENT = "The amount of hits the block can take before it breaks.";
	public static final String BLOCK_RESISTANCE_COMMENT = "The blocks resistance to explosions.";
	public static final String BLOCK_HARDNESS_KEY = "hardness";
	public static final String BLOCK_RESISTANCE_KEY = "resistance";
	public static final String CONFIG_CATEGORY_MOD_INTEROP = "mod interop";
	public static final String CONFIG_TE_MOD_ID_KEY = "ThermalExpansionModId";
	public static final String CONFIG_TE_MOD_ID_COMMENT = "The mod id for Thermal Expansion. This is used to include TE items in the recipes.";
	public static final String CONFIG_TE_MOD_HARDENED_GLASS_KEY = "TE_HardenedGlass";
	public static final String CONFIG_TE_MOD_HARDENED_GLASS_COMMENT = "The game registry key of Thermal Expansions hardened glass item.";
	public static final String CONFIG_TE_MOD_BRONZE_INGOT_KEY = "TE_BronzeIngots";
	public static final String CONFIG_TE_MOD_BRONZE_INGOT_COMMENT = "The game registry key of Thermal Expansions bronze(tinkers alloy) ingot.";

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

	public static String getModId()
	{
		return SimpleFluidTanks.MOD_ID;
	}
}