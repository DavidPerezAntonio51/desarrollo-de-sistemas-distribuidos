package mx.ipn.escom.tarearestapp.Controladores;


import mx.ipn.escom.tarearestapp.Entidades.Carrito;
import mx.ipn.escom.tarearestapp.RequestDTO.ArticuloCantidad;
import mx.ipn.escom.tarearestapp.Servicios.CarritoService;
import mx.ipn.escom.tarearestapp.Utils.ServiceFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/carrito")
public class CarritoController {

    private CarritoService carritoService;

    public CarritoController() {
        this.carritoService = ServiceFactory.getCarritoService();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerArticulosCarrito() {
        List<Carrito> articulosCarrito = carritoService.obtenerArticulosCarrito();
        if (articulosCarrito.isEmpty()) {
            return Response.noContent().build();
        } else {
            return Response.ok(articulosCarrito).build();
        }
    }

    @POST
    @Path("/agregar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarArticuloCarrito(ArticuloCantidad articuloCantidad) {
        if (articuloCantidad.getCantidad() < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La cantidad debe ser mayor o igual a 1.")
                    .build();
        }

        try {
            Carrito carrito = carritoService.agregarArticuloCarrito(articuloCantidad.getArticuloId(), articuloCantidad.getCantidad());
            return Response.ok(carrito).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarArticuloCarrito(@PathParam("id") int id) {
        try {
            carritoService.eliminarArticuloCarrito(id);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/actualizar/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response actualizarCantidadArticuloCarrito(@PathParam("id") int id, ArticuloCantidad articuloCantidad) {
        if (articuloCantidad.getCantidad() < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La cantidad debe ser mayor o igual a 1.")
                    .build();
        }

        try {
            carritoService.actualizarCantidadArticuloCarrito(id, articuloCantidad.getCantidad());
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/eliminar")
    public void eliminarCarrito() {
        carritoService.eliminarCarrito();
    }
}