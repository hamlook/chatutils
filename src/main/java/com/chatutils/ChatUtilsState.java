package com.chatutils;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;

public final class ChatUtilsState {

    private ChatUtilsState() {}

    public static IChatComponent currentFullMessage = null;
    public static IChatComponent lastFullMessage = null;
    public static NetworkPlayerInfo lastDetectedPlayer = null;
}