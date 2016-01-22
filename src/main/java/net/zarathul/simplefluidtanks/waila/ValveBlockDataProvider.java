package net.zarathul.simplefluidtanks.waila;

import mcp.mobius.waila.api.ITaggedList.ITipList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataAccessorServer;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.zarathul.simplefluidtanks.blocks.ValveBlock;
import net.zarathul.simplefluidtanks.tileentities.ValveBlockEntity;

/**
 * Provides Waila with tooltip information for {@link ValveBlock}s.
 */
public final class ValveBlockDataProvider implements IWailaDataProvider
{
	public static final ValveBlockDataProvider instance = new ValveBlockDataProvider();

	private ValveBlockDataProvider()
	{
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}

	@Override
	public NBTTagCompound getNBTData(TileEntity te, NBTTagCompound tag, IWailaDataAccessorServer accessor)
	{
		return null;
	}

	@Override
	public ITipList getWailaHead(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}

	@Override
	public ITipList getWailaBody(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		TileEntity entity = accessor.getTileEntity();

		if (entity != null && entity instanceof ValveBlockEntity)
		{
			ValveBlockEntity valveEntity = (ValveBlockEntity) entity;

			if (config.getConfig(Registry.WAILA_TANK_COUNT_KEY))
			{
				currenttip.add(StatCollector.translateToLocalFormatted(Registry.WAILA_TOOLTIP_TANKS, valveEntity.getLinkedTankCount()));
			}

			if (config.getConfig(Registry.WAILA_TOTAL_CAPACITY_KEY))
			{
				int amount = valveEntity.getFluidAmount();
				int capacity = valveEntity.getCapacity();
				int totalFillPercentage = (capacity > 0) ? MathHelper.clamp_int((int) ((long) amount * 100 / capacity), 0, 100) : 0;

				if (config.getConfig(Registry.WAILA_CAPACITY_IN_MILLIBUCKETS_KEY))
				{
					currenttip.add(StatCollector.translateToLocalFormatted(Registry.WAILA_TOOLTIP_VALVE_CAPACITY, amount, capacity, "mB", totalFillPercentage));
				}
				else
				{
					currenttip.add(StatCollector.translateToLocalFormatted(Registry.WAILA_TOOLTIP_VALVE_CAPACITY, amount / 1000, capacity / 1000, "B", totalFillPercentage));
				}
			}

			if (config.getConfig(Registry.WAILA_FLUID_NAME_KEY))
			{
				String fluidName = valveEntity.getLocalizedFluidName();

				if (fluidName == null)
				{
					fluidName = StatCollector.translateToLocal(Registry.WAILA_TOOLTIP_FLUID_EMPTY);
				}

				currenttip.add(StatCollector.translateToLocalFormatted(Registry.WAILA_TOOLTIP_FLUID, fluidName));
			}
		}

		return currenttip;
	}

	@Override
	public ITipList getWailaTail(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}
}