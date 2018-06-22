package net.elyland.localnet.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class WakeService {
    public static Boolean wake(String mac){
        Process p;
        String command = String.format("etherwake -i eth1 %s", mac);
        Boolean result = false;
        System.out.println(command);
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String s;
            while ((s = inputStream.readLine()) != null) {
                System.out.println(s);
                if (s.isEmpty()) {
                    result = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
