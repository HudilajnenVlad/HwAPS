package components;

import java.util.Comparator;
import java.util.Random;
import java.util.Vector;

public class Model {
    private Buffer buffer;
    private Generator generator;
    private Worker worker;
    private int countOfGenerators;
    private double lambda;
    private final int defaultCountRequests = 60;
    private Draw draw;

    public Model(int countOfGenerators, int countOfWorker, int countOfBuffer, double lambda) {
        buffer = new Buffer(countOfBuffer);
        worker = new Worker(countOfWorker, lambda);
        draw = new Draw(countOfGenerators, countOfBuffer, countOfWorker);
        this.lambda = lambda;
        this.countOfGenerators = countOfGenerators;
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
            requestVector.add(generatorVector.get(getRandomNumberOfGenerator(0, countOfGenerators)).generateRequest());
        }
        requestVector.sort(new Comparator<Request>() {
            @Override
            public int compare(Request o1, Request o2) {
                if (o1.getTimeCreate() - o2.getTimeCreate() < 0)
                    return -1;
                else
                    return 1;
            }
        });
        Vector<Double> events = new Vector<Double>();
        for (Request i : requestVector) {
            events.add(i.getTimeCreate());
        }
        Vector<Request> outRequestVector = new Vector<Request>();
        double currentTime = 0;
        while (events.size() != 0) {
            currentTime = events.firstElement();
            events.remove(events.firstElement());
            if (requestVector.size() != 0) {
                if (requestVector.firstElement().getTimeCreate() == currentTime) {

                    Request request = requestVector.firstElement();
                    //draw.drawLineFirst(request, currentTime,buffer,worker,countOfGenerators);
                    outRequestVector.add(request);
                    requestVector.remove(requestVector.firstElement());

                    if (worker.isFreeWorker()) {
                        events.add(worker.putInWorker(request, currentTime));
                        //draw.drawLineFirst(request, currentTime,buffer,worker,countOfGenerators);
                        events.sort(new Comparator<Double>() {
                            @Override
                            public int compare(Double o1, Double o2) {
                                if (o1 - o2 < 0)
                                    return -1;
                                else
                                    return 1;
                            }
                        });
                    } else {
                        buffer.putInBuffer(request, currentTime);
                    }
                   draw.drawLineFirst(request, currentTime, buffer, worker, countOfGenerators);
                    continue;
                }
            }

            worker.freeWorker(currentTime);
            if (!buffer.isEmpty()) {
                Request request = buffer.popOutBuffer(currentTime);
                events.add(worker.putInWorker(request, currentTime));
                events.sort(new Comparator<Double>() {
                    @Override
                    public int compare(Double o1, Double o2) {
                        if (o1 - o2 < 0)
                            return -1;
                        else
                            return 1;
                    }
                });

            }
           draw.drawLineSecond(currentTime,buffer, worker,countOfGenerators);


        }
        draw.print();
        return requestVector;
    }


}
