package io.akarin.server.core;

public enum PacketType {
    STATUS_OUT_SERVER_INFO,
    STATUS_OUT_PONG,
    
    PLAY_OUT_MAP_CHUNK,
    PLAY_OUT_SPAWN_POSITION,
    PLAY_OUT_CHAT,
    
    UNKNOWN;
}