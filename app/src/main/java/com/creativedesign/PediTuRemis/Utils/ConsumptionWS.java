package com.creativedesign.PediTuRemis.Utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ConsumptionWS {

    public static int postOneSignalJSON(String nombreChofer, String email) {
        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic M2VhNTM5MDAtMTBjOS00MWMzLTkyYTgtNDAyZDg5MWY1YmU2");
            con.setRequestMethod("POST");

            String strJsonBody = "{"
                    +   "\"app_id\": \"3045ae19-396a-4846-a792-181d9d331191\","
                    +   "\"headings\": {\"es\": \"UBIK\", \"en\": \"UBIK\"},"
                    +   "\"filters\" : [{\"field\": \"tag\", \"key\": \"email\", \"relation\": \"=\", \"value\": \"" + email + "\"}],"
                    +   "\"contents\": {\"en\": \"El vehículo " + nombreChofer +" lo esta esperando para iniciar el viaje.\", \"es\": \"El vehículo " + nombreChofer + " lo esta esperando para iniciar el viaje.\"}"
                    + "}";

            System.out.println("strJsonBody:\n" + strJsonBody);

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            System.out.println("httpResponse: " + httpResponse);

            if (  httpResponse >= HttpURLConnection.HTTP_OK
                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            System.out.println("jsonResponse:\n" + jsonResponse);
            return 1;
        } catch(Throwable t) {
            t.printStackTrace();
            return 0;
        }


    }
}
