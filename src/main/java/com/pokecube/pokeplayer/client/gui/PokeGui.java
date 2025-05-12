package com.pokecube.pokeplayer.client.gui;

import com.pokecube.pokeplayer.Reference;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import pokecube.api.entity.pokemob.IPokemob;
import pokecube.api.entity.pokemob.PokemobCaps;
import pokecube.core.client.GuiEvent;
import pokecube.core.client.gui.GuiDisplayPokecubeInfo;
import pokecube.core.client.gui.components.OutMobInfo;
import thut.api.ThutCaps;

@Mod.EventBusSubscriber(bus = Bus.MOD, modid = Reference.ID, value= Dist.CLIENT)
public class PokeGui
{
    public static class PokeplayerComponent extends OutMobInfo
    {
        @Override
        protected IPokemob getMob(){
            var copy = Minecraft.getInstance().player.getCapability(ThutCaps.COPYMOB).orElse(null);
            if(copy == null) return null;
            return PokemobCaps.getPokemobFor(copy.getCopiedMob());
        }

        @Override
        public void _drawGui (GuiEvent evt){
            this.pos.y0 += this.bounds.h;
            this.pos.y1 += this.bounds.h;
            super._drawGui(evt);
        }
    }

    public static class PokeplayerGuiOverride extends GuiDisplayPokecubeInfo implements com.pokecube.pokeplayer.client.gui.PokeplayerGuiOverride {
        public PokeplayerGuiOverride() {
            super();
        }

        @Override
        public IPokemob getCurrentPokemon()
        {
            var copy = Minecraft.getInstance().player.getCapability(ThutCaps.COPYMOB).orElse(null);
            if(copy == null) return super.getCurrentPokemob();
            var mob = PokemobCaps.getPokemobFor(copy.getCopiedMob());
            return mob == null ? super.getCurrentPokemob() : mob;
        }
    }

    @SubscribeEvent
    public static void init(FMLLoadCompleteEvent event){
        new PokeplayerGuiOverride();
    }
}
