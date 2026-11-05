package co.edu.umb.smartstock.service;

import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.model.Alerta;
import co.edu.umb.smartstock.model.Reposicion;
import co.edu.umb.smartstock.repository.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteService {
    private final InventarioService inventarioService;
    private final AlertaRepository alertas;
    private final ReposicionRepository reposiciones;
    private final MovimientoInventarioRepository movimientos;

    public ReporteService(InventarioService inventarioService, AlertaRepository alertas,
                          ReposicionRepository reposiciones, MovimientoInventarioRepository movimientos) {
        this.inventarioService = inventarioService;
        this.alertas = alertas;
        this.reposiciones = reposiciones;
        this.movimientos = movimientos;
    }

    @Transactional(readOnly = true)
    public DashboardDto dashboard() {
        List<InventarioDto> inv = inventarioService.listar(false);
        long bajos = inv.stream().filter(i -> !"ok".equals(i.nivel())).count();
        BigDecimal valor = inv.stream().map(InventarioDto::valor).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new DashboardDto(inv.size(), bajos, alertas.countByEstado(Alerta.PENDIENTE),
                reposiciones.countByEstado(Reposicion.SUGERIDA), valor,
                movimientos.countByFechaGreaterThanEqual(LocalDate.now().atStartOfDay()));
    }

    @Transactional(readOnly = true)
    public byte[] inventarioCsv() {
        StringBuilder sb = new StringBuilder("﻿Codigo,Producto,Categoria,Existencia,Stock minimo,Nivel,Ubicacion,Valor\n");
        for (InventarioDto i : inventarioService.listar(false)) {
            sb.append(fila(i.codigo(), i.producto(), i.categoria(), String.valueOf(i.cantidadActual()),
                    String.valueOf(i.stockMinimo()), i.nivel(), i.ubicacion(), i.valor().toPlainString()));
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    public byte[] movimientosCsv(LocalDate desde, LocalDate hasta) {
        StringBuilder sb = new StringBuilder("﻿Fecha,Tipo,Codigo,Producto,Cantidad,Motivo,Usuario\n");
        for (MovimientoDto m : inventarioService.listarMovimientos(null, null, desde, hasta, 5000)) {
            sb.append(fila(String.valueOf(m.fecha()), m.tipoMovimiento(), m.codigo(), m.producto(),
                    String.valueOf(m.cantidad()), m.motivo(), m.usuario()));
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String fila(String... campos) {
        return java.util.Arrays.stream(campos).map(ReporteService::celda).collect(Collectors.joining(",")) + "\n";
    }

    /** Escapa comillas/comas y neutraliza formulas (=, +, -, @) para que Excel no las ejecute. */
    static String celda(String valor) {
        if (valor == null) {
            return "";
        }
        String v = valor;
        if (!v.isEmpty() && "=+-@".indexOf(v.charAt(0)) >= 0 && !v.matches("-?\\d+(\\.\\d+)?")) {
            v = "'" + v;
        }
        if (v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r")) {
            v = "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }
}
