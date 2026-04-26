package com.app.Utility;

import com.app.Identification.FileIdentification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CallableFunctions {
    private static final Logger log = LogManager.getLogger(CallableFunctions.class);

    private static final String CONFIG_FILE = FileIdentification.Properties;

    public static List<String> GetAllAudioDevices() {
        List<String> devices = new ArrayList<>();
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();

        for (Mixer.Info info : mixers) {
            Mixer mixer = AudioSystem.getMixer(info);

            Line.Info[] targetLines = mixer.getTargetLineInfo();

            for (Line.Info lineInfo : targetLines) {
                if (TargetDataLine.class.isAssignableFrom(lineInfo.getLineClass())) {
                    devices.add(info.getName());
                    break;
                }
            }
        }

        return devices;
    }

    public static String loadAudioDevice() {
        Properties props = new Properties();
        try {
            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            props.load(fis);
            return props.getProperty("AudioDevice", "");
        } catch (Exception e) {
            log.error("Unable to load Audio Device from properties");
            return "";
        }
    }

    public static void saveAudioDevice(String deviceName) {
        Properties props = new Properties();
        try {
            try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                props.load(fis);
            } catch (Exception ignored) {}

            props.setProperty("AudioDevice", deviceName);

            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
                props.store(fos, "Application config");
            }

        } catch (Exception e) {
            log.error("Unable to save Audio Device");
        }
    }

    public static String loadMinPrefResolution() {
        Properties props = new Properties();
        try {
            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            props.load(fis);
            return props.getProperty("MinPrefResolution", "");
        } catch (Exception e) {
            log.error("Unable to load Min Preferred Resolution from properties");
            return "";
        }
    }

    public static void saveMinPrefResolution(String resolution) {
        Properties props = new Properties();
        try {
            try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                props.load(fis);
            } catch (Exception ignored) {}

            props.setProperty("MinPrefResolution", resolution);

            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
                props.store(fos, "Application config");
            }

        } catch (Exception e) {
            log.error("Unable to save Min Preferred Resolution");
        }
    }


    public static Boolean loadNextEp() {
        Properties props = new Properties();
        try {
            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            props.load(fis);
            String value = props.getProperty("NextEp", null);
            return value != null ? Boolean.parseBoolean(value) : null;
        } catch (Exception e) {
            log.error("Unable to load Next Episode preference from properties");
            return null;
        }
    }

    public static void saveNextEp(boolean value) {
        Properties props = new Properties();
        try {
            try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                props.load(fis);
            } catch (Exception ignored) {}

            props.setProperty("NextEp", String.valueOf(value));

            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
                props.store(fos, "Application config");
            }

        } catch (Exception e) {
            log.error("Unable to save Next Episode preference");
        }
    }

    public static void saveAutoAdapt(boolean value) {
        Properties props = new Properties();
        try {
            try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                props.load(fis);
            } catch (Exception ignored) {}

            props.setProperty("AutoAdapt", String.valueOf(value));

            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
                props.store(fos, "Application config");
            }

        } catch (Exception e) {
            log.error("Unable to save Auto Adapt preference");
        }
    }

    public static Boolean loadAutoAdapt() {
        Properties props = new Properties();
        try {
            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            props.load(fis);
            String value = props.getProperty("AutoAdapt", null);
            return value != null ? Boolean.parseBoolean(value) : null;
        } catch (Exception e) {
            log.error("Unable to load Auto Adapt preference from properties");
            return null;
        }
    }

    public static String loadRecordingsPath() {
        Properties props = new Properties();
        try {
            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            props.load(fis);
            return props.getProperty("RecordingsPath", "");
        } catch (Exception e) {
            log.error("Unable to load Properties Path from properties");
            return "";
        }
    }

    public static void saveRecordingsPath(String path) {
        Properties props = new Properties();

        try {
            try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                props.load(fis);
            } catch (Exception ignored) {}

            props.setProperty("RecordingsPath", path);

            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
                props.store(fos, "Application config");
            }

        } catch (Exception e) {
            log.error("Unable to save Recordings Path");
        }
    }

    public static String loadServerIp() {
        Properties props = new Properties();
        try {
            File configFile = new File(CONFIG_FILE);
            if (!configFile.exists()) {
                props.setProperty("ServerIp", "localhost");
                props.setProperty("Port", "8000");
                try (FileOutputStream fos = new FileOutputStream(configFile)) {
                    props.store(fos, "Application config");
                }
                return "localhost";
            }
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
            }
            return props.getProperty("ServerIp", "localhost");
        } catch (Exception e) {
            log.error("Unable to load Server IP from properties");
            return "localhost";
        }
    }

    public static int loadPort() {
        Properties props = new Properties();
        try {
            File configFile = new File(CONFIG_FILE);
            if (!configFile.exists()) {
                props.setProperty("ServerIp", "localhost");
                props.setProperty("Port", "8000");
                try (FileOutputStream fos = new FileOutputStream(configFile)) {
                    props.store(fos, "Application config");
                }
                return 8000;
            }
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
            }
            return Integer.parseInt(props.getProperty("Port", "8000"));
        } catch (Exception e) {
            log.error("Unable to load Port from properties");
            return 8000;
        }
    }

}