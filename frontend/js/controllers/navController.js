/**
 * controllers/navController.js
 * Utilidad compartida: resalta el enlace activo del menu lateral y
 * muestra el nombre del usuario con sesion simulada en la topbar.
 */

document.addEventListener("DOMContentLoaded", () => {
  const current = window.location.pathname.split("/").pop();
  document.querySelectorAll(".sidebar nav a").forEach(a => {
    if (a.getAttribute("href").endsWith(current)) {
      a.classList.add("active");
    }
  });

  const userLabel = document.getElementById("user-label");
  const userAvatar = document.getElementById("user-avatar");
  if (userLabel) {
    const raw = sessionStorage.getItem("smartstock_usuario");
    const usuario = raw ? JSON.parse(raw) : MOCK_USUARIOS[0];
    userLabel.textContent = `${usuario.nombre} ${usuario.apellido}`;
    if (userAvatar) userAvatar.textContent = usuario.nombre.charAt(0).toUpperCase();
  }
});
