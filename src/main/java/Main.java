import cern.jet.random.Poisson;
import cern.jet.random.engine.DRand;
import cern.jet.random.engine.RandomEngine;
import components.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class Main {

    public static void main(String[] args) throws IOException {

        InputStream inputStream = System.in;
        Reader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        System.out.println("Введите количество генераторов");
        int countOfGenerators = Integer.parseInt(bufferedReader.readLine());
        System.out.println("Введите количество приборов");
        int countOfWorker = Integer.parseInt(bufferedReader.readLine());
        System.out.println("Введите размер буфера");
        int countOfBuffer = Integer.parseInt(bufferedReader.readLine());
        System.out.println("Введите мат.ожидание для рассчёта отклонения случайных величин");
        int lambda = Integer.parseInt(bufferedReader.readLine());
        System.out.println("Выберете режим работы");
        System.out.println("1 - пошаговый");
        System.out.println("2 - автоматический");
        int mode = Integer.parseInt(bufferedReader.readLine());
        Model model = new Model(countOfGenerators,countOfWorker,countOfBuffer,lambda, mode);

        model.simulate();
    }
}