package model.tagSystem;
import model.imageSystem.Image;

import java.io.Serializable;
import java.util.ArrayList;

public class Tag implements Serializable{

    /**
     * The name of this Tag.
     */
    private String tagName;

    /**
     * The images that uses this Tag.
     */
    private ArrayList<Image> allImages;

    /**
     *  Create a new Tag to tag on the model.imageSystem.
     * @param tagName The name of this Tag specified by the user.
     */
    public Tag(String tagName) {
        this.tagName = tagName;
        this.allImages = new ArrayList<>();
    }

    public String getTagName() {
        return this.tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public ArrayList<Image> getAllImages() {
        return this.allImages;
    }

    /**
     * When user tag a model.imageSystem with this Tag, this will add the
     * model.imageSystem into the allImage of this Tag.
     * @param i The model.imageSystem that this Tag is tagged to.
     */
    public void addToImage(Image i) {
        if (!this.allImages.contains(i)) {
            this.allImages.add(i);
        }
    }

    /**
     *  If the user want to remove this Tag from a model.imageSystem, this will remove
     *  the model.imageSystem in allImages.
     * @param i The model.imageSystem this Tag is being removed from.
     */
    public void removeFromImage(Image i) {
        if (this.allImages.contains(i)) {
            this.allImages.remove(i);
        }
    }

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
     * Replace the model.imageSystem with the old path name that is associated with this tag
     * with a same model.imageSystem but with a renamed path name.
     * @param oldImage The model.imageSystem that was originally in allImage.
     * @param newImage The model.imageSystem that has a newer path name but same as oldImage.
     */
    public void replaceImage(Image oldImage, Image newImage) {
        if (this.allImages.contains(oldImage)) {
            int ind = this.allImages.indexOf(oldImage);
            this.allImages.set(ind, newImage);
        }
    }
}
