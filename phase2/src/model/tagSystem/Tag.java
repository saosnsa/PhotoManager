package model.tagSystem;

import model.imageSystem.Image;

import java.io.Serializable;
import java.util.ArrayList;

public class Tag implements Serializable {

  /** The name of this Tag. */
  private String tagName;

  /** The images that uses this Tag. */
  private ArrayList<Image> allImages;

  /**
   * Create a new Tag to tag on the model.imageSystem.
   *
   * @param tagName The name of this Tag specified by the user.
   */
  public Tag(String tagName) {
    this.tagName = tagName;
    this.allImages = new ArrayList<>();
  }

  public String getTagName() {
    return this.tagName;
  }

  public ArrayList<Image> getAllImages() {
    return this.allImages;
  }

  /**
   * When user tag a image with this Tag, this will add the image into the allImage of this Tag.
   *
   * @param image The image that this Tag is tagged to.
   */
  public void addToImage(Image image) {
    if (!this.allImages.contains(image)) {
      this.allImages.add(image);
    }
  }

  /**
   * If the user want to remove this Tag from a image, this will remove the image in allImages.
   *
   * @param image The image this Tag is being removed from.
   */
  void removeFromImage(Image image) {
    if (this.allImages.contains(image)) {
      this.allImages.remove(image);
    }
  }

  /**
   * Determine whether an obj and a tag is the same. Two tags are same only if they has the same tag
   * name. allImages variable is ignored during comparing.
   *
   * @param obj the object to be compared
   * @return whether the object is the same as this tag.
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Tag && ((Tag) obj).tagName.equals(this.tagName);
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + this.tagName.hashCode();
    return result;
  }

  /**
   * Replace the image with the old path name that is associated with this tag with a same image but
   * with a renamed path name.
   *
   * @param oldImage The image that was originally in allImage.
   * @param newImage The image that has a new path name, but all other features are the same.
   */
  public void replaceImage(Image oldImage, Image newImage) {
    if (this.allImages.contains(oldImage)) {
      int ind = this.allImages.indexOf(oldImage);
      this.allImages.set(ind, newImage);
    }
  }

  /**
   * Shows the string representation of the tag.
   *
   * @return the string representation of the tag.
   */
  @Override
  public String toString() {
    return this.tagName;
  }
}
