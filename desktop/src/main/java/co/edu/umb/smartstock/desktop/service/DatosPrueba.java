package co.edu.umb.smartstock.desktop.service;

import co.edu.umb.smartstock.desktop.model.ProductoDto;
import java.util.List;

public class DatosPrueba {

    public static List<ProductoDto> productos() {
        return List.of(
            new ProductoDto(1L, "MED-001", "Acetaminofén 500 mg", "Analgésicos", 50),
            new ProductoDto(2L, "MED-002", "Ibuprofeno 400 mg", "Antiinflamatorios", 40),
            new ProductoDto(3L, "MED-003", "Amoxicilina 500 mg", "Antibióticos", 30),
            new ProductoDto(4L, "MED-004", "Loratadina 10 mg", "Antialérgicos", 25)
        );
    }
}
