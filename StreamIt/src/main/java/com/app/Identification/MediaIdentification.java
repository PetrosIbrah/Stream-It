package com.app.Identification;

import java.util.List;

public class MediaIdentification {
    private static String[] FileNames;
    private static String Choice;

    public static void Init (int ImageCount) {
        FileNames = new String[ImageCount];
    }

    public static void AddFile (int i, String FileName) {
        FileNames[i] = FileName;
    }

    public static String[] GetAllFileNames () {
        return FileNames;
    }

    public static String GetChoice () {
        return Choice;
    }

    public static void SetChoice (String msg) {
        Choice = msg;
    }

    private static String Title;
    private static String Type;
    private static String Description;
    private static List<Integer> Seasons;
    private static long Duration;

    public static String getTitle() { return Title; }
    public static void setTitle(String title) { Title = title; }

    public static String getType() { return Type; }
    public static void setType(String type) { Type = type; }

    public static String getDescription() { return Description; }
    public static void setDescription(String description) { Description = description; }

    public static List<Integer> getSeasons() { return Seasons; }
    public static void setSeasons(List<Integer> seasons) { Seasons = seasons; }

    public static long GetDuration() {return Duration;}
    public static void setDuration(long duration) {Duration = duration;}
}