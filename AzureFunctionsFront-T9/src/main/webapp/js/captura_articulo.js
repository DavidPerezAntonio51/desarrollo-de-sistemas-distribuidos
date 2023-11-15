document.addEventListener("DOMContentLoaded", () => {
    const articuloForm = document.getElementById("articuloForm");

    articuloForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const nombre = document.getElementById("nombre").value.trim();
        const descripcion = document.getElementById("descripcion").value.trim();
        const precio = parseFloat(document.getElementById("precio").value);
        const cantidadAlmacen = parseInt(document.getElementById("cantidad").value);
        const foto = document.getElementById("foto").files[0];

        if (!nombre || !descripcion || !precio || !cantidadAlmacen || !foto) {
            showErrorModal("Todos los campos son obligatorios.");
            return;
        }

        const reader = new FileReader();
        reader.readAsDataURL(foto);
        reader.onload = async () => {
            const base64Data = reader.result.split(",");
            const payload = {
                id: null,
                nombre: nombre,
                descripcion: descripcion,
                precio: precio,
                cantidadAlmacen: cantidadAlmacen,
                formato: base64Data[0],
                fotografia: base64Data[1]
            };

            const apiPath = "/api/articulos";
            const baseUrl = "https://t9-af-2019630589.azurewebsites.net";
            const appPath = "";
            const apiUrl = baseUrl + appPath + apiPath;

            try {
                const response = await fetch(apiUrl, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    showModalInformativo("Artículo guardado exitosamente.");
                    articuloForm.reset();
                } else {
                    showErrorModal("Ocurrió un error al guardar el artículo.");
                }
            } catch (error) {
                console.error(error);
                showErrorModal("Ocurrió un error al guardar el artículo.");
            }
        };
    });
});
