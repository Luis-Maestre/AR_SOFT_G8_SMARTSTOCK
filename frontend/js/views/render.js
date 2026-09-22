/**
 * views/render.js
 * Capa de Vista — funciones puras que reciben datos y devuelven o
 * insertan HTML en el DOM. No contienen logica de negocio: solo
 * presentacion. Los Controladores son quienes las invocan.
 */

const RenderView = {

  /** Pinta un badge segun estado (ok | warning | critical | neutral). */
  badge(texto, estado) {
    const clase = { ok: "badge-ok", warning: "badge-warning", critical: "badge-critical" }[estado] || "badge-neutral";
    return `<span class="badge ${clase}">${texto}</span>`;
  },

  /** Pinta las 4 tarjetas de estadisticas del dashboard. */
  statCards(containerId, stats) {
    const el = document.getElementById(containerId);
    if (!el) return;
    el.innerHTML = stats.map(s => `
      <div class="stat-card ${s.estado ? 'is-' + s.estado : ''}">
        <div class="label">${s.label}</div>
        <div class="value">${s.value}</div>
      </div>
    `).join("");
  },

  /** Pinta una tabla generica a partir de columnas y filas ya formateadas. */
  table(containerId, columnas, filas) {
    const el = document.getElementById(containerId);
    if (!el) return;
    if (filas.length === 0) {
      el.innerHTML = `<p class="text-muted">No hay registros para mostrar todavia.</p>`;
      return;
    }
    const head = columnas.map(c => `<th>${c}</th>`).join("");
    const body = filas.map(fila => `<tr>${fila.map(c => `<td>${c}</td>`).join("")}</tr>`).join("");
    el.innerHTML = `<table><thead><tr>${head}</tr></thead><tbody>${body}</tbody></table>`;
  },

  /** Pinta la lista de alertas activas. */
  alertList(containerId, alertas, productos) {
    const el = document.getElementById(containerId);
    if (!el) return;
    if (alertas.length === 0) {
      el.innerHTML = `<p class="text-muted">Sin alertas activas por el momento.</p>`;
      return;
    }
    el.innerHTML = alertas.map(a => {
      const prod = productos.find(p => p.id_producto === a.id_producto);
      const tipoEtiqueta = Alerta.etiquetaTipo(a.tipo);
      const badgeEstado = a.estado === "pendiente"
        ? this.badge("Pendiente", "warning")
        : this.badge("Atendida", "ok");
      return `
        <div class="alert-item">
          <div class="meta">
            <span class="title">${prod ? prod.nombre : "Producto"} — ${tipoEtiqueta}</span>
            <span class="sub">${a.mensaje}</span>
          </div>
          ${badgeEstado}
        </div>
      `;
    }).join("");
  },
};
