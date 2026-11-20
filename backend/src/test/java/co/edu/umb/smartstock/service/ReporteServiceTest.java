package co.edu.umb.smartstock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ReporteServiceTest {
    @Test
    void escapaComasYComillas() {
        assertEquals("\"a,b\"", ReporteService.celda("a,b"));
        assertEquals("\"di \"\"hola\"\"\"", ReporteService.celda("di \"hola\""));
    }

    @Test
    void neutralizaFormulas() {
        assertEquals("'=SUMA(A1)", ReporteService.celda("=SUMA(A1)"));
        assertEquals("-5", ReporteService.celda("-5"));
        assertEquals("", ReporteService.celda(null));
    }
}
