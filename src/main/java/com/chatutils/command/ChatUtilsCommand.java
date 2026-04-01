package com.chatutils.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;


public class ChatUtilsCommand extends SimpleCommand {

    private static Runnable openGuiCallback = null;

    public static void setOpenGuiCallback(Runnable callback) {
        openGuiCallback = callback;
    }

    @Override
    public String getName()              { return "chatutils"; }

    @Override
    public String getUsage()             { return "/chatutils"; }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        if (openGuiCallback != null) openGuiCallback.run();
    }
}