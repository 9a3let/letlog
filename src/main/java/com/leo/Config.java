package com.leo;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import lombok.Getter;
import lombok.Setter;

public class Config {

    private static String configPath = "./letlog.conf";

    @Getter
    @Setter
    private static int mainFrameSizeX = 800;
    @Getter
    @Setter
    private static int mainFrameSizeY = 450;
    @Getter
    @Setter
    private static String dbPath = "./database.db";

    public static void readConfigFile() throws Exception {
        Configurations configs = new Configurations();
        INIConfiguration config = configs.ini(new File(configPath));

        String readdbpath = config.getString("General.databasePath");
        if (readdbpath == null) {
            throw new Exception("Database path can not be null");
        }
        dbPath = config.getString("General.databasePath");
        mainFrameSizeX = config.getInt("Window.sizeX");
        mainFrameSizeY = config.getInt("Window.sizeY");
    }

    public static void writeConfigFile() throws Exception {
        Configurations configs = new Configurations();
        INIConfiguration config = configs.ini(new File(configPath));

        config.setProperty("Window.sizeX", mainFrameSizeX);
        config.setProperty("Window.sizeY", mainFrameSizeY);
        config.setProperty("General.databasePath", dbPath);

        Writer writer = new FileWriter(configPath);
        config.write(writer);
        writer.close();

    }
}
