package mx.ipn.escom.azurefunctionst9.Repositorios;

import mx.ipn.escom.azurefunctionst9.Entidades.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Integer> {
    List<Articulo> findByNombreContainingOrDescripcionContaining(String nombre, String descripcion);
}

