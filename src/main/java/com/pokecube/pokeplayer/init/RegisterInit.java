package com.pokecube.pokeplayer.init;

import com.pokecube.pokeplayer.PokePlayer;
import com.pokecube.pokeplayer.client.container.MachineSlotMenu;
import com.pokecube.pokeplayer.itens.MachineRemote;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import pokecube.core.PokecubeItems;

public class RegisterInit
{
    public static final RegistryObject<Item> POKEPLAYER_MACHINE;
    public static final RegistryObject<MenuType<MachineSlotMenu>> MACHINE_SLOT;

    static{
        POKEPLAYER_MACHINE = PokePlayer.ITEMS.register("pokeplayer_machine",
                ()-> new MachineRemote("pokeplayer_machine", PokecubeItems.TAB_ITEMS, 1));

        MACHINE_SLOT = PokePlayer.MENU.register("machine_menu",
                () -> IForgeMenuType.create(MachineSlotMenu::new));
    }

    public static void init() {}
}
