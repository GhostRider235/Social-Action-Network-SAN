const elements = document.querySelectorAll('.fade-up');

const observer = new IntersectionObserver((entries) => {

    entries.forEach(entry => {

        if(entry.isIntersecting){
            entry.target.classList.add('active');
        }

    });

}, {
    threshold: 0.2
});

elements.forEach(el => {
    observer.observe(el);
});

    const saldoElement = document.getElementById("saldoUsuario");

    let saldo = saldoElement.innerText;

    saldo = Number(saldo);

    saldoElement.innerText = saldo.toLocaleString("es-CO");

