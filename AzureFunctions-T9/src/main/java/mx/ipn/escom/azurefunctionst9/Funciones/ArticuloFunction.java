package mx.ipn.escom.azurefunctionst9.Funciones;

import mx.ipn.escom.azurefunctionst9.Entidades.Articulo;
import mx.ipn.escom.azurefunctionst9.Servicios.ArticuloService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Function;

@Configuration
public class ArticuloFunction {


    @Bean
    public Function<Articulo, Articulo> crearArticulo(ArticuloService articuloService) {
        return articulo -> articuloService.crearArticulo(articulo);
    }

    /*Nota1: SpringCloud usa el nombre del metodo para el endpoint, asi: /buscarArticulos*/
    /*Nota2: El parametro String debe ser /{terminoBusqueda} */
    @Bean
    public Function<String, List<Articulo>> buscarArticulos(ArticuloService articuloService) {
        return terminoBusqueda -> articuloService.buscarArticulos(terminoBusqueda);
    }
}

