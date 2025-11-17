package com.app;

import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.net.Socket;

public class VideoHandler {
    private static String ffmpegloc = System.getenv("ffmpeg");
    private static String ffprobeloc = System.getenv("ffprobe");

    public static String Getffmpegloc () {
        if (ffmpegloc == null || ffmpegloc.isEmpty()) {
            ffmpegloc = "ffmpeg";
        }
        return ffmpegloc;
    }

    public static String Getffprobeloc () {
        if (ffprobeloc == null || ffprobeloc.isEmpty()) {
            ffprobeloc = "ffprobe";
        }
        return ffprobeloc;
    }

    public static void SendDetails (Socket socket, String Choice) {
        List<String> VideoList = GetAllAvailableVideos(Choice);
        SendVideos(socket, VideoList);
    }

    public static List<String> GetAllAvailableVideos (String Choice) {
        List<String> VideoList = null ;
        try (Stream<Path> walk = Files.walk(Paths.get("AvailableVideos/" + Choice))) {
            VideoList = walk
                    .filter(Files::isRegularFile)
                    .map(path -> path.toString().replace("\\", "/"))
                    .filter(path -> path.endsWith(".mp4") || path.endsWith(".mkv") || path.endsWith(".avi"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return VideoList;
    }

    public static void SendVideos(Socket socket, List<String> VideoList) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println(VideoList.size());
            String vid = null;
            for (String videoPath : VideoList) {
                out.println(videoPath);
                vid = videoPath;

            }
            System.out.println(getVideoLength(vid));
            out.println(getVideoLength(vid));
            System.out.println("Sent " + VideoList.size() + " videos successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static long getVideoLength(String filePath) {

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
            e.printStackTrace();
        }
        return (long) (seconds * 1000);
    }

    public static void VideoPopulation() {
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
            try (Stream<Path> walk = Files.walk(Paths.get("AvailableVideos"))) {
                VideoList = walk
                        .filter(Files::isRegularFile)
                        .map(path -> path.toString().replace("\\", "/"))
                        .filter(path -> path.endsWith(".mp4") || path.endsWith(".mkv") || path.endsWith(".avi"))
                        .collect(Collectors.toList());
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
                        System.out.println("Converted [" + Output + "] successfully.");
                    }
                }
            }

            System.out.println("Checked all videos and made new if needed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
