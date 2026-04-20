package com.clussmanproductions.trafficcontrol.event;

import com.clussmanproductions.trafficcontrol.ModBlocks;
import com.clussmanproductions.trafficcontrol.blocks.BlockBaseTrafficLight;
import com.clussmanproductions.trafficcontrol.util.TrafficLightFrameDyeHelper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Applies frame dye on the server before item/block use ordering can skip {@link net.minecraft.block.Block#onBlockActivated}.
 */
@EventBusSubscriber
public class TrafficLightFrameDyeEventHandler {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getWorld().isRemote || event.isCanceled()) {
			return;
		}
		EnumHand hand = event.getHand();
		if (hand == null) {
			return;
		}
		BlockPos pos = event.getPos();
		IBlockState state = event.getWorld().getBlockState(pos);

		BlockPos tePos = pos;
		if (state.getBlock() == ModBlocks.traffic_light_5_upper) {
			if (event.getWorld().getBlockState(pos.down()).getBlock() != ModBlocks.traffic_light_5) {
				return;
			}
			tePos = pos.down();
		} else if (!(state.getBlock() instanceof BlockBaseTrafficLight)) {
			return;
		}

		if (TrafficLightFrameDyeHelper.tryApplyDye(event.getWorld(), tePos, event.getEntityPlayer(), hand)) {
			event.setCanceled(true);
		}
	}
}
