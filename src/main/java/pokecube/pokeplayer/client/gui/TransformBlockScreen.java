package pokecube.pokeplayer.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import pokecube.pokeplayer.Reference;
import pokecube.pokeplayer.block.PokeTransformContainer;

public class TransformBlockScreen extends AbstractContainerScreen<PokeTransformContainer> {

	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Reference.ID,
			"textures/gui/pokeplayer_gui.png");

	public TransformBlockScreen(PokeTransformContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
		this.leftPos = 0;
		this.topPos = 0;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	@Override
	public void render(PoseStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
	}

	
	@Override
	protected void renderLabels(PoseStack mat, int mouseX, int mouseY) 
	{
		this.font.draw(mat, this.getTitle().getString(), 8.0f, 8.0f, 4210752);
		this.font.draw(mat, this.playerInventoryTitle, 8.0F, this.imageHeight - 96 + 2, 4210752);
	}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.enableBlend();
		RenderSystem._setShaderTexture(0, BACKGROUND_TEXTURE);
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
		RenderSystem.disableBlend();
	}
}