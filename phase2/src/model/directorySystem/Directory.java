package model.directorySystem;

import model.imageSystem.Image;

import java.io.File;
import java.util.ArrayList;

/** A class that manage one directory. We can check and operate its subdirectories images */
public class Directory extends File {

  // A list of all images in the directory
  private ArrayList<Image> images;
  // A list of all subdirectories in the directory
  private ArrayList<Directory> subDirectories;

  /**
   * Construct a new Directory and its subdirectories with the corresponding path. Directory is null
   * when the path is invalid.
   *
   * @param path the directory's path
   */
  public Directory(String path) {
    super(path);
    if (this.exists()) {
      subDirectories = new ArrayList<>();
      images = new ArrayList<>();
      File[] files = this.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            Directory dir = new Directory(file.getPath());
            this.subDirectories.add(dir);
          } else if (isImage(file)) {
            this.images.add(new Image(file.getPath()));
          }
        }
      }
    }
  }

  public ArrayList<Directory> getSubDirectories() {
    return subDirectories;
  }

  /**
   * Returns all images in the directory and its subdirectories.
   *
   * @return return the array list of all images under the directory
   */
  public ArrayList<Image> findUnderDirectory() {
    ArrayList<Image> result = new ArrayList<>();
    result.addAll(this.images);
    for (Directory sub : this.subDirectories) {
      // Recursively call to get all images.
      result.addAll(sub.findUnderDirectory());
    }
    return result;
  }

  /**
   * Returns all images in the directory. Images in subdirectories are not included.
   *
   * @return the array list of all images in the directory
   */
  public ArrayList<Image> findInDirectory() {
    return this.images;
  }

  /**
   * Determine whether a file is an model.imageSystem by checking the extension. Images without
   * extension are ignored.
   *
   * @param file The checked file
   * @return whether the file is an model.imageSystem
   */
  private static boolean isImage(File file) {
    try {
      String extension = file.getPath().split("\\.(?=[^.]+$)")[1];
      String[] extensionList = {"jpg", "eng", "gif", "jpeg", "bmp", "png"};
      for (String extensionType : extensionList) {
        if (extension.equals(extensionType)) {
          return true;
        }
      }
      // The file does not have extension.
    } catch (ArrayIndexOutOfBoundsException a) {
      return false;
    }
    return false;
  }
}
