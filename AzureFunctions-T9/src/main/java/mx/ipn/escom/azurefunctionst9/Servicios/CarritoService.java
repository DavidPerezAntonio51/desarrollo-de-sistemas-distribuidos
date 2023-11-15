package mx.ipn.escom.azurefunctionst9.Servicios;

import mx.ipn.escom.azurefunctionst9.Entidades.Articulo;
import mx.ipn.escom.azurefunctionst9.Entidades.Carrito;
import mx.ipn.escom.azurefunctionst9.Repositorios.ArticuloRepository;
import mx.ipn.escom.azurefunctionst9.Repositorios.CarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ArticuloRepository articuloRepository;

    public List<Carrito> obtenerArticulosCarrito() {
        return carritoRepository.findAll();
    }

    @Transactional
    public Carrito agregarArticuloCarrito(int articuloId, int cantidad) {
        Optional<Articulo> optionalArticulo = articuloRepository.findById(articuloId);
        if (optionalArticulo.isPresent()) {
            Articulo articulo = optionalArticulo.get();
            if (articulo.getCantidadAlmacen() >= cantidad) {
                articulo.setCantidadAlmacen(articulo.getCantidadAlmacen() - cantidad);
                articuloRepository.save(articulo);

                Carrito carrito = new Carrito();
                carrito.setArticulo(articulo);
                carrito.setCantidad(cantidad);
                return carritoRepository.save(carrito);
            } else {
                throw new RuntimeException("No hay suficientes artículos en el almacén.");
            }
        } else {
            throw new RuntimeException("No se encontró el artículo.");
        }
    }

    @Transactional
    public void eliminarArticuloCarrito(int carritoId) {
        Optional<Carrito> optionalCarrito = carritoRepository.findById(carritoId);
        if (optionalCarrito.isPresent()) {
            Carrito carrito = optionalCarrito.get();
            Articulo articulo = carrito.getArticulo();
            articulo.setCantidadAlmacen(articulo.getCantidadAlmacen() + carrito.getCantidad());
            articuloRepository.save(articulo);
            carritoRepository.deleteById(carritoId);
        } else {
            throw new RuntimeException("No se encontró el artículo en el carrito.");
        }
    }

    @Transactional
    public void actualizarCantidadArticuloCarrito(int carritoId, int nuevaCantidad) {
        Optional<Carrito> optionalCarrito = carritoRepository.findById(carritoId);
        if (optionalCarrito.isPresent()) {
            Carrito carrito = optionalCarrito.get();
            Articulo articulo = carrito.getArticulo();
            int diferenciaCantidad = nuevaCantidad - carrito.getCantidad();

            if (articulo.getCantidadAlmacen() >= diferenciaCantidad) {
                articulo.setCantidadAlmacen(articulo.getCantidadAlmacen() - diferenciaCantidad);
                articuloRepository.save(articulo);

                carrito.setCantidad(nuevaCantidad);
                carritoRepository.save(carrito);
            } else {
                throw new RuntimeException("No hay suficientes artículos en el almacén.");
            }
        } else {
            throw new RuntimeException("No se encontró el artículo en el carrito.");
        }
    }

    @Transactional
    public void eliminarCarrito() {
        List<Carrito> carritoList = carritoRepository.findAll();
        for (Carrito carrito : carritoList) {
            Articulo articulo = carrito.getArticulo();
            articulo.setCantidadAlmacen(articulo.getCantidadAlmacen() + carrito.getCantidad());
            articuloRepository.save(articulo);
            carritoRepository.deleteById(carrito.getId());
        }
    }
}
