package components;


public class Draw {
    private String output;
    private StringBuilder stringBuilder = new StringBuilder();

    public Draw(int countOfGenerators, int countOfBuffer, int countOfWorkers) {
        stringBuilder.append("Time |");
        for (int i = 0; i < countOfGenerators; i++) {
            stringBuilder.append("Gen" + i + " |");
        }
        for (int i = 0; i < countOfWorkers; i++) {
            stringBuilder.append("Wor" + i + " |");
        }
        for (int i = 0; i < countOfBuffer; i++) {
            stringBuilder.append("Buf" + i + " |");
        }
        stringBuilder.append("Err  ");
        stringBuilder.append("\n");
    }


    public void drawLineFirst(Request request,double currentTime, Buffer buffer, Worker worker, int countOfGenerators, String strErr)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(printTime(currentTime));
        for (int i=0; i<countOfGenerators; i++)
        {
            if(request.getNumOfGenerator()==i)
            {
                sb.append(request.getRequestNumber()+"|");
            }
            else {
                sb.append("     |");
            }
        }
        sb.append(worker.drawWorker());
        sb.append(buffer.drawBuffer());
        if (strErr == "")
        {
            sb.append("     ");
        }
        else
        {
            sb.append(strErr);
        }
        sb.append("\n");
        stringBuilder.append(sb.toString());
    }

    private String printTime(double currentTime)
    {
        String str = String.format("%.1f", currentTime);
        if(str.length()==3)
        {
            return (str+" "+" |");
        }
        else
        {
            return (str+" |");
        }
    }

    public void drawLineSecond(double currentTime, Buffer buffer, Worker worker, int countOfGenerators) {
        StringBuilder sb = new StringBuilder();


        sb.append(printTime(currentTime));
        for (int i=0; i<countOfGenerators; i++)
        {
            sb.append("     |");
        }
        sb.append(worker.drawWorker());
        sb.append(buffer.drawBuffer());
        sb.append("     ");
        sb.append("\n");
        stringBuilder.append(sb.toString());
    }

    public void print() {
        System.out.println(stringBuilder.toString());
    }
}