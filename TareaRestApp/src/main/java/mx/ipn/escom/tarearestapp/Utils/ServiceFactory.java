package mx.ipn.escom.tarearestapp.Utils;

import mx.ipn.escom.tarearestapp.Servicios.ArticuloService;
import mx.ipn.escom.tarearestapp.Servicios.CarritoService;

public class ServiceFactory {

    private static ArticuloService articuloService;
    private static CarritoService carritoService;

    public static ArticuloService getArticuloService() {
        if (articuloService == null) {
            articuloService = new ArticuloService();
        }
        return articuloService;
    }

    public static CarritoService getCarritoService() {
        if (carritoService == null) {
            carritoService = new CarritoService();
        }
        return carritoService;
    }
}