package com.chatutils.hook;

import net.minecraft.client.gui.ChatLine;

public interface GuiNewChatHook {

    ChatLine chatutils$getHoveredChatLine(int rawMouseX, int rawMouseY);

    ChatLine chatutils$getCurrentHoveredLine();
}