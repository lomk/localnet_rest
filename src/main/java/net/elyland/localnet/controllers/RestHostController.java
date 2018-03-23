package net.elyland.localnet.controllers;

import net.elyland.localnet.domains.NetHost;
import net.elyland.localnet.errors.CustomErrorType;
import net.elyland.localnet.repositories.NetHostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/admin/host")
public class RestHostController {
    @Autowired
    NetHostRepository hostRepository;

    @RequestMapping(value = "all", method = RequestMethod.GET)
    public ResponseEntity<?> getHosts() {
        List<NetHost> hostList = hostRepository.findAll();
        if (hostList == null){
            return new ResponseEntity(new CustomErrorType("No data found"),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<NetHost>>(hostList, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getHost(@PathVariable("id") Integer id) {
        NetHost host = hostRepository.findOne(id);
        if (host == null){
            return new ResponseEntity(new CustomErrorType(
                    "NetHost with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<NetHost>(host, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateHost(@PathVariable Integer id, @RequestBody NetHost host){
        NetHost currentHost = hostRepository.findOne(id);
        if (currentHost == null){
            return new ResponseEntity(new CustomErrorType(
                    "Unable to upate. NetHost with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        try {
            currentHost.setHostname(host.getHostname());
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
        currentHost.setIpAddress(host.getIpAddress());
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
        currentHost.setMacAddress(host.getMacAddress());
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
        currentHost.setCustomName(host.getCustomName());
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
        currentHost.setOs(host.getOs());
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
        currentHost.setPorts(host.getPorts());
        } catch (Exception e){
            e.printStackTrace();
        }

        hostRepository.save(currentHost);
        return new ResponseEntity<NetHost>(currentHost, HttpStatus.OK);
    }

    @RequestMapping(value="add", method=RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> addHost(@RequestBody NetHost host){

        if (host == null){
            return new ResponseEntity(new CustomErrorType("No host"),HttpStatus.NOT_ACCEPTABLE);
        }
        if (host.getHostname() == null || host.getHostname().isEmpty()){
            return new ResponseEntity(new CustomErrorType("No host name"),HttpStatus.NOT_ACCEPTABLE);
        }
        if (hostRepository.findByHostname(host.getHostname()) != null){
            return new ResponseEntity(new CustomErrorType("Unable to create. A host with hostname " +
                    host.getHostname() + " already exist."),HttpStatus.CONFLICT);
        }
        if (hostRepository.findByIpAddress(host.getIpAddress()) != null){
            return new ResponseEntity(new CustomErrorType("Unable to create. A host with IP-address " +
                    host.getIpAddress() + " already exist."),HttpStatus.CONFLICT);
        }
        if (hostRepository.findByMacAddress(host.getMacAddress()) != null){
            return new ResponseEntity(new CustomErrorType("Unable to create. A host with mac-address " +
                    host.getHostname() + " already exist."),HttpStatus.CONFLICT);
        }
        hostRepository.save(host);
        return new ResponseEntity<NetHost>(host, HttpStatus.CREATED);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delHost(@PathVariable("id") Integer id) {
        NetHost host = hostRepository.findOne(id);
        if (host == null ){
            return new ResponseEntity(new CustomErrorType(
                    "NetHost with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        try {
            hostRepository.delete(host);
        } catch (Exception e){
            return new ResponseEntity(new CustomErrorType(
                    "SQL error."),
                    HttpStatus.CONFLICT);
        }
        return new ResponseEntity<String>("Deleted", HttpStatus.OK);
    }
}
