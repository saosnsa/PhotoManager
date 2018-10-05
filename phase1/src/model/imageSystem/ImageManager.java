package model.imageSystem;

import model.InvalidRenameException;
import model.directorySystem.Directory;
import model.tagSystem.Tag;
import model.tagSystem.TagManager;

import java.io.*;
import java.util.ArrayList;

public class ImageManager {
  /** The model.imageSystem stored in this ImageManager */
  private ArrayList<Image> images = new ArrayList<>();

  private final String imageFile = "./ImageSer";
  /** Construct a new ImageManager. */
  public ImageManager() throws Exception {
    File file = new File(imageFile);
    if (file.exists()) {
      readFromFile(imageFile);
    } else if (!file.createNewFile()) {
      throw new Exception("Serialization failed");
    }
  }

  public ArrayList<Image> getImages() {
    return this.images;
  }

  /**
   * Add a single new Image
   *
   * @param image the model.imageSystem to add
   */
  public void addImage(Image image) {
    if (!this.images.contains(image)) {
      this.images.add(image);
    }
  }

  /**
   * Rename an model.imageSystem and change its path in hard drive. Can be used to move Images.
   *
   * @param image the model.imageSystem to be moved
   * @param newPath the targeted path
   * @throws InvalidRenameException if the renaming failed
   */
  private Image renameImagePath(Image image, String newPath) throws InvalidRenameException {
    if (this.images.contains(image)) {
      // Pass the originalName and tagHistory to the new model.imageSystem
      Image newImage =
          new Image(newPath, image.getTags(), image.getTagHistory(), image.getOriginalName());

      if (image.renameTo(newImage)) {
        this.images.set(this.images.indexOf(image), newImage);
        for (Tag tag : newImage.getTags()) {

          // update the reference in tag
          tag.replaceImage(image, newImage);

          // update the reference in directory
          // directory.changeImage(image, newImage);
        }
        return newImage;
      } else {
        throw new InvalidRenameException();
      }
    }
      return image;
  }

  public void removeImage(String imageName, TagManager tm) {
    for (Image i : this.images) {
      if (i.getPath().equals(imageName)) {
        this.images.remove(i);
        for (Tag tag : i.getTags()) {
          tm.deleteFromAvailableTags(tag);
          tag.removeFromImage(i);
        }
        break;
      }
    }
  }

  /**
   * Add a new Tag to an model.imageSystem.
   *
   * @param tagName the name of the new Tag to be created
   * @param image the model.imageSystem
   * @param tm the tagManager in this model
   * @throws InvalidRenameException if adding this tag results in an invalid renaming
   */
  public Image addTag(String tagName, Image image, TagManager tm) throws InvalidRenameException {
    return this.addTag(new Tag(tagName), image, tm);
  }

  /**
   * Add a Tag to an model.imageSystem.
   *
   * @param tag the Tag to be added
   * @param image the model.imageSystem
   * @param tm the tagManager in this model
   * @throws InvalidRenameException if adding this tag results in an invalid renaming
   */
  public Image addTag(Tag tag, Image image, TagManager tm) throws InvalidRenameException {
    if (!image.getTags().contains(tag)) {
      image.getTags().add(tag);
      String path = this.pathAndExtension(image.getPath())[0];
      String extension = "." + this.pathAndExtension(image.getPath())[1];
      image.updateHistory();

      // notify tag
      tag.addToImage(image);

      // notify tagManager
      tm.addToAvailableTags(tag);
      String newPath = path + " @" + tag.getTagName() + extension;
      return this.renameImagePath(image, newPath);
    }
    return image;
  }

  public Image changeImageName(Image image, String newName) throws InvalidRenameException {
    image.setOriginalName(newName);
    String extension = "." + pathAndExtension(image.getPath())[1];
    String resultPath =
        image.getParent()
            + File.separator
            + image.getOriginalName()
            + tagsToString(image.getTags())
            + extension;
    return this.renameImagePath(image, resultPath);
  }

  /**
   * Delete a Tag of a selected model.imageSystem.
   *
   * @param tag the Tag to be deleted
   * @param image the model.imageSystem selected
   * @param tm the tagManager in this model
   * @throws InvalidRenameException if deleting this tag results in an invalid renaming
   */
  public Image deleteTag(Tag tag, Image image, TagManager tm) throws InvalidRenameException {
    if (image.getTags().contains(tag)) {
      image.getTags().remove(tag);
      image.updateHistory();

      // notify tag
      tag.removeFromImage(image);

      // notify tagManager
      tm.deleteFromAvailableTags(tag);
      String newPath = image.getPath().replaceAll(" @" + tag.getTagName(), "");
      return this.renameImagePath(image, image.getPath().replaceAll(" @" + tag.getTagName(), ""));
    }
    return image;
  }

  /**
   * Replace the model.imageSystem's tags with an ArrayList of Tags and rename the
   * model.imageSystem.
   *
   * @param image the model.imageSystem to be rename.
   * @param tags the set of tags selected
   * @param directory the directory stores Image
   * @throws InvalidRenameException if replacing the tags results in an invalid renaming
   */
  public Image goToPreviousTags(Image image, ArrayList<Tag> tags, Directory directory)
      throws InvalidRenameException {
    image.setTags(tags);
    String extension = "." + pathAndExtension(image.getPath())[1];
    String resultPath =
        image.getParent()
            + File.separator
            + image.getOriginalName()
            + tagsToString(tags)
            + extension;

    image.updateHistory();
    return this.renameImagePath(image, resultPath);
  }

  /**
   * Move an model.imageSystem to a new directory in hard drive.
   *
   * @param image the model.imageSystem to be moved
   * @param newDirectory the targeted directory to be moved to
   * @throws InvalidRenameException if moving this model.imageSystem results in an invalid renaming
   */
  public Image moveImage(Image image, String newDirectory) throws InvalidRenameException {
    if (!newDirectory.substring(newDirectory.length() - 1).equals(File.separator))
      newDirectory = newDirectory + File.separator;
    String result = newDirectory + image.getName();
    return this.renameImagePath(image, result);
  }

  /**
   * Convert an ArrayList of Tags to String
   *
   * @param tags the ArrayList of Tags to be converted
   * @return a String representation of Tags in a specified format.
   */
  // A helper function to convert ArrayList of tags to String of all tags in specified format.
  private String tagsToString(ArrayList<Tag> tags) {
    StringBuilder resultTags = new StringBuilder();
    for (Tag tag : tags) {
      resultTags.append(" @");
      resultTags.append(tag.getTagName());
    }
    return resultTags.toString();
  }

  /**
   * Helps separating path and extension of a given path.
   *
   * @param totalPath the given path
   * @return a String[] where index 0 is the path without extension and index 1 is the extension.
   */
  // A helper to separate path and extension.
  private String[] pathAndExtension(String totalPath) {
    return totalPath.split("\\.(?=[^\\.]+$)");
  }

  private void readFromFile(String path) throws ClassNotFoundException, IOException {
    InputStream file = new FileInputStream(path);
    InputStream buffer = new BufferedInputStream(file);
    ObjectInput input = new ObjectInputStream(buffer);

    this.images = (ArrayList<Image>) input.readObject();
    input.close();
  }

  public void saveToFile(String path) throws IOException {
    OutputStream file = new FileOutputStream(path);
    OutputStream buffer = new BufferedOutputStream(file);
    ObjectOutput output = new ObjectOutputStream(buffer);

    output.writeObject(this.images);
    output.close();
  }

}
