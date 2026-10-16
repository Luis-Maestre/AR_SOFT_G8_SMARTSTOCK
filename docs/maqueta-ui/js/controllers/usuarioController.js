/**
 * controllers/usuarioController.js
 * Equivalente frontend de UsuarioController: administra usuarios y
 * su rol asociado.
 */

document.addEventListener("DOMContentLoaded", () => {
  const tablaEl = document.getElementById("tabla-usuarios");
  if (!tablaEl) return;

  function pintar() {
    const filas = MOCK_USUARIOS.map(u => {
      const usuario = new Usuario(u);
      const estadoBadge = u.estado ? RenderView.badge("Activo", "ok") : RenderView.badge("Inactivo", "critical");
      return [
        usuario.nombreCompleto(),
        u.correo,
        Usuario.nombreRol(u.id_rol, MOCK_ROLES),
        estadoBadge,
      ];
    });

    RenderView.table("tabla-usuarios", ["Nombre", "Correo", "Rol", "Estado"], filas);
  }

  const selectRol = document.getElementById("rol-usuario");
  if (selectRol) {
    MOCK_ROLES.forEach(r => {
      const opt = document.createElement("option");
      opt.value = r.id_rol;
      opt.textContent = r.nombre;
      selectRol.appendChild(opt);
    });
  }

  const form = document.getElementById("form-usuario");
  if (form) {
    form.addEventListener("submit", (e) => {
      e.preventDefault();
      MOCK_USUARIOS.push({
        id_usuario: MOCK_USUARIOS.length + 1,
        nombre: document.getElementById("nombre-usuario").value,
        apellido: document.getElementById("apellido-usuario").value,
        correo: document.getElementById("correo-usuario").value,
        id_rol: Number(selectRol.value) || 3,
        estado: true,
      });
      form.reset();
      pintar();
      alert("Usuario registrado en el prototipo (datos en memoria, no persistentes).");
    });
  }

  pintar();
});
