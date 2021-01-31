package org.serverct.parrot.sscampfire.command;

import org.serverct.parrot.parrotx.api.ParrotXAPI;
import org.serverct.parrot.parrotx.command.subcommands.DebugCommand;
import org.serverct.parrot.parrotx.command.subcommands.HelpCommand;
import org.serverct.parrot.parrotx.command.subcommands.ReloadCommand;
import org.serverct.parrot.parrotx.command.subcommands.VersionCommand;
import org.serverct.parrot.parrotx.data.autoload.PAutoload;
import org.serverct.parrot.sscampfire.SSCampfire;

@PAutoload
public class CommandHandler extends org.serverct.parrot.parrotx.command.CommandHandler {
    public CommandHandler() {
        super(ParrotXAPI.getPlugin(SSCampfire.class), "sscampfire");

        register(new ReloadCommand(plugin, "SSCampfire.admin"));
        register(new DebugCommand(plugin, "SSCampfire.admin"));
        register(new HelpCommand(plugin));
        register(new VersionCommand(plugin));
    }
}
