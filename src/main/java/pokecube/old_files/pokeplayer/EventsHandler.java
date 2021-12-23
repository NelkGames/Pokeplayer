//package pokecube.pokeplayer;
//
//import java.util.HashSet;
//import java.util.UUID;
//
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntitySize;
//import net.minecraft.entity.Pose;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.event.AttachCapabilitiesEvent;
//import net.minecraftforge.event.TickEvent.Phase;
//import net.minecraftforge.event.TickEvent.PlayerTickEvent;
//import net.minecraftforge.event.entity.EntityEvent;
//import net.minecraftforge.event.entity.EntityJoinWorldEvent;
//import net.minecraftforge.event.entity.living.LivingAttackEvent;
//import net.minecraftforge.event.entity.player.AttackEntityEvent;
//import net.minecraftforge.event.entity.player.PlayerEvent;
//import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import pokecube.core.events.pokemob.EvolveEvent;
//import pokecube.core.events.pokemob.RecallEvent;
//import pokecube.core.events.pokemob.combat.AttackEvent;
//import pokecube.core.interfaces.IPokemob;
//import pokecube.core.interfaces.capabilities.CapabilityPokemob;
//import pokecube.core.interfaces.pokemob.IHasCommands.Command;
//import pokecube.core.interfaces.pokemob.commandhandlers.AttackEntityHandler;
//import pokecube.core.items.pokecubes.PokecubeManager;
//import pokecube.core.network.pokemobs.PacketCommand;
//import pokecube.pokeplayer.network.DataSyncWrapper;
//import pokecube.pokeplayer.network.PacketTransform;
//import thut.core.common.handlers.PlayerDataHandler;
//
//public class EventsHandler
//{
//    public static final ResourceLocation DATACAP = new ResourceLocation(Reference.ID, "data");
//
//    public EventsHandler()
//    {
//    }
//
//    @SubscribeEvent
//    public void pokemobAttack(final AttackEvent evt)
//    {
//        if (evt.moveInfo.attacked instanceof PlayerEntity)
//        {
//            final PlayerEntity player = (PlayerEntity) evt.moveInfo.attacked;
//            final IPokemob pokemob = PokeInfo.getPokemob(player);
//            if (pokemob != null) evt.moveInfo.attacked = pokemob.getEntity();
//        }
//    }
//
//    @SubscribeEvent
//    public void attack(final AttackEntityEvent event)
//    {
//        final PlayerEntity player = event.getPlayer();
//        final IPokemob pokemob = PokeInfo.getPokemob(player);
//        if (pokemob == null) return;
//        if (player.getEntity().level.isClientSide()) PacketCommand.sendCommand(pokemob, Command.ATTACKENTITY,
//                new AttackEntityHandler(event.getTarget().getEntity().getId()).setFromOwner(true));
//        event.setCanceled(true);
//    }
//
//    /**
//     * Sync attacks to the players over to the pokemobs, and also notifiy the
//     * pokeinfo that the pokemob was attacked.
//     *
//     * @param event
//     */
//    @SubscribeEvent
//    public void attack(final LivingAttackEvent event)
//    {
//        if (event.getEntity().level.isClientSide()) return;
//        PlayerEntity player = null;
//        if (event.getEntity() instanceof PlayerEntity)
//        {
//            player = (PlayerEntity) event.getEntity();
//            final IPokemob pokemob = PokeInfo.getPokemob(player);
//            if (pokemob != null) pokemob.getEntity().hurt(event.getSource(), event.getAmount());
//        }
//        else if (event.getEntity().getEntity().getPersistentData().getBoolean("is_a_player"))
//        {
//            final IPokemob evo = CapabilityPokemob.getPokemobFor(event.getEntity());
//            if (evo != null)
//            {
//                final UUID uuid = UUID.fromString(event.getEntity().getPersistentData().getString("playerID"));
//                player = event.getEntity().level.getPlayerByUUID(uuid);
//            }
//        }
//        if (player != null)
//        {
//            final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
//            info.lastDamage = event.getSource();
//        }
//    }
//
//    @SubscribeEvent
//    public void doRespawn(final PlayerEvent.PlayerRespawnEvent event)
//    {
//        if (event.getPlayer() != null && !event.getPlayer().getEntity().level.isClientSide())
//        {
//            IPokemob pokemob = PokeInfo.getPokemob(event.getPlayer());
//            if (pokemob != null)
//            {
//                final ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
//                final ItemStack stack = PokecubeManager.pokemobToItem(pokemob);
//                PokecubeManager.heal(stack, event.getEntityLiving().level);
//                pokemob = PokecubeManager.itemToPokemob(stack, event.getPlayer().getEntity().level);
//                pokemob.getEntity().isAlive();
//                pokemob.getEntity().deathTime = -1;
//                PokeInfo.setPokemob(event.getPlayer(), pokemob);
//                PacketTransform.sendPacket(event.getPlayer(), player);
//                if (!player.getEntity().level.isClientSide())
//                {
//                    EventsHandler.sendUpdate(player);
//                    player.refreshContainer(player.containerMenu, player.containerMenu.getItems());
//                    // Fixes the inventories appearing to vanish
//                    player.getEntity().getPersistentData().putLong("_pokeplayer_evolved_", player.getEntity()
//                            .level.getGameTime() + 50);
//                }
//            }
//        }
//    }
//
//    @SubscribeEvent
//    public void recall(final RecallEvent.Pre evt)
//    {
//        if (evt.recalled.getEntity().getEntity().addTag("is_a_player")) evt.setCanceled(true);
//    }
//
//    @SubscribeEvent
//    public void evolve(final EvolveEvent.Post evt)
//    {
//        final Entity entity = evt.mob.getEntity();
//        if (entity.getEntity().getPersistentData().getBoolean("is_a_player"))
//        {
//            final UUID uuid = UUID.fromString(entity.getEntity().getStringUUID().concat("playerID"));
//            final PlayerEntity player = (PlayerEntity) entity.getEntity().level.getPlayerByUUID(uuid);
//            final IPokemob evo = evt.mob;
//            PokeInfo.setPokemob(player, evo);
//            evt.setCanceled(true);
//            if (!player.getEntity().level.isClientSide())
//            {
//                final ServerPlayerEntity playerMP = (ServerPlayerEntity) player;
//                PacketTransform.sendPacket(player, playerMP);
//                if (!player.getEntity().level.isClientSide())
//                {
//                    EventsHandler.sendUpdate(player);
//                    ((ServerPlayerEntity) player).refreshContainer(player.containerMenu,
//                            player.containerMenu.getItems());
//                    // Fixes the inventories appearing to vanish
//                    player.getEntity().getPersistentData().putLong("_pokeplayer_evolved_", player.getEntity()
//                            .level.getDayTime() + 50);
//                }
//            }
//            return;
//        }
//    }
//
//    static HashSet<UUID> syncSchedule = new HashSet<>();
//
//    @SubscribeEvent
//    public void PlayerLoggedInEvent(final PlayerEvent.PlayerLoggedInEvent event)
//    {
//        if (Dist.DEDICATED_SERVER != null) EventsHandler.syncSchedule.add(event.getPlayer().getUUID());
//    }
//
//    @SubscribeEvent
//    public void PlayerLoggedOutEvent(final PlayerEvent.PlayerLoggedOutEvent event)
//    {
//        EventsHandler.syncSchedule.remove(event.getPlayer().getUUID());
//    }
//
//    @SubscribeEvent
//    public void startTracking(final StartTracking event)
//    {
//        if (event.getTarget() instanceof PlayerEntity && event.getPlayer().isAddedToWorld()) PacketTransform.sendPacket(
//                (PlayerEntity) event.getTarget(), (ServerPlayerEntity) event.getPlayer());
//    }
//
//    @SubscribeEvent
//    public void postPlayerTick(final PlayerTickEvent event)
//    {
//        final PlayerEntity player = event.player;
//        if (player == null) return;
//        final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
//        if (event.phase == Phase.END) info.postPlayerTick(player);
//        else PokeInfo.updateInfo(player, player.getEntity().level);
//    }
//
//    @SubscribeEvent
//    public void onPlayerSizeChange(final EntityEvent.Size event)
//    {
//        if (!(event.getEntity() instanceof PlayerEntity)) return;
//        final PlayerEntity player = (PlayerEntity) event.getEntity();
//        final IPokemob pokemob = PokeInfo.getPokemob(player);
//        if (pokemob != null)
//        {
//            final float height = pokemob.getEntity().getBbHeight();//(Pose.STANDING);
//            final float width = pokemob.getEntity().getBbWidth();//(Pose.STANDING);
//            final float eye = pokemob.getEntity().getEyeHeight(Pose.STANDING);
//            event.setNewEyeHeight(eye);
//            event.setNewSize(EntitySize.fixed(width, height));
//        }
//    }
//
//    @SubscribeEvent
//    public void onEntityCapabilityAttach(final AttachCapabilitiesEvent<Entity> event)
//    {
//        if (event.getObject() instanceof PlayerEntity) event.addCapability(EventsHandler.DATACAP,
//                new DataSyncWrapper());
//    }
//
//    @SubscribeEvent
//    public void entityJoinWorld(final EntityJoinWorldEvent evt)
//    {
//        if (evt.getWorld().isClientSide()) return;
//        if (evt.getEntity().getEntity().getPersistentData().getBoolean("is_a_player"))
//        {
//            final IPokemob evo = CapabilityPokemob.getPokemobFor(evt.getEntity());
//            if (evo != null)
//            {
//                final UUID uuid = UUID.fromString(evt.getEntity().getPersistentData().getString("playerID"));
//                final PlayerEntity player = evt.getWorld().getPlayerByUUID(uuid);
//                PokeInfo.setPokemob(player, evo);
//                evt.setCanceled(true);
//                if (!player.getEntity().level.isClientSide()) PacketTransform.sendPacket(player, (ServerPlayerEntity) player);
//                return;
//            }
//        }
//        else if (evt.getEntity() instanceof ServerPlayerEntity) EventsHandler.sendUpdate((PlayerEntity) evt
//                .getEntity());
//    }
//
//    public static void sendUpdate(final PlayerEntity player)
//    {
//        PacketTransform.sendPacket(player, (ServerPlayerEntity) player);
//    }
//}
