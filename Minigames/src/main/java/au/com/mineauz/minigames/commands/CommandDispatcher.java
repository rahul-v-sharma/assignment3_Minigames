package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.set.SetCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CommandDispatcher implements CommandExecutor, TabCompleter {
    private static final Map<String, ICommand> commands = new HashMap<>();
    private static final Minigames plugin = Minigames.getPlugin();
    private static BufferedWriter cmdFile;

    static {
        if (plugin.getConfig().getBoolean("outputCMDToFile")) {
            try {
                cmdFile = new BufferedWriter(new FileWriter(plugin.getDataFolder() + "/cmds.txt"));
                cmdFile.write("{| class=\"wikitable\"");
                cmdFile.newLine();
                cmdFile.write("! Command");
                cmdFile.newLine();
                cmdFile.write("! Syntax");
                cmdFile.newLine();
                cmdFile.write("! Description");
                cmdFile.newLine();
                cmdFile.write("! Permission");
                cmdFile.newLine();
                cmdFile.write("! Alias");
                cmdFile.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        registerCommand(new CreateCommand());
        registerCommand(new SetCommand());
        registerCommand(new JoinCommand());
        registerCommand(new StartCommand());
        registerCommand(new StopCommand());
        registerCommand(new QuitCommand());
        registerCommand(new RevertCommand());
        registerCommand(new HintCommand());
        registerCommand(new EndCommand());
        registerCommand(new HelpCommand());
//        registerCommand(new ReloadCommand());
        registerCommand(new ListCommand());
        registerCommand(new ListPlaceholder());
        registerCommand(new ToggleTimerCommand());
        registerCommand(new DeleteCommand());
        registerCommand(new PartyModeCommand());
        registerCommand(new DeniedCommandCommand());
        registerCommand(new GlobalLoadoutCommand());
        registerCommand(new SpectateCommand());
        registerCommand(new PlayerCommand());
        registerCommand(new ScoreCommand());
        registerCommand(new TeleportCommand());
        registerCommand(new EditCommand());
        registerCommand(new ToolCommand());
        registerCommand(new ScoreboardCommand());
        registerCommand(new EnableAllCommand());
        registerCommand(new DisableAllCommand());
        registerCommand(new SaveCommand());
        registerCommand(new LoadoutCommand());
        registerCommand(new BackupCommand());
        registerCommand(new DebugCommand());
        registerCommand(new BackendCommand());
        registerCommand(new InfoCommand());
        registerCommand(new ResourcePackCommand());

        if (plugin.getConfig().getBoolean("outputCMDToFile")) {
            try {
                cmdFile.write("|}");
                cmdFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void registerCommand(ICommand command) {
        commands.put(command.getName(), command);

        if (plugin.getConfig().getBoolean("outputCMDToFile")) {
            try {
                cmdFile.write("|-");
                cmdFile.newLine();
                cmdFile.write("| '''" + command.getName() + "'''");
                cmdFile.newLine();
                if (command.getUsage() != null) {
                    int count = 0;
                    cmdFile.write("| ");
                    for (String use : command.getUsage()) {
                        cmdFile.write(use);
                        count++;
                        if (count != command.getUsage().length) {
                            cmdFile.write("\n\n");
                        }
                    }
                } else
                    cmdFile.write("| N/A");
                cmdFile.newLine();
                if (command.getDescription() != null)
                    cmdFile.write("| " + command.getDescription());
                else
                    cmdFile.write("| N/A");
                cmdFile.newLine();
                if (command.getPermission() != null)
                    cmdFile.write("| " + command.getPermission());
                else
                    cmdFile.write("| N/A");
                cmdFile.newLine();
                if (command.getAliases() != null) {
                    int count = 0;
                    cmdFile.write("| ");
                    for (String alias : command.getAliases()) {
                        cmdFile.write(alias);
                        count++;
                        if (count != command.getAliases().length) {
                            cmdFile.write("\n\n");
                        }
                    }
                } else
                    cmdFile.write("| N/A");
                cmdFile.newLine();
            } catch (IOException e) {
                //Failed to write
            }
        }
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player ply = getPlayer(sender);
        if (args != null && args.length > 0) {
            return handleCommand(sender, command, label, args, ply);
        } else {
            sendMinigameInfo(sender);
            return true;
        }
    }

    private Player getPlayer(CommandSender sender) {
        if (sender instanceof Player player) {
            return player;
        }
        return null;
    }

    private boolean handleCommand(CommandSender sender, Command command, String label, String[] args, Player ply) {
        ICommand comd = findCommand(args[0].toLowerCase());
        String[] shortArgs = getShortArgs(args);

        if (comd != null) {
            return executeCommand(sender, label, shortArgs, ply, comd);
        }
        return false;
    }

    private ICommand findCommand(String commandName) {
        ICommand comd = commands.get(commandName);
        if (comd == null) {
            for (ICommand com : commands.values()) {
                if (com.getAliases() != null && Arrays.asList(com.getAliases()).contains(commandName)) {
                    comd = com;
                    break;
                }
            }
        }
        return comd;
    }

    private String[] getShortArgs(String[] args) {
        if (args.length > 1) {
            return Arrays.copyOfRange(args, 1, args.length);
        }
        return new String[0];
    }

    private boolean executeCommand(CommandSender sender, String label, String[] shortArgs, Player ply, ICommand comd) {
        if (ply != null || comd.canBeConsole()) {
            if (ply == null || comd.getPermission() == null || ply.hasPermission(comd.getPermission())) {
                boolean returnValue = comd.onCommand(sender, null, label, shortArgs);
                if (!returnValue) {
                    sendCommandInfo(sender, comd);
                }
            } else {
                sender.sendMessage(ChatColor.RED + comd.getPermissionMessage());
                sender.sendMessage(ChatColor.RED + comd.getPermission());
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command!");
        }
        return true;
    }

    private void sendCommandInfo(CommandSender sender, ICommand comd) {
        sender.sendMessage(ChatColor.GREEN + "------------------Command Info------------------");
        sender.sendMessage(ChatColor.BLUE + "Description: " + ChatColor.WHITE + comd.getDescription());
        if (comd.getParameters() != null) {
            StringBuilder parameters = new StringBuilder();
            boolean switchColour = false;
            for (String par : comd.getParameters()) {
                parameters.append(switchColour ? ChatColor.WHITE : ChatColor.GRAY).append(par);
                if (!par.equalsIgnoreCase(comd.getParameters()[comd.getParameters().length - 1])) {
                    parameters.append(ChatColor.WHITE).append(", ");
                }
                switchColour = !switchColour;
            }
            sender.sendMessage(ChatColor.BLUE + "Parameters: " + parameters);
        }
        sender.sendMessage(ChatColor.BLUE + "Usage: ");
        sender.sendMessage(comd.getUsage());
        if (comd.getAliases() != null) {
            StringBuilder aliases = new StringBuilder();
            boolean switchColour = false;
            for (String alias : comd.getAliases()) {
                aliases.append(switchColour ? ChatColor.WHITE : ChatColor.GRAY).append(alias);
                if (!alias.equalsIgnoreCase(comd.getAliases()[comd.getAliases().length - 1])) {
                    aliases.append(ChatColor.WHITE).append(", ");
                }
                switchColour = !switchColour;
            }
            sender.sendMessage(ChatColor.BLUE + "Aliases: " + aliases);
        }
    }

    private void sendMinigameInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Minigames");
        sender.sendMessage(ChatColor.GRAY + "By: " + plugin.getDescription().getAuthors().get(0));
        sender.sendMessage(ChatColor.GRAY + "Version: " + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GRAY + "Type /minigame help for help");
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args != null && args.length > 0) {
            Player ply = null;
            if (sender instanceof Player) {
                ply = (Player) sender;
            }
            ICommand comd = null;
            String[] shortArgs = null;

            if (commands.containsKey(args[0].toLowerCase())) {
                comd = commands.get(args[0].toLowerCase());
            }

            if (args.length > 1) {
                shortArgs = new String[args.length - 1];
                System.arraycopy(args, 1, shortArgs, 0, args.length - 1);
            }

            if (comd != null) {
                if (ply != null) {
                    if (args.length > 1) {
                        List<String> l = comd.onTabComplete(sender, null, alias, shortArgs);
                        return Objects.requireNonNullElseGet(l, () -> List.of(""));
                    }
                }
            } else {
                List<String> ls = new ArrayList<>(commands.keySet());
                return MinigameUtils.tabCompleteMatch(ls, args[0]);
            }
        }
        return null;
    }
}
