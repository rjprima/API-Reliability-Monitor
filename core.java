import java.io.IOException;
import java.net.http.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class core {
    public static void main(String[] args) throws InterruptedException {
        String[] urls = new String[3];
        urls[0] = "https://www.google.com";
        urls[1] = "https://rileyprimavera.vercel.app";
        urls[2] = "https://w3schools.com";
        APIStatus[] stati = new APIStatus[3];
        for (int i = 0; i < stati.length; i++) {
            stati[i] = new APIStatus();
            stati[i].SetURL(urls[i]);
            stati[i].BuildRequest();
        }
        int c = 0;
        while (true) {
            for (int i = 0; i < stati.length; i++) {
                APIStatus x = stati[i];
                ping(x).thenRun(() -> {
                    System.out.println("Pinged " + x.GetURL() + " with latency: " + x.GetLatency() + "ms");
                });
            }
            if (c == 10) {
                System.out.println("average Latencies: \n" + stati[0].GetURL() + ": " + stati[0].GetAverageLatency() + "ms\n" +
                        stati[1].GetURL() + ": " + stati[1].GetAverageLatency() + "ms\n" +
                        stati[2].GetURL() + ": " + stati[2].GetAverageLatency() + "ms\n");
                c = 0;
            }
            else {
                c++;
            }
            TimeUnit.SECONDS.sleep(10);
        }
    }

    public static CompletableFuture<HttpResponse<String>> ping(APIStatus target) {
        HttpClient client = HttpClient.newHttpClient();
        CompletableFuture<HttpResponse<String>> response = CompletableFuture.supplyAsync(() -> {
            long time1 = System.nanoTime();
            try {
                    HttpResponse res = client.send(target.GetRequest(), HttpResponse.BodyHandlers.ofString());
                    long time2 = System.nanoTime();
                    target.SetCurrentLatency((int) ((time2 - time1)/1000000));
                    return res;
                }
            catch (IOException e) {e.printStackTrace();}
            catch (InterruptedException e) {e.printStackTrace();}
            return null;
        });
        return response;
    }
}