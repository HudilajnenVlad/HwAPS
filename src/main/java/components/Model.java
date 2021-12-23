package components;

import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.Random;
import java.util.Vector;

public class Model {
    private int mode;
    private Buffer buffer;
    private int countOfWorker;
    private Worker worker;
    private int countOfGenerators;
    private double lambda;
    private final int defaultCountRequests = 300;
    private final double eps = 0.00001;
    private Draw draw;
    private GridPane gridPane;
    private GridPane secGrid;


    public Model(int countOfGenerators, int countOfWorker, int countOfBuffer, double lambda, int mode, GridPane gridPane, GridPane secGrid) {
        this.mode = mode;
        buffer = new Buffer(countOfBuffer);
        worker = new Worker(countOfWorker);
        if (mode == 1) {
            draw = new Draw(countOfGenerators, countOfBuffer, countOfWorker, gridPane);
        }
        this.countOfWorker = countOfWorker;
        this.lambda = lambda;
        this.countOfGenerators = countOfGenerators;
        this.gridPane = gridPane;
        this.secGrid = secGrid;

    }

    private int getRandomNumberOfGenerator(int min, int max) {
        Random random = new Random();
        return random.ints(min, (max)).findFirst().getAsInt();
    }

    public Vector<Request> simulate() {
        Vector<Request> requestVector = new Vector<Request>();
        Vector<Generator> generatorVector = new Vector<Generator>();
        for (int i = 0; i < countOfGenerators; i++) {

            generatorVector.add(new Generator(this.lambda));
        }
        for (int i = 0; i < defaultCountRequests; i++) {
            for (int j = 0; j < countOfGenerators; j++) {
                requestVector.add(generatorVector.get(j).generateRequest());
            }
        }
        requestVector.sort((o1, o2) -> {
            if (o1.getTimeCreate() - o2.getTimeCreate() < 0)
                return -1;
            else if (o1.getTimeCreate() - o2.getTimeCreate() > 0)
                return 1;
            else {
                return 0;
            }
        });
        Vector<Double> events = new Vector<Double>();
        for (Request i : requestVector) {
            events.add(i.getTimeCreate());
        }
        Vector<Request> outRequestVector = new Vector<Request>();
        double currentTime = 0.1;
        while (events.size() != 0) {
            currentTime = events.remove(0);
            if (currentTime == 0) {
                System.out.println("ERROR");
            }

            if (requestVector.size() != 0) {
                if (Math.abs(requestVector.firstElement().getTimeCreate() - currentTime) < eps) {
                    String strErr = "";

                    Request request = requestVector.remove(0);
                    outRequestVector.add(request);

                    if (worker.isFreeWorker()) {
                        double time = worker.putInWorker(request, currentTime);
                        if (time == 0) {
                            continue;
                        }
                        events.add(time);
                        events.sort((o1, o2) -> {
                            if (o1 - o2 < 0)
                                return -1;
                            else if (o1 - o2 > 0)
                                return 1;
                            else
                                return 0;
                        });

                    } else {
                        strErr = buffer.putInBuffer(request, currentTime);
                    }
                    if (mode == 1) {
                        draw.drawLineFirst(request, currentTime, buffer, worker, countOfGenerators, strErr);
                    }
                    continue;
                }
            }

            worker.freeWorker(currentTime);
            if (!buffer.isEmpty()) {
                Request request = buffer.popOutBuffer(currentTime);
                double time = worker.putInWorker(request, currentTime);
                if (time == 0) {
                    continue;
                }
                events.add(time);
                events.sort((o1, o2) -> {
                    if (o1 - o2 < 0)
                        return -1;
                    else if (o1 - o2 > 0)
                        return 1;
                    else
                        return 0;
                });

            }
            if (mode == 1) {
                draw.drawLineSecond(currentTime, buffer, worker, countOfGenerators);
            }


        }
        if (mode == 1) {
            draw.print();
        } else {
            int numRequestsInGenerators[] = new int[countOfGenerators];
            int numErrorRequestsInGenerators[] = new int[countOfGenerators];
            double sumBuffTime[] = new double[countOfGenerators];
            double countStayTime[] = new double[countOfGenerators];//Без отказных
            double sumToWork[] = new double[countOfGenerators];
            double sumTimeForWorkers[] = new double[countOfWorker];
            double dispTimeForWorkers[] = new double[countOfGenerators];
            double distTimeForBP[] = new double[countOfGenerators];
            double deadline = currentTime;
            for (Request i : outRequestVector) {
                numRequestsInGenerators[i.getNumOfGenerator()]++;
                if (i.getTimeWorkerOutput() == 0) {
                    numErrorRequestsInGenerators[i.getNumOfGenerator()]++;
                } else {
                    sumBuffTime[i.getNumOfGenerator()] += i.getTimeBufferOutput() - i.getTimeBufferInput();
                    sumToWork[i.getNumOfGenerator()] += i.getTimeWorkerOutput() - i.getTimeWorkerInput();
                    countStayTime[i.getNumOfGenerator()]++;

                    dispTimeForWorkers[i.getNumOfGenerator()] += (i.getTimeBufferOutput() - i.getTimeBufferInput()) * (i.getTimeBufferOutput() - i.getTimeBufferInput());
                    distTimeForBP[i.getNumOfGenerator()] += (i.getTimeWorkerOutput() - i.getTimeWorkerInput()) * (i.getTimeWorkerOutput() - i.getTimeWorkerInput());

                }
                sumTimeForWorkers[i.getNumOfWorker()] += i.getTimeWorkerOutput() - i.getTimeWorkerInput();

            }
            for (int i = 0; i < 7; i++) {
                gridPane.getColumnConstraints().add(new ColumnConstraints(50));
            }
            for (int i = 0; i < countOfGenerators; i++) {
                gridPane.getRowConstraints().add(new RowConstraints(50));
            }
            for (int i = 0; i < countOfGenerators; i++) {
                gridPane.add(new Label("И" + i), 0, i + 1);
            }
            gridPane.add(new Label(" Колво"), 1, 0);
            gridPane.add(new Label(" Pотк"), 2, 0);
            gridPane.add(new Label(" Tпреб"), 3, 0);
            gridPane.add(new Label(" Тбп"), 4, 0);
            gridPane.add(new Label(" Tобсл"), 5, 0);
            gridPane.add(new Label(" Дбп"), 6, 0);
            gridPane.add(new Label(" Добсл"), 7, 0);
            for (int i = 0; i < countOfGenerators; i++) {

                gridPane.add(new Label(String.format(" %d", numRequestsInGenerators[i])), 1, i + 1);
                gridPane.add(new Label(String.format(" %.2f ", (double) numErrorRequestsInGenerators[i] / numRequestsInGenerators[i])), 2, i + 1);
                gridPane.add(new Label(String.format(" %.2f ", sumBuffTime[i] / countStayTime[i] + sumToWork[i] / countStayTime[i])), 3, i + 1);
                gridPane.add(new Label(String.format(" %.2f ", sumBuffTime[i] / countStayTime[i])), 4, i + 1);
                gridPane.add(new Label(String.format(" %.2f ", sumToWork[i] / countStayTime[i])), 5, i + 1);
                gridPane.add(new Label(String.format(" %.2f ", distTimeForBP[i] / countStayTime[i] - (distTimeForBP[i] / (countStayTime[i] * countStayTime[i])))), 6, i + 1);
                gridPane.add(new Label(String.format(" %.4f ", dispTimeForWorkers[i] / countStayTime[i] - (dispTimeForWorkers[i] / (countStayTime[i] * countStayTime[i])))), 7, i + 1);
            }
            for (int i = 0; i < countOfWorker; i++) {
                secGrid.getRowConstraints().add(new RowConstraints());
                secGrid.add(new Label("Коэфициент использования прибора " + i + ": " + sumTimeForWorkers[i] / deadline), 0, i);
            }

        }

        return requestVector;
    }


}
