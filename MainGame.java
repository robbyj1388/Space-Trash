import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


public class MainGame extends Application {
   
    /**
     * The start method. Required by Application
     * @param stage
     */
    public void start(Stage stage) {
        Pane root = new Pane( );
        Scene scene = new Scene( root, 800, 600 );

        stage.setTitle("Space Trash");
        stage.setScene(scene);
        stage.show();

    }

    
	/**
	 * The main( ) method is ignored in JavaFX applications.
	 * main( ) serves only as fallback in case the application is launched
	 * as a regular Java application, e.g., in IDEs with limited FX
	 * support.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
