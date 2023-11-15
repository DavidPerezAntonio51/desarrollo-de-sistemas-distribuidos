package mx.ipn.escom.azurefunctionst9.Servicios;

import mx.ipn.escom.azurefunctionst9.Entidades.Articulo;
import mx.ipn.escom.azurefunctionst9.Repositorios.ArticuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticuloService {

    private final ArticuloRepository articuloRepository;

    @Autowired
    public ArticuloService(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    public Articulo crearArticulo(Articulo articulo) {
        return articuloRepository.save(articulo);
    }

    public List<Articulo> buscarArticulos(String terminoBusqueda) {
        return articuloRepository.findByNombreContainingOrDescripcionContaining(terminoBusqueda, terminoBusqueda);
    }
}

