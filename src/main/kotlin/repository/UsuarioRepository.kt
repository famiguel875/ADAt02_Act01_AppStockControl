package repository

import model.Usuario
import utils.HibernateUtils
import jakarta.persistence.EntityManager

class UsuarioRepository {
    fun findByNombreUsuario(nombreUsuario: String): Usuario? {
        val em: EntityManager = HibernateUtils.getEntityManager("unidadStockControl")
        return em.find(Usuario::class.java, nombreUsuario).also {
            HibernateUtils.closeEntityManager(em)
        }
    }
}