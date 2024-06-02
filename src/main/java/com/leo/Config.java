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
        public static void setSizeX(int size) {
            sizeX = size;
        }
        public static void setSizeY(int size) {
            sizeY = size;
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
