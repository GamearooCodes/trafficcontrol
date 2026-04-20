package com.clussmanproductions.trafficcontrol.util;

import com.clussmanproductions.trafficcontrol.tileentity.BaseTrafficLightTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class TrafficLightFrameDyeHelper {

	private TrafficLightFrameDyeHelper() {
	}

	/**
	 * If the player is holding a supported dye, applies that frame color to the traffic light tile at {@code tePos} and consumes one dye (unless creative).
	 *
	 * @return true if a supported dye was used (interaction consumed)
	 */
	public static boolean tryApplyDye(World world, BlockPos tePos, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		EnumTrafficLightFrameColor color = EnumTrafficLightFrameColor.fromDyeStack(stack);
		if (color == null) {
			return false;
		}
		TileEntity te = world.getTileEntity(tePos);
		if (!(te instanceof BaseTrafficLightTileEntity)) {
			return false;
		}
		if (!world.isRemote) {
			((BaseTrafficLightTileEntity) te).setFrameColor(color);
			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
		}
		player.swingArm(hand);
		return true;
	}
}
