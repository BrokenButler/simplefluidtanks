package net.zarathul.simplefluidtanks.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.zarathul.simplefluidtanks.items.WrenchItem;

/**
 * A base class for blocks that have custom behavior when a buildcraft compatible wrenches is used on them.
 */
public abstract class WrenchableBlock extends BlockContainer
{

	protected WrenchableBlock(Material material)
	{
		super(material);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			ItemStack equippedItemStack = player.getCurrentEquippedItem();

			if (equippedItemStack != null)
			{
				if (equippedItemStack.getItem() instanceof WrenchItem)	// react to ToolWrench (TODO: Replace by an Wrench API interface (e.g.Thermal Expansion))
				{
					handleToolWrenchClick(world, pos, player, equippedItemStack);

					return true;
				}
			}

			return false;
		}

		return true;
	}

	/**
	 * Handles Buildcraft ToolWrenches used on the {@link BlockContainer}.
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
