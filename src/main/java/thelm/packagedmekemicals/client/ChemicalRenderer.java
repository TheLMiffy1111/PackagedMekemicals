package thelm.packagedmekemicals.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.fluids.FluidAttributes;

// Code from Refined Storage, modified for chemicals
public class ChemicalRenderer {

	public static final ChemicalRenderer INSTANCE = new ChemicalRenderer(16, 16, 16);

	private static final int TEX_WIDTH = 16;
	private static final int TEX_HEIGHT = 16;

	private final int width;
	private final int height;
	private final int minHeight;

	public ChemicalRenderer(int width, int height, int minHeight) {
		this.width = width;
		this.height = height;
		this.minHeight = minHeight;
	}

	private static TextureAtlasSprite getChemicalSprite(ChemicalStack<?> chemicalStack) {
		Minecraft minecraft = Minecraft.getInstance();
		Chemical<?> chemical = chemicalStack.getType();
		ResourceLocation icon = chemical.getIcon();
		return minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(icon);
	}

	private static void setGLColorFromInt(int color) {
		float red = (color >> 16 & 0xFF) / 255F;
		float green = (color >> 8 & 0xFF) / 255F;
		float blue = (color & 0xFF) / 255F;
		float alpha = 1F;
		RenderSystem.setShaderColor(red, green, blue, alpha);
	}

	private static void drawTextureWithMasking(Matrix4f matrix, float xCoord, float yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, float zLevel) {
		float uMin = textureSprite.getU0();
		float uMax = textureSprite.getU1();
		float vMin = textureSprite.getV0();
		float vMax = textureSprite.getV1();
		uMax = uMax - (maskRight / 16F * (uMax - uMin));
		vMax = vMax - (maskTop / 16F * (vMax - vMin));
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferBuilder.vertex(matrix, xCoord, yCoord + 16, zLevel).uv(uMin, vMax).endVertex();
		bufferBuilder.vertex(matrix, xCoord + 16 - maskRight, yCoord + 16, zLevel).uv(uMax, vMax).endVertex();
		bufferBuilder.vertex(matrix, xCoord + 16 - maskRight, yCoord + maskTop, zLevel).uv(uMax, vMin).endVertex();
		bufferBuilder.vertex(matrix, xCoord, yCoord + maskTop, zLevel).uv(uMin, vMin).endVertex();
		tessellator.end();
	}

	public void render(PoseStack poseStack, int xPosition, int yPosition, ChemicalStack<?> chemicalStack) {
		render(poseStack, xPosition, yPosition, chemicalStack, FluidAttributes.BUCKET_VOLUME);
	}

	public void render(PoseStack poseStack, int xPosition, int yPosition, ChemicalStack<?> chemicalStack, int capacity) {
		RenderSystem.enableBlend();
		drawChemical(poseStack, xPosition, yPosition, chemicalStack, capacity);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.disableBlend();
	}

	private void drawChemical(PoseStack poseStack, int xPosition, int yPosition, ChemicalStack<?> chemicalStack, int capacity) {
		if(capacity <= 0 || chemicalStack == null) {
			return;
		}
		Chemical<?> chemical = chemicalStack.getType();
		if(chemical == null) {
			return;
		}
		TextureAtlasSprite chemicalSprite = getChemicalSprite(chemicalStack);
		int chemicalColor = chemical.getTint();
		int amount = (int)chemicalStack.getAmount();
		int scaledAmount = (amount * height) / capacity;
		if(amount > 0 && scaledAmount < minHeight) {
			scaledAmount = minHeight;
		}
		if(scaledAmount > height) {
			scaledAmount = height;
		}
		drawTiledSprite(poseStack, xPosition, yPosition, width, height, chemicalColor, scaledAmount, chemicalSprite);
	}

	private void drawTiledSprite(PoseStack poseStack, int xPosition, int yPosition, int tiledWidth, int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite) {
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
		Matrix4f matrix = poseStack.last().pose();
		setGLColorFromInt(color);
		int xTileCount = tiledWidth / TEX_WIDTH;
		int xRemainder = tiledWidth - (xTileCount * TEX_WIDTH);
		int yTileCount = scaledAmount / TEX_HEIGHT;
		int yRemainder = scaledAmount - (yTileCount * TEX_HEIGHT);
		int yStart = yPosition + tiledHeight;
		for(int xTile = 0; xTile <= xTileCount; xTile++) {
			for(int yTile = 0; yTile <= yTileCount; yTile++) {
				int width = (xTile == xTileCount) ? xRemainder : TEX_WIDTH;
				int height = (yTile == yTileCount) ? yRemainder : TEX_HEIGHT;
				int x = xPosition + (xTile * TEX_WIDTH);
				int y = yStart - ((yTile + 1) * TEX_HEIGHT);
				if(width > 0 && height > 0) {
					int maskTop = TEX_HEIGHT - height;
					int maskRight = TEX_WIDTH - width;
					drawTextureWithMasking(matrix, x, y, sprite, maskTop, maskRight, 100);
				}
			}
		}
	}
}
