package net.elyland.localnet.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class PingService {
    public static Boolean pingHost(String ip){

        Process p;
        String command = String.format("fping -I eth1 %s", ip);
        Boolean result = false;
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String s;
            while ((s = inputStream.readLine()) != null) {
                if (s.contains("alive")) {
                    result = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static List<String> findHosts(String network){

        Process p = null;
        List<String> statusList = new ArrayList<>();
        String command = String.format("fping -g %s", network);

        try {
            p = Runtime.getRuntime().exec(command);
            InputStream input = p.getInputStream();
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(input));

            String s = "";
            while ((s = inputStream.readLine()) != null) {
                if (s.contains("alive")) {
                    String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

                    Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.find()) {
                        statusList.add(matcher.group());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return statusList;
    }
}
