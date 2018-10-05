package model.imageSystem;

import model.tagSystem.Tag;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Image extends File implements Serializable {
  /** The Tags of this image */
  private ArrayList<Tag> tags = new ArrayList<>();

  /** All sets of Tags the image ever had. */
  private ArrayList<ArrayList<Tag>> tagHistory = new ArrayList<>();

  /** The name of this image. path, tags and extension excluded. */
  private String originalName;

  /** The extension of the image */
  private final String extension;

  /** All names the image ever used. */
  private ArrayList<String> nameHistory = new ArrayList<>();

  /**
   * Determine whether tags of the image should be shown. If true, then the string form of the image
   * has tags. If false, only origin name will be shown.
   */
  private static boolean isTagShowing = true;


  /**
   * Returns the name history of the image.
   *
   * @return The name history of the image
   */
  public ArrayList<String> getNameHistory() {
    return nameHistory;
  }

  /**
   * Create a new image with a given path. This constructor is used only if this is a new image to
   * photoManager system. Constructor can identify tags (Maybe added using OS) in the image by
   * checking its name, and store them in tags variable Constructor can also identify its origin
   * name with tags, path and extension excluded.
   *
   * @param path the given path of this new image
   */
  public Image(String path) {
    super(path);
    String[] nameAndExtension = this.getName().split("\\.(?=[^.]+$)");
    String resultName = nameAndExtension[0];
    this.extension = nameAndExtension[1];
    if (resultName.contains(" @")) {

      String[] nameAndTags = resultName.split(" @");
      ArrayList<Tag> tags = new ArrayList<>();
      for (int i = 1; i < nameAndTags.length; i++) {
        Tag tag = new Tag(nameAndTags[i]);
        tags.add(tag);
      }
      this.tags = tags;
      resultName = resultName.split(" @")[0];

      // check if this is loaded from hard drive
      if (this.tagHistory.isEmpty()){
        this.updateTagHistory();
      }
    }
    this.originalName = resultName;
    this.updateNameHistory();
  }

  // A constructor to have an image with all information configured.(Polymorphism)
  /**
   * Create an image with all information provided. This constructor is used only when renaming an
   * image.
   *
   * @param path the given path of this new image
   * @param tags the Tags on this image
   * @param tagHistory all set of Tags this image has had
   * @param originalName the original name of this image
   * @param extension the extension of the image
   * @param nameHistory the history of the image's name
   */
  Image(
      String path,
      ArrayList<Tag> tags,
      ArrayList<ArrayList<Tag>> tagHistory,
      String originalName,
      String extension,
      ArrayList<String> nameHistory) {
    super(path);
    this.tags = tags;
    this.tagHistory = tagHistory;
    this.originalName = originalName;
    this.extension = extension;
    this.nameHistory = nameHistory;
  }

  /**
   * Update the tagHistory of this image. This method is called when add, change or remove tags in
   * this image.
   */
  void updateTagHistory() {
    ArrayList<Tag> currentTags = new ArrayList<>();
    currentTags.addAll(this.tags);
    this.tagHistory.add(currentTags);
  }

  /**
   * Update the nameHistory of this image. This method is called when changing the name of this
   * image.
   */
  void updateNameHistory() {
    if (!this.nameHistory.contains(this.originalName)) {
      this.nameHistory.add(this.originalName);
    }
  }

  public ArrayList<ArrayList<Tag>> getTagHistory() {
    return this.tagHistory;
  }

  public ArrayList<Tag> getTags() {
    return tags;
  }

  String getOriginalName() {
    return originalName;
  }

  void setTags(ArrayList<Tag> tags) {
    this.tags = tags;
  }
  /**
   * Adds the target tag to this image.
   *
   * @param tag the tag added to the image
   */
  void addTag(Tag tag) {
    this.tags.add(tag);
  }

  /**
   * Delete the target tag in this image. The tag is only removed in this image, but not other
   * images or tag manager.
   *
   * @param tag the tag should be removed
   */
  void removeTag(Tag tag) {
    this.tags.remove(tag);
  }

  /** Changes the string representation of images */
  static void tagSwitch() {
    isTagShowing = !isTagShowing;
  }

  void setOriginalName(String newName) {
    this.originalName = newName;
  }

  /**
   * Gets the string representation of this image. The string representation relies on isTagShowing
   * variable.
   *
   * @return the string representation of this image
   */
  @Override
  public String toString() {
    if (!isTagShowing) {
      return this.originalName + "." + this.extension;
    } else {
      return this.originalName + this.tagsToString() + "." + this.extension;
    }
  }

  /**
   * Convert the ArrayList of tags to String
   *
   * @return a String representation of Tags in a specified format.
   */
  // A helper function to convert ArrayList of tags to String of all tags in specified format.
  String tagsToString() {
    StringBuilder resultTags = new StringBuilder();
    for (Tag tag : this.tags) {
      resultTags.append(" @");
      resultTags.append(tag.getTagName());
    }
    return resultTags.toString();
  }

  String getExtension() {
    return extension;
  }

  public void setTagHistory(ArrayList<ArrayList<Tag>> tagHistory) {
    this.tagHistory = tagHistory;
  }

  public void setNameHistory(ArrayList<String> nameHistory) {
    this.nameHistory = nameHistory;
  }
}
