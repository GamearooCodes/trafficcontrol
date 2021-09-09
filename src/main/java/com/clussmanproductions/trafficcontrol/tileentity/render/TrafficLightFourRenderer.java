package com.clussmanproductions.trafficcontrol.tileentity.render;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.clussmanproductions.trafficcontrol.ModTrafficControl;
import com.clussmanproductions.trafficcontrol.tileentity.TrafficLightTileEntity;
import com.clussmanproductions.trafficcontrol.tileentity.render.BaseTrafficLightRenderer.BulbRenderer;
import com.clussmanproductions.trafficcontrol.util.EnumTrafficLightBulbTypes;
import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class TrafficLightFourRenderer extends BaseTrafficLightRenderer
{
	@Override
	protected double getBulbZLocation()
	{
		return 10.25;
	}
	
	@Override
	protected List<BulbRenderer> getBulbRenderers() 
	{
		return ImmutableList
				.<BulbRenderer>builder()
				.add(new BulbRenderer(5.2, 22, 0))
				.add(new BulbRenderer(5.2, 15.3, 1))
				.add(new BulbRenderer(5.2, 9, 2))
				.add(new BulbRenderer(5.2, 3, 3))
				.build();
	}
}
