import unittest

try:
    from fastapi.testclient import TestClient
    from app.main import app
    HAY_FASTAPI = True
except ImportError:  # FastAPI no instalado: se omiten estas pruebas
    HAY_FASTAPI = False


@unittest.skipUnless(HAY_FASTAPI, "fastapi no instalado")
class ApiTests(unittest.TestCase):
    def setUp(self):
        self.c = TestClient(app)

    def test_health(self):
        self.assertEqual(self.c.get("/health").json(), {"estado": "ok"})

    def test_predict_sin_historial(self):
        r = self.c.post("/predict", json={"productos": [
            {"productoId": 7, "existencia": 4, "stockMinimo": 10, "historial": []}]})
        self.assertEqual(r.status_code, 200)
        p = r.json()["predicciones"][0]
        self.assertEqual(p["productoId"], 7)
        self.assertEqual(p["modelo"], "regla_minimo")
        self.assertEqual(p["cantidadSugerida"], 16)

    def test_predict_valida_entrada(self):
        r = self.c.post("/predict", json={"productos": [], "horizonteDias": 0})
        self.assertEqual(r.status_code, 422)


if __name__ == "__main__":
    unittest.main()
