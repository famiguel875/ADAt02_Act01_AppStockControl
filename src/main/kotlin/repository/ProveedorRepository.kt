package repository

import model.Proveedor
import utils.HibernateUtils
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityTransaction

class ProveedorRepository {

    private fun getEntityManager() : EntityManager {
        return HibernateUtils.getEntityManager("unidadStockControl")
    }

    // Método para guardar un proveedor en la base de datos
    fun guardarProveedor(proveedor: Proveedor): Boolean {
        val em: EntityManager = getEntityManager()
        val tx: EntityTransaction = em.transaction
        return try {
            tx.begin()
            em.persist(proveedor)
            tx.commit()
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            if (tx.isActive) tx.rollback()
            false
        } finally {
            HibernateUtils.closeEntityManager(em)
        }
    }

    // Método para obtener todos los proveedores
    fun findAll(): List<Proveedor> {
        val em: EntityManager = getEntityManager()
        return try {
            em.createQuery("SELECT p FROM Proveedor p", Proveedor::class.java).resultList
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyList()
        } finally {
            HibernateUtils.closeEntityManager(em)
        }
    }

    // Método para obtener proveedores relacionados con un producto por ID de producto
    fun findProveedoresByProductoId(idProducto: String): List<Proveedor> {
        val em: EntityManager = getEntityManager()
        return try {
            em.createQuery(
                "SELECT p FROM Proveedor p JOIN p.productos prod WHERE prod.id = :idProducto",
                Proveedor::class.java
            ).setParameter("idProducto", idProducto).resultList
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyList()
        } finally {
            HibernateUtils.closeEntityManager(em)
        }
    }
}