package components;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.Random;
import java.util.Vector;

public class Worker {
    private final int countOfWorkers;
    private final Random random = new Random();
    private final double lambda;
    private final Vector<Request> workers;
    private final Vector<Boolean> workersEmployment;

    public Worker(int countOfWorkers) {
        this.lambda = 20;
        this.workers = new Vector<>();
        this.workersEmployment = new Vector<>();
        this.countOfWorkers = countOfWorkers;
        for (int i = 0; i < countOfWorkers; i++) {
            workers.add(new Request().getPlugRequest());
            workersEmployment.add(false);
        }
    }

    public boolean isFreeWorker() {
        for (boolean i : workersEmployment) {
            if (!i) {
                return true;
            }
        }
        return false;
    }

    public double getNext(int num) {
        if (num == 0)
        {
            return  1+10*Math.log(1 - random.nextDouble()) / (-lambda);
        }
        else if (num == 1)
        {
            return  1+8*Math.log(1 - random.nextDouble()) / (-lambda);
        }
        else if (num == 2)
        {
            return  1+6*Math.log(1 - random.nextDouble()) / (-lambda);
        }
        else if (num == 3)
        {
            return  1+5*Math.log(1 - random.nextDouble()) / (-lambda);
        }
        else if (num == 4)
        {
            return  1+4*Math.log(1 - random.nextDouble()) / (-lambda);
        }
        return  1+4*Math.log(1 - random.nextDouble()) / (-lambda);
    }

    public double putInWorker(Request request, double currentTime) {
        double timeOutput = currentTime;
        for (int i = 0; i < this.countOfWorkers; i++) {
            if (!workersEmployment.get(i)) {
                timeOutput = currentTime + getNext(request.getNumOfWorker());
                workers.set(i, request);
                workers.get(i).setTimeWorkerInput(currentTime);
                workers.get(i).setTimeWorkerOutput(timeOutput);
                workers.get(i).setNumOfWorker(i);
                workersEmployment.set(i, true);
                break;
            }
        }
        return timeOutput;
    }

    public Request freeWorker(double currentTime) {
        for (int i = 0; i < countOfWorkers; i++) {
            if (workersEmployment.get(i)) {
                double epsilon = 0.001;
                if (Math.abs(workers.get(i).getTimeWorkerOutput() - currentTime) <= epsilon) {
                    workersEmployment.set(i, false);
                    return workers.get(i);
                }
            }
        }
        return null;
    }

    public int drawWorker(GridPane gridPane,int columnIndex  ,int rowIndex ) {

        for (int i = 0; i < countOfWorkers; i++) {
            if (workersEmployment.get(i)) {
                gridPane.add(new Label(workers.get(i).getRequestNumber()),columnIndex,rowIndex);
            }
            rowIndex++;
        }
        return countOfWorkers;
    }
}
