import components.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class Scene {
    @FXML
    private Button stepButton;
    @FXML
    private GridPane grid;
    @FXML
    private Button autoButton;
    @FXML
    private Text text1;
    @FXML
    private Text text2;
    @FXML
    private Text text3;
    @FXML
    private Text text4;
    @FXML
    private TextField textBox1;
    @FXML
    private GridPane grd3;

    @FXML
    private TextField textBox2;

    @FXML
    private TextField textBox3;

    @FXML
    private TextField textBox4;

    @FXML
    private ScrollPane scroll;
    @FXML
    private AnchorPane front;
    @FXML
    private AnchorPane front1;

    @FXML
    private Text tex1;

    @FXML
    private Text tex2;
    @FXML
    private GridPane outGrid;
    @FXML
    private GridPane secGrid;

    public Scene() {
    }

    @FXML
    void initialize() {
        stepButton.setOnAction(actionEvent -> {
            Model model = new Model(Integer.parseInt(textBox1.getText()),Integer.parseInt(textBox2.getText()),Integer.parseInt(textBox3.getText()),Double.parseDouble(textBox4.getText()), 1, grid, secGrid);
            model.simulate();
            front.setVisible(false);
            scroll.setContent(grid);
            scroll.setVisible(true);
            grid.setVisible(true);
            });
        autoButton.setOnAction(actionEvent -> {
            Model model = new Model(Integer.parseInt(textBox1.getText()),Integer.parseInt(textBox2.getText()),Integer.parseInt(textBox3.getText()),Double.parseDouble(textBox4.getText()), 2, outGrid, secGrid);
            model.simulate();

            front.setVisible(false);
            front1.setVisible(true);
            outGrid.setVisible(true);


        });
    }

}

