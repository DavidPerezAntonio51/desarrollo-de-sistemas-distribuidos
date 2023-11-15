package mx.ipn.escom.azurefunctionst9.Funciones;

import mx.ipn.escom.azurefunctionst9.DTO.ArticuloCantidad;
import mx.ipn.escom.azurefunctionst9.Entidades.Carrito;
import mx.ipn.escom.azurefunctionst9.Servicios.CarritoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class CarritoFunction {

    @Bean
    public Function<ArticuloCantidad, Carrito> agregarArticuloCarrito(CarritoService carritoService) {
        return articuloCantidad -> carritoService.agregarArticuloCarrito(articuloCantidad.getArticuloId(), articuloCantidad.getCantidad());
    }

    @Bean
    public Function<Integer, Void> eliminarArticuloCarrito(CarritoService carritoService) {
        return id -> {
            carritoService.eliminarArticuloCarrito(id);
            return null;
        };
    }

    @Bean
    public Function<ArticuloCantidad, Void> actualizarCantidadArticuloCarrito(CarritoService carritoService) {
        return articuloCantidad -> {
            carritoService.actualizarCantidadArticuloCarrito(articuloCantidad.getArticuloId(), articuloCantidad.getCantidad());
            return null;
        };
    }

    @Bean
    public Supplier<List<Carrito>> obtenerArticulosCarrito(CarritoService carritoService) {
        return ()->carritoService.obtenerArticulosCarrito();
    }

    @Bean
    public Supplier<Void> eliminarCarrito(CarritoService carritoService) {
        return () -> {
            carritoService.eliminarCarrito();
            return null;
        };
    }
}