package com.pokecube.pokeplayer.client;

import com.pokecube.pokeplayer.Pokeplayer;
import com.pokecube.pokeplayer.data.PokeInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import pokecube.api.entity.pokemob.IPokemob;
import pokecube.core.client.GuiEvent;
import pokecube.core.client.gui.GuiDisplayPokecubeInfo;
import pokecube.core.client.gui.components.OutMobInfo;
import pokecube.core.items.pokecubes.PokecubeManager;
import thut.api.ThutCaps;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Pokeplayer.MODID, value = Dist.CLIENT)
public class PokeplayerClient {

    public static int currentLevel = 0;
    public static String[] currentMoves = new String[0];

    @SubscribeEvent
    public static void init(FMLLoadCompleteEvent event) {
        new PokeplayerGuiOverride();
    }

    public static class PokeplayerComponent extends OutMobInfo
    {
        @Override
        protected IPokemob getMob()
        {
            PokeplayerGuiOverride info = PokeplayerGuiOverride.instance();
            return info.getCurrentPokemob();
        }

        @Override
        public void _drawGui(GuiEvent evt) {
            this.pos.y0 += this.bounds.h;
            this.pos.y1 += this.bounds.h;
            super._drawGui(evt);
        }
    }

    public static class PokeplayerGuiOverride extends GuiDisplayPokecubeInfo
    {
        public static PokeplayerGuiOverride instance;

        public static PokeplayerGuiOverride instance() {
            if (instance == null) {
                instance = new PokeplayerGuiOverride();
            }
            return instance;
        }

        public PokeplayerGuiOverride() {super();}

        @Override
        public IPokemob[] getPokemobsToDisplay(){
            ItemStack stack = PokeInfo.POKE_INFO.getLastPokecube();
            var copyID = PokecubeManager.itemToPokemob(stack, Minecraft.getInstance().player.level);
            if(copyID != null) return new IPokemob[]{copyID};
            return super.getPokemobsToDisplay();
        }

        @Override
        public IPokemob getCurrentPokemob()
        {
            ItemStack stack = PokeInfo.POKE_INFO.getLastPokecube();
            var copyID = PokecubeManager.itemToPokemob(stack, Minecraft.getInstance().player.level);
            var copy = ThutCaps.getCopyMob(Minecraft.getInstance().player);
            if (copy == null) return super.getCurrentPokemob();
            //var mob = PokemobCaps.getPokemobFor(copy.getCopiedMob());
            return copyID == null ? super.getCurrentPokemob() : copyID;
        }
    }
}
