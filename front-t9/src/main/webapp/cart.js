import { BASE_URL } from './config.js';

// Función para obtener los artículos en el carrito
async function getCartItems() {
    try {
        let response = await fetch(`${BASE_URL}/api/carrito/articulos`);
        if(response.status === 200) {
            let cartItems = await response.json();
            displayCartItems(cartItems);
        }
    } catch(error) {
        console.error(`Error: ${error}`);
    }
}

// Función para desplegar los artículos en el carrito
function displayCartItems(cartItems) {
    let cartItemsContainer = document.getElementById('cartItemsContainer');
    cartItemsContainer.innerHTML = ''; // Limpiar el contenedor

    // Iterar a través de cada artículo y crear elementos HTML para ellos
    cartItems.forEach(item => {
        let cartItemDiv = document.createElement('div');
        cartItemDiv.classList.add('cart-item');

        let itemImage = document.createElement('img'); // Crear un nuevo elemento img
        itemImage.src = `data:${item.articulo.formato};base64,${item.articulo.fotografia}`; // Asegúrate de tener el formato y la fotografía en el objeto del artículo
        cartItemDiv.appendChild(itemImage);

        let itemName = document.createElement('p');
        itemName.textContent = item.articulo.nombre;
        cartItemDiv.appendChild(itemName);

        let itemQuantityInput = document.createElement('input');
        itemQuantityInput.type = 'number';
        itemQuantityInput.value = item.cantidad;
        itemQuantityInput.min = 1;
        cartItemDiv.appendChild(itemQuantityInput);

        let updateQuantityButton = document.createElement('button'); // Crear un nuevo botón
        updateQuantityButton.textContent = 'Cambiar cantidad';
        updateQuantityButton.onclick = () => updateCartItemQuantity(item.id, itemQuantityInput.value);
        cartItemDiv.appendChild(updateQuantityButton);

        let removeItemButton = document.createElement('button');
        removeItemButton.textContent = 'Eliminar';
        removeItemButton.onclick = () => removeCartItem(item.id);
        cartItemDiv.appendChild(removeItemButton);

        cartItemsContainer.appendChild(cartItemDiv);
    });
}


// Función para actualizar la cantidad de un artículo en el carrito
async function updateCartItemQuantity(cartId, quantity) {
    try {
        let response = await fetch(`${BASE_URL}/api/carrito/ajustar/${cartId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({cantidad: quantity})
        });
        let message = await response.text();
        alert(message);
        getCartItems(); // Actualizar la lista de artículos en el carrito
    } catch(error) {
        console.error(`Error: ${error}`);
    }
}

// Función para eliminar un artículo del carrito
async function removeCartItem(cartId) {
    if(confirm('¿Estás seguro de que quieres eliminar este artículo del carrito?')) {
        try {
            let response = await fetch(`${BASE_URL}/api/carrito/eliminar/${cartId}`, {
                method: 'DELETE'
            });
            let message = await response.text();
            alert(message);
            getCartItems(); // Actualizar la lista de artículos en el carrito
        } catch(error) {
            console.error(`Error: ${error}`);
        }
    }
}

// Función para vaciar el carrito
async function emptyCart() {
    if(confirm('¿Estás seguro de que quieres vaciar el carrito?')) {
        try {
            let response = await fetch(`${BASE_URL}/api/carrito/vaciar`, {
                method: 'DELETE'
            });
            let message = await response.text();
            alert(message);
            getCartItems(); // Actualizar la lista de artículos en el carrito
        } catch(error) {
            console.error(`Error: ${error}`);
        }
    }
}

// Ligar las funciones a los botones
document.getElementById('emptyCartButton').onclick = emptyCart;
document.getElementById('continueShoppingButton').onclick = () => location.href = 'buy_article.html';

// Cargar los artículos al abrir la página
getCartItems();
