package com.leo;

public class Config {

    private static int sizeX = 800;
    private static int sizeY = 450;
    private static String lookAndFeel = "system";
    private static String dbPath = "./database.db";

    public class MainFrame {
        public static int getSizeX() {
            return sizeX;
        }
        public static int getSizeY() {
            return sizeY;
        }
        public static String getLookAndFeel() {
            return lookAndFeel;
        }
    }

    public class Log {
        public static void setdbPath(String path) {
            dbPath = path;
        }
        public static String getdbPath() {
            return dbPath;
        }
    }
    
}
