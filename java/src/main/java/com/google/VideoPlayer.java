package com.google;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class VideoPlayer {

  private final VideoLibrary videoLibrary;
  private final HashMap<String, VideoPlaylist> videoPlaylists;
  private Video playingVideo;

  public VideoPlayer() {
    this.videoLibrary = new VideoLibrary();
    this.videoPlaylists = new HashMap<>();
  }

  public void numberOfVideos() {
    System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
  }

  public void showAllVideos() {
    List<Video> videos = sortVideosInList(videoLibrary.getVideos());
    
    System.out.println("Here's a list of all available videos:");
    videos.forEach(video -> {
      System.out.println(video.getFullDetail());
    });
  }

  public void playVideo(String videoId) {
    Video video = videoLibrary.getVideo(videoId);
    if(Objects.nonNull(video)){

      if(video.getFlagged()){
        System.out.printf("Cannot play video: Video is currently flagged (reason: %s)%n", video.getFlagReason().isEmpty()? "Not supplied" : video.getFlagReason());
        return;
      }
      
      // if there is a video currently playing - stop it first
      if(Objects.nonNull(playingVideo)){
        stopVideo();
      }
      
      // now play the new video
      System.out.printf("Playing video: %s%n", video.getTitle());
      playingVideo = video;

    } else {
      System.out.println("Cannot play video: Video does not exist");
    }
  }

  public void stopVideo() {
    try {
      playingVideo.setIsPauseed(false);
      System.out.printf("Stopping video: %s%n", playingVideo.getTitle());
      playingVideo = null;
    } catch (NullPointerException e) {
      System.out.println("Cannot stop video: No video is currently playing");
    }
  }

  public void playRandomVideo() {
    List<Video> videos = getUnflaggedVideos();
    if(videos.isEmpty()) {
      System.out.println("No videos available");
      return;
    }
    int randomIndex = (new Random()).nextInt(videos.size());
    Video video = videos.get(randomIndex);
    playVideo(video.getVideoId());
  }

  public void pauseVideo() {
    try {
      if(playingVideo.getIsPauseed()){
        System.out.printf("Video already paused: %s%n", playingVideo.getTitle());
      } else {
        playingVideo.setIsPauseed(true);
        System.out.printf("Pausing video: %s%n", playingVideo.getTitle());
      }
    } catch (NullPointerException e) {
      System.out.println("Cannot pause video: No video is currently playing");
    }
  }

  public void continueVideo() {
    try {
      if(playingVideo.getIsPauseed()){
        playingVideo.setIsPauseed(false);
        System.out.printf("Continuing video: %s%n", playingVideo.getTitle());
      } else {
        System.out.println("Cannot continue video: Video is not paused");
      }
    } catch (NullPointerException e) {
      System.out.println("Cannot continue video: No video is currently playing");
    }
  }

  public void showPlaying() {
    try {
      System.out.printf(
        "Currently playing: %s (%s) %s%s", 
        playingVideo.getTitle(),
        playingVideo.getVideoId(),
        playingVideo.getTags().toString().replace(",", ""),
        Boolean.TRUE.equals(playingVideo.getIsPauseed()) ? " - PAUSED" : "");
    } catch (NullPointerException e) {
      System.out.println("No video is currently playing");
    }
  }

  public void createPlaylist(String playlistName) {
    playlistName = cleanUpPlaylistName(playlistName);
    String playlistId = playlistName.toLowerCase();
    VideoPlaylist existingPlaylist = videoPlaylists.get(playlistId);
    
    if(Objects.nonNull(existingPlaylist)){
      System.out.println("Cannot create playlist: A playlist with the same name already exists");
      return;
    }

    videoPlaylists.put(playlistId, new VideoPlaylist(playlistName));
    System.out.printf("Successfully created new playlist: %s%n", playlistName);
  }

  public void addVideoToPlaylist(String playlistName, String videoId) {
    String playlistId = cleanUpPlaylistName(playlistName, true);
    VideoPlaylist existingPlaylist = videoPlaylists.get(playlistId);
    Video video = videoLibrary.getVideo(videoId);

    if(Objects.isNull(existingPlaylist)){
      System.out.printf("Cannot add video to %s: Playlist does not exist%n", playlistName);
      return;
    } else if(Objects.isNull(video)){
      System.out.printf("Cannot add video to %s: Video does not exist%n", playlistName);
      return;
    } else if (existingPlaylist.getVideoIds().contains(videoId)) {
      System.out.printf("Cannot add video to %s: Video already added%n", playlistName);
      return;
    } else if(video.getFlagged()){
      System.out.printf("Cannot add video to %s: Video is currently flagged (reason: %s)%n", playlistName, video.getFlagReason().isEmpty()? "Not supplied" : video.getFlagReason());
      return;
    }

    existingPlaylist.getVideoIds().add(videoId);
    System.out.printf("Added video to %s: %s%n", playlistName, video.getTitle());
    
  }

  public void showAllPlaylists() {
    if(videoPlaylists.size() < 1) {
      System.out.println("No playlists exist yet");
    } else {
      System.out.println("Showing all playlists:");
      videoPlaylists.values().forEach(videoPlaylist -> {
        System.out.printf("%s%n", videoPlaylist.getTitle());
      });
    }
    
  }

  public void showPlaylist(String playlistName) {
    String playlistId = cleanUpPlaylistName(playlistName, true);
    VideoPlaylist existingPlaylist = videoPlaylists.get(playlistId);

    if (Objects.nonNull(existingPlaylist)) {
      System.out.printf("Showing playlist: %s%n", playlistName);
      if(existingPlaylist.getVideoIds().isEmpty()){
        System.out.println("No videos here yet.");
      } else {
        existingPlaylist.getVideoIds().forEach(videoId -> {
          Video video = videoLibrary.getVideo(videoId);
          System.out.println(video.getFullDetail());
        });
      }
      
    } else {
      System.out.printf("Cannot show playlist %s: Playlist does not exist%n", playlistName);
    }
  }

  public void removeFromPlaylist(String playlistName, String videoId) {
    String playlistId = cleanUpPlaylistName(playlistName, true);
    VideoPlaylist existingPlaylist = videoPlaylists.get(playlistId);

    if (Objects.nonNull(existingPlaylist)) {
      Video video = videoLibrary.getVideo(videoId);
      
      if(Objects.isNull(video)){
        System.out.printf("Cannot remove video from %s: Video does not exist%n", playlistName);
        return;
      }

      if(!existingPlaylist.getVideoIds().contains(videoId)){
        System.out.printf("Cannot remove video from %s: Video is not in playlist%n", playlistName);
        return;
      }
      
      existingPlaylist.getVideoIds().remove(videoId);
      System.out.printf("Removed video from %s: %s%n", playlistName, video.getTitle());
      return;
    }

    System.out.printf("Cannot remove video from %s: Playlist does not exist%n", playlistName);
  }

  public void clearPlaylist(String playlistName) {
    String playlistId = cleanUpPlaylistName(playlistName, true);
    VideoPlaylist existingPlaylist = videoPlaylists.get(playlistId);

    if (Objects.nonNull(existingPlaylist)) {
      existingPlaylist.getVideoIds().clear();
      System.out.printf("Successfully removed all videos from %s%n", playlistName);
      return;
    }
    
    System.out.printf("Cannot clear playlist %s: Playlist does not exist%n", playlistName);
  }

  public void deletePlaylist(String playlistName) {
    String playlistId = cleanUpPlaylistName(playlistName, true);
    VideoPlaylist existingPlaylist = videoPlaylists.get(playlistId);

    if (Objects.nonNull(existingPlaylist)) {
      existingPlaylist = null;
      System.out.printf("Deleted playlist: %s%n", playlistName);
      return;
    }
    
    System.out.printf("Cannot delete playlist %s: Playlist does not exist%n", playlistName);
  }

  public void searchVideos(String searchTerm) {
    searchVideoList(searchTerm, false);
  }

  public void searchVideosWithTag(String videoTag) {
    searchVideoList(videoTag, true);
  }

  public void flagVideo(String videoId) {
    flagVideo(videoId, "");
  }

  public void flagVideo(String videoId, String reason) {
    Video video = videoLibrary.getVideo(videoId);
    if(Objects.nonNull(video)){

      if(video.getFlagged()){
        System.out.println("Cannot flag video: Video is already flagged");
        return;
      }

      if(Objects.nonNull(playingVideo) &&  video.getVideoId() == playingVideo.getVideoId()) {
        stopVideo();
      }
      
      video.setFlagged(true);
      video.setFlagReason(reason);
      System.out.printf("Successfully flagged video: %s (reason: %s)%n", video.getTitle(), reason.isEmpty()? "Not supplied" : reason);

    } else {
      System.out.println("Cannot flag video: Video does not exist");
    }
  }

  public void allowVideo(String videoId) {
    Video video = videoLibrary.getVideo(videoId);
    if(Objects.nonNull(video)){

      if(!video.getFlagged()){
        System.out.println("Cannot remove flag from video: Video is not flagged");
        return;
      }

      if(Objects.nonNull(playingVideo) &&  video.getVideoId().equals(playingVideo.getVideoId())) {
        stopVideo();
      }
      
      video.setFlagged(false);
      video.setFlagReason("");
      System.out.printf("Successfully removed flag from video: %s%n", video.getTitle());

    } else {
      System.out.println("Cannot remove flag from video: Video does not exist");
    }
  }

  private void searchVideoList(String searchTerm, Boolean useTag) {
    String cleanedUpSearchTerm = cleanUpSearchTerm(searchTerm).toLowerCase();
    List<Video> foundVideos = new ArrayList<Video>();

    List<Video> videos = getUnflaggedVideos();

    for (int i = 0; i < videos.size(); i++) {
      Video video = videos.get(i);
      String textToSearchIn = (useTag) ? video.getTitle() : video.getTitle();
      if (textToSearchIn.toLowerCase().contains(cleanedUpSearchTerm)){
        foundVideos.add(video);
      }
    }

    if(foundVideos.isEmpty()) {
      System.out.printf("No search results for %s%n", searchTerm);
      return;
    }

    // Sort the video by title ascendingly
    foundVideos = sortVideosInList(foundVideos);

    System.out.printf("Here are the results for %s:%n", searchTerm);
    for (int i = 0; i < foundVideos.size(); i++) {
      System.out.printf("%d) %s (%s) %s%n", i+1, foundVideos.get(i).getTitle(), 
      foundVideos.get(i).getVideoId(), foundVideos.get(i).getTags().toString().replace(",", ""));
    }
    System.out.println("Would you like to play any of the above? If yes, specify the number of the video.");
    System.out.println("If your answer is not a valid number, we will assume it's a no.");


    var scanner = new Scanner(System.in);
    String response = scanner.nextLine();
    scanner.close();
    int selectedNumber = 0;
    try {
      selectedNumber = Integer.parseInt(response);
      if(selectedNumber > 0 && selectedNumber <= foundVideos.size()){
        playVideo(foundVideos.get(selectedNumber-1).getVideoId());
      }
    } catch (NumberFormatException e) {
      
    }
  }

  private List<Video> sortVideosInList(List<Video> videos) {
    Video tempVideo;
    for (int i = 0; i < videos.size(); i++) {
      for (int j = i+1; j < videos.size(); j++) {
        if(videos.get(i).getTitle().compareTo(videos.get(j).getTitle()) > 0) {
          tempVideo = videos.get(i);
          videos.set(i, videos.get(j));
          videos.set(j, tempVideo);
        }
      }
    }
    return videos;
  }

  private String cleanUpPlaylistName(String playlistName) {
    return playlistName.replaceAll("[^A-Za-z0-9]+", " ").trim().replaceAll(" ", "_");
  }
  
  private String cleanUpPlaylistName(String playlistName, Boolean forId) {
    return cleanUpPlaylistName(playlistName).toLowerCase();
  }
  
  private String cleanUpSearchTerm(String searchTerm) {
    return cleanUpPlaylistName(searchTerm);
  }

  private List<Video> getUnflaggedVideos(){
    List<Video> videos = new ArrayList<Video>();
    for (int i = 0; i < videoLibrary.getVideos().size(); i++) {
      Video video = videoLibrary.getVideos().get(i);
      if(!video.getFlagged()) videos.add(video);
    }

    return videos;
  }
}