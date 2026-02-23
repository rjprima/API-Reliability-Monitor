import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class core {
    public static void main(String[] args) throws InterruptedException{
        Scanner input = new Scanner(System.in);
        APIStatus[] stati = InitializeURLs();
        
        CompletableFuture.supplyAsync(() -> {
            while (true) {
                for (int i = 0; i < stati.length; i++) {
                    APIStatus x = stati[i];
                    if (x.getConstraint() < (double) ((System.nanoTime() - x.getTOLP())/1000000)) {
                        ping(x);
                    }
                }
                try {TimeUnit.SECONDS.sleep(3);} 
                catch (InterruptedException e) {e.printStackTrace();}
        }});

        while (true) {
            System.out.println("1. to return latest ping \n2. to return average latency \n3. to exit \n");
            int command = input.nextInt();
            if (command == 1) {
                for (int i = 0; i < stati.length; i++) {
                    APIStatus x = stati[i];
                    System.out.println("Latest ping for " + x.GetURL() + ": " + x.GetLatency() + "ms");
                }
                System.out.println("\n");
            } 
            else if (command == 2) {
                for (int i = 0; i <stati.length; i++) {
                    APIStatus x = stati[i];
                    System.out.println("Latency average for " + x.GetURL() + ": " + x.GetAverageLatency() + "ms");
                }
                System.out.println("\n");
            }
            else if (command == 3) {
                System.exit(0);
            }
            else {
                System.out.println("invalid command \n");
            }
        }
    }

    public static CompletableFuture<HttpResponse<String>> ping(APIStatus target) {
        HttpClient client = HttpClient.newHttpClient();
        CompletableFuture<HttpResponse<String>> response = CompletableFuture.supplyAsync(() -> {
            long time1 = System.nanoTime();
            target.SetTOLP(time1);
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

    public static APIStatus[] InitializeURLs() {
        File APIs = new File(".gitignore/APIs.txt");
        ArrayList<APIStatus> stati = new ArrayList<APIStatus>();
        try (Scanner parse = new Scanner(APIs)) {
            while (parse.hasNextLine()) {
                String x = parse.nextLine();
                System.out.println(x);
                stati.add(new APIStatus(x));
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return stati.toArray(new APIStatus[stati.size()]);
    }
}