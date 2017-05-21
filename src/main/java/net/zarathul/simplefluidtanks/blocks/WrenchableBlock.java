package net.zarathul.simplefluidtanks.blocks;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.zarathul.simplefluidtanks.items.WrenchItem;

/**
 * A base class for blocks that have custom behavior when a wrench is used on them.
 */
public abstract class WrenchableBlock extends Block
{

	protected WrenchableBlock(Material material)
	{
		super(material);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
			EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			ItemStack heldItem = player.getHeldItem(hand);
			
			if (heldItem != null)
			{
				Item item = heldItem.getItem();

				if (item instanceof WrenchItem || item instanceof IToolWrench)	// react to Wrenches TODO: Add Thermal Expansion support when APIs update
				{
					handleToolWrenchClick(world, pos, player, heldItem);

					return true;
				}
			}

			return false;
		}

		return true;
	}

	/**
	 * Handles clicks with wrenches on the {@link BlockContainer}.
	 * 
	 * @param world
	 * The world.
	 * @param pos
	 * The {@link ValveBlock}s coordinates.
	 * @param player
	 * The player using the item.
	 * @param equippedItemStack
	 * The item(stack) used on the {@link ValveBlock}.
	 */
	protected abstract void handleToolWrenchClick(World world, BlockPos pos, EntityPlayer player, ItemStack equippedItemStack);
}
