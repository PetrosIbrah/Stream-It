package com.app.Identification;

import java.util.List;

public class MediaList {
    private List<Media> Media;

    public List<Media> getAllMedia() {
        return Media;
    }

    @SuppressWarnings("unused")
    public void setMedia(List<Media> allMedia) {
        this.Media = allMedia;
    }
}