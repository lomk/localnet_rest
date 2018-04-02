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
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NmapService {

    @Autowired
    NetHostRepository netHostRepository;

//    @Async
    @Scheduled(cron="5 * * * * *")
    public void runNmap(){

        String network = "192.168.0.0/24";
        Nmap4j nmap4j= new Nmap4j("/usr");
        nmap4j.includeHosts( network ) ;
        nmap4j.addFlags("-e eth1");
        nmap4j.addFlags("-sP");
        nmap4j.addFlags("-oX -");
        nmap4j.addFlags("--privileged");

        try {
            nmap4j.execute();

            String nmapRun = nmap4j.getOutput();
            OnePassParser opp = new OnePassParser();
            NMapRun nmapRun1 = opp.parse(nmapRun, OnePassParser.STRING_INPUT);
            ArrayList<Host> hosts = nmapRun1.getHosts();
            List<NetHost> newHosts = new ArrayList<>();

            for (Host host : hosts) {
                NetHost nh = new NetHost();

                String hostname = null;
                String ip = null;
                String mac = null;

                try {
                    hostname = host.getHostnames().getHostname().getName();
                } catch (Exception e ){
//                    e.printStackTrace();
                }
                try {
                    ip = host.getAddresses().get(0).getAddr();
                } catch (Exception e ){
                    e.printStackTrace();
                }
                try {
                    mac = host.getAddresses().get(1).getAddr();
                } catch (Exception e ){
                    e.printStackTrace();
                }
//                try {
//                    os = host.getOs().toString();
//                } catch (Exception e ){
//                    e.printStackTrace();
//                }
                try {
                    if (hostname != null) {
                        nh.setHostname(hostname);
                    } else {
                        nh.setHostname("noname");
                    }
                        nh.setIpAddress(ip);
                        nh.setMacAddress(mac);
                        nh.setIsUp(true);
//                        nh.setOs(os);
                        newHosts.add(nh);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            List<NetHost> netHosts = netHostRepository.findAll();
            List<NetHost> netHostsToDelete = new ArrayList<>();
            List<NetHost> netHostsToInsert = new ArrayList<>();
            List<NetHost> netHostsToUpdate = new ArrayList<>();


            if (!netHosts.isEmpty() && !newHosts.isEmpty()) {
                for (NetHost newhost : newHosts) {
                    if (netHosts.stream().anyMatch(h -> h.getIpAddress().equalsIgnoreCase(newhost.getIpAddress()))) {
                        for (NetHost netHost : netHosts) {
                            try {
                                if (newhost.getMacAddress().equals(netHost.getMacAddress())) {

                                    if (!newhost.getHostname().equals(netHost.getHostname())) {
                                        netHost.setHostname(newhost.getHostname());
                                    }
                                    if (!newhost.getIpAddress().equals(netHost.getIpAddress())) {
                                      netHost.setMacAddress(newhost.getMacAddress());
                                    }
                                    if (!newhost.getIsUp().equals(netHost.getIsUp())) {
                                        netHost.setIsUp(newhost.getIsUp());
                                    }

                                    netHost.setScanTime(new Date());
                                    netHostsToUpdate.add(netHost);
                                    break;
                                }
                            } catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    } else {
                        newhost.setScanTime(new Date());
                        netHostsToInsert.add(newhost);
                    }
                }

                for (NetHost netHost : netHosts){
                    if (newHosts.stream().noneMatch(h -> h.getIpAddress().equalsIgnoreCase(netHost.getIpAddress()))) {
                        if (!pingHost(netHost.getIpAddress())) {
                            netHost.setIsUp(false);
                            netHostsToUpdate.add(netHost);
                        }
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
                    for (NetHost host  : newHosts){
                        host.setScanTime(new Date());
                    }
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

    @Scheduled(cron="1 * * * * *")
    public  void pingAll(){
        List<NetHost> netHosts = null;
        try {
            netHosts = netHostRepository.findAll();
        }catch (Exception e){
            e.printStackTrace();
        }
        if (netHosts.isEmpty()) {
            for (NetHost host : netHosts) {
                Boolean status = host.getIsUp();
                host.setScanTime(new Date());
                try {
                    status = pingHost(host.getIpAddress());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!status == host.getIsUp()) {
                    host.setIsUp(status);
                }
                try {
                    netHostRepository.save(host);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

//    @Scheduled(cron="0 1 * * * *")
//    public void cleanAll(){
//        try {
//            List<NetHost> netHosts = netHostRepository.findAll();
//            for (NetHost host : netHosts){
//                Date oldDate = new Date(System.currentTimeMillis() - 14L * 24 * 3600 * 1000);
//                if (host.getScanTime().before(oldDate)){
//                    netHostRepository.delete(host);
//                }
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }

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
            e.printStackTrace();
        }
        return ports;
    }
}
