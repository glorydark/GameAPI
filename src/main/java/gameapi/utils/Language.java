package gameapi.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import gameapi.GameAPI;
import gameapi.annotation.Experimental;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Experimental
public class Language {
    protected static HashMap<String, Map<String, Object>> lang;

    protected String name;

    protected String defaultLanguage;

    public Language(String name){
        lang = new HashMap<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addLanguage(File file){
        if(file.getName().endsWith(".properties")){
            String locale = file.getName().replace(".properties", "");
            lang.put(locale, new Config(file, Config.PROPERTIES).getAll());
            Server.getInstance().getLogger().info("§aLanguage Loaded: "+locale);
        }else{
            Server.getInstance().getLogger().info("§cInvalid Language File: "+file.getName());
        }
    }

    public String getText(String key, Object... param){
        String processedText = (String) lang.getOrDefault(defaultLanguage, new HashMap<>()).getOrDefault(key, "§cNot Found!");
        if(param.length > 0){
            for(int i = 1; i<=param.length; i++){
                processedText = processedText.replaceAll("%"+i+"%", String.valueOf(param[i-1]));
            }
        }
        processedText = processedText.replace("\\n", "\n");
        return processedText;
    }

    public String getText(Player player, String key, Object... param){
        String processedText = (String) lang.getOrDefault(getLang(player), new HashMap<>()).getOrDefault(key, "§cNot Found!");
        if(param.length > 0){
            for(int i = 1; i<=param.length; i++){
                processedText = processedText.replaceAll("%"+i+"%", String.valueOf(param[i-1]));
            }
        }
        processedText = processedText.replace("\\n", "\n");
        return processedText;
    }

    public String getTextWithDefaultValue(Player player, String key, String defaultValue, Object... param){
        String processedText = (String) lang.getOrDefault(getLang(player), new HashMap<>()).getOrDefault(key, defaultValue);
        if(param.length > 0){
            for(int i = 1; i<=param.length; i++){
                processedText = processedText.replaceAll("%"+i+"%", String.valueOf(param[i-1]));
            }
        }
        processedText = processedText.replace("\\n", "\n");
        return processedText;
    }

    private String getLang(Player player){
        Config config = new Config(GameAPI.path+"/language_cache.yml", Config.YAML);
        if(config.exists(player.getName())){
            String prefer = config.getString(player.getName());
            if(lang.containsKey(prefer)){
                return prefer;
            }else{
                return defaultLanguage;
            }
        }else{
            return defaultLanguage;
        }
    }

    public void setPlayerPreferLanguage(Player player, String langName){
        Config config = new Config(GameAPI.path+"/language_cache.yml", Config.YAML);
        config.set(player.getName(), langName);
        config.save();
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }
}
