package com.pokecube.pokeplayer.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.pokecube.pokeplayer.Reference;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;


@Mod.EventBusSubscriber(modid = Reference.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindingsHandler
{
    public static final String CATEGORY = "key.pokecube.category";

    public static final KeyMapping MOVE_NEXT = new KeyMapping("key.pokecube.move_next", GLFW.GLFW_KEY_UP, CATEGORY);
    public static final KeyMapping MOVE_PREV = new KeyMapping("key.pokecube.move_prev", GLFW.GLFW_KEY_DOWN, CATEGORY);
    public static final KeyMapping MOVE_USE = new KeyMapping("key.pokecube.move_use", GLFW.GLFW_KEY_ENTER, CATEGORY);

    public static void init(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(MOVE_NEXT);
        ClientRegistry.registerKeyBinding(MOVE_PREV);
        ClientRegistry.registerKeyBinding(MOVE_USE);
    }

    public static void onKeyInput(InputEvent.KeyInputEvent event){
        if(KeyBindingsHandler.MOVE_NEXT.isDown()){
            KeyInputHandler.handleKeyPress(0);
        }
        if(KeyBindingsHandler.MOVE_PREV.isDown()){
            KeyInputHandler.handleKeyPress(1);
        }
        if(KeyBindingsHandler.MOVE_USE.isDown()){
            KeyInputHandler.handleKeyPress(2);
        }
    }
}
