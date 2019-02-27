package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import io.netty.channel.ChannelFuture;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginListener implements PacketLoginInListener, ITickable {

    private static final AtomicInteger b = new AtomicInteger(0);
    private static final Logger c = LogManager.getLogger();
    private static final Random random = new Random();
    private final byte[] e = new byte[4];
    private final MinecraftServer server;
    public final NetworkManager networkManager;
    private LoginListener.EnumProtocolState g;
    private int h;
    private GameProfile i;
    private final String j;
    private SecretKey loginKey;
    private EntityPlayer l;

    public LoginListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.g = LoginListener.EnumProtocolState.HELLO;
        this.j = "";
        this.server = minecraftserver;
        this.networkManager = networkmanager;
        LoginListener.random.nextBytes(this.e);
    }

    public void tick() {
        if (this.g == LoginListener.EnumProtocolState.READY_TO_ACCEPT) {
            this.b();
        } else if (this.g == LoginListener.EnumProtocolState.DELAY_ACCEPT) {
            EntityPlayer entityplayer = this.server.getPlayerList().a(this.i.getId());

            if (entityplayer == null) {
                this.g = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
                this.server.getPlayerList().a(this.networkManager, this.l);
                this.l = null;
            }
        }

        if (this.h++ == 600) {
            this.disconnect(new ChatMessage("multiplayer.disconnect.slow_login", new Object[0]));
        }

    }

    public void disconnect(IChatBaseComponent ichatbasecomponent) {
        try {
            LoginListener.c.info("Disconnecting {}: {}", this.c(), ichatbasecomponent.getString());
            this.networkManager.sendPacket(new PacketLoginOutDisconnect(ichatbasecomponent));
            this.networkManager.close(ichatbasecomponent);
        } catch (Exception exception) {
            LoginListener.c.error("Error whilst disconnecting player", exception);
        }

    }

    public void b() {
        if (!this.i.isComplete()) {
            this.i = this.a(this.i);
        }

        IChatBaseComponent ichatbasecomponent = this.server.getPlayerList().attemptLogin(this.networkManager.getSocketAddress(), this.i);

        if (ichatbasecomponent != null) {
            this.disconnect(ichatbasecomponent);
        } else {
            this.g = LoginListener.EnumProtocolState.ACCEPTED;
            if (this.server.aw() >= 0 && !this.networkManager.isLocal()) {
                this.networkManager.sendPacket(new PacketLoginOutSetCompression(this.server.aw()), (channelfuture) -> {
                    this.networkManager.setCompressionLevel(this.server.aw());
                });
            }

            this.networkManager.sendPacket(new PacketLoginOutSuccess(this.i));
            EntityPlayer entityplayer = this.server.getPlayerList().a(this.i.getId());

            if (entityplayer != null) {
                this.g = LoginListener.EnumProtocolState.DELAY_ACCEPT;
                this.l = this.server.getPlayerList().processLogin(this.i);
            } else {
                this.server.getPlayerList().a(this.networkManager, this.server.getPlayerList().processLogin(this.i));
            }
        }

    }

    public void a(IChatBaseComponent ichatbasecomponent) {
        LoginListener.c.info("{} lost connection: {}", this.c(), ichatbasecomponent.getString());
    }

    public String c() {
        return this.i != null ? this.i + " (" + this.networkManager.getSocketAddress() + ")" : String.valueOf(this.networkManager.getSocketAddress());
    }

    public void a(PacketLoginInStart packetlogininstart) {
        Validate.validState(this.g == LoginListener.EnumProtocolState.HELLO, "Unexpected hello packet", new Object[0]);
        this.i = packetlogininstart.b();
        if (this.server.getOnlineMode() && !this.networkManager.isLocal()) {
            this.g = LoginListener.EnumProtocolState.KEY;
            this.networkManager.sendPacket(new PacketLoginOutEncryptionBegin("", this.server.E().getPublic(), this.e));
        } else {
            this.g = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
        }

    }

    public void a(PacketLoginInEncryptionBegin packetlogininencryptionbegin) {
        Validate.validState(this.g == LoginListener.EnumProtocolState.KEY, "Unexpected key packet", new Object[0]);
        PrivateKey privatekey = this.server.E().getPrivate();

        if (!Arrays.equals(this.e, packetlogininencryptionbegin.b(privatekey))) {
            throw new IllegalStateException("Invalid nonce!");
        } else {
            this.loginKey = packetlogininencryptionbegin.a(privatekey);
            this.g = LoginListener.EnumProtocolState.AUTHENTICATING;
            this.networkManager.a(this.loginKey);
            Thread thread = new Thread("User Authenticator #" + LoginListener.b.incrementAndGet()) {
                public void run() {
                    GameProfile gameprofile = LoginListener.this.i;

                    try {
                        String s = (new BigInteger(MinecraftEncryption.a("", LoginListener.this.server.E().getPublic(), LoginListener.this.loginKey))).toString(16);

                        LoginListener.this.i = LoginListener.this.server.ap().hasJoinedServer(new GameProfile((UUID) null, gameprofile.getName()), s, this.a());
                        if (LoginListener.this.i != null) {
                            LoginListener.c.info("UUID of player {} is {}", LoginListener.this.i.getName(), LoginListener.this.i.getId());
                            LoginListener.this.g = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
                        } else if (LoginListener.this.server.H()) {
                            LoginListener.c.warn("Failed to verify username but will let them in anyway!");
                            LoginListener.this.i = LoginListener.this.a(gameprofile);
                            LoginListener.this.g = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
                        } else {
                            LoginListener.this.disconnect(new ChatMessage("multiplayer.disconnect.unverified_username", new Object[0]));
                            LoginListener.c.error("Username '{}' tried to join with an invalid session", gameprofile.getName());
                        }
                    } catch (AuthenticationUnavailableException authenticationunavailableexception) {
                        if (LoginListener.this.server.H()) {
                            LoginListener.c.warn("Authentication servers are down but will let them in anyway!");
                            LoginListener.this.i = LoginListener.this.a(gameprofile);
                            LoginListener.this.g = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
                        } else {
                            LoginListener.this.disconnect(new ChatMessage("multiplayer.disconnect.authservers_down", new Object[0]));
                            LoginListener.c.error("Couldn't verify username because servers are unavailable");
                        }
                    }

                }

                @Nullable
                private InetAddress a() {
                    SocketAddress socketaddress = LoginListener.this.networkManager.getSocketAddress();

                    return LoginListener.this.server.S() && socketaddress instanceof InetSocketAddress ? ((InetSocketAddress) socketaddress).getAddress() : null;
                }
            };

            thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LoginListener.c));
            thread.start();
        }
    }

    public void a(PacketLoginInCustomPayload packetloginincustompayload) {
        this.disconnect(new ChatMessage("multiplayer.disconnect.unexpected_query_response", new Object[0]));
    }

    protected GameProfile a(GameProfile gameprofile) {
        UUID uuid = EntityHuman.getOfflineUUID(gameprofile.getName());

        return new GameProfile(uuid, gameprofile.getName());
    }

    static enum EnumProtocolState {

        HELLO, KEY, AUTHENTICATING, NEGOTIATING, READY_TO_ACCEPT, DELAY_ACCEPT, ACCEPTED;

        private EnumProtocolState() {}
    }
}
