/**
 * controllers/authController.js
 * Equivalente frontend de AuthController: procesa el formulario de
 * inicio de sesion. En este prototipo no hay backend, por lo que
 * valida contra MOCK_USUARIOS unicamente para simular el flujo.
 */

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("login-form");
  if (!form) return;

  form.addEventListener("submit", (e) => {
    e.preventDefault();
    const correo = document.getElementById("correo").value.trim();
    const usuario = MOCK_USUARIOS.find(u => u.correo === correo) || MOCK_USUARIOS[0];

    // Simula sesion guardando el usuario activo para esta pestaña.
    sessionStorage.setItem("smartstock_usuario", JSON.stringify(usuario));
    window.location.href = "pages/dashboard.html";
  });
});
