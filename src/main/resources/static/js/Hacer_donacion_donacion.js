document.getElementById('donation-form').addEventListener('submit', function (e) {
    e.preventDefault();
    let id = localStorage.getItem('id');
    const formData = {
        monto: parseFloat(document.getElementById('donation-amount').value),
        fecha: new Date().toISOString().split('T')[0], // Fecha actual YYYY-MM-DD
        descripcion: `Donación con tarjeta ${document.getElementById('card-number').value.slice(-4)}`,
        usuarioId: id,
        fundacionId: parseInt(document.getElementById('create-fundacionId').value)
    };

    fetch(`http://localhost:8080/donaciones`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la petición: ' + response.statusText);
            }
            return response.json();
        })
        .then((data) => {
            alert('Donación añadida exitosamente!');
            console.log(data);
        })
        .catch(error => {
            console.error('Error al añadir la donación:', error);
            alert('Error al añadir la donación.');
        });
});

document.addEventListener('DOMContentLoaded', function () {
    const donationForm = document.getElementById('donation-form');
    const errorMessage = document.getElementById('error-message');
    const successMessage = document.getElementById('success-message');

    function showMessage(element, message) {
        element.textContent = message;
        element.style.display = 'block';
        setTimeout(() => {
            element.style.display = 'none';
        }, 5000);
    }

    donationForm.addEventListener('submit', function (event) {
        event.preventDefault();

        const userAuth = localStorage.getItem('usuario_auth');
        if (!userAuth) {
            showMessage(errorMessage, 'Debes iniciar sesión para realizar una donación.');
            return;
        }

        const donationAmount = document.getElementById('donation-amount').value.trim();
        const cardNumber = document.getElementById('card-number').value.trim();
        const cardExpiry = document.getElementById('card-expiry').value.trim();
        const cardCvv = document.getElementById('card-cvv').value.trim();

        if (!donationAmount || donationAmount <= 0 || !cardNumber || !cardExpiry || !cardCvv) {
            showMessage(errorMessage, 'Por favor, completa todos los campos correctamente.');
            return;
        }

        console.log('Donation submitted:', {
            amount: donationAmount,
            currency: document.getElementById('donation-currency').value,
            cardNumber: cardNumber,
            cardExpiry: cardExpiry,
            cardCvv: cardCvv
        });

        showMessage(successMessage, '¡Gracias por tu donación!');
        donationForm.reset();
    });
});
