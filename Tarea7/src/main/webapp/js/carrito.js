const carritoItems = document.getElementById("articulosCarrito");
const compraTotal = document.getElementById("totalCompra");
const eliminarBtnCarrito = document.getElementById("btnEliminarCarrito");

const baseDireccion = window.location.origin;
const rutaApp = window.location.pathname.split("/").slice(0, -1).join("/");
const rutaApi = "/api/basket";
const urlApi = baseDireccion + rutaApp + rutaApi;

function obtenerCarrito() {
    fetch(urlApi, {method: "GET"})
        .then(respuesta => {
            if (!respuesta.ok) {
                throw new Error(respuesta.statusText);
            }
            return respuesta.status === 204 ? [] : respuesta.json();
        })
        .then(carrito => {
            carritoItems.innerHTML = "";
            let sumaTotal = 0;

            carrito.forEach(item => {
                const fila = document.createElement("tr");

                const imagen = document.createElement("img");
                imagen.src = `${item.product.format},${item.product.photo}`;
                imagen.alt = item.product.name;
                imagen.width = 100;
                const celdaImagen = document.createElement("td");
                celdaImagen.appendChild(imagen);
                fila.appendChild(celdaImagen);

                const nombre = document.createElement("td");
                nombre.textContent = item.product.name;
                fila.appendChild(nombre);

                const cantidad = document.createElement("td");
                const cantidadEntrada = document.createElement("input");
                cantidadEntrada.type = "number";
                cantidadEntrada.value = item.quantity;
                cantidadEntrada.min = 1;
                cantidadEntrada.readOnly = true;
                cantidad.appendChild(cantidadEntrada);
                fila.appendChild(cantidad);

                const precio = document.createElement("td");
                precio.textContent = `$${item.product.price}`;
                fila.appendChild(precio);

                const costo = document.createElement("td");
                costo.textContent = `$${item.product.price * item.quantity}`;
                fila.appendChild(costo);

                const acciones = document.createElement("td");

                const btnEditar = document.createElement("button");
                btnEditar.textContent = "Modificar";
                btnEditar.classList.add("btn", "btn-warning", "btn-sm");
                btnEditar.addEventListener("click", () => {
                    cantidadEntrada.readOnly = false;

                    acciones.innerHTML = "";
                    const btnAceptar = document.createElement("button");
                    btnAceptar.textContent = "Aceptar";
                    btnAceptar.classList.add("btn", "btn-success", "btn-sm");
                    btnAceptar.addEventListener("click", () => {
                        cambiarCantidadCarrito(item.id, item.product.id, parseInt(cantidadEntrada.value));
                    });
                    acciones.appendChild(btnAceptar);

                    const btnCancelar = document.createElement("button");
                    btnCancelar.textContent = "Cancelar";
                    btnCancelar.classList.add("btn", "btn-danger", "btn-sm");
                    btnCancelar.addEventListener("click", () => {
                        cantidadEntrada.readOnly = true;
                        cantidadEntrada.value = item.cantidad;
                        acciones.innerHTML = "";
                        acciones.appendChild(btnEditar);
                        acciones.appendChild(btnEliminar);
                    });
                    acciones.appendChild(btnCancelar);
                });
                acciones.appendChild(btnEditar);

                const btnEliminar = document.createElement("button");
                btnEliminar.textContent = "Eliminar";
                btnEliminar.classList.add("btn", "btn-danger", "btn-sm");
                btnEliminar.addEventListener("click", () => {
                    quitarArticuloCarrito(item.id);
                });
                acciones.appendChild(btnEliminar);

                fila.appendChild(acciones);

                carritoItems.appendChild(fila);
                sumaTotal += item.product.price * item.quantity;
            });

            compraTotal.textContent = `$${sumaTotal.toFixed(2)}`;
        })
        .catch(error => {
            console.error("Error al cargar el carrito:", error);
            alert("No se pudo cargar el carrito de compra.");
        });
}

function cambiarCantidadCarrito(idCarrito, idArticulo, cantidad) {
    fetch(`${urlApi}/update/${idCarrito}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            productId: idArticulo,
            quantity:cantidad
        })
    })
        .then(respuesta => {
            if (!respuesta.ok) {
                return respuesta.text().then(textoError => {
                    throw new Error(textoError);
                });
            }
            return null;
        })
        .then(() => {
            alert("Cantidad de artículos actualizada correctamente");
            obtenerCarrito();
        })
        .catch(error => {
            console.error("Error al actualizar la cantidad en el carrito:", error);
            alert(error.message);
        });
}

function limpiarCarrito() {
    fetch(`${urlApi}/clear`, {
        method: "DELETE"
    })
        .then(respuesta => {
            if (!respuesta.ok) {
                throw new Error(respuesta.statusText);
            }
            alert("Carrito eliminado completamente");
            obtenerCarrito();
        })
        .catch(error => {
            console.error("Error al eliminar el carrito:", error);
            alert("No se pudo eliminar el carrito de compra.");
        });
}

function quitarArticuloCarrito(idCarrito) {
    fetch(`${urlApi}/${idCarrito}`, {
        method: "DELETE"
    })
        .then(respuesta => {
            if (!respuesta.ok) {
                return respuesta.text().then(textoError => {
                    throw new Error(textoError);
                });
            }
            alert("Artículo eliminado con éxito");
            obtenerCarrito();
        })
        .catch(error => {
            console.error("Error al eliminar el artículo del carrito:", error);
            alert(error.message);
        });
}

obtenerCarrito();

eliminarBtnCarrito.addEventListener("click", limpiarCarrito);
