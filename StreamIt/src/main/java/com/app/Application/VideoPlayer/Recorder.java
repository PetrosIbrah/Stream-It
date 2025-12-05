package com.app.Application.VideoPlayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.File;

public class Recorder {
    private static final Logger log = LogManager.getLogger(Recorder.class);

    private FFmpegFrameGrabber videoGrabber;
    private FFmpegFrameGrabber audioGrabber;
    private FFmpegFrameRecorder recorder;

    private volatile boolean running = false;

    public void startRecording(String windowTitle, String audioDevice, String outputFile) {
        File outFile = new File(outputFile);
        File parent = outFile.getParentFile();
        if (parent != null && !parent.exists()) {
            if(parent.mkdirs()){
                String msg = "Derectory" + parent;
                log.info(msg);
            }
        }

        running = true;
        log.info("Starting recording to {}", outputFile);

        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            startWindowsRecording(windowTitle, audioDevice, outputFile);
        } else if (os.contains("linux")) {
            startLinuxRecording(outputFile);
        } else {
            log.warn("Unsupported OS");
        }
    }

    private void startWindowsRecording(String windowTitle, String audioDevice, String outputFile) {
        log.info("Initializing Windows recording...");

        videoGrabber = new FFmpegFrameGrabber("title=" + windowTitle);
        videoGrabber.setFormat("gdigrab");
        videoGrabber.setFrameRate(30);
        try {videoGrabber.start();} catch (Exception e) {
            log.error("Windows - Unable to grab video.");
        }

        int width = videoGrabber.getImageWidth();
        int height = videoGrabber.getImageHeight();
        log.info("Video initialized: {}x{}", width, height);

        audioGrabber = new FFmpegFrameGrabber("audio=" + audioDevice);
        audioGrabber.setFormat("dshow");
        audioGrabber.setAudioChannels(2);
        try{audioGrabber.start();} catch (Exception e) {
            log.error("Windows - Unable to grab audio.");
        }
        log.info("Audio initialized: {}", audioDevice);

        startRecorder(outputFile, width, height);
    }

    private void startLinuxRecording(String outputFile) {
        log.info("Initializing Linux recording...");

        String display = System.getenv("DISPLAY");
        if (display == null) display = ":0";

        videoGrabber = new FFmpegFrameGrabber(display + ".0");
        videoGrabber.setFormat("x11grab");
        videoGrabber.setFrameRate(30);
        try {videoGrabber.start();} catch (Exception e) {
            log.error("Linux - Unable to grab video.");
        }

        int width = videoGrabber.getImageWidth();
        int height = videoGrabber.getImageHeight();
        log.info("Video Initialized: {}x{}", width, height);

        audioGrabber = new FFmpegFrameGrabber("default");
        audioGrabber.setFormat("pulse");
        audioGrabber.setAudioChannels(2);
        try{audioGrabber.start();} catch (Exception e) {
            log.error("Linux - Unable to grab audio.");
        }

        log.info("Audio initialized: pulse:default");

        startRecorder(outputFile, width, height);
    }

    private void startRecorder(String outputFile, int width, int height) {
        recorder = new FFmpegFrameRecorder(outputFile, width, height, 2);
        recorder.setFormat("mp4");
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        recorder.setFrameRate(30);
        recorder.setVideoBitrate(4000 * 1000);

        try{recorder.start();} catch (Exception e) {
            log.error("Unable to start recording.");
        }
        log.info("Recorder started → {}", outputFile);

        Thread videoThread = new Thread(() -> {
            try {
                Frame frame;
                while (running && (frame = videoGrabber.grab()) != null) {
                    recorder.record(frame);
                }
            } catch (Exception e) {
                log.error("Video thread error", e);
            }
        }, "VideoThread");
        videoThread.setDaemon(true);
        videoThread.start();

        Thread audioThread = new Thread(() -> {
            try {
                Frame frame;
                while (running && (frame = audioGrabber.grab()) != null) {
                    recorder.record(frame);
                }
            } catch (Exception e) {
                log.error("Audio thread error", e);
            }
        }, "AudioThread");
        audioThread.setDaemon(true);
        audioThread.start();
    }

    public void stopRecording() {
        log.info("Stopping recording...");

        try {
            running = false;

            Thread.sleep(100);

            if (videoGrabber != null) {
                videoGrabber.stop();
                videoGrabber.release();
                videoGrabber = null;
                log.info("Video grabber stopped");
            }

            if (audioGrabber != null) {
                audioGrabber.stop();
                audioGrabber.release();
                audioGrabber = null;
                log.info("Audio grabber stopped");
            }

            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
                log.info("Recorder finalized and file saved");
            }

        } catch (Exception e) {
            log.error("Error while stopping recording", e);
        }
    }
}
