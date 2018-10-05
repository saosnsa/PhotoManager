package model.imageSystem;

import model.InvalidRenameException;
import model.tagSystem.Tag;

import java.io.*;
import java.util.ArrayList;

public class ImageManager {
  /** The images stored in this ImageManager */
  private ArrayList<Image> images = new ArrayList<>();

  public ArrayList<Image> getImages() {
    return this.images;
  }

  /**
   * Add a new Image to this image manager.
   *
   * @param image the target image
   */
  public void addImage(Image image) {
    if (!this.images.contains(image)) {
      this.images.add(image);
    }
  }

  // This method actually do the renaming job in hard drive.
  /**
   * Rename an image and change its path in hard drive. Can be used to move Images.
   *
   * @param image the image to be moved
   * @param newPath the targeted path
   * @throws InvalidRenameException if the renaming failed, throws this exception.
   */
  private Image renameImagePath(Image image, String newPath) throws InvalidRenameException {
    if (this.images.contains(image)) {

      // Pass the originalName and tagHistory to the new image
      Image newImage =
          new Image(
              newPath,
              image.getTags(),
              image.getTagHistory(),
              image.getOriginalName(),
              image.getExtension(),
              image.getNameHistory());

      if (image.renameTo(newImage)) {
        this.images.set(this.images.indexOf(image), newImage);
        for (Tag tag : newImage.getTags()) {

          // update the reference in tags
          tag.replaceImage(image, newImage);
        }
        return newImage;
      } else {
        throw new InvalidRenameException();
      }
    }
    return image;
  }

  /**
   * Remove a image from this image manager
   *
   * @param image the image to be removed
   */
  public void removeImage(Image image) {
    this.images.remove(image);
  }

  /**
   * Add a Tag to an model.imageSystem.
   *
   * @param tag the Tag to be added
   * @param image the image
   * @throws InvalidRenameException if adding this tag results in an invalid renaming.
   */
  public Image addTag(Tag tag, Image image) throws InvalidRenameException {
    if (!image.getTags().contains(tag)) {
      image.addTag(tag);
      String newPath = getImagePath(image);

      // Recover the image if add tag results in file name conflict
      if (new File(newPath).exists()) {
        image.removeTag(tag);
      } else {
        image.updateTagHistory();
        return this.renameImagePath(image, newPath);
      }
    }
    return image;
  }

  /**
   * Rename the target image and returns it. The new image has same information with the old one
   * except name.
   *
   * @param image the image should be renamed
   * @param newName the new name
   * @return the image with new name.
   */
  public Image changeImageName(Image image, String newName) throws InvalidRenameException {

    String tempOriginalName = image.getOriginalName();
    image.setOriginalName(newName);
    String newPath = getImagePath(image);
      // Recover the image if change name results in file name conflict
    if (new File(newPath).exists()) {
      image.setOriginalName(tempOriginalName);
      return image;
    } else {
      image.updateNameHistory();
      return this.renameImagePath(image, newPath);
    }
  }

  /**
   * This getter returns the path of an Image based on its current information but not the absolute path. this is different of
   * getPath() method in File.
   *
   * @param image the target image
   * @return the path of this image based on its current information.
   */
  private String getImagePath(Image image) {
    return image.getParent()
        + File.separator
        + image.getOriginalName()
        + image.tagsToString()
        + "."
        + image.getExtension();
  }

  /**
   * Delete a Tag of a selected image
   *
   * @param tag the tag to be removed
   * @param image the target image
   * @throws InvalidRenameException throws if removing this tag results in an invalid renaming
   */
  public Image removeTag(Tag tag, Image image) throws InvalidRenameException {
    if (image.getTags().contains(tag)) {
      image.removeTag(tag);
      String newPath = getImagePath(image);

        // Recover the image if remove this tag results in file name conflict
      if (new File(newPath).exists()) {
        image.addTag(tag);
      } else {
        return this.renameImagePath(image, newPath);
      }
    }
    return image;
  }

  /**
   * Replace the image's tags with an ArrayList of Tags and rename the image.
   *
   * @param image the image to be rename.
   * @param tags the set of tags selected
   * @throws InvalidRenameException throws if replacing the tags results in an invalid renaming
   */
  public Image changeTags(Image image, ArrayList<Tag> tags) throws InvalidRenameException {
    ArrayList<Tag> newTags = new ArrayList<>();
    ArrayList<Tag> oldTags = new ArrayList<>();
    oldTags.addAll(image.getTags());
    newTags.addAll(tags);

    image.setTags(newTags);
    String newPath = getImagePath(image);
    if (new File(newPath).exists()) {
      image.setTags(oldTags);
      return image;
    }
    image.updateTagHistory();
    return this.renameImagePath(image, newPath);
  }

  /**
   * Move an image to a new directory in hard drive.
   *
   * @param image the image to be moved
   * @param newDirectory the targeted directory to be moved to
   * @throws InvalidRenameException throws if moving this image results in an invalid renaming
   */
  public Image moveImage(Image image, String newDirectory) throws InvalidRenameException {
    if (!newDirectory.substring(newDirectory.length() - 1).equals(File.separator)) {
      newDirectory = newDirectory + File.separator;
      String newPath = newDirectory + image.getName();

        // Recover the image if moving this Image results in file name conflict
      if (!new File(newPath).exists()) {
        return this.renameImagePath(image, newPath);
      }
    }
    return image;
  }

  /** Change the string representation of images. */
  public void tagSwitch() {
    Image.tagSwitch();
  }

  /**
   * Deserialize the image manager.
   *
   * @param path the path of the ser file.
   * @throws ClassNotFoundException throws when this,images does not exist.
   * @throws IOException throws when the ser file does not exist.
   */
  @SuppressWarnings("unchecked")
  public void readFromFile(String path) throws ClassNotFoundException, IOException {
    InputStream file = new FileInputStream(path);
    InputStream buffer = new BufferedInputStream(file);
    ObjectInput input = new ObjectInputStream(buffer);

    this.images = (ArrayList<Image>) input.readObject();
    input.close();
  }

  /**
   * Serialize the image manager.
   *
   * @param path the path of the ser file.
   * @throws IOException throws when the ser file does not exists.
   */
  public void saveToFile(String path) throws IOException {
    OutputStream file = new FileOutputStream(path);
    OutputStream buffer = new BufferedOutputStream(file);
    ObjectOutput output = new ObjectOutputStream(buffer);

    output.writeObject(this.images);
    output.close();
  }
}
