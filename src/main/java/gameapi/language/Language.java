package gameapi.language;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import gameapi.GameAPI;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Language {

    protected HashMap<String, Map<String, Object>> lang;

    protected String pluginName;

    protected String defaultLanguage;

    public Language(String pluginName) {
        lang = new HashMap<>();
        this.pluginName = pluginName;
        this.defaultLanguage = Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
    }

    public String getPluginName() {
        return pluginName;
    }

    public void addLanguage(File file) {
        if (file.getName().endsWith(".properties")) {
            String locale = file.getName().replace(".properties", "");
            lang.put(locale, new Config(file, Config.PROPERTIES).getAll());
            GameAPI.plugin.getLogger().info("§aLanguage Loaded: " + locale);
        } else {
            GameAPI.plugin.getLogger().info("§cInvalid Language File: " + file.getName());
        }
    }

    public String getTranslationWithDefaultValue(String language, String key, String defaultValue, Object... param) {
        String processedText = (String) lang.getOrDefault(language, new HashMap<>()).getOrDefault(key, defaultValue == null ? "§cNot Found!" : defaultValue);
        if (param.length > 0) {
            for (int i = 1; i <= param.length; i++) {
                processedText = processedText.replaceAll("%" + i + "%", String.valueOf(param[i - 1]));
            }
        }
        processedText = processedText.replace("\\n", "\n");
        return processedText;
    }

    public String getTranslation(String key, Object... param) {
        return getTranslationWithDefaultValue(defaultLanguage, key, key, param);
    }

    public String getTranslation(CommandSender sender, String key, Object... param) {
        if (sender.isPlayer()) {
            return getTranslation((Player) sender, key, param);
        } else {
            return getTranslation(key, param);
        }
    }

    public String getTranslation(Player player, String key, Object... param) {
        return getTranslationWithDefaultValue(getLang(player), key, key, param);
    }

    private String getLang(Player player) {
        String languageCode = player.getLoginChainData().getLanguageCode();
        return lang.containsKey(languageCode) ? languageCode : defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

}
