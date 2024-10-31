package model

import jakarta.persistence.*

@Entity
@Table(name = "usuarios")
class Usuario(
    @Id
    @Column(name = "nombre_usuario", nullable = false, length = 50)
    val nombreUsuario: String,

    @Column(name = "password", nullable = false, length = 20)
    var password: String
)
