import javax.swing.*;
import java.io.IOException;

public class MPML {
    MPML_GUI gui;

    MPML() {
    }

    public static void main(String[] args) {
        MPML app = new MPML();
        MPML_GUI gui = new MPML_GUI(app);
        app.setGui(gui);
    }

    public void setGui(MPML_GUI gui) {
        this.gui = gui;
    }
}
