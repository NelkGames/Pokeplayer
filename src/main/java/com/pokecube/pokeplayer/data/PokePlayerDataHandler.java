package com.pokecube.pokeplayer.data;

import com.pokecube.pokeplayer.client.container.MachineSlotMenu;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import pokecube.api.data.PokedexEntry;
import pokecube.api.entity.pokemob.IPokemob;
import pokecube.core.database.Database;
import thut.api.ThutCaps;
import thut.api.entity.ICopyMob;

import java.util.List;

public class PokePlayerDataHandler
{
    private static final PokePlayerDataHandler INSTANCE = new PokePlayerDataHandler();

    public static PokePlayerDataHandler getInstance() { return INSTANCE; }

//    public IPokemob getPokemobForPlayer(Player player){
//        ICopyMob copy = ThutCaps.getCopyMob(player);
//        if(copy != null){
//            return PokemobCaps.getPokemobFor(copy.getCopiedMob());
//        }
//        return null;
//    }

    public void transformToPokemob(Player player, IPokemob pokemob) {
        ICopyMob copy = ThutCaps.getCopyMob(player);
        PokedexEntry poke = Database.getEntry(pokemob);
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(poke.getEntityType());
        if(copy != null){
            if(id != null)  copy.setCopiedID(id);

            if (player instanceof ServerPlayer serverplayer) {
                List<SynchedEntityData.DataValue<?>> dataValues = player.getEntityData().getNonDefaultValues();
                if (dataValues != null) {
                    ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(player.getId(), dataValues);
                    serverplayer.connection.send(packet);
                }
            //if (player instanceof ServerPlayer serverplayer) {
            //    serverplayer.connection.send(new ClientboundSetEntityDataPacket(player.getId(), player.getEntityData(), true));
            }

            String[] moves = pokemob.getMoves();
            if(moves != null){
                for (int i = 0; i < moves.length; i++){
                    if(moves[i] != null) {
                        MachineSlotMenu.guistate.put("move_" + i, moves[i]);
                    }
                }
            }
        }
    }

    public void revertToPlayer(Player player) {
        ICopyMob copyMob = ThutCaps.getCopyMob(player);
        player.getPersistentData().remove("PokePlayerForm");
        if(copyMob != null){
            copyMob.setCopiedID(null);
            if (player instanceof ServerPlayer serverplayer) {
                List<SynchedEntityData.DataValue<?>> dataValues = player.getEntityData().getNonDefaultValues();
                if (dataValues != null) {
                    ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(player.getId(), dataValues);
                    serverplayer.connection.send(packet);
                }
            }
            if(player.isCreative()) {
                player.getAbilities().mayfly = true;
                player.getAbilities().instabuild = true;
            } else{
                player.getAbilities().mayfly = false;
                player.getAbilities().instabuild = false;
            }
            player.onUpdateAbilities();

            player.setPose(Pose.STANDING);
            player.refreshDimensions();

            MachineSlotMenu.guistate.clear();
        }
        player.refreshDimensions();
        player.setPos(player.getX(), player.getY(), player.getZ());
        player.level.getProfiler().push("reposition");
        player.level.getProfiler().pop();
    }
}
