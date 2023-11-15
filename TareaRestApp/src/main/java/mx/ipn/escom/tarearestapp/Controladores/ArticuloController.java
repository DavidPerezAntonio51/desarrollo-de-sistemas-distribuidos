package mx.ipn.escom.tarearestapp.Controladores;

import mx.ipn.escom.tarearestapp.Entidades.Articulo;
import mx.ipn.escom.tarearestapp.Servicios.ArticuloService;
import mx.ipn.escom.tarearestapp.Utils.ServiceFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/articulos")
public class ArticuloController {

    private ArticuloService articuloService;

    public ArticuloController() {
        this.articuloService = ServiceFactory.getArticuloService();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Articulo crearArticulo(Articulo articulo) {
        return articuloService.crearArticulo(articulo);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Articulo> buscarArticulos(@QueryParam("terminoBusqueda") String terminoBusqueda) {
        return articuloService.buscarArticulos(terminoBusqueda);
    }
}