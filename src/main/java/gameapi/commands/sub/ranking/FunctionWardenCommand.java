package gameapi.commands.sub.ranking;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.form.AdvancedFormWindowCustom;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.form.element.ResponsiveElementButton;
import gameapi.form.element.ResponsiveElementDropdown;
import gameapi.tools.SmartTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @author glorydark
 */
public class FunctionWardenCommand extends EasySubCommand {

    public final List<String> managers = new ArrayList<String>() {
        {
            this.add("niangaoqiqi");
            this.add("mcfatienr");
            this.add("BizarreDark");
        }
    };

    public FunctionWardenCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender.getName().equals("BizarreDark")) {
            showRevisionUI(commandSender.asPlayer());
        } else {
            showPunishUI(commandSender.asPlayer(), 0, "", false, "");
        }
        return false;
    }

    public void showRevisionUI(Player sender) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("正义之光");
        simple.addButton(
                new ResponsiveElementButton("审核处理")
                        .onRespond(this::showRevisionList)
        );
        simple.addButton(
                new ResponsiveElementButton("处罚")
                        .onRespond(player -> {
                            showPunishUI(player, 0, "", false, "");
                        })
        );
        simple.showToPlayer(sender);
    }

    public void showRevisionList(Player sender) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("未审核列表");
        Config config = new Config(GameAPI.getPath() + "/report.yml", Config.YAML);
        ConfigSection undone = config.getSection("unrevised_reports");
        if (undone.isEmpty()) {
            simple.setContent(TextFormat.RED + "暂无待审核处理哦！");
        } else {
            for (String key : undone.getKeys(false)) {
                simple.addButton(
                        new ResponsiveElementButton(key)
                                .onRespond(player -> showRevisionDetail(player, key))
                );
            }
        }
        simple.showToPlayer(sender);
    }

    public void showRevisionDetail(Player sender, String key) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(key);
        Config config = new Config(GameAPI.getPath() + "/report.yml", Config.YAML);
        ConfigSection undone = config.getSection("unrevised_reports");
        ConfigSection details = undone.getSection(key);
        simple.setContent(
                "处理员: " + TextFormat.YELLOW + details.getString("manager") + TextFormat.RESET + "\n" +
                "玩家: " + TextFormat.YELLOW + details.getString("name") + TextFormat.RESET + "\n" +
                "作弊游戏: " + TextFormat.YELLOW + details.getString("game") + TextFormat.RESET + "\n" +
                "原因: " + TextFormat.YELLOW + details.getString("reason")
        );
        simple.addButton(
                new ResponsiveElementButton(TextFormat.GREEN + "处理无误")
                        .onRespond(player -> {
                            details.set("status", "right");
                            Config conf = new Config(GameAPI.getPath() + "/report.yml", Config.YAML);
                            ConfigSection done = config.getSection("revised_reports");
                            done.set(key, details);
                            conf.set("revised_reports", done);
                            ConfigSection undone1 = config.getSection("unrevised_reports");
                            undone1.remove(key);
                            conf.set("unrevised_reports", undone1);
                            conf.save();
                            player.sendMessage(TextFormat.GREEN + "处理完毕，标记为正常处理！");
                        })
        );
        simple.addButton(
                new ResponsiveElementButton(TextFormat.RED + "处理有误")
                        .onRespond(player -> {
                            details.set("status", "wrong");
                            Config conf = new Config(GameAPI.getPath() + "/report.yml", Config.YAML);
                            ConfigSection done = config.getSection("revised_reports");
                            done.set(key, details);
                            conf.set("revised_reports", done);
                            ConfigSection undone1 = config.getSection("unrevised_reports");
                            undone1.remove(key);
                            conf.set("unrevised_reports", undone1);
                            conf.save();
                            player.sendMessage(TextFormat.RED + "处理完毕，标记为异常处理！");
                        })
        );
        simple.showToPlayer(sender);
    }

    public void showPunishUI(Player sender, int gameType, String reason, boolean promise, String content) {
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom("正义之光");
        List<String> playerOnline = new ArrayList<>();
        playerOnline.add("- 未选择 -");
        for (Player p : Server.getInstance().getOnlinePlayers().values()) {
            playerOnline.add(p.getName());
        }
        custom.label(new ElementLabel(content.isEmpty()? TextFormat.RED + "请务必在封禁前留存视频证据或对局录像证据，恶意处理将被处罚！": content));
        custom.dropdown(new ResponsiveElementDropdown("玩家名", playerOnline));
        custom.input(new ElementInput("玩家名（离线）", "", ""));
        custom.dropdown(new ElementDropdown("作弊游戏", Arrays.asList("主城跑酷", "跑酷练习", "跑酷乐园", "PVP", "PVE"), gameType));
        custom.input(new ElementInput("原因", "", reason));
        custom.toggle(new ElementToggle(TextFormat.GREEN + "我保证我的处理合规且已留存证据！", promise));
        custom.onRespond((player, formResponseCustom) -> {
            String name = formResponseCustom.getDropdownResponse(1).getElementContent();
            if (name.isEmpty()) {
                name = formResponseCustom.getInputResponse(2);
            }
            String game = formResponseCustom.getDropdownResponse(3).getElementContent();
            String reason1 = formResponseCustom.getInputResponse(4);
            boolean promise1 = formResponseCustom.getToggleResponse(5);
            if (name.isEmpty()) {
                showPunishUI(player, 0, reason1, true, TextFormat.RED + "[提示] 您未选择玩家，请重新选择！”");
                return;
            }
            if (player.getName().equals("BizarreDark") || managers.contains(name)) {
                player.sendMessage(TextFormat.RED + "该玩家为正义之光小组成员，需要由组长处理。");
                return;
            }
            if (promise1) {
                if (Server.getInstance().lookupName(name).isPresent()) {
                    Config config = new Config(GameAPI.getPath() + "/report.yml", Config.YAML);
                    ConfigSection undone = config.getSection("unrevised_reports");
                    String code = SmartTools.dateToString(Calendar.getInstance().getTime());
                    String finalName = name;
                    undone.set(code, new ConfigSection() {{
                        this.put("name", finalName);
                        this.put("game", game);
                        this.put("reason", reason1);
                        this.put("promise", promise1);
                        this.put("manager", player.getName());
                    }});
                    config.set("unrevised_reports", undone);
                    Server.getInstance().getNameBans().addBan(name, reason1);
                    Player player1 = Server.getInstance().getPlayer(name);
                    if (player1 != null) {
                        player1.kick(TextFormat.RED + "您已被正义之光制裁，制裁代码: " + code);
                    }
                    config.save();
                    player.sendMessage(TextFormat.GREEN + "系统已经对该玩家进行处理，具体审核将在1-2个工作日内完成！");
                } else {
                    showPunishUI(player, formResponseCustom.getDropdownResponse(3).getElementID(), reason1, true, TextFormat.RED + "[提示] 玩家不存在，您输入的玩家名：" + name + "，请检查大小写！”");
                }
            } else {
                showPunishUI(player, formResponseCustom.getDropdownResponse(3).getElementID(), reason1, false, TextFormat.RED + "[提示] 请选中“我保证我的处理合规且已留存证据！”");
            }
        });
        custom.showToPlayer(sender);
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return managers.contains(commandSender.getName());
    }
}
