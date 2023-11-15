package mx.ipn.escom.azurefunctionst9.Adaptadores;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import mx.ipn.escom.azurefunctionst9.Entidades.Articulo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class ArticuloFunctionAdapter{

    private Function<Articulo,Articulo> crearArticulo;
    private Function<String,List<Articulo>> buscarArticulos;
    private ObjectMapper objectMapper; // Jackson ObjectMapper

    public ArticuloFunctionAdapter(Function<Articulo, Articulo> crearArticulo, Function<String, List<Articulo>> buscarArticulos, ObjectMapper objectMapper) {
        this.crearArticulo = crearArticulo;
        this.buscarArticulos = buscarArticulos;
        this.objectMapper = objectMapper;
    }

    @FunctionName("crearArticulo")
    public HttpResponseMessage runCrearArticulo(
            @HttpTrigger(name = "request", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS,route = "articulos")
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        Optional<String> optArticulo = request.getBody();
        if (optArticulo.isPresent()) {
            try {
                Articulo articulo = objectMapper.readValue(optArticulo.get(), Articulo.class);
                Articulo resultado = crearArticulo.apply(articulo);
                return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").body(objectMapper.writeValueAsString(resultado)).build();
            } catch (Exception e) {
                context.getLogger().severe("Error en la conversión JSON a Articulo: " + e.getMessage());
            }
        }
        return request.createResponseBuilder(HttpStatus.BAD_REQUEST).header("Content-Type", "application/json").body("No se pudo crear el artículo.").build();
    }

    @FunctionName("buscarArticulos")
    public HttpResponseMessage runBuscarArticulos(
            @HttpTrigger(name = "request", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS, route = "articulos")
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        String terminoBusqueda = request.getQueryParameters().get("terminoBusqueda");

        List<Articulo> articulos = buscarArticulos.apply(terminoBusqueda);
        try {
            return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").body(objectMapper.writeValueAsString(articulos)).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
