package de.hellfirepvp.cmd.crespawn;

import de.hellfirepvp.cmd.MessageAssist;
import de.hellfirepvp.cmd.PlayerCmobCommand;
import de.hellfirepvp.file.write.RespawnDataWriter;
import de.hellfirepvp.lib.LibLanguageOutput;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * HellFirePvP@Admin
 * Date: 16.05.2015 / 00:04
 * on Project CustomMobs
 * CommandCrespawnRemove
 */
public class CommandCrespawnRemove extends PlayerCmobCommand {
    @Override
    public void execute(Player p, String[] args) {
        String name = args[1];

        switch (RespawnDataWriter.resetRespawnSettings(name)) {
            case MOB_DOESNT_EXIST:
                p.sendMessage(LibLanguageOutput.PREFIX + ChatColor.RED + name + " isn't set to respawn. Nothing to remove.");
                break;
            case IO_EXCEPTION:
                MessageAssist.msgIOException(p);
                break;
            case SUCCESS:
                p.sendMessage(LibLanguageOutput.PREFIX + ChatColor.GREEN + name + " doesn't respawn anymore.");
                break;
        }
    }

    @Override
    public String getCommandStart() {
        return "remove";
    }

    @Override
    public boolean hasFixedLength() {
        return true;
    }

    @Override
    public int getFixedArgLength() {
        return 2;
    }

    @Override
    public int getMinArgLength() {
        return 0;
    }

}