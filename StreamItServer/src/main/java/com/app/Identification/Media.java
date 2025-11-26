package com.app.Identification;

import java.util.List;

public class Media {
    private String Title;
    private String Type;
    private String Description;
    private List<Integer> Seasons;
    private static String StreamingFile = null;

    public String getTitle() { return Title; }
    public void setTitle(String title) { this.Title = title; }

    public String getType() { return Type; }
    public void setType(String type) { this.Type = type; }

    public String getDescription() { return Description; }
    public void setDescription(String description) { this.Description = description; }

    public List<Integer> getSeasons() { return Seasons; }
    public void setSeasons(List<Integer> seasons) { this.Seasons = seasons; }

    public static String getStreamingFile() {
        return StreamingFile;
    }

    public static void setStreamingFile(String streamingFile) {
        StreamingFile = streamingFile;
    }
}
