package service

import model.Proveedor
import model.RespuestaHTTP
import repository.ProveedorRepository

class ProveedorService {
    private val proveedorRepository = ProveedorRepository()

    fun altaProveedor(proveedor: Proveedor): RespuestaHTTP<Proveedor> {
        return try {
            proveedorRepository.guardarProveedor(proveedor)
            RespuestaHTTP(200, "Proveedor creado exitosamente", proveedor)
        } catch (e: Exception) {
            RespuestaHTTP(500, "Error al crear el proveedor: ${e.message}", null)
        }
    }

    fun getProveedoresProducto(idProducto: String): RespuestaHTTP<List<Proveedor>> {
        return try {
            val proveedores = proveedorRepository.findProveedoresByProductoId(idProducto)
            if (proveedores.isNotEmpty()) {
                RespuestaHTTP(200, "Proveedores encontrados", proveedores)
            } else {
                RespuestaHTTP(404, "No se encontraron proveedores para el producto con ID: $idProducto", emptyList())
            }
        } catch (e: Exception) {
            RespuestaHTTP(500, "Error al obtener los proveedores del producto: ${e.message}", emptyList())
        }
    }

    fun getTodosProveedores(): RespuestaHTTP<List<Proveedor>> {
        return try {
            val proveedores = proveedorRepository.findAll()
            if (proveedores.isNotEmpty()) {
                RespuestaHTTP(200, "Proveedores obtenidos correctamente", proveedores)
            } else {
                RespuestaHTTP(404, "No se encontraron proveedores en el sistema", emptyList())
            }
        } catch (e: Exception) {
            RespuestaHTTP(500, "Error al obtener todos los proveedores: ${e.message}", emptyList())
        }
    }
}
