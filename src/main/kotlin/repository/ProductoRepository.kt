package repository

import model.Producto
import utils.HibernateUtils
import jakarta.persistence.EntityManager

class ProductoRepository {

    private fun getEntityManager() : EntityManager {
        return HibernateUtils.getEntityManager("unidadStockControl")
    }

    fun saveProducto(producto: Producto): Boolean {
        val em: EntityManager = getEntityManager()
        return try {
            em.transaction.begin()

            // Asegúrate de que el proveedor está administrado por la sesión
            if (producto.proveedor?.id != null) {
                producto.proveedor = em.merge(producto.proveedor)
            } else if (producto.proveedor != null) {
                em.persist(producto.proveedor)
            } else {
                // Si proveedor es null, tal vez quieras lanzar una excepción o manejarlo
                throw IllegalArgumentException("Proveedor no puede ser null")
            }

            em.persist(producto)
            em.transaction.commit()
            true
        } catch (e: Exception) {
            em.transaction.rollback()
            e.printStackTrace()
            false
        } finally {
            HibernateUtils.closeEntityManager(em)
        }
    }


    fun findById(id: String): Producto? {
        val em: EntityManager = getEntityManager()
        return em.find(Producto::class.java, id).also {
            HibernateUtils.closeEntityManager(em)
        }
    }

    fun deleteProducto(id: String): Boolean {
        val em: EntityManager = getEntityManager()
        return try {
            val producto = em.find(Producto::class.java, id)
            if (producto != null) {
                em.transaction.begin()
                em.remove(producto)
                em.transaction.commit()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            em.transaction.rollback()
            e.printStackTrace()
            false
        } finally {
            HibernateUtils.closeEntityManager(em)
        }
    }

    fun updateProducto(producto: Producto): Boolean {
        val em: EntityManager = getEntityManager()
        return try {
            em.transaction.begin()
            em.merge(producto)
            em.transaction.commit()
            true
        } catch (e: Exception) {
            em.transaction.rollback()
            e.printStackTrace()
            false
        } finally {
            HibernateUtils.closeEntityManager(em)
        }
    }

    fun findProductosConStock(): List<Producto> {
        val em: EntityManager = getEntityManager()
        return em.createQuery("SELECT p FROM Producto p WHERE p.stock > 0", Producto::class.java)
            .resultList.also {
                HibernateUtils.closeEntityManager(em)
            }
    }

    fun findProductosSinStock(): List<Producto> {
        val em: EntityManager = getEntityManager()
        return em.createQuery("SELECT p FROM Producto p WHERE p.stock = 0", Producto::class.java)
            .resultList.also {
                HibernateUtils.closeEntityManager(em)
            }
    }
}