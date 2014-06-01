package net.zarathul.simplefluidtanks.blocks;

import java.util.Random;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.zarathul.simplefluidtanks.SimpleFluidTanks;
import net.zarathul.simplefluidtanks.common.Direction;
import net.zarathul.simplefluidtanks.common.Utils;
import net.zarathul.simplefluidtanks.tileentities.ValveBlockEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Represents a valve in the mods multiblock structure.
 */
public class ValveBlock extends WrenchableBlock
{
	public ValveBlock(int blockId)
	{
		super(blockId, TankMaterial.tankMaterial);
		
		setUnlocalizedName(SimpleFluidTanks.REGISTRY_VALVEBLOCK_NAME);
		setCreativeTab(SimpleFluidTanks.creativeTab);
		setHardness(SimpleFluidTanks.valveBlockHardness);
		setResistance(SimpleFluidTanks.valveBlockResistance);
		setStepSound(soundMetalFootstep);
	}

	@SideOnly(Side.CLIENT)
	private Icon icon;
	@SideOnly(Side.CLIENT)
	private Icon iconTank;
	@SideOnly(Side.CLIENT)
	private Icon iconIo;

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int meta)
	{
		return (side == meta) ? iconIo : (side == Direction.YPOS) ? iconTank : icon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side)
	{
		ValveBlockEntity valveEntity = Utils.getTileEntityAt(blockAccess, ValveBlockEntity.class, x, y, z);
		
		if (valveEntity != null && valveEntity.hasTanks())
		{
			if (valveEntity.isFacingTank(side))
			{
				return iconTank;
			}
			
			return iconIo;
		}
		
		return getIcon(side, blockAccess.getBlockMetadata(x, y, z));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		icon = iconRegister.registerIcon(SimpleFluidTanks.MOD_ID + ":valve");
		iconTank = iconRegister.registerIcon(SimpleFluidTanks.MOD_ID + ":valve_tank");
		iconIo = iconRegister.registerIcon(SimpleFluidTanks.MOD_ID + ":valve_io");
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new ValveBlockEntity();
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack items)
	{
		super.onBlockPlacedBy(world, x, y, z, player, items);
		
		int l = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int direction;
        
        switch (l)
        {
        	case 1:
        		direction = Direction.XPOS;
        		break;
        	
        	case 2:
        		direction = Direction.ZPOS;
        		break;
        	
        	case 3:
        		direction = Direction.XNEG;
        		break;
        		
        	default:
        		direction = Direction.ZNEG;
        		break;
        }
        
        world.setBlockMetadataWithNotify(x, y, z, direction, 2);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);
		
		if (!world.isRemote)
		{
			ValveBlockEntity valveEntity = Utils.getTileEntityAt(world, ValveBlockEntity.class, x, y, z);
			
			if (valveEntity != null)
			{
				valveEntity.rebuild();
			}
		}
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int par5)
	{
		reset(world, x, y, z);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		if (!world.isRemote)
		{
			ItemStack equippedItemStack = player.getCurrentEquippedItem();
			
			if (equippedItemStack != null)
			{
				if (FluidContainerRegistry.isContainer(equippedItemStack))	// react to registered fluid containers
				{
					handleContainerClick(world, x, y, z, player, equippedItemStack);
					
					return true;
				}
			}
		}
		
		return super.onBlockActivated(world, x, y, z, player, par6, par7, par8, par9);
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random)
	{
		return 1;
	}
	
	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side)
	{
		ValveBlockEntity valveEntity = Utils.getTileEntityAt(world, ValveBlockEntity.class, x, y, z);
		
		if (valveEntity != null)
		{
			float capacity = valveEntity.getCapacity();
			float fluidAmount = valveEntity.getFluidAmount();
			int signalStrength = ((int)Math.floor((fluidAmount / capacity)  * 14.0f)) + ((fluidAmount > 0) ? 1 : 0);
			
			return signalStrength;
		}
		
		return 0;
	}
	
	@Override
	protected void handleToolWrenchClick(World world, int x, int y, int z, EntityPlayer player, ItemStack equippedItemStack)
	{
		if (player.isSneaking())
		{
			// dismantle aka. instantly destroy the valve and drop the appropriate item, unlinking all connected tanks in the process
			reset(world, x, y, z);
			// destroy the valve block
			world.setBlockToAir(x, y, z);
			// last two parameters are metadata and fortune
			dropBlockAsItem(world, x, y, z, 0, 0);
		}
		else
		{
			// rebuild the tank
			ValveBlockEntity entity = Utils.getTileEntityAt(world, ValveBlockEntity.class, x, y, z);
			entity.rebuild();
		}
	}

	/**
	 * Handles fluid containers used on the {@link ValveBlock}. Currently only buckets (empty and filled) are supported,
	 * @param world
	 * The world.
	 * @param x
	 * The {@link ValveBlock}s x-coordinate.
	 * @param y
	 * The {@link ValveBlock}s y-coordinate.
	 * @param z
	 * The {@link ValveBlock}s z-coordinate.
	 * @param player
	 * The player using the item.
	 * @param equippedItemStack
	 * The item(stack) used on the {@link ValveBlock}.
	 */
	private void handleContainerClick(World world, int x, int y, int z, EntityPlayer player, ItemStack equippedItemStack)
	{
		ValveBlockEntity valveEntity = Utils.getTileEntityAt(world, ValveBlockEntity.class, x, y, z);

		if (valveEntity != null)
		{
			// only deal with buckets, all other fluid containers need to use pipes
			if (FluidContainerRegistry.isBucket(equippedItemStack))
			{
				if (FluidContainerRegistry.isEmptyContainer(equippedItemStack))
				{
					fillBucketFromTank(world, x, y, z, player, equippedItemStack, valveEntity);
				}
				else
				{
					drainBucketIntoTank(valveEntity, player, equippedItemStack);
				}
			}
		}
	}

	/**
	 * Fills an empty bucket with the liquid contained in the multiblock tank.
	 * @param world
	 * The world.
	 * @param x
	 * The {@link ValveBlock}s x-coordinate.
	 * @param y
	 * The {@link ValveBlock}s y-coordinate.
	 * @param z
	 * The {@link ValveBlock}s z-coordinate.
	 * @param player
	 * The player using the bucket.
	 * @param equippedItemStack
	 * The {@link ItemStack} that contains the bucket.
	 * @param valveEntity
	 * The affected {@link ValveBlock}s {@link TileEntity} ({@link ValveBlockEntity}).
	 */
	private void fillBucketFromTank(World world, int x, int y, int z, EntityPlayer player, ItemStack equippedItemStack, ValveBlockEntity valveEntity)
	{
		// fill empty bucket with liquid from the tank if it has stored enough
		if (valveEntity.getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME)
		{
			FluidStack oneBucketOfFluid = new FluidStack(valveEntity.getFluid(), FluidContainerRegistry.BUCKET_VOLUME);
			ItemStack filledBucket = FluidContainerRegistry.fillFluidContainer(oneBucketOfFluid, FluidContainerRegistry.EMPTY_BUCKET);
			
			if (filledBucket != null && valveEntity.drain(null, oneBucketOfFluid, true).amount == FluidContainerRegistry.BUCKET_VOLUME)
			{
				// add filled bucket to player inventory or drop it to the ground if the inventory is full
	            if (!player.inventory.addItemStackToInventory(filledBucket))
	            {
	                world.spawnEntityInWorld(new EntityItem(world, x + 0.5D, y + 1.5D, z + 0.5D, filledBucket));
	            }
	            else if (player instanceof EntityPlayerMP)
	            {
	                ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
	            }
			}

            if (--equippedItemStack.stackSize <= 0)
            {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
            }
		}
	}
	
	/**
	 * Drains the contents of a bucket into the multiblock tank.
	 * @param valveEntity
	 * The affected {@link ValveBlock}s {@link TileEntity} ({@link ValveBlockEntity}).
	 * @param player
	 * The player using the bucket.
	 * @param equippedItemStack
	 * The {@link ItemStack} that contains the bucket.
	 */
	private void drainBucketIntoTank(ValveBlockEntity valveEntity, EntityPlayer player, ItemStack equippedItemStack)
	{
		// fill the liquid from the bucket into the tank
		if ((valveEntity.getFluidAmount() == 0 || valveEntity.getFluid().isFluidEqual(equippedItemStack)) && valveEntity.getCapacity() - valveEntity.getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME)
		{
			FluidStack fluidFromBucket = FluidContainerRegistry.getFluidForFilledItem(equippedItemStack);
			
			if (valveEntity.fill(null, fluidFromBucket, true) == FluidContainerRegistry.BUCKET_VOLUME)
			{
				// don't consume the filled bucket in creative mode 
                if (!player.capabilities.isCreativeMode)
                {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Item.bucketEmpty));
                }
			}
		}
	}
	
	/**
	 * Resets aka. disconnects all connected {@link TankBlock}s. 
	 * @param world
	 * The world.
	 * @param x
	 * The {@link ValveBlock}s x-coordinate.
	 * @param y
	 * The {@link ValveBlock}s y-coordinate.
	 * @param z
	 * The {@link ValveBlock}s z-coordinate.
	 */
	private void reset(World world, int x, int y, int z)
	{
		if (!world.isRemote)
		{
			ValveBlockEntity valveEntity = Utils.getTileEntityAt(world, ValveBlockEntity.class, x, y, z);
			
			if (valveEntity != null)
			{
				valveEntity.reset();
			}
		}
	}
}
