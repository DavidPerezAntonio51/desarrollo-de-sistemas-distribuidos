package mx.ipn.escom.azurefunctionst9.Repositorios;

import mx.ipn.escom.azurefunctionst9.Entidades.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
}