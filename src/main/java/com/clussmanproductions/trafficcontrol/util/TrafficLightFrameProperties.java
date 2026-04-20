package com.clussmanproductions.trafficcontrol.util;

import net.minecraftforge.common.property.IUnlistedProperty;

public final class TrafficLightFrameProperties {

	public static final IUnlistedProperty<Integer> FRAME_COLOR = new IUnlistedProperty<Integer>() {
		@Override
		public String getName() {
			return "tc_framecolor";
		}

		@Override
		public boolean isValid(Integer value) {
			return value != null && value >= 0 && value <= 3;
		}

		@Override
		public Class<Integer> getType() {
			return Integer.class;
		}

		@Override
		public String valueToString(Integer value) {
			return String.valueOf(value);
		}
	};

	private TrafficLightFrameProperties() {
	}
}
