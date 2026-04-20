package com.clussmanproductions.trafficcontrol.client;

import java.util.ArrayList;
import java.util.List;

import com.clussmanproductions.trafficcontrol.ModTrafficControl;
import com.clussmanproductions.trafficcontrol.util.EnumTrafficLightFrameColor;
import com.clussmanproductions.trafficcontrol.util.TrafficLightFrameProperties;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

@SuppressWarnings("deprecation")
public class TrafficLightFrameCompositeBakedModel implements IBakedModel {

	private final IBakedModel base;
	private TextureAtlasSprite spriteBlackFlat;
	private TextureAtlasSprite spriteBlackTextured;
	private TextureAtlasSprite spriteBackGreen;
	private TextureAtlasSprite spriteBackYellow;
	private TextureAtlasSprite spriteBackOrange;

	public TrafficLightFrameCompositeBakedModel(IBakedModel base) {
		this.base = base;
	}

	private void ensureSprites() {
		if (spriteBlackFlat != null) {
			return;
		}
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		String mod = ModTrafficControl.MODID;
		spriteBlackFlat = map.getAtlasSprite(mod + ":blocks/black");
		spriteBlackTextured = map.getAtlasSprite(mod + ":blocks/black_textured");
		spriteBackGreen = map.getAtlasSprite(mod + ":blocks/frame_back_green");
		spriteBackYellow = map.getAtlasSprite(mod + ":blocks/frame_back_yellow");
		spriteBackOrange = map.getAtlasSprite(mod + ":blocks/frame_back_orange");
	}

	private static int vertexStrideBytes(VertexFormat format) {
		int sum = 0;
		for (int i = 0; i < format.getElementCount(); i++) {
			sum += format.getElement(i).getSize();
		}
		return sum;
	}

	private static int blockAtlasUvByteOffset(VertexFormat format) {
		int offset = 0;
		for (int i = 0; i < format.getElementCount(); i++) {
			VertexFormatElement el = format.getElement(i);
			if (el.getUsage() == VertexFormatElement.EnumUsage.UV && el.getIndex() == 0) {
				return offset;
			}
			offset += el.getSize();
		}
		return -1;
	}

	/**
	 * Rebinds the quad to another atlas sprite and remaps UVs from the quad's current sprite so faces
	 * sample the correct region (swapping sprite alone leaves UVs pointing at the old texture).
	 */
	private static BakedQuad retextureQuadUntinted(BakedQuad quad, TextureAtlasSprite newSprite) {
		TextureAtlasSprite oldSprite = quad.getSprite();
		VertexFormat fmt = quad.getFormat();
		int uvByteOffset = blockAtlasUvByteOffset(fmt);
		if (uvByteOffset < 0 || oldSprite == newSprite) {
			return new BakedQuad(quad.getVertexData(), -1, quad.getFace(), newSprite, quad.shouldApplyDiffuseLighting(), fmt);
		}
		int bytesPerVertex = vertexStrideBytes(fmt);
		int intsPerVertex = bytesPerVertex / 4;
		int uvOffsetInts = uvByteOffset / 4;
		int[] data = quad.getVertexData().clone();
		float duOld = oldSprite.getMaxU() - oldSprite.getMinU();
		float dvOld = oldSprite.getMaxV() - oldSprite.getMinV();
		float duNew = newSprite.getMaxU() - newSprite.getMinU();
		float dvNew = newSprite.getMaxV() - newSprite.getMinV();
		if (duOld <= 1e-6f || dvOld <= 1e-6f) {
			return new BakedQuad(data, -1, quad.getFace(), newSprite, quad.shouldApplyDiffuseLighting(), fmt);
		}
		for (int v = 0; v < 4; v++) {
			int base = v * intsPerVertex + uvOffsetInts;
			float u = Float.intBitsToFloat(data[base]);
			float vv = Float.intBitsToFloat(data[base + 1]);
			float uRel = (u - oldSprite.getMinU()) / duOld;
			float vRel = (vv - oldSprite.getMinV()) / dvOld;
			float nu = newSprite.getMinU() + uRel * duNew;
			float nv = newSprite.getMinV() + vRel * dvNew;
			data[base] = Float.floatToIntBits(nu);
			data[base + 1] = Float.floatToIntBits(nv);
		}
		return new BakedQuad(data, -1, quad.getFace(), newSprite, quad.shouldApplyDiffuseLighting(), fmt);
	}

	/**
	 * Remap tinted frame quads to full textures where we ship art (black: blinders + back;
	 * green/yellow/orange: backing only so blinders keep exact dye tints).
	 */
	private BakedQuad remapFrameQuad(BakedQuad quad, int frameOrdinal) {
		int ti = quad.getTintIndex();
		if (ti < 0) {
			return quad;
		}
		if (frameOrdinal == EnumTrafficLightFrameColor.BLACK.ordinal()) {
			TextureAtlasSprite spr = ti == 0 ? spriteBlackFlat : spriteBlackTextured;
			return retextureQuadUntinted(quad, spr);
		}
		if (ti != 1) {
			return quad;
		}
		TextureAtlasSprite back = null;
		if (frameOrdinal == EnumTrafficLightFrameColor.GREEN.ordinal()) {
			back = spriteBackGreen;
		} else if (frameOrdinal == EnumTrafficLightFrameColor.YELLOW.ordinal()) {
			back = spriteBackYellow;
		} else if (frameOrdinal == EnumTrafficLightFrameColor.ORANGE.ordinal()) {
			back = spriteBackOrange;
		}
		if (back == null) {
			return quad;
		}
		return retextureQuadUntinted(quad, back);
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		List<BakedQuad> quads = base.getQuads(state, side, rand);
		if (!(state instanceof IExtendedBlockState)) {
			return quads;
		}
		Integer fc = ((IExtendedBlockState) state).getValue(TrafficLightFrameProperties.FRAME_COLOR);
		int frameOrdinal = fc != null ? fc.intValue() : EnumTrafficLightFrameColor.BLACK.ordinal();
		ensureSprites();
		List<BakedQuad> out = new ArrayList<>(quads.size());
		for (BakedQuad q : quads) {
			out.add(remapFrameQuad(q, frameOrdinal));
		}
		return out;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return base.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return base.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return base.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return base.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return base.getOverrides();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return base.getItemCameraTransforms();
	}
}
