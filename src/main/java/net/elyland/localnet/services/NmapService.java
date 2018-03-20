package net.elyland.localnet.services;

import net.elyland.localnet.domains.NetHost;
import net.elyland.localnet.repositories.NetHostRepository;
import org.nmap4j.Nmap4j;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.nmap4j.data.NMapRun;
import org.nmap4j.data.nmaprun.Host;
import org.nmap4j.parser.OnePassParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
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
public class NmapService {

    @Autowired
    NetHostRepository netHostRepository;

    @Async
    @Scheduled(cron="2 * * * * *")
    public void runNmap(){

//        System.out.println("STARTING NEW SCAN");
        String network = "192.168.0.0/24";
        Nmap4j nmap4j= new Nmap4j("/usr");
        nmap4j.includeHosts( network ) ;
        nmap4j.addFlags("-sP");
        nmap4j.addFlags("-oX -");

        try {
            nmap4j.execute();

            String nmapRun = nmap4j.getOutput();
            OnePassParser opp = new OnePassParser();
            NMapRun nmapRun1 = opp.parse(nmapRun, OnePassParser.STRING_INPUT);
            ArrayList<Host> hosts = nmapRun1.getHosts();
            List<NetHost> newHosts = new ArrayList<>();
//            System.out.println("START NMAP");
//            System.out.println(hosts.size());
            for (Host host : hosts) {
                NetHost nh = new NetHost();
                try {
                    String hostname = host.getHostnames().getHostname().getName();
                    String ip = host.getAddresses().get(0).getAddr();
//                    String mac = host.getAddresses().get(1).getAddrtype();
                    if (hostname.contains("mylands.local") || hostname.contains("MYLANDS.LOCAL")) {
                        nh.setHostname(host.getHostnames().getHostname().getName());
                        nh.setIpAddress(ip);
//                        nh.setMacAddress(mac);
                        nh.setIsUp(true);
                        newHosts.add(nh);
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
            List<NetHost> netHosts = netHostRepository.findAll();
            List<NetHost> netHostsToDelete = new ArrayList<>();
            List<NetHost> netHostsToInsert = new ArrayList<>();
            List<NetHost> netHostsToUpdate = new ArrayList<>();

            if (!netHosts.isEmpty()) {
                for (NetHost newhost : newHosts) {
                    if (netHosts.stream().anyMatch(h -> h.getHostname().equalsIgnoreCase(newhost.getHostname()))) {
                        for (NetHost netHost : netHosts) {
                            try {
                                if (newhost.getHostname().equals(netHost)) {
                                    boolean isUpdated = false;
                                    if (!newhost.getIpAddress().equals(netHost.getIpAddress())) {
                                        netHost.setIpAddress(newhost.getIpAddress());
//                                        netHostsToUpdate.add(netHost);
                                        isUpdated = true;
                                    }
                                    if (!newhost.getMacAddress().equals(netHost.getMacAddress())) {
                                      netHost.setMacAddress(newhost.getMacAddress());
//                                        netHostsToUpdate.add(netHost);
                                        isUpdated = true;
                                    }
                                    if (!newhost.getIsUp().equals(netHost.getIsUp())) {
                                        netHost.setIsUp(newhost.getIsUp());
//                                        netHostsToUpdate.add(netHost);
                                        isUpdated = true;
                                    }
                                    if (isUpdated) {
                                        netHostsToUpdate.add(netHost);
                                    }
                                    break;
                                }
                            } catch (NullPointerException e){
//                            e.printStackTrace();
                            }
                        }
                    } else {
                        netHostsToUpdate.add(newhost);
                    }
                }


                if (!netHostsToInsert.isEmpty()) {
                    netHostRepository.save(netHostsToInsert);
                }
                if (!netHostsToUpdate.isEmpty()) {
                    netHostRepository.save(netHostsToUpdate);
                }
                if (!netHostsToDelete.isEmpty()) {
                    netHostRepository.delete(netHostsToDelete);
                }
            } else {
                if (!newHosts.isEmpty()){
                    netHostRepository.save(newHosts);
                }
            }
        } catch(NMapInitializationException e){
                e.printStackTrace();
        } catch(NMapExecutionException e){
                e.printStackTrace();
        } catch(Exception e){
                e.printStackTrace();
        }
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
//            e.printStackTrace();
        }
        return statusList;
    }

    public static Boolean pingHost(String ip){

        Process p;
        String command = String.format("fping %s", ip);
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
//            e.printStackTrace();
        }
        return result;
    }

    public static Boolean wake(String mac){
        Process p;
        String command = String.format("etherwake -i eth1 %s", mac);
        Boolean result = false;
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String s;
            while ((s = inputStream.readLine()) != null) {
                if (s.isEmpty()) {
                    result = true;
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return result;
    }


    /*public static String getHostName(InetAddress addr) {
        String host = null;
        List<NameService> nameServicesImpl = new ArrayList<>();
        try {
            Field nameServices = InetAddress.class.getDeclaredField("nameServices");
            nameServices.setAccessible(true);
            nameServicesImpl = (List<NameService>) nameServices.get(null);
        } catch (Throwable t) {
            throw new RuntimeException("Got caught doing naughty things.", t);
        }
        for (NameService nameService : nameServicesImpl) {
            try {
                host = nameService.getHostByAddr(addr.getAddress());
            } catch (Throwable t) {
            }
        }
        return host != null ? host : "UNNOWN";
    }*/


    public static List<String> portFinder(String host){

        Process p;
        List<String> ports = new ArrayList<>();
        String command = String.format("nmap -sS %s", host);
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String s;
            while ((s = inputStream.readLine()) != null) {
                if (s.contains("open")) {
                    ports.add(s);
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return ports;
    }
}
