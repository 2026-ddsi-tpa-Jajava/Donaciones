package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Subcategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubcategoriaRepository extends JpaRepository<Subcategoria,Long> {
    List<Subcategoria> findByCategoria_Id(Long id);
}
