package model.imageSystem;

import model.tagSystem.Tag;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Image extends File implements Serializable {
  /** The Tags of this model.imageSystem */
  private ArrayList<Tag> tags = new ArrayList<>();

  /** All sets of Tags this model.imageSystem ever had. */
  private ArrayList<ArrayList<Tag>> tagHistory = new ArrayList<>();

  /** the name of this model.imageSystem, path and extension excluded. */
  private String originalName;

  /**
   * Create a new model.imageSystem with a given path. This constructor is used only if this is a
   * new model.imageSystem to photoManager system.
   *
   * @param path the given path of this new model.imageSystem
   */
  public Image(String path) {
    super(path);
    this.originalName = this.getName().split("\\.(?=[^.]+$)")[0];
  }

  // A constructor to have an model.imageSystem with all information configured.
  /**
   * Create an model.imageSystem with all information provided. This constructor is used only when
   * renaming an model.imageSystem.
   *
   * @param path the given path of this new model.imageSystem
   * @param tags the Tags on this model.imageSystem
   * @param tagHistory all set of Tags this model.imageSystem has had
   * @param originalName the original name of this model.imageSystem
   */
  public Image(
          String path, ArrayList<Tag> tags, ArrayList<ArrayList<Tag>> tagHistory, String originalName) {
    super(path);
    this.tags = tags;
    this.tagHistory = tagHistory;
    this.originalName = originalName;
  }

  /** Update the tagHistory of this model.imageSystem. */
  void updateHistory() {
    ArrayList<Tag> temp = new ArrayList<>();
    temp.addAll(this.tags);
    this.tagHistory.add(temp);
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

  void setOriginalName(String newName) {
    this.originalName = newName;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Image && this.originalName.equals(((Image) obj).originalName);
  }
}
