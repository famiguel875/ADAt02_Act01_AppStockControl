package service

import model.RespuestaHTTP
import model.Producto
import repository.ProductoRepository

class ProductoService(private val productoRepository: ProductoRepository) {

    fun altaProducto(producto: Producto): RespuestaHTTP<Producto> {
        return if (productoRepository.saveProducto(producto)) {
            RespuestaHTTP(200, "Producto creado exitosamente", producto)
        } else {
            RespuestaHTTP(500, "Error al crear el producto", null)
        }
    }

    fun bajaProducto(id: String): RespuestaHTTP<Unit> {
        return if (productoRepository.deleteProducto(id)) {
            RespuestaHTTP(200, "Producto eliminado exitosamente", Unit)
        } else {
            RespuestaHTTP(404, "Producto no encontrado", null)
        }
    }

    fun modificarNombreProducto(id: String, nuevoNombre: String): RespuestaHTTP<Producto> {
        val producto = productoRepository.findById(id) ?: return RespuestaHTTP(404, "Producto no encontrado", null)
        producto.nombre = nuevoNombre
        return if (productoRepository.updateProducto(producto)) {
            RespuestaHTTP(200, "Nombre actualizado exitosamente", producto)
        } else {
            RespuestaHTTP(500, "Error al actualizar el nombre", null)
        }
    }

    fun modificarStockProducto(id: String, nuevoStock: Int): RespuestaHTTP<Producto> {
        val producto = productoRepository.findById(id) ?: return RespuestaHTTP(404, "Producto no encontrado", null)
        producto.stock = nuevoStock
        return if (productoRepository.updateProducto(producto)) {
            RespuestaHTTP(200, "Stock actualizado exitosamente", producto)
        } else {
            RespuestaHTTP(500, "Error al actualizar el stock", null)
        }
    }

    fun getProducto(id: String): RespuestaHTTP<Producto> {
        val producto = productoRepository.findById(id)
        return if (producto != null) {
            RespuestaHTTP(200, "Producto encontrado", producto)
        } else {
            RespuestaHTTP(404, "Producto no encontrado", null)
        }
    }

    fun getProductosConStock(): RespuestaHTTP<List<Producto>> {
        val productos = productoRepository.findProductosConStock()
        return RespuestaHTTP(200, "Productos con stock obtenidos", productos)
    }

    fun getProductosSinStock(): RespuestaHTTP<List<Producto>> {
        val productos = productoRepository.findProductosSinStock()
        return RespuestaHTTP(200, "Productos sin stock obtenidos", productos)
    }
}