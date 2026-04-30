package com.app.Identification;

import java.util.List;

public class Media {
    private String Title;
    private String Type;
    private String Description;
    private List<Integer> Seasons;

    public String getTitle() { return Title; }
    @SuppressWarnings("unused")
    public void setTitle(String title) { this.Title = title; }

    public String getType() { return Type; }
    @SuppressWarnings("unused")
    public void setType(String type) { this.Type = type; }

    public String getDescription() { return Description; }
    @SuppressWarnings("unused")
    public void setDescription(String description) { this.Description = description; }

    public List<Integer> getSeasons() { return Seasons; }
    @SuppressWarnings("unused")
    public void setSeasons(List<Integer> seasons) { this.Seasons = seasons; }
}