package net.zarathul.simplefluidtanks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.zarathul.simplefluidtanks.SimpleFluidTanks;
import net.zarathul.simplefluidtanks.common.Utils;
import net.zarathul.simplefluidtanks.configuration.Config;
import net.zarathul.simplefluidtanks.tileentities.ValveBlockEntity;

import javax.annotation.Nullable;

/**
 * Represents a valve in the mods multiblock structure.
 */
public class ValveBlock extends WrenchableBlock
{
	public static final IntegerProperty DOWN = IntegerProperty.create("down", 0, 2);
	public static final IntegerProperty UP = IntegerProperty.create("up", 0, 2);
	public static final IntegerProperty NORTH = IntegerProperty.create("north", 0, 2);
	public static final IntegerProperty SOUTH = IntegerProperty.create("south", 0, 2);
	public static final IntegerProperty WEST = IntegerProperty.create("west", 0, 2);
	public static final IntegerProperty EAST = IntegerProperty.create("east", 0, 2);
	
	private static final int GRATE_TEXTURE_ID = 0;
	private static final int IO_TEXTURE_ID = 1;
	private static final int TANK_TEXTURE_ID = 2;

	public ValveBlock()
	{
		super(Block.Properties.create(TankMaterial.tankMaterial)
		.hardnessAndResistance(Config.valveBlockHardness, Config.valveBlockResistance)
		.sound(SoundType.METAL)
		.harvestLevel(2)
		.harvestTool(ToolType.PICKAXE));

		setRegistryName(SimpleFluidTanks.VALVE_BLOCK_NAME);
		//setUnlocalizedName(SimpleFluidTanks.VALVE_BLOCK_NAME);
		//setCreativeTab(SimpleFluidTanks.creativeTab);

		this.setDefaultState(this.getStateContainer().getBaseState()
				.with(UP, GRATE_TEXTURE_ID)
				.with(NORTH, IO_TEXTURE_ID));
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new ValveBlockEntity();
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(DOWN, UP, NORTH, SOUTH, WEST, EAST);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos)
	{
		ValveBlockEntity valveEntity = Utils.getTileEntityAt(world, ValveBlockEntity.class, pos);

		if (valveEntity != null)
		{
			if (valveEntity.hasTanks())
			{
				state = state.with(DOWN , (valveEntity.isFacingTank(Direction.DOWN))  ? GRATE_TEXTURE_ID : IO_TEXTURE_ID)
							 .with(UP   , (valveEntity.isFacingTank(Direction.UP))    ? GRATE_TEXTURE_ID : IO_TEXTURE_ID)
							 .with(NORTH, (valveEntity.isFacingTank(Direction.NORTH)) ? GRATE_TEXTURE_ID : IO_TEXTURE_ID)
							 .with(SOUTH, (valveEntity.isFacingTank(Direction.SOUTH)) ? GRATE_TEXTURE_ID : IO_TEXTURE_ID)
							 .with(WEST , (valveEntity.isFacingTank(Direction.WEST))  ? GRATE_TEXTURE_ID : IO_TEXTURE_ID)
							 .with(EAST , (valveEntity.isFacingTank(Direction.EAST))  ? GRATE_TEXTURE_ID : IO_TEXTURE_ID);
			}
			else
			{
				Direction facing = valveEntity.getFacing();

				state = state.with(DOWN , TANK_TEXTURE_ID)
							 .with(UP   , GRATE_TEXTURE_ID)
							 .with(NORTH, (facing == Direction.NORTH) ? IO_TEXTURE_ID : TANK_TEXTURE_ID)
							 .with(SOUTH, (facing == Direction.SOUTH) ? IO_TEXTURE_ID : TANK_TEXTURE_ID)
							 .with(WEST , (facing == Direction.WEST)  ? IO_TEXTURE_ID : TANK_TEXTURE_ID)
							 .with(EAST , (facing == Direction.EAST)  ? IO_TEXTURE_ID : TANK_TEXTURE_ID);
			}
		}

		return state;
	}

	@Override
	public boolean ticksRandomly(BlockState state)
	{
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack items)
	{
		super.onBlockPlacedBy(world, pos, state, placer, items);
		
		if (!world.isRemote)
		{
			Direction facing = placer.getHorizontalFacing().getOpposite();
			
			ValveBlockEntity valveEntity = Utils.getTileEntityAt(world, ValveBlockEntity.class, pos);
			
			if (valveEntity != null)
			{
				valveEntity.setFacing(facing);
				world.markChunkDirty(pos, valveEntity);
				world.notifyBlockUpdate(pos, state, state, 3);
			}
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving)
	{
		if (!world.isRemote)
		{
			ValveBlockEntity valveEntity = Utils.getTileEntityAt(world, ValveBlockEntity.class, pos);

			if (valveEntity != null)
			{
				valveEntity.formMultiblock();
			}
		}
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if (!world.isRemote)
		{
			ValveBlockEntity valveEntity = Utils.getTileEntityAt(world, ValveBlockEntity.class, pos);

			if (valveEntity != null)
			{
				IFluidHandler handler = valveEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, hit.getFace()).orElse(null);

				if (handler != null)
				{
					// FIXME: Horrible workaround until Forge fixes sounds not playing client-side.
					FluidStack tankFluidBefore = (valveEntity.getFluidAmount() > 0) ? valveEntity.getFluid().copy() : null;

					if (FluidUtil.interactWithFluidHandler(player, hand, handler))
					{
						// Pick a sound depending on what happens with the held container item.
						SoundEvent soundevent = (tankFluidBefore == null || tankFluidBefore.amount < valveEntity.getFluidAmount())
							? valveEntity.getFluid().getFluid().getEmptySound()
							: tankFluidBefore.getFluid().getFillSound();

						((ServerPlayerEntity)player).connection.sendPacket(new SPlaySoundEffectPacket(
							soundevent,
							player.getSoundCategory(),
							player.posX, player.posY, player.posZ,
							1.0f, 1.0f));
					}
				}
			}
		}

		if (FluidUtil.getFluidHandler(player.getHeldItem(hand)) != null) return true;
		
		return super.onBlockActivated(state, world, pos, player, hand, hit);
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos)
	{
		ValveBlockEntity valveEntity = Utils.getTileEntityAt(world, ValveBlockEntity.class, pos);

		if (valveEntity != null)
		{
			float fluidAmount = valveEntity.getFluidAmount();
			float capacity = valveEntity.getCapacity();
			int signalStrength = Utils.getComparatorLevel(fluidAmount, capacity);

			return signalStrength;
		}

		return 0;
	}

	// FIXME: Is there no other way to get notified when the block breaks?
	@Override
	public void dropXpOnBlockBreak(World world, BlockPos pos, int amount)
	{
		if (!world.isRemote)
		{
			// disband the multiblock if the valve is mined/destroyed
			ValveBlockEntity valveEntity = Utils.getTileEntityAt(world, ValveBlockEntity.class, pos);

			if (valveEntity != null)
			{
				valveEntity.disbandMultiblock();
			}
		}
	}

	@Override
	protected void handleToolWrenchClick(World world, BlockPos pos, PlayerEntity player, ItemStack equippedItemStack)
	{
		// On sneak use: disband the multiblock | On use: rebuild the multiblock

		ValveBlockEntity valveEntity = Utils.getTileEntityAt(world, ValveBlockEntity.class, pos);

		if (player.isSneaking())
		{
			if (valveEntity != null)
			{
				valveEntity.disbandMultiblock();
			}

			/*
			world.setBlockToAir(pos);
			dropBlockAsItem(world, pos, this.getDefaultState(), 0);
			*/
			// FIXME: Is this enough?
			world.destroyBlock(pos, true);
		}
		else if (valveEntity != null)
		{
			// rebuild the tank
			valveEntity.formMultiblock();
		}
	}
}
