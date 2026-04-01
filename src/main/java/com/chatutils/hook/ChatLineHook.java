package com.chatutils.hook;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;


public interface ChatLineHook {

    boolean chatutils$hasDetected();

    NetworkPlayerInfo chatutils$getPlayerInfo();

    long chatutils$getUniqueId();

    IChatComponent chatutils$getFullMessage();
}