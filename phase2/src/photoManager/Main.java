package photoManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Model;
import model.imageSafeSystem.SafeManager;
import model.imageSystem.ImageManager;
import model.tagSystem.TagManager;

import java.io.IOException;

public class Main extends Application {
  private Controller controller;
  private Model model;

  @Override
  public void start(Stage primaryStage) throws Exception {
    SafeManager sm = new SafeManager();
    ImageManager im = new ImageManager();
    TagManager tm = new TagManager();
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("Photomanager.fxml"));
//  Code adapted from https://stackoverflow.com/questions/19895951/what-is-the-main-way-to-connect-a-view-and-a-model-in-javafx
    loader.setControllerFactory(new Callback<Class<?>, Object>() {
      @Override
      public Object call(Class<?> aClass) {
        return new Controller(model);
      }
    });
    Parent root = loader.load();
    this.controller = loader.getController();
    model = new Model();
    model.load();
    controller.setModel(model);
    primaryStage.setTitle("Photo manager");
    primaryStage.setScene(new Scene(root, 1280, 900));
    primaryStage.show();
  }

  @Override
  public void stop() throws IOException {
    model.save();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
