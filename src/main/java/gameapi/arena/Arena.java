package gameapi.arena;

import cn.nukkit.Server;
import gameapi.MainClass;
import gameapi.utils.FileHandler;

import java.io.File;
import java.io.IOException;

public class Arena {
    public static Boolean saveWorld(cn.nukkit.level.Level world){
        String rootPath = Server.getInstance().getDataPath()+"/worlds/"+world.getName()+"/";
        String savePath = MainClass.path+"/worlds/"+world.getName()+"/";
        try {
            FileHandler.copyDir(rootPath,savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Boolean copyWorld(String saveWorld,String name){
        Server.getInstance().broadcastMessage(Server.getInstance().getDataPath());
        String worldPath = Server.getInstance().getDataPath()+"/worlds/"+saveWorld+"/";
        String savePath = MainClass.path+"/worlds/"+name+"/";
        try {
            FileHandler.copyDir(savePath, worldPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Boolean copyWorldAndLoad(String saveWorld,String name){
        String worldPath = Server.getInstance().getDataPath()+"/worlds/"+saveWorld+"/";
        String savePath = MainClass.path+"/worlds/"+name+"/";
        try {
            FileHandler.copyDir(savePath, worldPath);
            Server.getInstance().loadLevel(saveWorld);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Boolean deleteDir(String saveWorld){
        String worldPath = Server.getInstance().getDataPath()+"/worlds/"+saveWorld+"/";
        File file = new File(worldPath);
        if(file.isDirectory()) {
            FileHandler.delete(file);
        }
        return true;
    }

    public static void createVoidWorld(String worldname){
        Server.getInstance().generateLevel(worldname);
    }
}
