package com.pokecube.pokeplayer.data;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import pokecube.api.PokecubeAPI;
import pokecube.api.data.PokedexEntry;
import pokecube.api.entity.pokemob.IPokemob;
import pokecube.api.entity.pokemob.PokemobCaps;
import pokecube.core.database.Database;
import pokecube.core.items.pokecubes.PokecubeManager;
import thut.api.ThutCaps;
import thut.api.entity.ICopyMob;
import thut.lib.RegHelper;
import java.util.Objects;

public class PokeInfo {

    public static final PokeInfo POKE_INFO = new PokeInfo();

    public static ItemStack stack = ItemStack.EMPTY;

    public static PokeInfo getInstance() { return POKE_INFO; }

    public void TransformMob(ItemStack pokecube, Player entity, Level world) {

        stack = pokecube == null ? ItemStack.EMPTY : pokecube;

        ICopyMob copy = ThutCaps.getCopyMob(entity);
        if (PokecubeManager.isFilled(stack) && copy != null) {
            IPokemob pokemob = PokecubeManager.itemToPokemob(stack, world);
            PokedexEntry poke = Database.getEntry(pokemob);
            copy.setCopiedID(RegHelper.getKey(poke.getEntityType()));
            PacketTransform.sendTransform((ServerPlayer) entity, level, moveList);

            //Debug
            PokecubeAPI.LOGGER.info("Pokemob encontrado: " + pokemob.getPokedexEntry().getName());
            PokecubeAPI.LOGGER.info("Lv: " + pokemob.getLevel());
            for (String move : pokemob.getMoves()) {
                if (move != null) PokecubeAPI.LOGGER.info("Movimento: " + move);
            }
        }
    }

    public void RevertMob(Player entity) {
        //this.revertToPlayer(entity);
        ICopyMob copyMob = ThutCaps.getCopyMob(entity);
        stack = ItemStack.EMPTY;
        copyMob.setCopiedID(null);

        if (entity.isCreative()) {
            entity.getAbilities().mayfly = true;
            entity.getAbilities().instabuild = true;
        } else {
            entity.getAbilities().mayfly = false;
            entity.getAbilities().instabuild = false;
        }
        entity.setHealth(entity.getMaxHealth());
        Objects.requireNonNull(entity.getAttributes().getInstance(Attributes.MAX_HEALTH))
                .setBaseValue(20.0D);
        entity.onUpdateAbilities();
        entity.setNoGravity(false);
        entity.setPose(Pose.STANDING);
        entity.refreshDimensions();
    }

    public ItemStack getLastPokecube() {
        return stack;
    }

//    public IPokemob getPokemobForPlayer(Player entity){
//        ICopyMob copy = ThutCaps.getCopyMob(entity);
//        if(copy != null){
//            System.out.println("This Pokemob");
//            return PokemobCaps.getPokemobFor(copy.getCopiedMob());
//        }
//        return null;
//    }

//    public void transformToPokemob(Player player, IPokemob pokemob) {
//        ICopyMob copy = ThutCaps.getCopyMob(player);
//        PokedexEntry poke = Database.getEntry(pokemob);
//        if (copy != null) {
//            copy.setCopiedID(RegHelper.getKey(poke.getEntityType()));
//        }
//    }

//    public void revertToPlayer(Player player) {
//        ICopyMob copyMob = ThutCaps.getCopyMob(player);
//        if (copyMob != null) {
//            copyMob.setCopiedID(null);
//
//            if (player.isCreative()) {
//                player.getAbilities().mayfly = true;
//                player.getAbilities().instabuild = true;
//            } else {
//                player.getAbilities().mayfly = false;
//                player.getAbilities().instabuild = false;
//            }
//            player.onUpdateAbilities();
//            player.setNoGravity(false);
//            player.setPose(Pose.STANDING);
//            player.refreshDimensions();
//        }
//    }
}
