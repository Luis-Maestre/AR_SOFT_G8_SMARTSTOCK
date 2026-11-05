import unittest
from datetime import date, timedelta

from app.forecast import (MODELO_MEDIA, MODELO_REGLA, predecir, serie_diaria)

HOY = date(2026, 11, 5)


def historial(dias, f):
    return [(HOY - timedelta(days=dias - 1 - i), f(i)) for i in range(dias)]


class SerieTests(unittest.TestCase):
    def test_rellena_huecos_con_cero(self):
        y, ini = serie_diaria([(HOY - timedelta(days=4), 3), (HOY, 5)], HOY)
        self.assertEqual(list(y), [3, 0, 0, 0, 5])
        self.assertEqual(ini, HOY - timedelta(days=4))

    def test_ignora_futuro_y_negativos(self):
        y, ini = serie_diaria([(HOY + timedelta(days=1), 9), (HOY, -2)], HOY)
        self.assertEqual(len(y), 0)
        self.assertIsNone(ini)

    def test_suma_varias_salidas_del_mismo_dia(self):
        y, _ = serie_diaria([(HOY, 2), (HOY, 3)], HOY)
        self.assertEqual(list(y), [5])


class PrediccionTests(unittest.TestCase):
    def test_sin_historial_usa_regla_minimo(self):
        r = predecir(1, [], existencia=10, stock_minimo=20, hoy=HOY)
        self.assertEqual(r.modelo, MODELO_REGLA)
        self.assertEqual(r.cantidad_sugerida, 30)  # 2*20 - 10

    def test_sin_historial_y_stock_suficiente_no_sugiere(self):
        r = predecir(1, [], existencia=50, stock_minimo=20, hoy=HOY)
        self.assertEqual(r.cantidad_sugerida, 0)

    def test_demanda_constante(self):
        r = predecir(1, historial(30, lambda i: 4), existencia=10, stock_minimo=5, hoy=HOY, horizonte_dias=14)
        self.assertAlmostEqual(r.demanda_horizonte, 56, delta=3)
        # 56 + 5 - 10 = 51
        self.assertAlmostEqual(r.cantidad_sugerida, 51, delta=3)
        self.assertIsNotNone(r.mae)

    def test_historial_corto_usa_media_movil(self):
        r = predecir(1, historial(10, lambda i: 2), existencia=0, stock_minimo=0, hoy=HOY, horizonte_dias=7)
        self.assertEqual(r.modelo, MODELO_MEDIA)
        self.assertEqual(r.cantidad_sugerida, 14)
        self.assertIsNone(r.mae)

    def test_existencia_sobrada_da_cero(self):
        r = predecir(1, historial(30, lambda i: 1), existencia=1000, stock_minimo=5, hoy=HOY)
        self.assertEqual(r.cantidad_sugerida, 0)

    def test_nunca_negativo_ni_nan(self):
        r = predecir(1, historial(40, lambda i: max(0, 20 - i)), existencia=0, stock_minimo=0, hoy=HOY)
        self.assertGreaterEqual(r.cantidad_sugerida, 0)
        self.assertEqual(r.demanda_horizonte, r.demanda_horizonte)  # no NaN

    def test_tendencia_creciente_gana_a_la_media(self):
        r = predecir(1, historial(45, lambda i: i), existencia=0, stock_minimo=0, hoy=HOY, horizonte_dias=7)
        media = sum(range(38, 45)) / 7 * 7  # media de la última semana * 7
        self.assertGreaterEqual(r.demanda_horizonte, media * 0.95)

    def test_horizonte_invalido(self):
        with self.assertRaises(ValueError):
            predecir(1, [], 0, 0, HOY, horizonte_dias=0)


if __name__ == "__main__":
    unittest.main()
