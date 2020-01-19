package Executor;

class Reporter {

    private String methodName;
    private long latency = 0;
    private int availability = 0;


    void setMethodName(String methodName) {
        this.methodName = methodName;
    }


    void setLatency(long latency) {
        this.latency = latency;
    }

    void wasAvailable() {
        this.availability = 1;
    }

    @Override
    public String toString() {
        return this.methodName + ":\n   Avaliability: " + this.availability + "\n   Latency: " + latency;
    }
}
