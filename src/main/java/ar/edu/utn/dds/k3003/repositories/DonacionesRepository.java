package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Donacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DonacionesRepository extends JpaRepository<Donacion, Long> {
    List<Donacion> findByDonadorIDAndFechaGreaterThanEqual(String donadorID, LocalDate fecha);
}
