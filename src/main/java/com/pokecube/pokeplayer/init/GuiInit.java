package com.pokecube.pokeplayer.init;

import com.pokecube.pokeplayer.Pokeplayer;
import com.pokecube.pokeplayer.world.inventory.MachineSlotMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;

public class GuiInit {

    public static final DeferredHolder<MenuType<?>, MenuType<MachineSlotMenu>> MACHINE_MENU;

    static {
        MACHINE_MENU = Pokeplayer.MENUS.register("pokeplayer_menu", () ->
                IMenuTypeExtension.create(MachineSlotMenu::new));
    }

    public static void init(){}
}
