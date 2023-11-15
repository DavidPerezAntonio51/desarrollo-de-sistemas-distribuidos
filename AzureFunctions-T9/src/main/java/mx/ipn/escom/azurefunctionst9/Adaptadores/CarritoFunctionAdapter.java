package mx.ipn.escom.azurefunctionst9.Adaptadores;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import mx.ipn.escom.azurefunctionst9.DTO.ArticuloCantidad;
import mx.ipn.escom.azurefunctionst9.Entidades.Carrito;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class CarritoFunctionAdapter {

    private final Function<ArticuloCantidad, Carrito> agregarArticuloCarrito;
    private final Function<Integer, Void> eliminarArticuloCarrito;
    private final Function<ArticuloCantidad, Void> actualizarCantidadArticuloCarrito;
    private final Supplier<List<Carrito>> obtenerArticulosCarrito;
    private final Supplier<Void> eliminarCarrito;
    private final ObjectMapper objectMapper; // Jackson ObjectMapper

    public CarritoFunctionAdapter(Function<ArticuloCantidad, Carrito> agregarArticuloCarrito, Function<Integer, Void> eliminarArticuloCarrito, Function<ArticuloCantidad, Void> actualizarCantidadArticuloCarrito, Supplier<List<Carrito>> obtenerArticulosCarrito, Supplier<Void> eliminarCarrito, ObjectMapper objectMapper) {
        this.agregarArticuloCarrito = agregarArticuloCarrito;
        this.eliminarArticuloCarrito = eliminarArticuloCarrito;
        this.actualizarCantidadArticuloCarrito = actualizarCantidadArticuloCarrito;
        this.obtenerArticulosCarrito = obtenerArticulosCarrito;
        this.eliminarCarrito = eliminarCarrito;
        this.objectMapper = objectMapper;
    }

    // Implementación de los métodos @FunctionName
    @FunctionName("agregarArticuloCarrito")
    public HttpResponseMessage runAgregarArticuloCarrito(
            @HttpTrigger(name = "request", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS, route = "carrito")
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        Optional<String> optArticuloCantidad = request.getBody();
        if (optArticuloCantidad.isPresent()) {
            try {
                ArticuloCantidad articuloCantidad = objectMapper.readValue(optArticuloCantidad.get(), ArticuloCantidad.class);
                Carrito resultado = agregarArticuloCarrito.apply(articuloCantidad);
                return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").body(objectMapper.writeValueAsString(resultado)).build();
            } catch (Exception e) {
                context.getLogger().severe("Error en la conversión JSON a ArticuloCantidad: " + e.getMessage());
            }
        }
        return request.createResponseBuilder(HttpStatus.BAD_REQUEST).header("Content-Type", "application/json").body("No se pudo agregar el artículo al carrito.").build();
    }

    @FunctionName("eliminarArticuloCarrito")
    public HttpResponseMessage runEliminarArticuloCarrito(
            @HttpTrigger(name = "request", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS, route = "carrito/{id}")
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context,
            @BindingName("id") int id) {
        eliminarArticuloCarrito.apply(id);
        return request.createResponseBuilder(HttpStatus.OK).build();
    }

    @FunctionName("actualizarCantidadArticuloCarrito")
    public HttpResponseMessage runActualizarCantidadArticuloCarrito(
            @HttpTrigger(name = "request", methods = {HttpMethod.PUT}, authLevel = AuthorizationLevel.ANONYMOUS, route = "carrito")
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        Optional<String> optArticuloCantidad = request.getBody();
        if (optArticuloCantidad.isPresent()) {
            try {
                ArticuloCantidad articuloCantidad = objectMapper.readValue(optArticuloCantidad.get(), ArticuloCantidad.class);
                actualizarCantidadArticuloCarrito.apply(articuloCantidad);
                return request.createResponseBuilder(HttpStatus.OK).build();
            } catch (Exception e) {
                context.getLogger().severe("Error en la conversión JSON a ArticuloCantidad: " + e.getMessage());
            }
        }
        return request.createResponseBuilder(HttpStatus.BAD_REQUEST).header("Content-Type", "application/json").body("No se pudo actualizar la cantidad del artículo en el carrito.").build();
    }

    @FunctionName("obtenerArticulosCarrito")
    public HttpResponseMessage runObtenerArticulosCarrito(
            @HttpTrigger(name = "request", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS, route = "carrito")
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        List<Carrito> carrito = obtenerArticulosCarrito.get();
        try {
            return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").body(objectMapper.writeValueAsString(carrito)).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionName("eliminarCarrito")
    public HttpResponseMessage runEliminarCarrito(
            @HttpTrigger(name = "request", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS, route = "carrito")
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        eliminarCarrito.get();
        return request.createResponseBuilder(HttpStatus.OK).build();
    }
}
