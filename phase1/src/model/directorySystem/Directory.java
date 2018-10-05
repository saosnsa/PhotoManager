package model.directorySystem;

import model.imageSystem.Image;
import model.imageSystem.ImageManager;
import model.tagSystem.Tag;
import model.tagSystem.TagManager;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/** A class that manage one directory. We can check and operate its subdirectories images */
public class Directory extends File {

  // A list of all images in the directory
  private ArrayList<Image> images;
  // A list of all subdirectories
  private ArrayList<Directory> subDirectories;

  /**
   * Construct a new Directory and its subdirectories with the corresponding path
   *
   * @param path the path this/ directory at.
   */
  public Directory(String path) throws IOException {
    super(path);
    if (!this.exists()) {
      throw new NullPointerException("This directory does not exist");
    } else {
      subDirectories = new ArrayList<>();
      images = new ArrayList<>();
      File[] files = this.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            Directory dir = new Directory(file.getPath());
            this.subDirectories.add(dir);
          } else if (isImage(file)) {
            Image i = new Image(file.getPath());
            this.images.add(i);
          }
        }
      }
    }
  }

  /**
   * Return all images in the directory and its subdirectories.
   *
   * @return return the array list of all images under the directory
   */
  public ArrayList<Image> findUnderDirectory() {
    ArrayList<Image> result = new ArrayList<>();
    result.addAll(this.images);
    for (Directory sub : this.subDirectories) {
      result.addAll(sub.findUnderDirectory());
    }
    return result;
  }

  /**
   * Get all images in the directory
   *
   * @return the array list of all images in the directory
   */
  public ArrayList<Image> findInDirectory() {
    return this.images;
  }

  /**
   * Determine whether a file is an model.imageSystem From:
   * http://blog.csdn.net/top_code/article/details/38977065
   *
   * @param resFile the file to be checked
   * @return whether the file is an model.imageSystem
   */
  private static boolean isImage(File resFile) throws IOException {
    ImageInputStream image = null;
    try {
      image = ImageIO.createImageInputStream(resFile);
      Iterator<ImageReader> iterator = ImageIO.getImageReaders(image);
      if (iterator.hasNext()) {
        return true;
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (image != null) {
        try {
          image.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }

  private Image createImage(File target, TagManager tm, ImageManager im){
    String name = target.getName();
    String originalName = "";
    Boolean findName = false;
    ArrayList<Tag> tags = new ArrayList<>();
    for(int i = 0; i < name.length(); i++){
      if (name.substring(i, i + 1).equals("@")){
        if (!findName){
          originalName = name.substring(0,i);
        }
        findName = true;
        StringBuilder tagName = new StringBuilder();
        int s = i + 1;
        while ( s < name.length() - 1 & ! name.substring(s, s + 1).equals("@") & ! name.substring(s,s + 1).equals(".")){
          tagName.append(a.substring(s, s + 1));
          s++;
        }
        boolean findTag = false;
        for(Tag t: tm.getAvailableTags().keySet()){
          if (t.getTagName().equals(tagName.toString())){
            tags.add(t);
            findTag = true;
          }
        }
        if (!findTag){
          Tag newTag = new Tag(tagName.toString());
          tm.addToAvailableTags(newTag);
          tags.add(newTag);
        }
      }
      Image image = new Image(target.getPath(), tags, new ArrayList<>(), originalName);
    }
    return null;
  }
}