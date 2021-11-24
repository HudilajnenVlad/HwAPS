package components;

import java.util.Comparator;
import java.util.Random;
import java.util.Vector;

public class Model {
    private int mode;
    private Buffer buffer;
    private int countOfWorker;
    private Worker worker;
    private int countOfGenerators;
    private double lambda;
    private final int defaultCountRequests = 60;
    private final double eps = 0.00001;
    private Draw draw;

    public Model(int countOfGenerators, int countOfWorker, int countOfBuffer, double lambda, int mode) {
        this.mode = mode;
        buffer = new Buffer(countOfBuffer);
        worker = new Worker(countOfWorker, lambda);
        draw = new Draw(countOfGenerators, countOfBuffer, countOfWorker);
        this.countOfWorker = countOfWorker;
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
                    String strErr = new String("");

                    Request request = requestVector.firstElement();
                    outRequestVector.add(request);
                    requestVector.remove(requestVector.firstElement());

                    if (worker.isFreeWorker()) {
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
                    } else {
                        strErr = buffer.putInBuffer(request, currentTime);
                    }
                   draw.drawLineFirst(request, currentTime, buffer, worker, countOfGenerators, strErr);
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
        if (mode == 1)
        {
            draw.print();
        }
        else
        {
            int numRequestsInGenerators[] = new int[countOfGenerators];
            int numErrorRequestsInGenerators[] = new int[countOfGenerators];
            double sumBuffTime[] = new double[countOfGenerators];
            double countStayTime[] = new double[countOfGenerators];//Без отказных
            double sumToWork[] = new double[countOfGenerators];
            double sumTimeForWorkers[] = new double[countOfWorker];
            double dispTimeForWorkers[] = new double[countOfGenerators];
            double distTimeForBP[] = new double[countOfGenerators];
            double deadline = currentTime;
            for (Request i: outRequestVector)
            {
                numRequestsInGenerators[i.getNumOfGenerator()]++;
                if (i.getTimeWorkerOutput()==0)
                {
                    numErrorRequestsInGenerators[i.getNumOfGenerator()]++;
                }
                else
                {
                    sumBuffTime[i.getNumOfGenerator()]+=i.getTimeBufferOutput()-i.getTimeBufferInput();
                    sumToWork[i.getNumOfGenerator()]+=i.getTimeWorkerOutput()-i.getTimeWorkerInput();
                    countStayTime[i.getNumOfGenerator()]++;

                    dispTimeForWorkers[i.getNumOfGenerator()]+=(i.getTimeBufferOutput()-i.getTimeBufferInput())*(i.getTimeBufferOutput()-i.getTimeBufferInput());
                    distTimeForBP[i.getNumOfGenerator()]+=(i.getTimeWorkerOutput()-i.getTimeWorkerInput())*(i.getTimeWorkerOutput()-i.getTimeWorkerInput());

                }
                sumTimeForWorkers[i.getNumOfWorker()] += i.getTimeWorkerOutput()-i.getTimeWorkerInput();

            }
          /*  for (int i=0; i<countOfGenerators; i++)
            {
                System.out.println("Количество заявок в "+i+" источнике: "+numRequestsInGenerators[i]+" Вероятность отказа: "+(double)numErrorRequestsInGenerators[i]/numRequestsInGenerators[i]);
                System.out.println(String.format("Среднее время пребывание в БП: %.2f Среднее время обслуживания: %.2f Среднее время пребывания заявки в системе: %.2f", sumBuffTime[i]/countStayTime[i], sumToWork[i]/countStayTime[i], sumBuffTime[i]/countStayTime[i] +sumToWork[i]/countStayTime[i]));
                System.out.println();
            }
*/
            StringBuilder sb = new StringBuilder();
            sb.append("     |Колво|Pотк |Tпреб| Тбп |Tобсл| Дбп |Добсл");
            for (int i=0; i<countOfGenerators; i++)
            {
              sb.append("\n");
              sb.append("И"+i+"   |");
              if (numRequestsInGenerators[i]>9)
              {
                  sb.append(numRequestsInGenerators[i]+"   ");
              }
              else
              {
                  sb.append(numRequestsInGenerators[i]+"    ");
              }
              sb.append(String.format("|%.2f |",(double)numErrorRequestsInGenerators[i]/numRequestsInGenerators[i]));
              sb.append(String.format("%.2f |%.2f |%.2f |", sumBuffTime[i]/countStayTime[i] +sumToWork[i]/countStayTime[i], sumBuffTime[i]/countStayTime[i], sumToWork[i]/countStayTime[i]));
              sb.append(String.format("%.2f |%.2f ", distTimeForBP[i]/countStayTime[i] - (distTimeForBP[i]/(countStayTime[i]*countStayTime[i])),dispTimeForWorkers[i]/countStayTime[i] - (dispTimeForWorkers[i]/(countStayTime[i]*countStayTime[i]))));
              sb.append("\n");
            }
            System.out.println(sb.toString());
            for (int i=0; i<countOfWorker;i++)
            {
                System.out.println("Коэфициент использования прибора " + i + ": " + sumTimeForWorkers[i]/deadline);
            }

        }

        return requestVector;
    }


}
