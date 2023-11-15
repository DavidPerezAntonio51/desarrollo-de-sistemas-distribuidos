document.addEventListener("DOMContentLoaded", () => {
    const busquedaInput = document.getElementById("busqueda");
    const resultadosContenedor = document.getElementById("resultados");

    document.getElementById("btnBuscar").addEventListener("click", async () => {
        const terminoBusqueda = busquedaInput.value.trim();

        if (!terminoBusqueda) {
            alert("Por favor, ingrese una palabra para buscar.");
            return;
        }

        const rutaApi = "/api/productos";
        const baseDireccion = window.location.origin;
        const rutaApp = window.location.pathname.split("/").slice(0, -1).join("/");
        const urlApi = baseDireccion + rutaApp + rutaApi;

        try {
            const respuesta = await fetch(`${urlApi}?searchTerm=${encodeURIComponent(terminoBusqueda)}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            });

            if (respuesta.ok) {
                const articulos = await respuesta.json();
                mostrarResultados(articulos);
            } else {
                alert("Ocurrió un error al realizar la búsqueda.");
            }
        } catch (error) {
            console.error(error);
            alert("Por favor, ingrese una palabra para buscar.");
        }
    });

    function mostrarResultados(articulos) {
        resultadosContenedor.innerHTML = "";

        if (articulos.length === 0) {
            const noResultados = document.createElement("p");
            noResultados.textContent = "No se encontraron artículos.";
            resultadosContenedor.appendChild(noResultados);
            return;
        }

        articulos.forEach(articulo => {
            const articuloContenedor = document.createElement("div");
            articuloContenedor.classList.add("articulo", "card", "mb-3", "p-2");

            const contenidoTarjeta = document.createElement("div");
            contenidoTarjeta.classList.add("d-flex", "align-items-start");
            articuloContenedor.appendChild(contenidoTarjeta);

            const imagen = document.createElement("img");
            imagen.src = `${articulo.format},${articulo.photo}`;
            imagen.alt = articulo.name;
            imagen.width = 100;
            imagen.classList.add("me-3");
            contenidoTarjeta.appendChild(imagen);

            const cuerpoTarjeta = document.createElement("div");
            cuerpoTarjeta.classList.add("d-flex");
            contenidoTarjeta.appendChild(cuerpoTarjeta);

            const tituloContenedor = document.createElement("div");
            tituloContenedor.classList.add("d-flex", "align-items-center", "justify-content-start", "mb-1");
            tituloContenedor.style.flexDirection = "column";
            cuerpoTarjeta.appendChild(tituloContenedor);

            const middleContainer = document.createElement("div");
            middleContainer.classList.add("d-flex", "align-items-center", "justify-content-start","mb-1","mt-2");
            cuerpoTarjeta.appendChild(middleContainer);

            const nombre = document.createElement("h5");
            nombre.textContent = articulo.name;
            nombre.classList.add("card-title", "mb-0");
            tituloContenedor.appendChild(nombre);

            const descripcionContenedor = document.createElement("div");
            descripcionContenedor.classList.add("d-flex", "flex-column", "align-items-center", "ms-auto", "mt-2");
            middleContainer.appendChild(descripcionContenedor);

            const btnDescripcion = document.createElement("button");
            btnDescripcion.textContent = "Descripción";
            btnDescripcion.classList.add("btn", "btn-secondary", "btn-sm");
            btnDescripcion.addEventListener("click", () => {
                const descripcionModal = new bootstrap.Modal(document.getElementById("descripcionModal"));
                document.getElementById("descripcionModalBody").textContent = articulo.description;
                descripcionModal.show();
            });

            descripcionContenedor.appendChild(btnDescripcion);

            const precio = document.createElement("p");
            precio.textContent = `Precio: $${articulo.price}`;
            precio.classList.add("card-text", "mb-auto");
            tituloContenedor.appendChild(precio);

            const accionesContenedor = document.createElement("div");
            accionesContenedor.classList.add("ms-auto", "d-flex", "flex-column", "align-items-end");
            contenidoTarjeta.appendChild(accionesContenedor);

            if (articulo.cantidadAlmacen === 0) {
                const sinStockIcono = document.createElement("i");
                sinStockIcono.classList.add("bi", "bi-exclamation-triangle-fill", "text-danger", "me-2");
                accionesContenedor.appendChild(sinStockIcono);

                const sinStockTexto = document.createElement("span");
                sinStockTexto.textContent = "Sin stock";
                sinStockTexto.classList.add("text-danger");
                accionesContenedor.appendChild(sinStockTexto);
            } else {
                const inputGroup = document.createElement("div");
                inputGroup.classList.add("input-group", "input-group-sm", "mb-2", "w-auto");
                accionesContenedor.appendChild(inputGroup);

                const inputCantidad = document.createElement("input");
                inputCantidad.type = "number";
                inputCantidad.min = 1;
                inputCantidad.max = articulo.stockQuantity;
                inputCantidad.value = 1;
                inputCantidad.classList.add("form-control", "me-1");
                inputGroup.appendChild(inputCantidad);

                const btnCompra = document.createElement("button");
                btnCompra.textContent = "Compra";
                btnCompra.classList.add("btn", "btn-primary");
                btnCompra.addEventListener("click", () => {
                    agregarAlCarrito(articulo.id, parseInt(inputCantidad.value));
                });
                inputGroup.appendChild(btnCompra);
            }

            resultadosContenedor.appendChild(articuloContenedor);
        });

    }

    async function agregarAlCarrito(productId, quantity) {
        const rutaApi = "/api/basket/add";
        const baseDireccion = window.location.origin;
        const rutaApp = window.location.pathname.split("/").slice(0, -1).join("/");
        const urlApi = baseDireccion + rutaApp + rutaApi;

        try {
            const respuesta = await fetch(urlApi, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ productId, quantity })
            });

            if (respuesta.ok) {
                alert("Se agregó correctamente al carrito.");
            } else if (respuesta.status === 400) {
                const mensajeError = await respuesta.text();
                alert(mensajeError);
            } else {
                alert("Ocurrió un error al agregar al carrito.");
            }
        } catch (error) {
            console.error(error);
            alert("Ocurrió un error al agregar al carrito.");
        }
    }

});
