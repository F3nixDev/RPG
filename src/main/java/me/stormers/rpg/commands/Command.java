package me.stormers.rpg.commands;


import me.stormers.rpg.Main;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Command implements CommandExecutor {
    protected Main main;

    public Command(Main main, String name) {
        this.main = main;
        main.getCommand(name).setExecutor(this);
    }

    protected abstract void execute(Player paramPlayer, String[] paramArrayOfString);

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String alias, String[] args) {
        if (sender instanceof Player)
            execute((Player)sender, args);
        return true;
    }
}
