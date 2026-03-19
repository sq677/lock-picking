package net.skittle.lockpicking.UI.notice;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class NoticeConfig {
    private static final File CONFIG_FILE = new File("config/lockpicking-notice.properties");
    private static final String DONT_SHOW_KEY = "dontShowNotice";

    public static boolean shouldShowNotice() {
        if (!CONFIG_FILE.exists()) {
            return true;
        }

        Properties props = new Properties();
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            props.load(reader);
            return !Boolean.parseBoolean(props.getProperty(DONT_SHOW_KEY, "false"));
        } catch (IOException e) {
            return true;
        }
    }

    public static void setDontShowAgain(boolean dontShow) {
        Properties props = new Properties();

        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                props.load(reader);
            } catch (IOException e) {
            }
        }

        props.setProperty(DONT_SHOW_KEY, String.valueOf(dontShow));

        File configDir = CONFIG_FILE.getParentFile();
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            props.store(writer, "Lockpicking Mod Notice Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
