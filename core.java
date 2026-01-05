import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.concurrent.CompletableFuture;

public class core {
    public static void main(String[] args) throws IOException, InterruptedException{
        String url = "https://rileyprimavera.vercel.app";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        CompletableFuture<HttpResponse<String>> response = CompletableFuture.supplyAsync(() -> {
            long time1 = System.nanoTime();
            try {
                    HttpResponse res = client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println("delay: " + (System.nanoTime() - time1 )/1000000 + " ms");
                    return res;
                }
            catch (IOException e) {e.printStackTrace();}
            catch (InterruptedException e) {e.printStackTrace();}
            return null;
            });
        response.thenAccept(s -> {System.out.println("code: " + s.statusCode());});
        while (true) {}
    }
}