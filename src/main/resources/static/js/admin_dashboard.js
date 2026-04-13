
// Sidebar y lógica responsive (sin cambios)
  document.addEventListener('DOMContentLoaded', function () {
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('mainContent');
    const toggleSidebar = document.getElementById('toggleSidebar');
    const logoutBtn = document.getElementById('logoutBtn');

    toggleSidebar.addEventListener('click', function () {
      sidebar.classList.toggle('collapsed');
      mainContent.style.marginLeft = sidebar.classList.contains('collapsed') ? '80px' : '250px';
    });

    logoutBtn.addEventListener('click', function () {
      if (confirm('¿Estás seguro de que quieres cerrar sesión?')) {
        localStorage.removeItem('usuario_auth');
        localStorage.removeItem('id');
        window.location.href = '/login';
      }
    });

    if (window.innerWidth <= 768) {
      sidebar.classList.add('collapsed');
      mainContent.style.marginLeft = '0';
      toggleSidebar.addEventListener('click', function () {
        sidebar.classList.toggle('active');
      });
    }

    document.addEventListener('click', function (event) {
      if (window.innerWidth <= 768 && !sidebar.contains(event.target) && !toggleSidebar.contains(event.target)) {
        sidebar.classList.remove('active');
      }
    });

    // 🎬 Animaciones GSAP
    const tl = gsap.timeline();

    tl.from(".sidebar", {
      x: -250,
      opacity: 0,
      duration: 1,
      ease: "power2.out"
    });

    tl.from(".content-header span", {
      y: -50,
      opacity: 0,
      duration: 0.8,
      ease: "power2.out"
    }, "-=0.5");

    tl.from(".toggle-sidebar", {
      scale: 0,
      duration: 0.5,
      ease: "back.out(1.7)"
    }, "-=0.5");

    tl.from(".section-card", {
      opacity: 0,
      y: 50,
      duration: 1,
      ease: "power2.out"
    }, "-=0.5");
  });