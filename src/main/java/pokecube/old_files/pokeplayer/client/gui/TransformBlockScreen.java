//package pokecube.pokeplayer.client.gui;
//
//import com.mojang.blaze3d.matrix.MatrixStack;
//import com.mojang.blaze3d.systems.RenderSystem;
//
//import net.minecraft.client.gui.screen.inventory.ContainerScreen;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.text.ITextComponent;
//import pokecube.pokeplayer.Reference;
//import pokecube.pokeplayer.block.PokeTransformContainer;
//
//public class TransformBlockScreen extends ContainerScreen<PokeTransformContainer> {
//
//	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Reference.ID,
//			"textures/gui/pokeplayer_gui.png");
//
//	public TransformBlockScreen(PokeTransformContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
//		super(screenContainer, inv, titleIn);
//		this.leftPos = 0;
//		this.topPos = 0;
//		this.imageWidth = 176;
//		this.imageHeight = 166;
//	}
//
//	@Override
//	public void render(MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
//		this.renderBackground(matrixStack);
//		super.render(matrixStack, mouseX, mouseY, partialTicks);
//		this.renderTooltip(matrixStack, mouseX, mouseY);
//	}
//
//	
//	@Override
//	protected void renderLabels(MatrixStack mat, int mouseX, int mouseY) 
//	{
//		this.font.draw(mat, this.getTitle().getString(), 8.0f, 8.0f, 4210752);
//		this.font.draw(mat, this.inventory.getName().getString(), 8.0F, this.imageHeight - 96 + 2, 4210752);
//	}
//
//	@Override
//	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
//		RenderSystem.enableBlend();
//		this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
//		int x = (this.width - this.imageWidth) / 2;
//		int y = (this.height - this.imageHeight) / 2;
//		this.blit(matrixStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
//		RenderSystem.disableBlend();
//	}
//}