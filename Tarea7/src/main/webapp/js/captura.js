document.addEventListener("DOMContentLoaded", () => {
    const formularioArticulo = document.getElementById("articuloForm");

    formularioArticulo.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const tituloArticulo = document.getElementById("nombre").value.trim();
        const detalleArticulo = document.getElementById("descripcion").value.trim();
        const costoArticulo = parseFloat(document.getElementById("precio").value);
        const almacenCantidad = parseInt(document.getElementById("cantidad").value);
        const imagenArticulo = document.getElementById("foto").files[0];

        if (!tituloArticulo || !detalleArticulo || !costoArticulo || !almacenCantidad || !imagenArticulo) {
            alert("Todos los campos son obligatorios.");
            return;
        }

        const lector = new FileReader();
        lector.readAsDataURL(imagenArticulo);
        lector.onload = async () => {
            const datosBase64 = lector.result.split(",");
            const dataEnviar = {
                id: null,
                name: tituloArticulo,
                description: detalleArticulo,
                price: costoArticulo,
                stockQuantity: almacenCantidad,
                format: datosBase64[0],
                photo: datosBase64[1]
            };

            const rutaAPI = "/api/productos";
            const urlBase = window.location.origin;
            const rutaApp = window.location.pathname.split("/").slice(0, -1).join("/");
            const urlAPI = urlBase + rutaApp + rutaAPI;

            try {
                const respuesta = await fetch(urlAPI, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(dataEnviar)
                });

                if (respuesta.ok) {
                    alert("Artículo guardado exitosamente.");
                    formularioArticulo.reset();
                } else {
                    alert("Ocurrió un error al guardar el artículo.");
                }
            } catch (error) {
                console.error(error);
                alert("Ocurrió un error al guardar el artículo.");
            }
        };
    });
});
