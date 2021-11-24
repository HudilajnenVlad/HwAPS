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

    public Model(int countOfGenerators, int countOfWorker, int countOfBuffer, double lambda) {
        buffer = new Buffer(countOfBuffer);
        worker = new Worker(countOfWorker, lambda);
        this.lambda = lambda;
        this.countOfGenerators = countOfGenerators;
    }

    private int getRandomNumberOfGenerator(int min, int max)
    {
        Random random = new Random();
        return random.ints(min,(max)).findFirst().getAsInt();
    }

    public Vector<Request> simulate() {
        Vector<Request> requestVector = new Vector<Request>();
        Vector<Generator> generatorVector = new Vector<Generator>();
        for (int i=0; i<countOfGenerators; i++)
        {
            generatorVector.add(new Generator(this.lambda));
        }
        for (int i = 0; i < defaultCountRequests; i++) {
            requestVector.add(generatorVector.get(getRandomNumberOfGenerator(0, countOfGenerators)).generateRequest());
        }
        requestVector.sort(new Comparator<Request>() {
            @Override
            public int compare(Request o1, Request o2) {
                return (int) (o1.getTimeCreate() - o2.getTimeCreate());
            }
        });
        Vector<Double> events = new Vector<Double>();
        for(Request i:requestVector)
        {
            events.add(i.getTimeCreate());
        }
        Vector<Request> outRequestVector = new Vector<Request>();
        double currentTime = 0;
        while (events.size()!=0)
        {
            currentTime = events.get(0);
            events.remove(0);
            if (requestVector.get(0).getTimeCreate() == currentTime)
            {
                if(worker.isFreeWorker())
                {
                    events.add(worker.putInWorker(requestVector.get(0),currentTime));
                }
            }
        }
        return requestVector;
    }
}
