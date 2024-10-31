package model

data class RespuestaHTTP<T>(var codigo: Int, var mensaje: String, var obj: T?)