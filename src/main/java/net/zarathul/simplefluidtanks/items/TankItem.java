package net.zarathul.simplefluidtanks.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.zarathul.simplefluidtanks.SimpleFluidTanks;
import net.zarathul.simplefluidtanks.blocks.TankBlock;
import net.zarathul.simplefluidtanks.common.Utils;
import net.zarathul.simplefluidtanks.configuration.Config;
import net.zarathul.simplefluidtanks.registration.Registry;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * {@link TankBlock} in item form.
 */
public class TankItem extends ItemBlock
{
	private static final String toolTipKey = "item." + Registry.TANKITEM_NAME + ".toolTip";
	private static final String toolTipDetailsKey = "item." + Registry.TANKITEM_NAME + ".toolTipDetails";

	public TankItem(Block block)
	{
		super(block);
		this.setMaxStackSize(64);
		this.setCreativeTab(SimpleFluidTanks.creativeTab);
		this.setUnlocalizedName(Registry.TANKITEM_NAME);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getSpriteNumber()
	{
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack items, EntityPlayer player, List list, boolean advancedItemTooltipsEnabled)
	{
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			list.addAll(Utils.multiLineTranslateToLocal(toolTipDetailsKey, Config.bucketsPerTank));
		}
		else
		{
			list.add(StatCollector.translateToLocal(toolTipKey));
		}
	}
}