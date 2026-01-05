import java.net.URI;
import java.net.http.*;

public class APIStatus {
    private String URL;
    private String StatusCode;
    private int CurrentLatency;
    private int[] Latency = new int[10];
    private int counter = 0;
    private HttpRequest request;

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

    public void BuildRequest() {
        request = HttpRequest.newBuilder().uri(URI.create(URL)).build();
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
}