package model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "proveedores")
class Proveedor(
    @Id
    @Column(nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true, length = 50)
    var nombre: String,

    @Column(nullable = false)
    var direccion: String,

    @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val productos: MutableList<Producto> = mutableListOf()
) {
    override fun toString(): String {
        return """
            Proveedor(
                id='$id',
                nombre='$nombre',
                direccion='$direccion'
            )
        """.trimIndent()
    }
}