package model.tagSystem;

import model.InvalidRenameException;
import model.imageSystem.Image;
import model.imageSystem.ImageManager;

import java.io.*;
import java.util.HashMap;

public class TagManager {

  /** A HashMap that contains all the tags that the user can choose from. */
  private HashMap<Tag, Integer> availableTags;

  private final String tagFile = "./TagSer";

  /**
   * Creates a new TagManager to manage all the tags the user used. Also creates a file for
   * serialization if the file specified does not exist.
   */
  public TagManager() throws IOException, ClassNotFoundException {
    this.availableTags = new HashMap<>();
    File file = new File(tagFile);
    if (file.exists()) {
      readFromFile(tagFile);
    } else {
      file.createNewFile();
    }
  }

  public HashMap<Tag, Integer> getAvailableTags() {
    return this.availableTags;
  }

  /**
   * Add a tag to availableTags when the user tags a model.imageSystem, it will create a new one if
   * the tag is used for the first time.
   *
   * @param t The tag the user wants to use.
   */
  public void addToAvailableTags(Tag t) {
    if (this.availableTags.containsKey(t)) {
      this.availableTags.put(t, this.availableTags.get(t) + 1);
    } else {
      this.availableTags.put(t, 1);
    }
  }

  /**
   * Reduce the usage of the Tag when it is removed from an model.imageSystem by the user.
   *
   * @param t The Tag that is being removed from the model.imageSystem.
   */
  public void deleteFromAvailableTags(Tag t) {
    if (this.availableTags.containsKey(t)) {
      this.availableTags.put(t, this.availableTags.get(t) - 1);
    }
  }

  /**
   * Deletes Tag from the list of tags the user can choose from. Also removes all the usage of the
   * tag if the user choose to remove the tag.
   *
   * @param t Tag the user wants to remove.
   * @param im ImageManager to delete the tag from model.imageSystem.
   * @throws InvalidRenameException If deleting the tag will result in InvalidRenameException
   */
  public void deleteAllUsage(Tag t, ImageManager im) throws InvalidRenameException {
    if (this.availableTags.keySet().size() != 0) {
      if (this.availableTags.get(t) == 0) {
        this.availableTags.remove(t);
      } else {
        for (Tag tag : this.availableTags.keySet()) {
          if (tag.getTagName().equals(t.getTagName())) {
            for (Image i : tag.getAllImages()) {
              im.deleteTag(t, i, this);
            }
            break;
          }
        }
        this.availableTags.remove(t);
      }
    }
  }

  /**
   * Un-serializes the serialization file and retrieve it's info into the HashMap availableTags.
   *
   * @param path The path of the serialization file.
   * @throws ClassNotFoundException If the target class to be un-serialized is not found.
   * @throws IOException If the target file does not exist.
   */
  private void readFromFile(String path) throws ClassNotFoundException, IOException {
    InputStream file = new FileInputStream(path);
    InputStream buffer = new BufferedInputStream(file);
    ObjectInput input = new ObjectInputStream(buffer);

    this.availableTags = (HashMap<Tag, Integer>) input.readObject();
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
