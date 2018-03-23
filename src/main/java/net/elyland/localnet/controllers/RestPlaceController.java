package net.elyland.localnet.controllers;


import net.elyland.localnet.domains.Place;
import net.elyland.localnet.errors.CustomErrorType;
import net.elyland.localnet.repositories.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/admin/place")
public class RestPlaceController {

    @Autowired
    PlaceRepository placeRepository;

    @RequestMapping(value = "all", method = RequestMethod.GET)
    public ResponseEntity<?> listPlace() {
        List<Place> placeList = placeRepository.findAll();
        if (placeList == null){
            return new ResponseEntity(new CustomErrorType("No data found"),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Place>>(placeList, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getPlace(@PathVariable("id") Integer id) {
        Place place = placeRepository.findOne(id);
        if (place == null){
            return new ResponseEntity(new CustomErrorType(
                    "Place with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Place>(place, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePlace(@PathVariable Integer id, @RequestBody Place place){
        Place currentPlace = placeRepository.findOne(id);
        if (currentPlace == null){
            return new ResponseEntity(new CustomErrorType(
                    "Unable to upate. Place with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        currentPlace.setName(place.getName());
        placeRepository.save(currentPlace);
        return new ResponseEntity<Place>(currentPlace, HttpStatus.OK);
    }

    @RequestMapping(value="add", method=RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> addPlace(@RequestBody Place place){

        if (place == null){
            return new ResponseEntity(new CustomErrorType("No place"),HttpStatus.NOT_ACCEPTABLE);
        }
        if (place.getName() == null || place.getName().isEmpty()){
            return new ResponseEntity(new CustomErrorType("No place name"),HttpStatus.NOT_ACCEPTABLE);
        }
        if (placeRepository.findByName(place.getName()) != null){
            return new ResponseEntity(new CustomErrorType("Unable to create. A place with name " +
                    place.getName() + " already exist."),HttpStatus.CONFLICT);
        }
        placeRepository.save(place);
        return new ResponseEntity<Place>(place, HttpStatus.CREATED);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delPlace(@PathVariable("id") Integer id) {
        Place place = placeRepository.findOne(id);
        if (place == null ){
            return new ResponseEntity(new CustomErrorType(
                    "Place with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        try {
            placeRepository.delete(place);
        } catch (Exception e){
            return new ResponseEntity(new CustomErrorType(
                    "SQL error."),
                    HttpStatus.CONFLICT);
        }
        return new ResponseEntity<String>("Deleted", HttpStatus.OK);
    }
}
