package com.google;

import java.util.ArrayList;
import java.util.List;

/** A class used to represent a Playlist */
class VideoPlaylist {
  private final String title;
  private final List<String> videoIds;

  VideoPlaylist(String title) {
    this.title = title;
    this.videoIds = new ArrayList<String>();
  }

  String getTitle() {
    return title;
  }

  List<String> getVideoIds() {
    return this.videoIds;
  }
}
