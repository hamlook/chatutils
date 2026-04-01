package com.chatutils.mixin;

import com.chatutils.ChatUtils;
import com.chatutils.ChatUtilsState;
import com.chatutils.hook.ChatLineHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Mixin(ChatLine.class)
public class MixinChatLine implements ChatLineHook {


    @Unique private boolean chatutils$detected = false;

    @Unique private NetworkPlayerInfo chatutils$playerInfo = null;

    @Unique private NetworkPlayerInfo chatutils$detectedPlayerInfo = null;

    @Unique private long chatutils$uniqueId = 0L;

    @Unique private IChatComponent chatutils$fullMsg = null;

    @Unique private static long chatutils$lastUniqueId = 0L;

    @Unique private static final Pattern SPLIT_PATTERN = Pattern.compile("(§.)|\\W");


    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(int updateCounter, IChatComponent lineString, int chatLineID, CallbackInfo ci) {

        chatutils$uniqueId = ++chatutils$lastUniqueId;

        chatutils$fullMsg = ChatUtilsState.currentFullMessage;

        if (chatutils$fullMsg != null && chatutils$fullMsg == ChatUtilsState.lastFullMessage) {
            return;
        }
        ChatUtilsState.lastFullMessage = chatutils$fullMsg;

        NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();
        if (netHandler == null) return;

        String beforeColon = StringUtils.substringBefore(lineString.getFormattedText(), ":");

        Map<String, NetworkPlayerInfo> nicknameCache = new HashMap<>();

        try {
            for (String word : SPLIT_PATTERN.split(beforeColon)) {
                if (word.isEmpty()) continue;

                NetworkPlayerInfo info = netHandler.getPlayerInfo(word);

                if (info == null) {
                    info = chatutils$getPlayerFromNickname(word, netHandler, nicknameCache);
                }

                if (info != null) {
                    chatutils$detected          = true;
                    chatutils$detectedPlayerInfo = info;

                    if (ChatUtilsState.lastDetectedPlayer != null &&
                            info.getGameProfile() == ChatUtilsState.lastDetectedPlayer.getGameProfile()
                            && ChatUtils.Config.hideHeadOnConsecutive) {
                        chatutils$playerInfo = null;
                    } else {
                        chatutils$playerInfo = info;
                    }

                    ChatUtilsState.lastDetectedPlayer = info;
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Unique
    @Nullable
    private static NetworkPlayerInfo chatutils$getPlayerFromNickname(
            String word,
            NetHandlerPlayClient connection,
            Map<String, NetworkPlayerInfo> cache) {

        if (cache.isEmpty()) {
            for (NetworkPlayerInfo p : connection.getPlayerInfoMap()) {
                IChatComponent displayName = p.getDisplayName();
                if (displayName == null) continue;

                String nickname = displayName.getUnformattedTextForChat();
                if (word.equals(nickname)) {
                    cache.clear(); // signal "already found, no need to keep"
                    return p;
                }
                cache.put(nickname, p);
            }
            return null;
        } else {
            return cache.get(word);
        }
    }


    @Override
    public boolean chatutils$hasDetected() {
        return chatutils$detected;
    }

    @Override
    public NetworkPlayerInfo chatutils$getPlayerInfo() {
        return chatutils$playerInfo;
    }

    @Override
    public long chatutils$getUniqueId() {
        return chatutils$uniqueId;
    }

    @Override
    public IChatComponent chatutils$getFullMessage() {
        return chatutils$fullMsg;
    }
}