package com.app.InitiatingClasses;

import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VideoHandler {
    private static final Logger log = LogManager.getLogger(VideoHandler.class);

    private static String ffmpegloc = System.getenv("ffmpeg");
    private static String ffprobeloc = System.getenv("ffprobe");

    public static String Getffmpegloc () {
        if (ffmpegloc == null || ffmpegloc.isEmpty()) {
            ffmpegloc = "ffmpeg";
        }
        return ffmpegloc;
    }

    private static String Getffprobeloc () {
        if (ffprobeloc == null || ffprobeloc.isEmpty()) {
            ffprobeloc = "ffprobe";
        }
        return ffprobeloc;
    }

    protected static void SendDetails (SSLSocket socket, String Choice) {
        List<String> VideoList = GetAllAvailableVideos(Choice);
        SendVideos(socket, VideoList);
    }

    private static List<String> GetAllAvailableVideos (String Choice) {
        List<String> VideoList = null ;
        try (Stream<Path> walk = Files.walk(Paths.get("VideosAndPictures/AvailableVideos/" + Choice))) {
            VideoList = walk
                    .filter(Files::isRegularFile)
                    .map(path -> path.toString().replace("\\", "/"))
                    .filter(path -> path.endsWith(".mp4") || path.endsWith(".mkv") || path.endsWith(".avi"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Unable to See all available videos.");
        }
        return VideoList;
    }

    private static void SendVideos(SSLSocket socket, List<String> VideoList) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println(VideoList.size());
            String vid = null;
            for (String videoPath : VideoList) {
                out.println(videoPath);
                vid = videoPath;

            }
            out.println(getVideoLength(vid));
            String msg = "Sent " + VideoList.size() + " videos successfully.";
            log.info(msg);
        } catch (Exception e) {
            log.error("Unable to send video list to client.");
        }
    }

    private static long getVideoLength(String filePath) {
        double seconds = 0;
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    ffprobeloc, "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    filePath
            );
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor();

            seconds = Double.parseDouble(line);
        } catch (Exception e){
            log.error("Unable to extract video length.");
        }
        return (long) (seconds * 1000);
    }

    protected static void VideoPopulation() {
        try {
            FFmpegExecutor executor;
            FFmpeg ffmpeg = new FFmpeg(Getffmpegloc ());
            FFprobe ffprobe = new FFprobe(Getffprobeloc ());

            String[] Formats = {"mkv", "mp4", "avi"};
            String[] FormatNames = {"matroska", "mp4", "avi"};
            String[] Codecs = {"libx264", "libx264", "mpeg4"};
            String[] heights = {"240", "360", "480", "720", "1080"};
            String[] widths = {"426", "640", "854", "1280", "1920"};

            List<String> VideoList;
            try (Stream<Path> walk = Files.walk(Paths.get("VideosAndPictures/AvailableVideos"))) {
                VideoList = walk
                        .filter(Files::isRegularFile)
                        .map(path -> path.toString().replace("\\", "/"))
                        .filter(path -> path.endsWith(".mp4") || path.endsWith(".mkv") || path.endsWith(".avi"))
                        .toList();
            }

            for (String Input : VideoList) {
                File inputFile = new File(Input);
                if (!inputFile.exists()) {
                    continue;
                }

                String parentFolder = inputFile.getParent().replace("\\", "/");

                String filename = inputFile.getName();
                String Name = filename.substring(0, filename.lastIndexOf('.'));

                Name = Name.replaceAll("-(\\d{2,4})p$", "");

                for (int j = 0; j < Formats.length; j++) {
                    String Codec = Codecs[j];
                    String FormatName = FormatNames[j];
                    String format = Formats[j];

                    for (int i = 0; i < heights.length; i++) {
                        String height = heights[i];
                        String width = widths[i];

                        String Output = parentFolder + "/" + Name + "-" + height + "p." + format;
                        File checkfile = new File(Output);
                        if (checkfile.exists()) {
                            continue;
                        }

                        FFmpegBuilder builder = new FFmpegBuilder()
                                .setInput(Input)
                                .overrideOutputFiles(true)
                                .addOutput(Output)
                                .setFormat(FormatName)
                                .setVideoResolution(Integer.parseInt(width), Integer.parseInt(height))
                                .setVideoCodec(Codec)
                                .done();

                        executor = new FFmpegExecutor(ffmpeg, ffprobe);
                        executor.createJob(builder).run();
                        String msg = "Converted [" + Output + "] successfully.";
                        log.info(msg);
                    }
                }
            }

            log.info("Checked all videos and made new if needed.");
        } catch (Exception e) {
            log.error("Unable to Upscale and downscale videos.");
        }
    }

}