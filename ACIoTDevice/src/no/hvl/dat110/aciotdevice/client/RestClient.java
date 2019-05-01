package no.hvl.dat110.aciotdevice.client;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.post;

public class RestClient {

    public RestClient() {
        // TODO Auto-generated constructor stub
    }

    private static String logpath = "/accessdevice/log";

    public void doPostAccessEntry(String message) {

        // TODO: implement a HTTP POST on the service to post the message

        try (Socket s = new Socket(Configuration.host, Configuration.port)) {

            // construct the HTTP request
            Gson gson = new Gson();
            AccessMessage msg = new AccessMessage(message);
            String jsonbody = gson.toJson(msg);

            String httppostrequest =
                    "POST " + logpath + "/ HTTP/1.1\r\n" +
                            "Host: " + Configuration.host + "\r\n" +
                            "Content-type: application/json\r\n" +
                            "Content-length: " + jsonbody.length() + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n" +
                            jsonbody +
                            "\r\n";

            // send the response over the TCP connection
            OutputStream output = s.getOutputStream();

            PrintWriter pw = new PrintWriter(output, false);
            pw.print(httppostrequest);
            pw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String codepath = "/accessdevice/code";

    public AccessCode doGetAccessCode() {

        AccessCode code = new AccessCode();

        // TODO: implement a HTTP GET on the service to get current access code

        try (Socket s = new Socket(Configuration.host, Configuration.port)) {

            Gson gson = new Gson();

            // construct the GET request
            String httpgetrequest = "GET " + codepath + " HTTP/1.1\r\n" + "Accept: application/json\r\n"
                    + "Host: " + Configuration.host + "\r\n" + "Connection: close\r\n" + "\r\n";

            // sent the HTTP request
            OutputStream output = s.getOutputStream();

            PrintWriter pw = new PrintWriter(output, false);

            pw.print(httpgetrequest);
            pw.flush();

            // read the HTTP response
            InputStream in = s.getInputStream();

            Scanner scan = new Scanner(in);
            StringBuilder jsonresponse = new StringBuilder();
            boolean header = true;

            while (scan.hasNext()) {

                String nextline = scan.nextLine();

                if (header) {
                    System.out.println(nextline);
                } else {
                    jsonresponse.append(nextline);
                }

                // simplified approach to identifying start of body: the empty line
                if (nextline.isEmpty()) {
                    header = false;
                }

            }

            code = gson.fromJson(jsonresponse.toString(), AccessCode.class);
            System.out.println(code);

            scan.close();

        } catch (IOException ex) {
            System.err.println(ex);
        }

        return code;
    }
}
