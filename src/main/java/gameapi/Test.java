package gameapi;

import java.io.File;

/**
 * @author glorydark
 */
public class Test {

    public static void main(String[] args) {
        File file = new File("E:/dependencies/moli-test-server/plugins/GameAPI/worlds/RecklessHero/");
        for (File listFile : file.listFiles()) {
            String name = listFile.getName();
            if (name.endsWith("-convert")) {
                name = name.replace("-convert", "");
            }
            listFile.renameTo(new File("E:/dependencies/moli-test-server/plugins/GameAPI/worlds/RecklessHero/" + name));
        }
    }
}
