package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria,Long> {
}
