package com.chatutils.command;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class CopyToClipboardCommand extends SimpleCommand {

    @Override
    public String getName()  { return "copytoclipboard"; }

    @Override
    public String getUsage() { return "/copytoclipboard <text>"; }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        if (args == null || args.length == 0) return;
        try {
            GuiScreen.setClipboardString(String.join(" ", args));
        } catch (Exception ignored) {}
    }
}