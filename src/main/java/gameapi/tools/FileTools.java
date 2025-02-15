package gameapi.tools;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import gameapi.GameAPI;
import gameapi.utils.GsonAdapter;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lt_name (CrystalWar)
 */
public class FileTools {

    public static boolean copyFiles(String from, String target) {
        return copyFiles(new File(from), new File(target));
    }

    /**
     * 复制文件
     */
    public static boolean copyFiles(File from, File target) {

        int load = 1;
        File[] files = from.listFiles();
        if (files != null) {
            for (File value : files) {
                GameAPI.getInstance().getLogger().info("Copy World ... " + ((load / (float) files.length) * 100) + "%");
                load++;
                if (value.isFile()) {
                    // 复制文件
                    try {
                        File file1 = new File(target + File.separator + value.getName());
                        if (!file1.exists()) {
                            try {
                                file1.createNewFile();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        copyByChannelToChannel(value, file1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (value.isDirectory()) {
                    // 复制目录
                    String sourceDir = from + File.separator + value.getName();
                    String targetDir = target + File.separator + value.getName();
                    try {
                        copyDirectiory(sourceDir, targetDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return true;
    }

    /**
     * 通过channel到channel直接传输
     *
     * @param source 源文件
     * @param target 目标文件
     * @throws IOException 异常
     */
    public static void copyByChannelToChannel(File source, File target) throws IOException {

        RandomAccessFile sourceFile = new RandomAccessFile(source, "r");
        FileChannel sourceChannel = sourceFile.getChannel();

        if (!target.isFile()) {
            if (!target.createNewFile()) {
                sourceChannel.close();
                sourceFile.close();
                return;
            }
        }
        RandomAccessFile destFile = new RandomAccessFile(target, "rw");
        FileChannel destChannel = destFile.getChannel();
        long leftSize = sourceChannel.size();
        long position = 0;
        while (leftSize > 0) {
            long writeSize = sourceChannel.transferTo(position, leftSize, destChannel);
            position += writeSize;
            leftSize -= writeSize;
        }
        sourceChannel.close();
        sourceFile.close();
        destChannel.close();
        destFile.close();
    }


    /**
     * 复制文件夹
     */
    private static void copyDirectiory(String sourceDir, String targetDir)
            throws IOException {
        // 新建目标目录
        File file = new File(targetDir);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                GameAPI.getInstance().getLogger().error("新建" + targetDir + "失败");
            }
        }
        // 获取源文件夹当前下的文件或目录
        File[] files = (new File(sourceDir)).listFiles();
        if (files != null) {
            for (File value : files) {
                if (value.isFile()) {
                    // 源文件
                    // 目标文件
                    File targetFile = new
                            File(new File(targetDir).getAbsolutePath()
                            + File.separator + value.getName());
                    copyByChannelToChannel(value, targetFile);

                }
                if (value.isDirectory()) {
                    // 准备复制的源文件夹
                    String dir1 = sourceDir + File.separator + value.getName();
                    // 准备复制的目标文件夹
                    String dir2 = targetDir + File.separator + value.getName();
                    copyDirectiory(dir1, dir2);
                }
            }
        }

    }


    /**
     * 画一条进度条
     * ■■■■□□□□□□
     *
     * @param progress    进度（百分比）
     * @param size        总长度
     * @param hasDataChar 自定义有数据图案 ■
     * @param noDataChar  自定义无数据图案 □
     * @return 画出来的线
     * by sobadfish
     */
    public static String drawLine(float progress, int size, String hasDataChar, String noDataChar) {
        int l = (int) (size * progress);
        int other = size - l;
        StringBuilder ls = new StringBuilder();
        if (l > 0) {
            for (int i = 0; i < l; i++) {
                ls.append(hasDataChar);
            }
        }
        StringBuilder others = new StringBuilder();
        if (other > 0) {
            for (int i = 0; i < other; i++) {
                others.append(noDataChar);
            }
        }
        return TextFormat.colorize('&', ls + others.toString());
    }

    /**
     * 获取百分比
     * 保留两位有效数字
     *
     * @param n   当前值
     * @param max 最大值
     * @return 计算出的百分比
     */
    public static double getPercent(int n, int max) {
        double r = 0;
        if (n > 0) {
            r = (double) n / (double) max;
        }
        return Double.parseDouble(String.format("%.2f", r));
    }

    public static boolean delete(File deleteFile) {
        try {
            if (!deleteFile.exists()) {
                return true;
            }
            File[] files = deleteFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        delete(file);
                    } else if (!file.delete()) {
                        throw new IOException(GameAPI.getLanguage().getTranslation("file.delete.error", file.getName()));
                    }
                }
            }
            if (!deleteFile.delete()) {
                throw new IOException(GameAPI.getLanguage().getTranslation("file.delete.error", deleteFile.getName()));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean copy(String from, String to) {
        return copy(new File(from), new File(to));
    }

    @Deprecated
    public static boolean copy(File from, File to) {
        try {
            File[] files = from.listFiles();
            if (files != null) {
                if (!to.exists() && !to.mkdirs()) {
                    throw new IOException("文件夹: " + to.getName() + " 创建失败！");
                }
                for (File file : files) {
                    if (file.isDirectory()) {
                        copy(file, new File(to, file.getName()));
                    } else {
                        Utils.copyFile(file, new File(to, file.getName()));
                    }
                }
                return true;
            } else {
                Utils.copyFile(from, to);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Map<String, Object> convertConfigToMap(File file) {
        if (file.getName().endsWith(".json")) {
            InputStream stream;
            try {
                stream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8); //一定要以utf-8读取
            JsonReader reader = new JsonReader(streamReader);
            Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<Map<String, Object>>() {
            }.getType(), new GsonAdapter()).create();
            Map<String, Object> mainMap = gson.fromJson(reader, new TypeToken<Map<String, Object>>() {
            }.getType());

            // Remember to close the streamReader after your implementation.
            try {
                reader.close();
                streamReader.close();
                stream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return mainMap;
        } else if (file.getName().endsWith(".yml")) {
            return new Config(file, Config.YAML).getAll();
        }
        return new HashMap<>();
    }


}