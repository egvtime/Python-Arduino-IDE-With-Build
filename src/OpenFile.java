import UI.Controllers.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.stage.*;

public class OpenFile extends Application {
    private static String fileToOpen = null;

    public static void setFileToOpen(String path) {
        fileToOpen = path;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("UI/FXML/MainUIRemake.fxml")));
        Parent root = loader.load();
        MainUIController ctrl = loader.getController();

        if (fileToOpen != null) {
            ctrl.setFileLocation(fileToOpen);
            ctrl.setTextAreaText((new String(Files.readAllBytes(Paths.get(fileToOpen)))).replaceAll("from arduino import \\*", ""));
            ctrl.setTitle(new File(fileToOpen).getName() + "    |    Python Arduino IDE");
            stage.setTitle(new File(fileToOpen).getName() + "    |    Python Arduino IDE");
        } else {
            stage.setTitle("Untitled Sketch    |    Python Arduino IDE");
        }

        stage.setScene(new Scene(root));
        ctrl.setPrimaryStage(stage);
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Assets/Icons/Py Arduino IDE Icon.png")));
        stage.getIcons().add(icon);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        ctrl.countLines();
        ctrl.setUpToDate(true);
        ctrl.setSaved(true);
        ctrl.UpdateSaveIndicator();
        stage.show();
    }

    public static void newApplication() {
        launch();
    }

    public static void main(String[] args) {
        newApplication();
    }
}