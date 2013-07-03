package org.bukkit.craftbukkit.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.*;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.NotImplementedException;

import org.bukkit.*;
import org.bukkit.Achievement;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.bukkit.craftbukkit.conversations.ConversationTracker;
import org.bukkit.craftbukkit.CraftEffect;
import org.bukkit.craftbukkit.CraftOfflinePlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.map.CraftMapView;
import org.bukkit.craftbukkit.map.RenderData;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboard;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerUnregisterChannelEvent;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.scoreboard.Scoreboard;

@DelegateDeserialization(CraftOfflinePlayer.class)
public class CraftPlayer extends CraftHumanEntity implements Player {
    private long firstPlayed = 0;
    private long lastPlayed = 0;
    private boolean hasPlayedBefore = false;
    private final ConversationTracker conversationTracker = new ConversationTracker();
    private final Set<String> channels = new HashSet<String>();
    private final Map<String, Player> hiddenPlayers = new MapMaker().softValues().makeMap();
    private int hash = 0;
    private boolean scaledHealth;

    public CraftPlayer(CraftServer server, EntityPlayer entity) {
        super(server, entity);

        firstPlayed = System.currentTimeMillis();
    }

    @Override
    public boolean isOp() {
        return server.getHandle().isOp(getName());
    }

    @Override
    public void setOp(boolean value) {
        if (value == isOp()) return;

        if (value) {
            server.getHandle().addOp(getName());
        } else {
            server.getHandle().removeOp(getName());
        }

        perm.recalculatePermissions();
    }

    public boolean isOnline() {
        for (Object obj : server.getHandle().players) {
            EntityPlayer player = (EntityPlayer) obj;
            if (player.getName().equalsIgnoreCase(getName())) {
                return true;
            }
        }
        return false;
    }

    public InetSocketAddress getAddress() {
        if (getHandle().playerConnection == null) return null;

        SocketAddress addr = getHandle().playerConnection.networkManager.getSocketAddress();
        if (addr instanceof InetSocketAddress) {
            return (InetSocketAddress) addr;
        } else {
            return null;
        }
    }

    @Override
    public double getEyeHeight() {
        return getEyeHeight(false);
    }

    @Override
    public double getEyeHeight(boolean ignoreSneaking) {
        if (ignoreSneaking) {
            return 1.62D;
        } else {
            if (isSneaking()) {
                return 1.54D;
            } else {
                return 1.62D;
            }
        }
    }

    public void sendRawMessage(String message) {
        if (getHandle().playerConnection == null) return;

        getHandle().playerConnection.sendPacket(new Packet3Chat(ChatMessage.d(message)));
    }

    public void sendMessage(String message) {
        if (!conversationTracker.isConversingModaly()) {
            this.sendRawMessage(message);
        }
    }

    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    public String getDisplayName() {
        return getHandle().displayName;
    }

    public void setDisplayName(final String name) {
        getHandle().displayName = name;
    }

    public String getPlayerListName() {
        return getHandle().listName;
    }

    public void setPlayerListName(String name) {
        String oldName = getHandle().listName;

        if (name == null) {
            name = getName();
        }

        if (oldName.equals(name)) {
            return;
        }

        if (name.length() > 16) {
            throw new IllegalArgumentException("Player list names can only be a maximum of 16 characters long");
        }

        // Collisions will make for invisible people
        for (int i = 0; i < server.getHandle().players.size(); ++i) {
            if (((EntityPlayer) server.getHandle().players.get(i)).listName.equals(name)) {
                throw new IllegalArgumentException(name + " is already assigned as a player list name for someone");
            }
        }

        getHandle().listName = name;

        // Change the name on the client side
        Packet201PlayerInfo oldpacket = new Packet201PlayerInfo(oldName, false, 9999);
        Packet201PlayerInfo packet = new Packet201PlayerInfo(name, true, getHandle().ping);
        for (int i = 0; i < server.getHandle().players.size(); ++i) {
            EntityPlayer entityplayer = (EntityPlayer) server.getHandle().players.get(i);
            if (entityplayer.playerConnection == null) continue;

            if (entityplayer.getBukkitEntity().canSee(this)) {
                entityplayer.playerConnection.sendPacket(oldpacket);
                entityplayer.playerConnection.sendPacket(packet);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OfflinePlayer)) {
            return false;
        }
        OfflinePlayer other = (OfflinePlayer) obj;
        if ((this.getName() == null) || (other.getName() == null)) {
            return false;
        }

        boolean nameEquals = this.getName().equalsIgnoreCase(other.getName());
        boolean idEquals = true;

        if (other instanceof CraftPlayer) {
            idEquals = this.getEntityId() == ((CraftPlayer) other).getEntityId();
        }

        return nameEquals && idEquals;
    }

    public void kickPlayer(String message) {
        if (getHandle().playerConnection == null) return;

        getHandle().playerConnection.disconnect(message == null ? "" : message);
    }

    public void setCompassTarget(Location loc) {
        if (getHandle().playerConnection == null) return;

        // Do not directly assign here, from the packethandler we'll assign it.
        getHandle().playerConnection.sendPacket(new Packet6SpawnPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    public Location getCompassTarget() {
        return getHandle().compassTarget;
    }

    public void chat(String msg) {
        if (getHandle().playerConnection == null) return;

        getHandle().playerConnection.chat(msg, false);
    }

    public boolean performCommand(String command) {
        return server.dispatchCommand(this, command);
    }

    public void playNote(Location loc, byte instrument, byte note) {
        if (getHandle().playerConnection == null) return;

        int id = getHandle().world.getTypeId(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        getHandle().playerConnection.sendPacket(new Packet54PlayNoteBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), id, instrument, note));
    }

    public void playNote(Location loc, Instrument instrument, Note note) {
        if (getHandle().playerConnection == null) return;

        int id = getHandle().world.getTypeId(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        getHandle().playerConnection.sendPacket(new Packet54PlayNoteBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), id, instrument.getType(), note.getId()));
    }

    public void playSound(Location loc, Sound sound, float volume, float pitch) {
        if (loc == null || sound == null || getHandle().playerConnection == null) return;

        double x = loc.getBlockX() + 0.5;
        double y = loc.getBlockY() + 0.5;
        double z = loc.getBlockZ() + 0.5;

        Packet62NamedSoundEffect packet = new Packet62NamedSoundEffect(CraftSound.getSound(sound), x, y, z, volume, pitch);
        getHandle().playerConnection.sendPacket(packet);
    }

    public void playEffect(Location loc, Effect effect, int data) {
        if (getHandle().playerConnection == null) return;

        int packetData = effect.getId();
        Packet61WorldEvent packet = new Packet61WorldEvent(packetData, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), data, false);
        getHandle().playerConnection.sendPacket(packet);
    }

    public <T> void playEffect(Location loc, Effect effect, T data) {
        if (data != null) {
            Validate.isTrue(data.getClass().equals(effect.getData()), "Wrong kind of data for this effect!");
        } else {
            Validate.isTrue(effect.getData() == null, "Wrong kind of data for this effect!");
        }

        int datavalue = data == null ? 0 : CraftEffect.getDataValue(effect, data);
        playEffect(loc, effect, datavalue);
    }

    public void sendBlockChange(Location loc, Material material, byte data) {
        sendBlockChange(loc, material.getId(), data);
    }

    public void sendBlockChange(Location loc, int material, byte data) {
        if (getHandle().playerConnection == null) return;

        Packet53BlockChange packet = new Packet53BlockChange(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), ((CraftWorld) loc.getWorld()).getHandle());

        packet.material = material;
        packet.data = data;
        getHandle().playerConnection.sendPacket(packet);
    }

    public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data) {
        if (getHandle().playerConnection == null) return false;

        /*
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        int cx = x >> 4;
        int cz = z >> 4;

        if (sx <= 0 || sy <= 0 || sz <= 0) {
            return false;
        }

        if ((x + sx - 1) >> 4 != cx || (z + sz - 1) >> 4 != cz || y < 0 || y + sy > 128) {
            return false;
        }

        if (data.length != (sx * sy * sz * 5) / 2) {
            return false;
        }

        Packet51MapChunk packet = new Packet51MapChunk(x, y, z, sx, sy, sz, data);

        getHandle().playerConnection.sendPacket(packet);

        return true;
        */

        throw new NotImplementedException("Chunk changes do not yet work"); // TODO: Chunk changes.
    }

    public void sendMap(MapView map) {
        if (getHandle().playerConnection == null) return;

        RenderData data = ((CraftMapView) map).render(this);
        for (int x = 0; x < 128; ++x) {
            byte[] bytes = new byte[131];
            bytes[1] = (byte) x;
            for (int y = 0; y < 128; ++y) {
                bytes[y + 3] = data.buffer[y * 128 + x];
            }
            Packet131ItemData packet = new Packet131ItemData((short) Material.MAP.getId(), map.getId(), bytes);
            getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        EntityPlayer entity = getHandle();

        if (getHealth() == 0 || entity.dead) {
            return false;
        }

        if (entity.playerConnection == null || entity.playerConnection.disconnected) {
            return false;
        }

        if (entity.vehicle != null || entity.passenger != null) {
            return false;
        }

        // From = Players current Location
        Location from = this.getLocation();
        // To = Players new Location if Teleport is Successful
        Location to = location;
        // Create & Call the Teleport Event.
        PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
        server.getPluginManager().callEvent(event);

        // Return False to inform the Plugin that the Teleport was unsuccessful/cancelled.
        if (event.isCancelled()) {
            return false;
        }

        // Update the From Location
        from = event.getFrom();
        // Grab the new To Location dependent on whether the event was cancelled.
        to = event.getTo();
        // Grab the To and From World Handles.
        WorldServer fromWorld = ((CraftWorld) from.getWorld()).getHandle();
        WorldServer toWorld = ((CraftWorld) to.getWorld()).getHandle();

        // Close any foreign inventory
        if (getHandle().activeContainer != getHandle().defaultContainer) {
            getHandle().closeInventory();
        }

        // Check if the fromWorld and toWorld are the same.
        if (fromWorld == toWorld) {
            entity.playerConnection.teleport(to);
        } else {
            server.getHandle().moveToWorld(entity, toWorld.dimension, true, to, true);
        }
        return true;
    }

    public void setSneaking(boolean sneak) {
        getHandle().setSneaking(sneak);
    }

    public boolean isSneaking() {
        return getHandle().isSneaking();
    }

    public boolean isSprinting() {
        return getHandle().isSprinting();
    }

    public void setSprinting(boolean sprinting) {
        getHandle().setSprinting(sprinting);
    }

    public void loadData() {
        server.getHandle().playerFileData.load(getHandle());
    }

    public void saveData() {
        server.getHandle().playerFileData.save(getHandle());
    }

    @Deprecated
    public void updateInventory() {
        getHandle().updateInventory(getHandle().activeContainer);
    }

    public void setSleepingIgnored(boolean isSleeping) {
        getHandle().fauxSleeping = isSleeping;
        ((CraftWorld) getWorld()).getHandle().checkSleepStatus();
    }

    public boolean isSleepingIgnored() {
        return getHandle().fauxSleeping;
    }

    public void awardAchievement(Achievement achievement) {
        sendStatistic(achievement.getId(), 1);
    }

    public void incrementStatistic(Statistic statistic) {
        incrementStatistic(statistic, 1);
    }

    public void incrementStatistic(Statistic statistic, int amount) {
        sendStatistic(statistic.getId(), amount);
    }

    public void incrementStatistic(Statistic statistic, Material material) {
        incrementStatistic(statistic, material, 1);
    }

    public void incrementStatistic(Statistic statistic, Material material, int amount) {
        if (!statistic.isSubstatistic()) {
            throw new IllegalArgumentException("Given statistic is not a substatistic");
        }
        if (statistic.isBlock() != material.isBlock()) {
            throw new IllegalArgumentException("Given material is not valid for this substatistic");
        }

        int mat = material.getId();

        if (!material.isBlock()) {
            mat -= 255;
        }

        sendStatistic(statistic.getId() + mat, amount);
    }

    private void sendStatistic(int id, int amount) {
        if (getHandle().playerConnection == null) return;

        while (amount > Byte.MAX_VALUE) {
            sendStatistic(id, Byte.MAX_VALUE);
            amount -= Byte.MAX_VALUE;
        }

        getHandle().playerConnection.sendPacket(new Packet200Statistic(id, amount));
    }

    public void setPlayerTime(long time, boolean relative) {
        getHandle().timeOffset = time;
        getHandle().relativeTime = relative;
    }

    public long getPlayerTimeOffset() {
        return getHandle().timeOffset;
    }

    public long getPlayerTime() {
        return getHandle().getPlayerTime();
    }

    public boolean isPlayerTimeRelative() {
        return getHandle().relativeTime;
    }

    public void resetPlayerTime() {
        setPlayerTime(0, true);
    }

    public void setPlayerWeather(WeatherType type) {
        getHandle().setPlayerWeather(type, true);
    }

    public WeatherType getPlayerWeather() {
        return getHandle().getPlayerWeather();
    }

    public void resetPlayerWeather() {
        getHandle().resetPlayerWeather();
    }

    public boolean isBanned() {
        return server.getHandle().getNameBans().isBanned(getName().toLowerCase());
    }

    public void setBanned(boolean value) {
        if (value) {
            BanEntry entry = new BanEntry(getName().toLowerCase());
            server.getHandle().getNameBans().add(entry);
        } else {
            server.getHandle().getNameBans().remove(getName().toLowerCase());
        }

        server.getHandle().getNameBans().save();
    }

    public boolean isWhitelisted() {
        return server.getHandle().getWhitelisted().contains(getName().toLowerCase());
    }

    public void setWhitelisted(boolean value) {
        if (value) {
            server.getHandle().addWhitelist(getName().toLowerCase());
        } else {
            server.getHandle().removeWhitelist(getName().toLowerCase());
        }
    }

    @Override
    public void setGameMode(GameMode mode) {
        if (getHandle().playerConnection == null) return;

        if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
        }

        if (mode != getGameMode()) {
            PlayerGameModeChangeEvent event = new PlayerGameModeChangeEvent(this, mode);
            server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            getHandle().playerInteractManager.setGameMode(EnumGamemode.a(mode.getValue()));
            getHandle().playerConnection.sendPacket(new Packet70Bed(3, mode.getValue()));
        }
    }

    @Override
    public GameMode getGameMode() {
        return GameMode.getByValue(getHandle().playerInteractManager.getGameMode().a());
    }

    public void giveExp(int exp) {
        getHandle().giveExp(exp);
    }

    public void giveExpLevels(int levels) {
        getHandle().levelDown(levels);
    }

    public float getExp() {
        return getHandle().exp;
    }

    public void setExp(float exp) {
        getHandle().exp = exp;
        getHandle().lastSentExp = -1;
    }

    public int getLevel() {
        return getHandle().expLevel;
    }

    public void setLevel(int level) {
        getHandle().expLevel = level;
        getHandle().lastSentExp = -1;
    }

    public int getTotalExperience() {
        return getHandle().expTotal;
    }

    public void setTotalExperience(int exp) {
        getHandle().expTotal = exp;
    }

    public float getExhaustion() {
        return getHandle().getFoodData().exhaustionLevel;
    }

    public void setExhaustion(float value) {
        getHandle().getFoodData().exhaustionLevel = value;
    }

    public float getSaturation() {
        return getHandle().getFoodData().saturationLevel;
    }

    public void setSaturation(float value) {
        getHandle().getFoodData().saturationLevel = value;
    }

    public int getFoodLevel() {
        return getHandle().getFoodData().foodLevel;
    }

    public void setFoodLevel(int value) {
        getHandle().getFoodData().foodLevel = value;
    }

    public Location getBedSpawnLocation() {
        World world = getServer().getWorld(getHandle().spawnWorld);
        ChunkCoordinates bed = getHandle().getBed();

        if (world != null && bed != null) {
            bed = EntityHuman.getBed(((CraftWorld) world).getHandle(), bed, getHandle().isRespawnForced());
            if (bed != null) {
                return new Location(world, bed.x, bed.y, bed.z);
            }
        }
        return null;
    }

    public void setBedSpawnLocation(Location location) {
        setBedSpawnLocation(location, false);
    }

    public void setBedSpawnLocation(Location location, boolean override) {
        if (location == null) {
            getHandle().setRespawnPosition(null, override);
        } else {
            getHandle().setRespawnPosition(new ChunkCoordinates(location.getBlockX(), location.getBlockY(), location.getBlockZ()), override);
            getHandle().spawnWorld = location.getWorld().getName();
        }
    }

    public void hidePlayer(Player player) {
        Validate.notNull(player, "hidden player cannot be null");
        if (getHandle().playerConnection == null) return;
        if (equals(player)) return;
        if (hiddenPlayers.containsKey(player.getName())) return;
        hiddenPlayers.put(player.getName(), player);

        //remove this player from the hidden player's EntityTrackerEntry
        EntityTracker tracker = ((WorldServer) entity.world).tracker;
        EntityPlayer other = ((CraftPlayer) player).getHandle();
        EntityTrackerEntry entry = (EntityTrackerEntry) tracker.trackedEntities.get(other.id);
        if (entry != null) {
            entry.clear(getHandle());
        }

        //remove the hidden player from this player user list
        getHandle().playerConnection.sendPacket(new Packet201PlayerInfo(player.getPlayerListName(), false, 9999));
    }

    public void showPlayer(Player player) {
        Validate.notNull(player, "shown player cannot be null");
        if (getHandle().playerConnection == null) return;
        if (equals(player)) return;
        if (!hiddenPlayers.containsKey(player.getName())) return;
        hiddenPlayers.remove(player.getName());

        EntityTracker tracker = ((WorldServer) entity.world).tracker;
        EntityPlayer other = ((CraftPlayer) player).getHandle();
        EntityTrackerEntry entry = (EntityTrackerEntry) tracker.trackedEntities.get(other.id);
        if (entry != null && !entry.trackedPlayers.contains(getHandle())) {
            entry.updatePlayer(getHandle());
        }

        getHandle().playerConnection.sendPacket(new Packet201PlayerInfo(player.getPlayerListName(), true, getHandle().ping));
    }

    public boolean canSee(Player player) {
        return !hiddenPlayers.containsKey(player.getName());
    }

    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("name", getName());

        return result;
    }

    public Player getPlayer() {
        return this;
    }

    @Override
    public EntityPlayer getHandle() {
        return (EntityPlayer) entity;
    }

    public void setHandle(final EntityPlayer entity) {
        super.setHandle(entity);
    }

    @Override
    public String toString() {
        return "CraftPlayer{" + "name=" + getName() + '}';
    }

    @Override
    public int hashCode() {
        if (hash == 0 || hash == 485) {
            hash = 97 * 5 + (this.getName() != null ? this.getName().toLowerCase().hashCode() : 0);
        }
        return hash;
    }

    public long getFirstPlayed() {
        return firstPlayed;
    }

    public long getLastPlayed() {
        return lastPlayed;
    }

    public boolean hasPlayedBefore() {
        return hasPlayedBefore;
    }

    public void setFirstPlayed(long firstPlayed) {
        this.firstPlayed = firstPlayed;
    }

    public void readExtraData(NBTTagCompound nbttagcompound) {
        hasPlayedBefore = true;
        if (nbttagcompound.hasKey("bukkit")) {
            NBTTagCompound data = nbttagcompound.getCompound("bukkit");

            if (data.hasKey("firstPlayed")) {
                firstPlayed = data.getLong("firstPlayed");
                lastPlayed = data.getLong("lastPlayed");
            }

            if (data.hasKey("newExp")) {
                EntityPlayer handle = getHandle();
                handle.newExp = data.getInt("newExp");
                handle.newTotalExp = data.getInt("newTotalExp");
                handle.newLevel = data.getInt("newLevel");
                handle.expToDrop = data.getInt("expToDrop");
                handle.keepLevel = data.getBoolean("keepLevel");
            }
        }
    }

    public void setExtraData(NBTTagCompound nbttagcompound) {
        if (!nbttagcompound.hasKey("bukkit")) {
            nbttagcompound.setCompound("bukkit", new NBTTagCompound());
        }

        NBTTagCompound data = nbttagcompound.getCompound("bukkit");
        EntityPlayer handle = getHandle();
        data.setInt("newExp", handle.newExp);
        data.setInt("newTotalExp", handle.newTotalExp);
        data.setInt("newLevel", handle.newLevel);
        data.setInt("expToDrop", handle.expToDrop);
        data.setBoolean("keepLevel", handle.keepLevel);
        data.setLong("firstPlayed", getFirstPlayed());
        data.setLong("lastPlayed", System.currentTimeMillis());
    }

    public boolean beginConversation(Conversation conversation) {
        return conversationTracker.beginConversation(conversation);
    }

    public void abandonConversation(Conversation conversation) {
        conversationTracker.abandonConversation(conversation, new ConversationAbandonedEvent(conversation, new ManuallyAbandonedConversationCanceller()));
    }

    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
        conversationTracker.abandonConversation(conversation, details);
    }

    public void acceptConversationInput(String input) {
        conversationTracker.acceptConversationInput(input);
    }

    public boolean isConversing() {
        return conversationTracker.isConversing();
    }

    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        StandardMessenger.validatePluginMessage(server.getMessenger(), source, channel, message);
        if (getHandle().playerConnection == null) return;

        if (channels.contains(channel)) {
            Packet250CustomPayload packet = new Packet250CustomPayload();
            packet.tag = channel;
            packet.length = message.length;
            packet.data = message;
            getHandle().playerConnection.sendPacket(packet);
        }
    }

    public void setTexturePack(String url) {
        Validate.notNull(url, "Texture pack URL cannot be null");

        byte[] message = (url + "\0" + "16").getBytes();
        Validate.isTrue(message.length <= Messenger.MAX_MESSAGE_SIZE, "Texture pack URL is too long");

        getHandle().playerConnection.sendPacket(new Packet250CustomPayload("MC|TPack", message));
    }

    public void addChannel(String channel) {
        if (channels.add(channel)) {
            server.getPluginManager().callEvent(new PlayerRegisterChannelEvent(this, channel));
        }
    }

    public void removeChannel(String channel) {
        if (channels.remove(channel)) {
            server.getPluginManager().callEvent(new PlayerUnregisterChannelEvent(this, channel));
        }
    }

    public Set<String> getListeningPluginChannels() {
        return ImmutableSet.copyOf(channels);
    }

    public void sendSupportedChannels() {
        if (getHandle().playerConnection == null) return;
        Set<String> listening = server.getMessenger().getIncomingChannels();

        if (!listening.isEmpty()) {
            Packet250CustomPayload packet = new Packet250CustomPayload();

            packet.tag = "REGISTER";
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            for (String channel : listening) {
                try {
                    stream.write(channel.getBytes("UTF8"));
                    stream.write((byte) 0);
                } catch (IOException ex) {
                    Logger.getLogger(CraftPlayer.class.getName()).log(Level.SEVERE, "Could not send Plugin Channel REGISTER to " + getName(), ex);
                }
            }

            packet.data = stream.toByteArray();
            packet.length = packet.data.length;

            getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        server.getPlayerMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return server.getPlayerMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return server.getPlayerMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        server.getPlayerMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    @Override
    public boolean setWindowProperty(Property prop, int value) {
        Container container = getHandle().activeContainer;
        if (container.getBukkitView().getType() != prop.getType()) {
            return false;
        }
        getHandle().setContainerData(container, prop.getId(), value);
        return true;
    }

    public void disconnect(String reason) {
        conversationTracker.abandonAllConversations();
        perm.clearPermissions();
    }

    public boolean isFlying() {
        return getHandle().abilities.isFlying;
    }

    public void setFlying(boolean value) {
        if (!getAllowFlight() && value) {
            throw new IllegalArgumentException("Cannot make player fly if getAllowFlight() is false");
        }

        getHandle().abilities.isFlying = value;
        getHandle().updateAbilities();
    }

    public boolean getAllowFlight() {
        return getHandle().abilities.canFly;
    }

    public void setAllowFlight(boolean value) {
        if (isFlying() && !value) {
            getHandle().abilities.isFlying = false;
        }

        getHandle().abilities.canFly = value;
        getHandle().updateAbilities();
    }

    @Override
    public int getNoDamageTicks() {
        if (getHandle().invulnerableTicks > 0) {
            return Math.max(getHandle().invulnerableTicks, getHandle().noDamageTicks);
        } else {
            return getHandle().noDamageTicks;
        }
    }

    public void setFlySpeed(float value) {
        validateSpeed(value);
        EntityPlayer player = getHandle();
        player.abilities.flySpeed = value / 2f;
        player.updateAbilities();

    }

    public void setWalkSpeed(float value) {
        validateSpeed(value);
        EntityPlayer player = getHandle();
        player.abilities.walkSpeed = value / 2f;
        player.updateAbilities();
    }

    public float getFlySpeed() {
        return getHandle().abilities.flySpeed * 2f;
    }

    public float getWalkSpeed() {
        return getHandle().abilities.walkSpeed * 2f;
    }

    private void validateSpeed(float value) {
        if (value < 0) {
            if (value < -1f) {
                throw new IllegalArgumentException(value + " is too low");
            }
        } else {
            if (value > 1f) {
                throw new IllegalArgumentException(value + " is too high");
            }
        }
    }

    @Override
    public void setMaxHealth(double amount) {
        super.setMaxHealth(amount);
        getHandle().triggerHealthUpdate();
    }

    @Override
    public void resetMaxHealth() {
        super.resetMaxHealth();
        getHandle().triggerHealthUpdate();
    }

    public CraftScoreboard getScoreboard() {
        return this.server.getScoreboardManager().getPlayerBoard(this);
    }

    public void setScoreboard(Scoreboard scoreboard) {
        Validate.notNull(scoreboard, "Scoreboard cannot be null");
        PlayerConnection playerConnection = getHandle().playerConnection;
        if (playerConnection == null) {
            throw new IllegalStateException("Cannot set scoreboard yet");
        }
        if (playerConnection.disconnected) {
            throw new IllegalStateException("Cannot set scoreboard for invalid CraftPlayer");
        }

        this.server.getScoreboardManager().setPlayerBoard(this, scoreboard);
    }

    public float getScaledHealth() {
        return (float) (this.scaledHealth ? getHealth() / getMaxHealth() * 20.0D : getHealth());
    }

    public void setScaleHealth(boolean scale) {
        this.scaledHealth = scale;
    }

    public boolean isScaledHealth() {
        return this.scaledHealth;
    }
}
