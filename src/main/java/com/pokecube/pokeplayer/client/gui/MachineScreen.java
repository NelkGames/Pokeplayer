package com.pokecube.pokeplayer.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pokecube.pokeplayer.world.inventory.MachineSlotMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public class MachineScreen extends AbstractContainerScreen<MachineSlotMenu> {
    private final static HashMap<String, Object> guistate = MachineSlotMenu.guistate;
    private final Level world;
    private int x, y, z;
    private final Player entity;

    public MachineScreen(MachineSlotMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.entity = container.entity;
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    private static final ResourceLocation TEXTURE = ResourceLocation.parse("pokeplayer:textures/screens/pokeplayer_gui.png");

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        if (mouseX > leftPos + 75 && mouseX < leftPos + 99 && mouseY > topPos + 29 && mouseY < topPos + 53) {
            guiGraphics.renderTooltip(font, Component.translatable("gui.pokeplayer.machine_gui.tooltip_slot"), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(key, b, c);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, Component.translatable("gui.pokeplayer.machine_gui.title"), 7, 6, 4210752, false);
    }

    @Override
    public void init() {
        super.init();
    }
}
