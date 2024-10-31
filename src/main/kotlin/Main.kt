package appstockcontrol

import model.*
import service.*
import repository.*
import java.util.*
import kotlin.system.exitProcess

fun main() {
    /*
    Declaro aquí variables que voy a usar durante la ejecución del main
     */
    val scan = Scanner(System.`in`)
    var login = false  // Variable para comprobar si se hace un login correcto o no
    var usuario = Usuario("root", "") // Variable para almacenar al usuario que se ha logado

    /*
    Inicializamos los repositorios y servicios necesarios
     */
    val usuarioRepository = UsuarioRepository()
    val productoRepository = ProductoRepository()
    val proveedorRepository = ProveedorRepository()

    val userService = UserService(usuarioRepository)
    val productoService = ProductoService(productoRepository)
    val proveedorService = ProveedorService()

    /*
    1A PARTE. LOGIN
    */
    do {
        try {
            Thread.sleep(500)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        println(
            """
            ******************************************************
            ****    Bienvenid@ a StockControl               ******
            ******************************************************
                            
            Introduzca su usuario y contrasena para continuar (0 para salir)
            """.trimIndent()
        )
        print("user: ")
        val userInput = scan.nextLine()

        if ("0".equals(userInput, ignoreCase = true)) {
            println("Saliendo...")
            exitProcess(0)
        } else {
            print("password: ")
            val passwordInput = scan.nextLine()

            val respuestaHTTP = userService.login(userInput, passwordInput)

            try {
                if (respuestaHTTP.codigo == 200) {
                    respuestaHTTP.obj?.let {
                        println("Bienvenid@")
                        usuario = it
                        login = true
                    } ?: run {
                        System.err.println("¡INTRODUCE EL OBJETO EN LA RESPUESTA HTTP DESDE EL CONTROLLER!")
                    }
                } else {
                    println("Error en el login\n\t-codigo ${respuestaHTTP.codigo}\n\t-${respuestaHTTP.mensaje}")
                }
            } catch (e: Exception) {
                println("Error controlado")
            }
        }
    } while (!login)

    /*
    2A PARTE. GESTION DE STOCK
     */
    var opc: String

    do {
        println(
            """
            ******************************************************
            ****            APP STOCK CONTROL               ******
            ******************************************************
                            
            1. Alta producto
            2. Baja producto
            3. Modificar nombre producto
            4. Modificar stock producto
            5. Get producto por id
            6. Get productos con stock
            7. Get productos sin stock
            8. Get proveedores de un producto
            9. Get todos los proveedores
            0. Salir
            """.trimIndent()
        )
        print("Seleccione una opción: ")
        opc = scan.nextLine()

        try {
            when (opc) {
                "1" -> altaProducto(productoService, proveedorService)
                "2" -> bajaProducto(productoService)
                "3" -> modificarNombreProducto(productoService)
                "4" -> modificarStockProducto(productoService)
                "5" -> getProductoPorId(productoService)
                "6" -> getProductosConStock(productoService)
                "7" -> getProductosSinStock(productoService)
                "8" -> getProveedoresDeUnProducto(proveedorService)
                "9" -> getTodosLosProveedores(proveedorService)
                "0" -> println("Saliendo...")
                else -> println("Error en la elección")
            }
        }
        catch (e: Exception) {
            println("ERROR CONTROLADO")
        }
    } while (opc != "0")
}

fun altaProducto(productoService: ProductoService, proveedorService: ProveedorService) {
    val scan = Scanner(System.`in`)

    println("1. Alta producto")

    println("DETALLES PRODUCTO")
    print("categoria: ")
    val categoriaProducto = scan.nextLine()
    print("nombre: ")
    val nombreProducto = scan.nextLine()
    print("precio sin IVA: ")
    val precioSinIva = scan.nextLine().toDoubleOrNull() ?: 0.0
    print("descripcion: ")
    val descripcionProducto = scan.nextLine()
    print("stock inicial: ")
    val stockInicial = scan.nextLine().toIntOrNull() ?: 0

    println("DETALLES PROVEEDOR")
    print("nombre del proveedor: ")
    val nombreProveedor = scan.nextLine()
    print("dirección del proveedor: ")
    val direccionProveedor = scan.nextLine()

    // Crear el objeto Proveedor
    val proveedor = Proveedor(
        nombre = nombreProveedor,
        direccion = direccionProveedor
    )

    // Llamar al servicio para dar de alta el proveedor y guardar el proveedor
    val respuestaProveedor = proveedorService.altaProveedor(proveedor)
    if (respuestaProveedor.codigo != 200) {
        println("Error al guardar el proveedor\n\t-codigo ${respuestaProveedor.codigo}\n\t-${respuestaProveedor.mensaje}")
        return // Salir de la función si el proveedor no se guarda correctamente
    }

    // Crear el id único del Producto
    val productoId = Producto.generarIdProducto(categoriaProducto, nombreProducto, proveedor)

    // Crear el objeto Producto con todos los parámetros requeridos
    val nuevoProducto = Producto(
        id = productoId,
        nombre = nombreProducto,
        categoria = categoriaProducto,
        precioSinIva = precioSinIva,
        descripcion = descripcionProducto,
        stock = stockInicial,
        proveedor = proveedor
    )

    // Llamar al servicio para dar de alta el producto
    val respuestaProducto = productoService.altaProducto(nuevoProducto)
    if (respuestaProducto.codigo == 200) {
        println("PRODUCTO INSERTADO CORRECTAMENTE\n${respuestaProducto.obj}")
    } else {
        println("Error en la operacion\n\t-codigo ${respuestaProducto.codigo}\n\t-${respuestaProducto.mensaje}")
    }
}

fun bajaProducto(productoService: ProductoService) {
    val scan = Scanner(System.`in`)
    println("2. Baja producto")

    print("Introduzca el id del producto: ")
    val idProducto = scan.nextLine()
    val respuesta = productoService.bajaProducto(idProducto)

    if (respuesta.codigo == 200) {
        println("OPERACION EXITOSA")
    } else {
        println("Error en la operacion\n\t-codigo ${respuesta.codigo}\n\t-${respuesta.mensaje}")
    }
}

fun modificarNombreProducto(productoService: ProductoService) {
    val scan = Scanner(System.`in`)
    println("3. Modificar nombre producto")
    print("Introduzca el id del producto: ")
    val idProducto = scan.nextLine()
    print("Introduzca el nuevo nombre del producto: ")
    val nuevoNombre = scan.nextLine()
    val respuesta = productoService.modificarNombreProducto(idProducto, nuevoNombre)

    if (respuesta.codigo == 200) {
        println("OPERACION EXITOSA")
    } else {
        println("Error en la operacion\n\t-codigo ${respuesta.codigo}\n\t-${respuesta.mensaje}")
    }
}

fun modificarStockProducto(productoService: ProductoService) {
    val scan = Scanner(System.`in`)
    println("4. Modificar stock producto")

    print("Introduzca el id del producto: ")
    val idProducto = scan.nextLine()
    print("Introduzca el nuevo stock: ")
    val nuevoStock = try {
        scan.nextLine().toInt()
    } catch (e: NumberFormatException) {
        println("Error: el stock debe ser un número entero.")
        return
    }

    // Llamada al servicio para modificar el stock
    val respuesta = productoService.modificarStockProducto(idProducto, nuevoStock)

    // Comprobar la respuesta
    if (respuesta.codigo == 200) {
        println("OPERACION EXITOSA")
    } else {
        println("Error en la operacion\n\t-codigo ${respuesta.codigo}\n\t-${respuesta.mensaje}")
    }
}

fun getProductoPorId(productoService: ProductoService) {
    val scan = Scanner(System.`in`)
    println("5. Get producto por id")

    print("Introduzca el id del producto: ")
    val idProducto = scan.nextLine()
    val respuesta = productoService.getProducto(idProducto)

    if (respuesta.codigo == 200) {
        println("OPERACION EXITOSA")
        println(respuesta.obj)
    } else {
        println("Error en la operacion\n\t-codigo ${respuesta.codigo}\n\t-${respuesta.mensaje}")
    }
}

fun getProductosConStock(productoService: ProductoService) {
    println("6. Get productos con stock")
    val respuesta = productoService.getProductosConStock()

    if (respuesta.codigo == 200) {
        println("OPERACION EXITOSA")
        respuesta.obj?.forEach { producto ->
            println(producto)
        }
    } else {
        println("Error en la operacion\n\t-codigo ${respuesta.codigo}\n\t-${respuesta.mensaje}")
    }
}

fun getProductosSinStock(productoService: ProductoService) {
    println("7. Get productos sin stock")
    val respuesta = productoService.getProductosSinStock()

    if (respuesta.codigo == 200) {
        println("OPERACION EXITOSA")
        respuesta.obj?.forEach { producto ->
            println(producto)
        }
    } else {
        println("Error en la operacion\n\t-codigo ${respuesta.codigo}\n\t-${respuesta.mensaje}")
    }
}

fun getProveedoresDeUnProducto(proveedorService: ProveedorService) {
    val scan = Scanner(System.`in`)
    println("8. Get proveedores de un producto")

    print("Introduzca el id del producto: ")
    val idProducto = scan.nextLine()
    val respuesta = proveedorService.getProveedoresProducto(idProducto)

    if (respuesta.codigo == 200) {
        println("OPERACION EXITOSA")
        respuesta.obj?.forEach { proveedor ->
            println(proveedor)
        }
    } else {
        println("Error en la operacion\n\t-codigo ${respuesta.codigo}\n\t-${respuesta.mensaje}")
    }
}

fun getTodosLosProveedores(proveedorService: ProveedorService) {
    println("9. Get todos los proveedores")

    val respuesta = proveedorService.getTodosProveedores()

    if (respuesta.codigo == 200) {
        println("OPERACION EXITOSA")
        respuesta.obj?.forEach { proveedor ->
            println(proveedor)
        }
    } else {
        println("Error en la operacion\n\t-codigo ${respuesta.codigo}\n\t-${respuesta.mensaje}")
    }
}