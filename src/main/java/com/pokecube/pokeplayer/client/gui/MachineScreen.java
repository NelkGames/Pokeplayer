package com.pokecube.pokeplayer.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.pokecube.pokeplayer.Reference;
import com.pokecube.pokeplayer.client.container.MachineSlotMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MachineScreen extends AbstractContainerScreen<MachineSlotMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.ID,"textures/gui/pokeplayer_gui.png");

    public MachineScreen(MachineSlotMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) /2;
        int y = (this.height - this.imageHeight) /2;
        this.blit(poseStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick){
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY){
        this.font.draw(stack, this.title.getString(), 8, 6, 4210752);
        this.font.draw(stack, this.playerInventoryTitle.getString(),8, this.imageHeight - 96 +2, 4210752);
    }

    @Override
    public void containerTick(){
        super.containerTick();
    }
}
