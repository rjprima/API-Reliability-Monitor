import java.net.URI;
import java.net.http.*;

public class APIStatus {
    private String URL;
    private String StatusCode;
    private int CurrentLatency;
    private int[] Latency = new int[10];
    private int counter = 0;
    private HttpRequest request;
    private long TOLP; //time of last ping
    private double constraint;

    public APIStatus(String URL, double constraint) {
        this.URL = URL;
        this.constraint = constraint;
        this.request = HttpRequest.newBuilder().uri(URI.create(URL)).build();
    }

    public APIStatus(String URL) {
        this.URL = URL;
        this.constraint = 0;
        this.request = HttpRequest.newBuilder().uri(URI.create(URL)).build();
    }

    public APIStatus() {
        this.constraint = 0;
    }

    public void SetURL(String x) {
        URL = x;
    }

    public void SetStatusCode(String x) {
        StatusCode = x;
    }

    public void SetCurrentLatency(int x) {
        CurrentLatency = x;
        if (counter == 9) {
            Latency[9] = x;
            counter = 0;
        }
        else {
            Latency[counter] = x;
            counter++;
        }
    }

    public void SetTOLP(long time) {
        this.TOLP = time;
    }

    public void BuildRequest() {
        this.request = HttpRequest.newBuilder().uri(URI.create(URL)).build();
    }
    
    public String GetURL() {
        return URL;
    }

    public HttpRequest GetRequest() {
        return request;
    }

    public String GetStatusCode() {
        return StatusCode;
    }

    public int GetLatency() {
        return CurrentLatency;
    }

    public double GetAverageLatency() {
        int c = 0;
        for (int i = 0; i <= 9; i++) {
            c += Latency[i];
        }
        return (c/10.0);
    }

    public double getConstraint() {
        return this.constraint;
    }

    public long getTOLP() {
        return this.TOLP;
    }
}