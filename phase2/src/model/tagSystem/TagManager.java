package model.tagSystem;

import model.imageSystem.Image;

import java.io.*;
import java.util.ArrayList;

public class TagManager {

  /** An ArrayList that contains all the tags that the user can choose from. */
  private ArrayList<Tag> availableTags = new ArrayList<>();

  public ArrayList<Tag> getAvailableTags() {
    return this.availableTags;
  }

  /**
   * Adds a tag to the available tags but not adds it to any image. The method is called when only
   * creating a tag for future use. If the tag has already existed, then do nothing.
   *
   * @param name The name of the tag that is created for future use.
   */
  public void createTag(String name) {
    Tag tag = new Tag(name);
    if (!this.availableTags.contains(tag)) {
      this.availableTags.add(tag);
    }
  }

  /**
   * Add a tag to availableTags when the user tags a image, it will create a new tag if the tag is
   * used for the first time.
   *
   * @param tag The tag the user wants to use.
   * @param image The image add the tag to.
   */
  public void addImageToTag(Tag tag, Image image) {
    if (!this.availableTags.contains(tag)) {
      this.availableTags.add(tag);
      if (!tag.getAllImages().contains(image)) {
        tag.getAllImages().add(image);
      }
    } else {
      int index = this.availableTags.indexOf(tag);
      Tag origin = this.availableTags.get(index);
      if (!origin.getAllImages().contains(image)) {
        origin.getAllImages().add(image);
      }
    }
  }

  /**
   * Reduce the usage of the Tag when it is removed from an image.
   *
   * @param tag The tag that is being removed from the image.
   * @param image The image the tag is removed from.
   */
  public void removeImageFromTag(Tag tag, Image image) {
    if (this.availableTags.contains(tag)) {
      int index = this.availableTags.indexOf(tag);
      Tag origin = this.availableTags.get(index);
      origin.removeFromImage(image);
    }
  }

  /**
   * Removing the tag from the ArrayList available tags after it is not tagged onto any Images.
   *
   * @param tag The tag that is being removed from availableTags.
   */
  public void clearTag(Tag tag) {
    this.availableTags.remove(tag);
  }

  /**
   * Un-serializes the serialization file and retrieve it's info into the HashMap availableTags.
   *
   * @param path The path of the serialization file.
   * @throws ClassNotFoundException If the target class to be un-serialized is not found.
   * @throws IOException If the target file does not exist.
   */
  @SuppressWarnings("unchecked")
  public void readFromFile(String path) throws ClassNotFoundException, IOException {
    InputStream file = new FileInputStream(path);
    InputStream buffer = new BufferedInputStream(file);
    ObjectInput input = new ObjectInputStream(buffer);

    this.availableTags = (ArrayList<Tag>) input.readObject();
    input.close();
  }

  /**
   * Serializes the HashMap availableTags into a serialization file to store.
   *
   * @param path The path of the serialization file.
   * @throws IOException When the serialization file is not found or corrupted.
   */
  public void saveToFile(String path) throws IOException {
    OutputStream file = new FileOutputStream(path);
    OutputStream buffer = new BufferedOutputStream(file);
    ObjectOutput output = new ObjectOutputStream(buffer);

    output.writeObject(this.availableTags);
    output.close();
  }
}
