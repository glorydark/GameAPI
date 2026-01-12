package gameapi.utils;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import gameapi.GameAPI;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Language {

    protected final HashMap<String, Map<String, Object>> lang;

    protected final String pluginName;

    protected String defaultLanguage;

    public Language(String pluginName) {
        this(pluginName, Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry());
    }

    public Language(String pluginName, String defaultLanguage) {
        lang = new HashMap<>();
        this.pluginName = pluginName;
        this.defaultLanguage = defaultLanguage;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void addLanguage(File file) {
        if (file.getName().endsWith(".properties")) {
            String locale = file.getName().substring(0, file.getName().lastIndexOf("."));
            lang.put(locale, new Config(file, Config.PROPERTIES).getAll());
            GameAPI.getInstance().getLogger().info("§aLanguage Loaded: " + locale);
        } else {
            GameAPI.getInstance().getLogger().info("§cInvalid Language File: " + file.getName());
        }
    }

    public String getTranslationWithDefaultValue(String language, String key, String defaultValue, Object... param) {
        String processedText = (String) lang.getOrDefault(language, new HashMap<>()).getOrDefault(key, defaultValue == null ? key : defaultValue);
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

    public String translate(String key, Object... param) {
        return getTranslation(key, param);
    }

    public String translate(CommandSender sender, String key, Object... param) {
        return getTranslation(sender, key, param);
    }

    public String translate(String language, String key, String defaultValue, Object... param) {
        return getTranslationWithDefaultValue(language, key, defaultValue, param);
    }

    private String getLang(Player player) {
        String languageCode = player.getLoginChainData().getLanguageCode();
        return lang.containsKey(languageCode) ? languageCode : defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getTranslationByFormat(String key, Object... param) {
        String processedText = key;
        if (param.length > 0) {
            for (int i = 1; i <= param.length; i++) {
                processedText = processedText.replaceAll("%" + i + "%", String.valueOf(param[i - 1]));
            }
        }
        return processedText;
    }
}
