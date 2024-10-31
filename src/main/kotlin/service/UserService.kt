package service

import model.Usuario
import model.RespuestaHTTP
import repository.UsuarioRepository

class UserService(private val usuarioRepository: UsuarioRepository) {
    fun login(username: String, password: String): RespuestaHTTP<Usuario> {
        // Permitir que "root" inicie sesión con contraseña vacía
        if (username == "root" && password.isEmpty()) {
            val rootUser = Usuario(nombreUsuario = "root", password = "")
            return RespuestaHTTP(200, "Login exitoso", rootUser)
        }

        // Si no es "root", validar el usuario normalmente
        val user = usuarioRepository.findByNombreUsuario(username) // Corrección aquí
            ?: return RespuestaHTTP(404, "Usuario no encontrado", null)

        return if (user.password == password) {
            RespuestaHTTP(200, "Login exitoso", user)
        } else {
            RespuestaHTTP(400, "Nombre de usuario o contraseña incorrectos", null)
        }
    }
}

