package components;


import cern.jet.random.Poisson;
import cern.jet.random.engine.RandomEngine;

import java.util.Vector;

public class Generator {

    private static int countOfGenerators = 0;
    private int numOfGenerator;
    private double timeInGenerator;
    private int cuntOfRequests;

    private double lambda;
    private Poisson poissonGenerator;


    public Generator(double lambda) {
        this.lambda = lambda;
        countOfGenerators += 1;
        cuntOfRequests = 0;
        this.numOfGenerator = countOfGenerators;
        this.timeInGenerator = 0;
        poissonGenerator = new Poisson(this.lambda, RandomEngine.makeDefault());
    }


    public Request generateRequest() {
        this.timeInGenerator += poissonGenerator.nextDouble()*0.1;
        this.cuntOfRequests++;
        Request request = new Request(this.timeInGenerator, this.numOfGenerator, this.cuntOfRequests);
        return request;
    }

    public Vector<Request> generateSeveralRequest(int count) {
        Vector<Request> temp = new Vector<Request>();
        for (int i = 0; i < count; i++) {
            temp.add(this.generateRequest());
        }
        return temp;
    }
}

