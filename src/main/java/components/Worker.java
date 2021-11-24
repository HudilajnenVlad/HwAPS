package components;

import java.util.Random;
import java.util.Vector;

public class Worker {
    private int countOfWorkers;
    private Random random = new Random();
    private double lambda;
    private Vector<Request> workers;
    private Vector<Boolean> workersEmployment;
    private double epsilon = 0.00001;

    public Worker(int countOfWorkers, double lambda) {
        this.lambda = lambda;
        this.workers = new Vector<Request>();
        this.workersEmployment = new Vector<Boolean>();
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

    public double getNext() {
        return 10 * Math.log(1 - random.nextDouble()) / (-lambda);
    }

    public double putInWorker(Request request, double currentTime) {
        double timeOutput = 0.0;
        for (int i = 0; i < this.countOfWorkers; i++) {
            if (!workersEmployment.get(i)) {
                timeOutput = currentTime + getNext();
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
                if (Math.abs(workers.get(i).getTimeWorkerOutput() - currentTime) <= epsilon) {
                    workersEmployment.set(i, false);
                    return workers.get(i);
                }
            }
        }
        return null;
    }

    public String drawWorker() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < countOfWorkers; i++) {
            if (workersEmployment.get(i)) {
                String str = workers.get(i).getRequestNumber();
                sb.append(str);
            } else {
                sb.append("     ");
            }
            sb.append("|");
        }
        return sb.toString();
    }
}
