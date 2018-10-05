package model;

import model.directorySystem.Directory;
import model.imageSafeSystem.SafeManager;
import model.imageSystem.Image;
import model.imageSystem.ImageManager;
import model.tagSystem.Tag;
import model.tagSystem.TagManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/** The model that manage all back end functions. */
public class Model {
  public TagManager tm;
  public ImageManager im;
  public SafeManager sm;
  private final String tagFilePath = "./TagSer";
  private final String imageFilePath = "./ImageSer";
  private final String passWordPath = "./PassWordSer";

  /** Initialize the model. */
  public Model() {
    tm = new TagManager();
    im = new ImageManager();
    sm = new SafeManager();
  }

  /**
   * Remove the target image from this model.
   *
   * @param image the image to be removed.
   */
  public void removeImage(Image image) {
    if (im.getImages().contains(image)) {
      im.removeImage(image);
      for (Tag tag : image.getTags()) {
        this.tm.removeImageFromTag(tag, image);
      }
    }
  }

  /**
   * Add multiple tags to the target image.
   *
   * @param tags multiple tags that should be added
   * @param image image that tags add to
   * @return new image with tags added. It has all same features as the old one but added tags.
   * @throws InvalidRenameException throws when invalid renaming occurs.
   */
  public Image addTags(ArrayList<Tag> tags, Image image) throws InvalidRenameException {
    if (!tags.isEmpty()) {
      ArrayList<Tag> newTags = new ArrayList<>();

      newTags.addAll(image.getTags());
      for (Tag tag : tags) {
        if (!newTags.contains(tag)) {
          newTags.add(tag);
        }
      }

      return changeTags(image, newTags);
    }
    return image;
  }

  /**
   * Remove multiple tags from the target image. This method is better when multiple tags should be
   * removed because it will not ruin the history. We don't want to just call deleteTags multiple
   *
   * @param tags multiple tags to be removed.
   * @param image the target image.
   * @return new image with tags removed. It has all same features as the old one but removed some
   *     tags.
   * @throws InvalidRenameException throws when invalid renaming occurs.
   */
  public Image removeTags(ArrayList<Tag> tags, Image image) throws InvalidRenameException {
    ArrayList<Tag> newTags = new ArrayList<>();

    newTags.addAll(image.getTags());
    newTags.removeAll(tags);

    return changeTags(image, newTags);
  }

  /**
   * Add a new Tag to an model.imageSystem.
   *
   * @param tagName the name of the new Tag to be created
   * @param image the model.imageSystem
   * @throws InvalidRenameException throws if adding this tag results in an invalid renaming
   */
  public Image addTag(String tagName, Image image) throws InvalidRenameException {
    return this.addTag(new Tag(tagName), image);
  }

  /**
   * @param tag the tag to be removed.
   * @param image the target image.
   * @return new image with tag removed. It has all same features as the old one but removed the
   *     tag.
   * @throws InvalidRenameException throws when invalid renaming occurs.
   */
  public Image removeTag(Tag tag, Image image) throws InvalidRenameException {

    // notify tagManager
    tm.removeImageFromTag(tag, image);

    return im.removeTag(tag, image);
  }

  /**
   * @param tag the tag to be added
   * @param image the target image
   * @return new image with tag added. It has all same features as the old one but add the tag.
   * @throws InvalidRenameException throws when invalid renaming occurs.
   */
  public Image addTag(Tag tag, Image image) throws InvalidRenameException {
    if (!image.getTags().contains(tag)) {

      // notify tagManager
      tm.addImageToTag(tag, image);
      return im.addTag(tag, image);
    }
    return image;
  }

  /**
   * Set the image with target list tags.
   *
   * @param image the target image.
   * @param tags tags set to the target image.
   * @return new image with tag changed. It has all same features as the old one but change tags.
   * @throws InvalidRenameException throws when invalid renaming occurs.
   */
  public Image changeTags(Image image, ArrayList<Tag> tags) throws InvalidRenameException {
    for (Tag tag : image.getTags()) {

      tm.removeImageFromTag(tag, image);
    }

    for (Tag tag : tags) {
      tag.addToImage(image);
      tm.addImageToTag(tag, image);
    }

    return im.changeTags(image, tags);
  }

  /**
   * Deserialize model.
   *
   * @throws Exception throws when deserialize failed.
   */
  public void load() throws Exception {
    File tagsFile = new File(tagFilePath);
    File imagesFile = new File(imageFilePath);
    File passWordFile = new File(passWordPath);
    if (tagsFile.exists()) {
      tm.readFromFile(tagFilePath);
    } else if (!tagsFile.createNewFile()) {
      throw new Exception("tagManager deserialization failed");
    }

    if (imagesFile.exists()) {
      im.readFromFile(imageFilePath);
    } else if (!imagesFile.createNewFile()) {
      throw new Exception("imageManager deserialization failed");
    }

    if (passWordFile.exists()) {
      sm.readFromFile(passWordPath);
    } else if (!passWordFile.createNewFile()) {
      throw new Exception("secureManager deserialization failed");
    }
  }

  /**
   * Serialize model.
   *
   * @throws IOException occurs when target file does not exist.
   */
  public void save() throws IOException {
    im.saveToFile(imageFilePath);
    tm.saveToFile(tagFilePath);
    sm.saveToFile(passWordPath);
  }

  /**
   * Deletes Tag from the list of tags the user can choose from. Also removes all the usage of the
   * tag if the user choose to remove the tag.
   *
   * @param tag The tag that is being removed from all of the images that has it.
   * @throws InvalidRenameException throws when invalid renaming occurs.
   */
  public void deleteAllUsage(Tag tag) throws InvalidRenameException {
    if (tm.getAvailableTags().size() != 0) {
      if (tm.getAvailableTags().contains(tag)) {
        int index = tm.getAvailableTags().indexOf(tag);
        Tag origin = tm.getAvailableTags().get(index);
        if (origin.getAllImages().size() != 0) {
          ArrayList<Image> allImages = new ArrayList<>();
          allImages.addAll(origin.getAllImages());
          for (Image image : allImages) {
            this.removeTag(tag, image);
          }
        }
        tm.clearTag(tag);
      }
    }
  }

  /**
   * Add a image to image manager. If the image has some tags, tags will be automatically added to
   * tag manager.
   *
   * @param image the target image
   */
  public void addImage(Image image) {
    this.im.addImage(image);
    for (Tag tag : image.getTags()) {
      if (!tm.getAvailableTags().contains(tag)) {
        tm.createTag(tag.getTagName());
      }
      tm.addImageToTag(tm.getAvailableTags().get(tm.getAvailableTags().indexOf(tag)), image);
    }
  }

  /**
   * Return a list of all images in the root directory. If a image has tags, it will be
   * automatically added to image manager and tags will be added to tag manager.
   *
   * @param rootDirectory the target directory
   * @return all images in the root directory
   */
  public ArrayList<Image> browseIn(Directory rootDirectory) {
    ArrayList<Image> result = rootDirectory.findInDirectory();
    for (Image image : result) {
      if (image.getTags().size() != 0) this.addImage(image);
    }
    return result;
  }

  /**
   * Return a list of all images under the root directory.(Images in its subdirectories are
   * included) If a image has tags, it will be automatically added to image manager and tags will be
   * added to tag manager.
   *
   * @param rootDirectory the target directory
   * @return all images under the root directory
   */
  public ArrayList<Image> browseUnder(Directory rootDirectory) {
    ArrayList<Image> result = rootDirectory.findInDirectory();
    for (Directory subDir : rootDirectory.getSubDirectories()) {
      result.addAll(subDir.findUnderDirectory());
    }
    for (Image image : result) {
      if (image.getTags().size() != 0) this.addImage(image);
    }
    return result;
  }
}
