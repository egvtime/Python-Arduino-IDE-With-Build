import UI.Controllers.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.stage.*;

public class StartScreen extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage newStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/FXML/StartScreen.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        newStage.setScene(new Scene(root));
        newStage.initStyle(StageStyle.UNDECORATED);
        Image icon = new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/Assets/Icons/Py Arduino IDE Icon.png")));
        newStage.getIcons().add(icon);
        StartScreenController ctrl = loader.getController();
        ctrl.setPrimaryStage(newStage);
        newStage.setTitle("Welcome To Arduino Python IDE");
        newStage.show();
    }
}
