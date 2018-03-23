package net.elyland.localnet.repositories;

import net.elyland.localnet.domains.Place;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Integer> {
    Place findByName(String name);
}