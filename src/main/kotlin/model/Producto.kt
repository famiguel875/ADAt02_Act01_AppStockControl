package model

import jakarta.persistence.*
import java.util.*
import java.math.BigDecimal
import java.math.RoundingMode

@Entity
@Table(name = "productos")
class Producto(
    @Id
    @Column(name = "id", nullable = false, length = 9)
    val id: String,

    @Column(nullable = false, length = 50)
    var categoria: String,

    @Column(nullable = false, length = 50)
    var nombre: String,

    @Column
    var descripcion: String,

    @Column(name = "precio_sin_iva", nullable = false)
    var precioSinIva: Double,

    @Column(name = "precio_con_iva", nullable = false)
    var precioConIva: Double = calcularPrecioConIva(precioSinIva),

    @Column(name = "fecha_alta", nullable = false)
    var fechaAlta: Date = Date(),

    @Column(nullable = false)
    var stock: Int,

    @ManyToOne(cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @JoinColumn(name = "id_proveedor")
    var proveedor: Proveedor?
) {

    override fun toString(): String {
        return """
            Producto(
                id='$id',
                nombre='$nombre',
                categoria='$categoria',
                precioSinIva=$precioSinIva,
                descripcion='$descripcion',
                stock=$stock,
                proveedor=${proveedor?.nombre}  
            )
        """.trimIndent()
    }

    companion object {
        // Función para calcular precio con IVA y limitar a dos decimales
        private fun calcularPrecioConIva(precioSinIva: Double): Double {
            val precioConIva = precioSinIva * 1.21
            return BigDecimal(precioConIva).setScale(2, RoundingMode.HALF_UP).toDouble()
        }

        // Generación de ID a partir de los primeros caracteres
        fun generarIdProducto(categoria: String, nombre: String, proveedor: Proveedor): String {
            val catPrefix = categoria.take(3).uppercase()
            val nomPrefix = nombre.take(3).uppercase()
            val provPrefix = proveedor.nombre.take(3).uppercase()
            return "$catPrefix$nomPrefix$provPrefix"
        }
    }
}
