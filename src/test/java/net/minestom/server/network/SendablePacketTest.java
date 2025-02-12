package net.minestom.server.network;

import net.kyori.adventure.text.Component;
import net.minestom.server.message.ChatPosition;
import net.minestom.server.network.packet.server.CachedPacket;
import net.minestom.server.network.packet.server.LazyPacket;
import net.minestom.server.network.packet.server.play.ChatMessagePacket;
import net.minestom.server.utils.PacketUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class SendablePacketTest {

    @Test
    public void lazy() {
        var packet = new ChatMessagePacket(Component.text("Hello World!"), ChatPosition.CHAT, UUID.randomUUID());
        AtomicBoolean called = new AtomicBoolean(false);
        var lazy = new LazyPacket(() -> {
            if (called.getAndSet(true))
                fail();
            return packet;
        });
        assertSame(packet, lazy.packet());
        assertSame(packet, lazy.packet());
    }

    @Test
    public void cached() {
        var packet = new ChatMessagePacket(Component.text("Hello World!"), ChatPosition.CHAT, UUID.randomUUID());
        var cached = new CachedPacket(packet);
        assertSame(packet, cached.packet());

        var buffer = PacketUtils.allocateTrimmedPacket(packet);
        var cachedBuffer = cached.body();
        assertEquals(buffer.body(), cachedBuffer);
        // May fail in the very unlikely case where soft references are cleared
        // Rare enough to make this test worth it
        assertSame(cached.body(), cachedBuffer);

        assertSame(packet, cached.packet());
    }
}
