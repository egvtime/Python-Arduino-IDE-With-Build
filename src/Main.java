import javafx.scene.control.*;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            OpenFile.setFileToOpen(args[0]);
            OpenFile.main(args);
        } else {
            StartScreen.main(args);
        }
    }
}
