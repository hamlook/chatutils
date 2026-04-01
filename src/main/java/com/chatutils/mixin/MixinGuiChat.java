package com.chatutils.mixin;

import com.chatutils.ChatUtils;
import com.chatutils.hook.ChatLineHook;
import com.chatutils.hook.GuiNewChatHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChat.class)
public class MixinGuiChat {

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void chatutils$onMouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (!ChatUtils.Config.chatCopyEnabled) return;
        if (mouseButton != 0) return;
        if (!GuiScreen.isShiftKeyDown() && !GuiScreen.isCtrlKeyDown()) return;

        int rawX = Mouse.getX();
        int rawY = Minecraft.getMinecraft().displayHeight - Mouse.getY() - 1;

        GuiNewChatHook chatGUI = (GuiNewChatHook) Minecraft.getMinecraft().ingameGUI.getChatGUI();
        ChatLine line = chatGUI.chatutils$getCurrentHoveredLine();
        if (line == null) return;

        String text;
        if (GuiScreen.isCtrlKeyDown()) {
            text = EnumChatFormatting.getTextWithoutFormattingCodes(
                    line.getChatComponent().getFormattedText());
        } else {
            IChatComponent fullMsg = ((ChatLineHook) line).chatutils$getFullMessage();
            IChatComponent src = (fullMsg != null) ? fullMsg : line.getChatComponent();
            text = EnumChatFormatting.getTextWithoutFormattingCodes(src.getFormattedText());
        }

        GuiScreen.setClipboardString(text);
        String preview = text.length() > 60 ? text.substring(0, 57) + "..." : text;

        ci.cancel();
    }
}