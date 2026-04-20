package com.clussmanproductions.trafficcontrol.util;

import javax.annotation.Nullable;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Frame colors supported when dyeing a placed traffic light (black, green, yellow, orange).
 */
public enum EnumTrafficLightFrameColor {
	BLACK(0),
	GREEN(1),
	YELLOW(2),
	ORANGE(3);

	public static final String NBT_KEY = "TCFrameColor";

	private final byte id;

	EnumTrafficLightFrameColor(int id) {
		this.id = (byte) id;
	}

	public byte getId() {
		return id;
	}

	public int getTintRgb() {
		switch (this) {
		case BLACK:
			return 0x191919;
		case GREEN:
			return EnumDyeColor.GREEN.getColorValue();
		case YELLOW:
			return EnumDyeColor.YELLOW.getColorValue();
		case ORANGE:
			return EnumDyeColor.ORANGE.getColorValue();
		default:
			return 0x191919;
		}
	}

	public int getTintArgb() {
		return 0xFF000000 | getTintRgb();
	}

	public static EnumTrafficLightFrameColor fromId(int id) {
		for (EnumTrafficLightFrameColor c : values()) {
			if (c.id == id) {
				return c;
			}
		}
		return BLACK;
	}

	@Nullable
	public static EnumTrafficLightFrameColor fromDyeStack(ItemStack stack) {
		if (stack.isEmpty() || !(stack.getItem() instanceof ItemDye)) {
			return null;
		}
		EnumDyeColor dye = EnumDyeColor.byDyeDamage(stack.getMetadata());
		switch (dye) {
		case BLACK:
			return BLACK;
		case GREEN:
		case LIME:
			return GREEN;
		case YELLOW:
			return YELLOW;
		case ORANGE:
			return ORANGE;
		default:
			return null;
		}
	}

	public static EnumTrafficLightFrameColor readFromItemTag(@Nullable NBTTagCompound tag) {
		if (tag == null || !tag.hasKey(NBT_KEY)) {
			return BLACK;
		}
		return fromId(tag.getByte(NBT_KEY));
	}

	public void writeToItemTag(NBTTagCompound tag) {
		tag.setByte(NBT_KEY, id);
	}
}
