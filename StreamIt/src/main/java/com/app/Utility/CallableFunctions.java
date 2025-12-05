package com.app.Utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CallableFunctions {
    private static final Logger log = LogManager.getLogger(CallableFunctions.class);

    private static final String CONFIG_FILE = "config.properties";

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

}