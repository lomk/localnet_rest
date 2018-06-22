package net.elyland.localnet.controllers;

import net.elyland.localnet.domains.NetHost;
import net.elyland.localnet.errors.CustomErrorType;
import net.elyland.localnet.repositories.NetHostRepository;
import net.elyland.localnet.services.NmapService;
import net.elyland.localnet.services.PingAllService;
import net.elyland.localnet.services.PingService;
import net.elyland.localnet.services.WakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/admin/scan")
public class RestScanController {

    @Autowired
    PingService pingService;

    @Autowired
    WakeService wakeService;

    @Autowired
    NetHostRepository netHostRepository;

//    @RequestMapping(value = "run", method = RequestMethod.GET)
//    public void scan() {
//
//        nmapService.runNmap();
//    }

    @RequestMapping(value = "ping/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> ping(@PathVariable("id") Integer id) {
        NetHost host = netHostRepository.findOne(id);

        if (pingService.pingHost(host.getIpAddress())) {
            if (!host.getIsUp()) {
                host.setIsUp(true);
                netHostRepository.save(host);
            }
        } else {
            if (host.getIsUp()) {
                host.setIsUp(false);
                netHostRepository.save(host);
            }
        }

        return new ResponseEntity<NetHost>(host, HttpStatus.OK);
    }

    @RequestMapping(value = "wake/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> wake(@PathVariable("id") Integer id) {
        NetHost host = netHostRepository.findOne(id);

        if (wakeService.wake(host.getMacAddress())) {
            return new ResponseEntity<String>("sent", HttpStatus.OK);
        } else {
            return new ResponseEntity(new CustomErrorType(
                    "Something wrong"),
                    HttpStatus.NOT_FOUND);
        }
    }
}
