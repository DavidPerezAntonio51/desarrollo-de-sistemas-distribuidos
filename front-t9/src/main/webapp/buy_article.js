import { BASE_URL } from './config.js';
document.addEventListener('DOMContentLoaded', function() {

    document.getElementById('searchForm').addEventListener('submit', async function(e) {
        e.preventDefault();

        // Obtener el término de búsqueda
        const searchTerm = document.getElementById('searchTerm').value;

        // Realizar la búsqueda
        try {
            const response = await fetch(`${BASE_URL}/api/articulo/search/${searchTerm}`);

            if (response.status === 200) {
                const articles = await response.json();

                // Limpiar los artículos anteriores
                document.getElementById('articles').innerHTML = '';

                // Mostrar los nuevos artículos
                for (const article of articles) {
                    // Crear elementos para mostrar el artículo
                    const articleDiv = document.createElement('div');
                    const nameElement = document.createElement('p');
                    const descriptionElement = document.createElement('p');
                    const priceElement = document.createElement('p');
                    const stockElement = document.createElement('p');
                    const imageElement = document.createElement('img');
                    const quantityInput = document.createElement('input');
                    const addToCartButton = document.createElement('button');

                    // Poner la información del artículo en los elementos
                    nameElement.textContent = `Nombre: ${article.nombre}`;
                    descriptionElement.textContent = `Descripción: ${article.descripcion}`;
                    priceElement.textContent = `Precio: ${article.precio}`;
                    stockElement.textContent = `Cantidad en almacén: ${article.cantidadAlmacen}`;
                    imageElement.src = `data:${article.formato};base64,${article.fotografia}`;
                    // En la parte donde creas el input de cantidad, establece un valor mínimo de 1
                    quantityInput.type = 'number';
                    quantityInput.min = '1';
                    quantityInput.value = '1';
                    addToCartButton.textContent = 'Agregar al carrito';

                    // Añadir los elementos al div del artículo
                    articleDiv.appendChild(nameElement);
                    articleDiv.appendChild(descriptionElement);
                    articleDiv.appendChild(priceElement);
                    articleDiv.appendChild(stockElement);
                    articleDiv.appendChild(imageElement);
                    articleDiv.appendChild(quantityInput);
                    articleDiv.appendChild(addToCartButton);

                    // Añadir el div del artículo a la página
                    document.getElementById('articles').appendChild(articleDiv);

                    // Escuchar el clic en el botón de agregar al carrito
                    // En la parte donde escuchas el clic en el botón de agregar al carrito
                    addToCartButton.addEventListener('click', function() {
                        if (quantityInput.value < 1) {
                            alert('La cantidad no puede ser menor a 1.');
                        } else {
                            addToCart(article.id, quantityInput.value);
                        }
                    });
                }
            } else if (response.status === 404) {
                const errorMessage = await response.text();
                alert(errorMessage);
            } else {
                alert('Ocurrió un error al buscar los artículos.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Ocurrió un error al buscar los artículos.');
        }
    });

    async function addToCart(articleId, quantity) {
        // Agregar el artículo al carrito
        try {
            const response = await fetch(`${BASE_URL}/api/carrito/agregar`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    articuloId: articleId,
                    cantidad: quantity,
                }),
            });

            const message = await response.text();

            if (response.ok) {
                alert(message);
            } else {
                alert(`Error: ${message}`);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Ocurrió un error al agregar el artículo al carrito.');
        }
    }
});

