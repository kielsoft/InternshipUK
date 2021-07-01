package com.google;

import java.util.Collections;
import java.util.List;

/** A class used to represent a video. */
class Video {

  private final String title;
  private final String videoId;
  private final List<String> tags;
  
  private Boolean paused = false;
  private Boolean flagged = false;
  private String flagReason = "";

  Video(String title, String videoId, List<String> tags) {
    this.title = title;
    this.videoId = videoId;
    this.tags = Collections.unmodifiableList(tags);
  }

  /** Returns the title of the video. */
  String getTitle() {
    return title;
  }

  /** Returns the video id of the video. */
  String getVideoId() {
    return videoId;
  }

  /** Returns a readonly collection of the tags of the video. */
  List<String> getTags() {
    return tags;
  }

  public Boolean getIsPauseed() {
    return paused;
  }

  public void setIsPauseed(Boolean pausedValue) {
    paused = pausedValue;
  }

  public String getFlagReason() {
    return flagReason;
  }

  public void setFlagReason(String flagReason) {
    this.flagReason = flagReason;
  }

  public Boolean getFlagged() {
    return flagged;
  }

  public void setFlagged(Boolean flagged) {
    this.flagged = flagged;
  }

  public String getFullDetail() {
    String flagReasonText = !flagReason.isEmpty() ? flagReason : "Not supplied";
    String flgMessage = flagged ? String.format(" - FLAGGED (reason: %s)", flagReasonText) : "";
    return String.format("%s (%s) %s%s", title, videoId, tags.toString().replace(",", ""), flgMessage);
  }
}
