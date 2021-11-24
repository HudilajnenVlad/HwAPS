package components;

import java.util.Vector;

public class Buffer {
    Vector<Request> buffer;
    private int countOfBuffers;
    private int indexOfInput = 0;
    private int indexOfOutput = 0;
    private int indexOldest;
    private boolean empty;

    public Buffer(int countOfBuffers) {
        this.buffer = new Vector<Request>(countOfBuffers);
        this.countOfBuffers = countOfBuffers;
        for (int i = 0; i < countOfBuffers; i++) {
            buffer.add(new Request().getPlugRequest());
        }
    }


    private boolean checkNonEntry(int i) {
        return buffer.get(i).getTimeBufferOutput() != 0;
    }

    private void incrementIndexOfInput() {
        if (indexOfInput == countOfBuffers - 1) {
            indexOfInput = 0;
        } else {
            indexOfInput++;
        }
    }

    private void incrementIndexOfOutput() {
        if (indexOfOutput == countOfBuffers - 1) {
            indexOfOutput = 0;
        } else {
            indexOfOutput++;
        }
    }

    public boolean isEmpty() {
        for (int i = 0; i < countOfBuffers; i++) {
            if (checkNonEntry(i)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public void putInBuffer(Request request, double currentTime) {
        if (checkNonEntry(indexOfInput)) {
            buffer.set(indexOfInput, request);
            buffer.get(indexOfInput).setTimeBufferInput(currentTime);
            incrementIndexOfInput();
        } else {
            int temp = indexOfInput;
            incrementIndexOfInput();
            while (temp != indexOfInput) {
                if (checkNonEntry(indexOfInput)) {
                    buffer.set(indexOfInput, request);
                    buffer.get(indexOfInput).setTimeBufferInput(currentTime);
                    return;
                } else {
                    incrementIndexOfInput();
                }
            }
            incrementIndexOfInput();
            buffer.get(indexOldest).setTimeBufferOutput(currentTime);
            buffer.set(indexOldest, request);
            buffer.get(indexOldest).setTimeBufferInput(currentTime);
            for (int i = 0; i < countOfBuffers; i++) {
                if (buffer.get(i).getTimeBufferInput() < buffer.get(indexOldest).getTimeBufferInput()) {
                    indexOldest = i;
                }
            }
        }
        return;
    }

    public Request popOutBuffer(double currentTime) {
        buffer.get(indexOfOutput).setTimeBufferOutput(currentTime);
        int copyIndex = indexOfOutput;
        incrementIndexOfOutput();
        return buffer.get(copyIndex);
    }

    public String drawBuffer() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < countOfBuffers; i++) {
            if (!checkNonEntry(i)) {
                String str = buffer.get(i).getRequestNumber();
                sb.append(str);
            } else {
                sb.append("     ");
            }
            sb.append("|");
        }
        return sb.toString();
    }
}
