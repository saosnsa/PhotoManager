package photoManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Model;
import model.imageSystem.ImageManager;
import model.tagSystem.TagManager;

import java.io.IOException;

public class Main extends Application {
  private Controller controller;
  private Model model;
  private final String imageFile = "./ImageSer";
  private final String tagFile = "./TagSer";

  @Override
  public void start(Stage primaryStage) throws Exception {
    ImageManager im = new ImageManager();
    TagManager tm = new TagManager();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("Photomanager.fxml"));
    Parent root = (Parent) loader.load();
    this.controller = loader.getController();
    model = new Model();
    model.setIm(im);
    model.setTm(tm);
    controller.setModel(model);
    primaryStage.setTitle("Photo manager");
    primaryStage.setScene(new Scene(root, 1280, 900));
    primaryStage.show();
  }

  @Override
  public void stop() throws IOException {
    controller.getImageManager().saveToFile(imageFile);
    controller.getTagManager().saveToFile(tagFile);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
