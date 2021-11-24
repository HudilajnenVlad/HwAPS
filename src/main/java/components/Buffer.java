package components;

import java.util.Vector;

public class Buffer {
    Vector<Request> buffer;
    private int countOfBuffers;
    private int indexOfInput = 0;
    private int indexOfOutput = 0;
    private int indexOldest;

    public Buffer(int countOfBuffers) {
        this.buffer = new Vector<Request>(countOfBuffers);
        this.countOfBuffers = countOfBuffers;
        for (int i = 0; i < countOfBuffers; i++) {
            buffer.add(new Request());
        }
    }


    private boolean checkNonEntry(int i) {
        return buffer.get(i).getTimeBufferOutput() != 0;
    }

    private void incrementIndex(int index) {
        if (index == countOfBuffers - 1) {
            index = 0;
        } else {
            index++;
        }
    }

    public void putInBuffer(Request request, double currentTime) {
        if (checkNonEntry(indexOfInput)) {
            buffer.set(indexOfInput, request);
            buffer.get(indexOfInput).setTimeBufferInput(currentTime);
        } else {
            int temp = indexOfInput;
            incrementIndex(this.indexOfInput);
            while (temp != indexOfInput) {
                if (checkNonEntry(indexOfInput)) {
                    buffer.set(indexOfInput, request);
                    buffer.get(indexOfInput).setTimeBufferInput(currentTime);
                    return;
                } else {
                    incrementIndex(this.indexOfInput);
                }
            }
            buffer.get(indexOldest).setTimeBufferOutput(currentTime);
            buffer.set(indexOldest, request);
            buffer.get(indexOldest).setTimeBufferInput(currentTime);
            for (int i = 0; i < countOfBuffers; i++) {
                if (buffer.get(i).getTimeBufferInput() < buffer.get(indexOldest).getTimeBufferInput())
                {
                    indexOldest = i;
                }
            }
        }
        return;
    }

    public Request popOutBuffer(double currentTime)
    {
        buffer.get(indexOfOutput).setTimeBufferOutput(currentTime);
        int copyIndex = indexOfOutput;
        incrementIndex(this.indexOfOutput);
        return buffer.get(copyIndex);
    }
}
