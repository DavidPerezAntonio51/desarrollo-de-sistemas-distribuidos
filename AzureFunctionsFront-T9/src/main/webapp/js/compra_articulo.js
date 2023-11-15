document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("busqueda");
    const resultsContainer = document.getElementById("resultados");

    document.getElementById("btnBuscar").addEventListener("click", async () => {
        const searchTerm = searchInput.value.trim();

        if (!searchTerm) {
            showErrorModal("Por favor, ingrese una palabra para buscar.")
            return;
        }

        const apiPath = "/api/articulos";
        const baseUrl = "https://t9-af-2019630589.azurewebsites.net";
        const appPath = "";
        const apiUrl = baseUrl + appPath + apiPath;

        try {
            // Realizar la búsqueda en el backend utilizando searchTerm como query parameter
            const response = await fetch(`${apiUrl}?terminoBusqueda=${encodeURIComponent(searchTerm)}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            });

            if (response.ok) {
                const articulos = await response.json();
                displayResults(articulos);
            } else {
                showErrorModal("Ocurrió un error al realizar la búsqueda.")
            }
        } catch (error) {
            console.error(error);
            showErrorModal("Por favor, ingrese una palabra para buscar.")
        }
    });

    function displayResults(articulos) {
        resultsContainer.innerHTML = "";

        if (articulos.length === 0) {
            const noResults = document.createElement("p");
            noResults.textContent = "No se encontraron artículos.";
            resultsContainer.appendChild(noResults);
            return;
        }

        articulos.forEach(articulo => {
            const articuloContainer = document.createElement("div");
            articuloContainer.classList.add("articulo", "card", "mb-3", "p-2");

            const cardContent = document.createElement("div");
            cardContent.classList.add("d-flex", "align-items-start");
            articuloContainer.appendChild(cardContent);

            const img = document.createElement("img");
            img.src = `${articulo.formato},${articulo.fotografia}`;
            img.alt = articulo.nombre;
            img.width = 100;
            img.classList.add("me-3");
            cardContent.appendChild(img);

            const cardBody = document.createElement("div");
            cardBody.classList.add("d-flex");
            cardContent.appendChild(cardBody);

            const titleContainer = document.createElement("div");
            titleContainer.classList.add("d-flex", "align-items-center", "justify-content-start", "mb-1");
            titleContainer.style.flexDirection = "column";
            cardBody.appendChild(titleContainer);

            const middleContainer = document.createElement("div");
            middleContainer.classList.add("d-flex", "align-items-center", "justify-content-start","mb-1","mt-2");
            cardBody.appendChild(middleContainer);

            const nombre = document.createElement("h5");
            nombre.textContent = articulo.nombre;
            nombre.classList.add("card-title", "mb-0");
            titleContainer.appendChild(nombre);

            const descripcionContainer = document.createElement("div");
            descripcionContainer.classList.add("d-flex", "flex-column", "align-items-center", "ms-auto", "mt-2");
            middleContainer.appendChild(descripcionContainer);

            const btnDescripcion = document.createElement("button");
            btnDescripcion.textContent = "Descripción";
            btnDescripcion.classList.add("btn", "btn-secondary", "btn-sm");
            btnDescripcion.addEventListener("click", () => {
                const descripcionModal = new bootstrap.Modal(document.getElementById("descripcionModal"));
                document.getElementById("descripcionModalBody").textContent = articulo.descripcion;
                descripcionModal.show();
            });

            descripcionContainer.appendChild(btnDescripcion);

            const precio = document.createElement("p");
            precio.textContent = `Precio: $${articulo.precio}`;
            precio.classList.add("card-text", "mb-auto");
            titleContainer.appendChild(precio);

            const actionsContainer = document.createElement("div");
            actionsContainer.classList.add("ms-auto", "d-flex", "flex-column", "align-items-end");
            cardContent.appendChild(actionsContainer);

            if (articulo.cantidadAlmacen === 0) {
                const noStockIcon = document.createElement("i");
                noStockIcon.classList.add("bi", "bi-exclamation-triangle-fill", "text-danger", "me-2");
                actionsContainer.appendChild(noStockIcon);

                const noStockText = document.createElement("span");
                noStockText.textContent = "Sin stock";
                noStockText.classList.add("text-danger");
                actionsContainer.appendChild(noStockText);
            } else {
                const inputGroup = document.createElement("div");
                inputGroup.classList.add("input-group", "input-group-sm", "mb-2", "w-auto");
                actionsContainer.appendChild(inputGroup);

                const inputCantidad = document.createElement("input");
                inputCantidad.type = "number";
                inputCantidad.min = 1;
                inputCantidad.max = articulo.cantidadAlmacen;
                inputCantidad.value = 1;
                inputCantidad.classList.add("form-control", "me-1");
                inputGroup.appendChild(inputCantidad);

                const btnCompra = document.createElement("button");
                btnCompra.textContent = "Compra";
                btnCompra.classList.add("btn", "btn-primary");
                // Agregar funcionalidad para manejar la compra
                btnCompra.addEventListener("click", () => {
                    agregarAlCarrito(articulo.id, parseInt(inputCantidad.value));
                });
                inputGroup.appendChild(btnCompra);
            }

            resultsContainer.appendChild(articuloContainer);
        });

    }

    async function agregarAlCarrito(articuloId, cantidad) {
        const apiPath = "/api/carrito";
        const baseUrl = "https://t9-af-2019630589.azurewebsites.net";
        const appPath = "";
        const apiUrl = baseUrl + appPath + apiPath;

        try {
            const response = await fetch(apiUrl, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ articuloId, cantidad })
            });

            if (response.ok) {
                showModalInformativo("Se agregó correctamente al carrito.");
            } else if (response.status === 400) {
                const errorMessage = await response.text(); // Cambiamos response.json() a response.text()
                showErrorModal(errorMessage);
            } else {
                showErrorModal("Ocurrió un error al agregar al carrito.");
            }
        } catch (error) {
            console.error(error);
            showErrorModal("Ocurrió un error al agregar al carrito.");
        }
    }

});
