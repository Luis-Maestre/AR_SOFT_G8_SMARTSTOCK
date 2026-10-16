/**
 * models/usuario.js
 * Capa de Modelo — representa la entidad Usuario y su Rol asociado.
 * La autenticacion en este prototipo es simulada (sin backend real).
 */

class Usuario {
  constructor({ id_usuario, nombre, apellido, correo, id_rol, estado }) {
    this.id_usuario = id_usuario;
    this.nombre = nombre;
    this.apellido = apellido;
    this.correo = correo;
    this.id_rol = id_rol;
    this.estado = estado;
  }

  nombreCompleto() {
    return `${this.nombre} ${this.apellido}`;
  }

  static nombreRol(idRol, roles) {
    const r = roles.find(x => x.id_rol === idRol);
    return r ? r.nombre : "Sin rol";
  }
}
