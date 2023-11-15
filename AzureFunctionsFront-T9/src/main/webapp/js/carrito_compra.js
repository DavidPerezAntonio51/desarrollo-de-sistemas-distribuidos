const articulosCarrito = document.getElementById("articulosCarrito");
const totalCompra = document.getElementById("totalCompra");
const btnEliminarCarrito = document.getElementById("btnEliminarCarrito");

const baseUrl = "https://t9-af-2019630589.azurewebsites.net";
const appPath = "";
const apiPath = "/api/carrito";
const apiUrl = baseUrl + appPath + apiPath;

function cargarCarrito() {
    // Obtener los artículos del carrito desde el backend
    fetch(apiUrl, {
        method: "GET"
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(response.statusText);
            }
            if (response.status === 204) {
                return [];
            } else {
                return response.json();
            }
        })
        .then(carrito => {
            articulosCarrito.innerHTML = "";
            let total = 0;

            carrito.forEach(articulo => {
                const row = document.createElement("tr");

                const img = document.createElement("img");
                img.src = `${articulo.articulo.formato},${articulo.articulo.fotografia}`;
                img.alt = articulo.articulo.nombre;
                img.width = 100;
                const imgCell = document.createElement("td");
                imgCell.appendChild(img);
                row.appendChild(imgCell);

                const nombre = document.createElement("td");
                nombre.textContent = articulo.articulo.nombre;
                row.appendChild(nombre);

                const cantidad = document.createElement("td");
                const cantidadInput = document.createElement("input");
                cantidadInput.type = "number";
                cantidadInput.value = articulo.cantidad;
                cantidadInput.min = 1;
                cantidadInput.readOnly = true;
                cantidad.appendChild(cantidadInput);
                row.appendChild(cantidad);

                const precio = document.createElement("td");
                precio.textContent = `$${articulo.articulo.precio}`;
                row.appendChild(precio);

                const costo = document.createElement("td");
                costo.textContent = `$${articulo.articulo.precio * articulo.cantidad}`;
                row.appendChild(costo);

                const acciones = document.createElement("td");

                const btnModificar = document.createElement("button");
                btnModificar.textContent = "Modificar";
                btnModificar.classList.add("btn", "btn-warning", "btn-sm");
                btnModificar.addEventListener("click", () => {
                    cantidadInput.readOnly = false;

                    acciones.innerHTML = "";
                    const btnAceptar = document.createElement("button");
                    btnAceptar.textContent = "Aceptar";
                    btnAceptar.classList.add("btn", "btn-success", "btn-sm");
                    btnAceptar.addEventListener("click", () => {
                        actualizarCantidadCarrito(articulo.id, articulo.articulo.id, parseInt(cantidadInput.value));
                    });
                    acciones.appendChild(btnAceptar);

                    const btnCancel = document.createElement("button");
                    btnCancel.textContent = "Cancelar";
                    btnCancel.classList.add("btn", "btn-danger", "btn-sm");
                    btnCancel.addEventListener("click", () => {
                        cantidadInput.readOnly = true;
                        cantidadInput.value = articulo.cantidad; // Restaurar el valor original de la cantidad
                        acciones.innerHTML = "";
                        acciones.appendChild(btnModificar);
                        acciones.appendChild(btnEliminar);
                    });
                    acciones.appendChild(btnCancel);
                });
                acciones.appendChild(btnModificar);

                const btnEliminar = document.createElement("button");
                btnEliminar.textContent = "Eliminar";
                btnEliminar.classList.add("btn", "btn-danger", "btn-sm");
                btnEliminar.addEventListener("click", () => {
                    eliminarArticuloCarrito(articulo.id);
                });
                acciones.appendChild(btnEliminar);

                row.appendChild(acciones);

                articulosCarrito.appendChild(row);
                total += articulo.articulo.precio * articulo.cantidad;
            });

            totalCompra.textContent = `$${total.toFixed(2)}`;
        })
        .catch(error => {
            console.error("Error al cargar el carrito:", error);
            showErrorModal("No se pudo cargar el carrito de compra.");
        });
}

function actualizarCantidadCarrito(carritoId, articuloId, cantidad) {
    fetch(`${apiUrl}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            articuloId:carritoId,
            cantidad:cantidad
        })
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(errorText => {
                    throw new Error(errorText);
                });
            }
            return null;
        })
        .then(() => {
            showModalInformativo("Cantidad de artículos actualizada correctamente");
            cargarCarrito();
        })
        .catch(error => {
            console.error("Error al actualizar la cantidad en el carrito:", error);
            showErrorModal(error.message);
        });
}

function eliminarCarrito() {
    fetch(`${apiUrl}`, {
        method: "DELETE"
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(response.statusText);
            }
            showModalInformativo("Carrito eliminado completamente");
            cargarCarrito();
        })
        .catch(error => {
            console.error("Error al eliminar el carrito:", error);
            showErrorModal("No se pudo eliminar el carrito de compra.");
        });
}

function eliminarArticuloCarrito(carritoId) {
    fetch(`${apiUrl}/${carritoId}`, {
        method: "DELETE"
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(errorText => {
                    throw new Error(errorText);
                });
            }
            showModalInformativo("Artículo eliminado con éxito");
            cargarCarrito();
        })
        .catch(error => {
            console.error("Error al eliminar el artículo del carrito:", error);
            showErrorModal(error.message);
        });
}

// Cargar el carrito al cargar la página
cargarCarrito();

btnEliminarCarrito.addEventListener("click", () => {
    // Implementar la eliminación del carrito de compra
    eliminarCarrito();
});

