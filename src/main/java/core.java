import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;

public class core {
    public static void main(String[] args) throws InterruptedException, IOException, SQLException{
        Connection DBC = loadDB();
        Statement DB = DBC.createStatement();
        Scanner input = new Scanner(System.in);
        APIStatus[] stati = InitializeURLs();
        CompletableFuture.supplyAsync(() -> {
            while (true) {
                for (int i = 0; i < stati.length; i++) {
                    APIStatus x = stati[i];
                    if (x.getConstraint() < (double) ((System.nanoTime() - x.getTOLP())/1000000)) {
                        ping(x, DB);
                    }
                }
                try {TimeUnit.SECONDS.sleep(3); DBC.commit();} 
                catch (Exception e) {e.printStackTrace();}
            }
        });
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

    public static CompletableFuture<HttpResponse<String>> ping(APIStatus target, Statement DB) {
        HttpClient client = HttpClient.newHttpClient();
        CompletableFuture<HttpResponse<String>> response = CompletableFuture.supplyAsync(() -> {
            long time1 = System.nanoTime();
            target.SetTOLP(time1);
            try {
                HttpResponse res = client.send(target.GetRequest(), HttpResponse.BodyHandlers.ofString());
                long time2 = System.nanoTime();
                target.SetCurrentLatency((int) ((time2 - time1)/1000000));
                try {
                    System.out.println("INSERT INTO log (name, latency) VALUES (\'" + target.GetURL() + "\', " + target.GetLatency() + ");");
                    DB.execute("INSERT INTO log (name, latency) VALUES (\'" + target.GetURL() + "\', " + target.GetLatency() + ");");
                }
                catch (Exception e) {
                    System.out.println(e);
                    try{
                        TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(1,10));
                        DB.execute("INSERT INTO log (name, latency) VALUES (\'" + target.GetURL() + "\', " + target.GetLatency() + ");");
                    }
                    catch (Exception s) {}
                }
                return res;
            }
            catch (Exception e) {e.printStackTrace();}
            return null;
        });
        return response;
    }

    public static APIStatus[] InitializeURLs() {
        InputStream APIs = core.class.getResourceAsStream("/APIs.txt");
        ArrayList<APIStatus> stati = new ArrayList<APIStatus>();
        try (Scanner parse = new Scanner(APIs)) {
            while (parse.hasNextLine()) {
                String x = parse.nextLine();
                System.out.println(x);
                stati.add(new APIStatus(x));
            }
        }
        catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return stati.toArray(new APIStatus[stati.size()]);
    }

    public static Connection loadDB() throws IOException{
        try {
            String home = System.getProperty("user.home");
            if (Files.isDirectory(Paths.get(home+"/AppData/Local/APIRM"))) {
                if (Files.exists(Paths.get(home+"/AppData/Local/APIRM/log.db"))) {
                    home = home.replace("\\","/");
                    Connection DB = DriverManager.getConnection("jdbc:sqlite:"+home+"/AppData/Local/APIRM/log.db");
                    DB.setAutoCommit(false);
                    return DB;
                }
                else {
                    Files.createFile(Paths.get(home+"/AppData/Local/APIRM/log.db"));
                    home = home.replace("\\","/");
                    Connection DB = DriverManager.getConnection("jdbc:sqlite:"+home+"/AppData/Local/APIRM/log.db");
                    Statement builder = DB.createStatement();
                    DB.setAutoCommit(false);
                    builder.execute("CREATE TABLE IF NOT EXISTS log ("+
                    "id INTEGER PRIMARY KEY, "+
                    "name TEXT, "+
                    "latency INTEGER"+
                    ");"
                    );
                    builder.execute("CREATE TABLE IF NOT EXISTS APIs ("+
                    "id INTEGER PRIMARY KEY, "+
                    "name TEXT, "+
                    "URL TEXT"+
                    ");"
                    );
                    DB.commit();
                }
            }
            else {
                Files.createDirectory(Paths.get(home+"/AppData/Local/APIRM"));
                Files.createFile(Paths.get(home+"/AppData/Local/APIRM/log.db"));
                home = home.replace("\\","/");
                Connection DB = DriverManager.getConnection("jdbc:sqlite:"+home+"/AppData/Local/APIRM/log.db");
                DB.setAutoCommit(false);
                Statement builder = DB.createStatement();
                builder.execute("CREATE TABLE IF NOT EXISTS log ("+
                "id INTEGER PRIMARY KEY, "+
                "name TEXT, "+
                "latency INTEGER"+
                ");"
                );
                builder.execute("CREATE TABLE IF NOT EXISTS APIs ("+
                "id INTEGER PRIMARY KEY, "+
                "name TEXT, "+
                "URL TEXT"+
                ");"
                );
                DB.commit();
            }
        }
        catch (Exception e) {System.out.println(e);}
        return null;
    }
}