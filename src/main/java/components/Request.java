package components;

import lombok.Data;

@Data
public class Request {
    private double timeCreate;
    private double timeBufferInput;
    private double timeBufferOutput;
    private double timeWorkerInput;
    private double timeWorkerOutput;
    private int numOfGenerator;
    private int numOfWorker;
    private int numOfBuffer;
    private int orderRequest;

    public Request(double timeCreate, int numOfGenerator, int orderRequest) {
        this.timeCreate = timeCreate;
        this.numOfGenerator = numOfGenerator;
        this.orderRequest = orderRequest;
    }

    public Request() {
    }


    public Request getPlugRequest() {
        this.timeBufferOutput = 1;
        return this;
    }

    public String getRequestNumber() {
        StringBuilder sb = new StringBuilder();
        String str = numOfGenerator + "." + orderRequest;
        if (str.length() == 3) {
            sb.append(" " + str + " ");
        } else if (str.length() == 4) {
            sb.append(" " + str);
        } else {
            sb.append(str);
        }
        return sb.toString();
    }
}
