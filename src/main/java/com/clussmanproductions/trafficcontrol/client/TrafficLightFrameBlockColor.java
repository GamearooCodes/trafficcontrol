package com.clussmanproductions.trafficcontrol.client;

import com.clussmanproductions.trafficcontrol.ModBlocks;
import com.clussmanproductions.trafficcontrol.tileentity.BaseTrafficLightTileEntity;
import com.clussmanproductions.trafficcontrol.util.EnumTrafficLightFrameColor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public final class TrafficLightFrameBlockColor implements IBlockColor {

	public static final IBlockColor INSTANCE = new TrafficLightFrameBlockColor();

	private TrafficLightFrameBlockColor() {
	}

	@Override
	public int colorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos, int tintIndex) {
		if (tintIndex != 0 && tintIndex != 1) {
			return 0xFFFFFF;
		}
		if (world == null || pos == null) {
			return EnumTrafficLightFrameColor.BLACK.getTintRgb();
		}
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof BaseTrafficLightTileEntity) {
			return ((BaseTrafficLightTileEntity) te).getFrameColor().getTintRgb();
		}
		if (state.getBlock() == ModBlocks.traffic_light_5_upper) {
			te = world.getTileEntity(pos.down());
			if (te instanceof BaseTrafficLightTileEntity) {
				return ((BaseTrafficLightTileEntity) te).getFrameColor().getTintRgb();
			}
		}
		return EnumTrafficLightFrameColor.BLACK.getTintRgb();
	}
}
