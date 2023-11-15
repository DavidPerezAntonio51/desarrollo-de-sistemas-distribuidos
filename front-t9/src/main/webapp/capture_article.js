import { BASE_URL } from './config.js';
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('articleForm');

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        console.log("formulario enviado")

        // Obtener los valores de los campos
        const name = document.getElementById('name').value;
        const description = document.getElementById('description').value;
        const price = document.getElementById('price').value;
        const stock = document.getElementById('stock').value;
        const photoFile = document.getElementById('photo').files[0];

        // Validar que los campos no estén vacíos
        if (!name || !description || !price || !stock || !photoFile) {
            alert('Por favor, completa todos los campos.');
            return;
        }

        // Leer la fotografía como Base64
        const reader = new FileReader();
        reader.readAsDataURL(photoFile);
        reader.onloadend = async function() {
            const fullResult = reader.result;
            const base64photo = fullResult.replace(/^data:image\/\w+;base64,/, '');

            // Obtener el formato de la imagen
            const formato = fullResult.split(';')[0];

            // Crear el objeto artículo
            const article = {
                id: null,
                nombre: name,
                descripcion: description,
                precio: Number(price),
                cantidadAlmacen: Number(stock),
                fotografia: base64photo,
                formato: formato
            };

            // Enviar al backend
            try {
                const response = await fetch(`${BASE_URL}/api/articulo`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(article),
                });

                if (response.status === 200) {
                    alert('Artículo capturado correctamente.');
                } else {
                    alert('Error al capturar el artículo.');
                }
            } catch (error) {
                console.error('Error:', error);
                alert('Error al capturar el artículo.');
            }
        };
    });
});
