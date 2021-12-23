package components;


import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class Draw {
    private String output;
    private StringBuilder stringBuilder = new StringBuilder();
    GridPane grid;
    int columnIndex;
    int rowIndex;
    int width = 50;
    int extraWidth;

    public Draw(int countOfGenerators, int countOfBuffer, int countOfWorkers, GridPane grid) {

        this.grid = grid;
        for (int i = 0; i < countOfGenerators; i++) {
            grid.getRowConstraints().add(new RowConstraints(width));
            grid.add(new Label("Gen" + i), 0, i);
        }
        for (int i = 0; i < countOfWorkers; i++) {
            grid.getRowConstraints().add(new RowConstraints(width));
            grid.add(new Label("Wor" + i), 0, countOfGenerators + i);
        }
        for (int i = 0; i < countOfBuffer; i++) {
            grid.getRowConstraints().add(new RowConstraints(width));
            grid.add(new Label("Buf" + i), 0, countOfGenerators + countOfWorkers+ i);
        }
        grid.getRowConstraints().add(new RowConstraints(width));
        grid.add(new Label("Err"), 0, countOfBuffer + countOfGenerators + countOfWorkers);
        grid.getRowConstraints().add(new RowConstraints(width));
        grid.add(new Label("Time"), 0, countOfBuffer + countOfGenerators + countOfWorkers+1);
        columnIndex = 0;
        rowIndex = 0;
        extraWidth =0;
    }

    public void drawLineFirst(Request request, double currentTime, Buffer buffer, Worker worker, int countOfGenerators, String strErr) {
        grid.getColumnConstraints().add(new ColumnConstraints(width));
        columnIndex++;
        extraWidth+=50;

        for (int i = 0; i < countOfGenerators; i++) {
            if (request.getNumOfGenerator() == i) {
                grid.add(new Label(request.getRequestNumber()), columnIndex , rowIndex);
            }
            rowIndex++;
        }
        rowIndex += worker.drawWorker(grid, columnIndex , rowIndex);
        rowIndex += buffer.drawBuffer(grid,  columnIndex ,rowIndex);
        if (strErr != "") {
            grid.add(new Label(strErr), columnIndex , rowIndex);
        }
        rowIndex++;
        grid.add(new Label(String.format("%.1f", currentTime)),columnIndex,rowIndex);
        rowIndex = 0;
    }

    public void drawLineSecond(double currentTime, Buffer buffer, Worker worker, int countOfGenerators) {
        grid.getColumnConstraints().add(new ColumnConstraints(width));
        columnIndex++;


        //sb.append(printTime(currentTime));
        rowIndex+=countOfGenerators;

        rowIndex += worker.drawWorker(grid,columnIndex , rowIndex);
        rowIndex += buffer.drawBuffer(grid,columnIndex  , rowIndex);
        grid.add(new Label(String.format("%.1f", currentTime)),columnIndex,rowIndex);
        rowIndex = 0;
    }

    public void print() {
        System.out.println(stringBuilder.toString());
    }
}