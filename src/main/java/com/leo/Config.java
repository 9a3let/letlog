package com.leo;

public class Config {

    private static int sizeX = 800;
    private static int sizeY = 450;
    private static String dbPath = "./database.db";

    public class MainFrame {
        public static int getSizeX() {
            return sizeX;
        }
        public static int getSizeY() {
            return sizeY;
        }
    }

    public class Log {
        public static String getdbPath() {
            return dbPath;
        }
    }
}
