package net.elyland.localnet.services;

import net.elyland.localnet.domains.NetHost;
import net.elyland.localnet.repositories.NetHostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PingAllService {
    @Autowired
    NetHostRepository netHostRepository;
    @Autowired
    PingService pingService;
    @Scheduled(cron="3 * * * * *")
    public  void pingAll(){
        List<NetHost> netHosts = null;
        try {
            netHosts = netHostRepository.findAll();
        } catch (Exception e){
            e.printStackTrace();
        }
        if (netHosts.isEmpty()) {
            for (NetHost host : netHosts) {
                Boolean status = host.getIsUp();
                host.setScanTime(new Date());
                try {
                    status = pingService.pingHost(host.getIpAddress());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!status == host.getIsUp()) {
                    host.setIsUp(status);

                }
                try {
                    NetHost oldHost = netHostRepository.getOne(host.getId());
                    if (host.getIsUp() != oldHost.getIsUp()){
                        oldHost.setIsUp(host.getIsUp());
                    }
                    netHostRepository.save(oldHost);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
