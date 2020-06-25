package com.nc.aspect

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

fun main() {

    println("===============Proxy===========")
    val proxiedConnection = Proxy.newProxyInstance(
        JdbcConnection::class.java.classLoader,
        arrayOf(Connection::class.java),
        ConnectionInvocationHandler(JdbcConnection())
    ) as Connection
    proxiedConnection.connect()
    proxiedConnection.disconnect()
    val statementFromProxy = proxiedConnection.preparedStatement("SELECT FROM DUAL")
    println(statementFromProxy)

    println("===============Simple===========")
    val simpleConnection = JdbcConnection()
    simpleConnection.connect()
    simpleConnection.disconnect()
    val statementFromSimple = simpleConnection.preparedStatement("SELECT FROM DUAL")
    println(statementFromSimple)
}

interface Connection {

    fun connect()

    fun disconnect()

    fun preparedStatement(query: String): PreparedStatement
}

class JdbcConnection : Connection {

    override fun connect() {
        println("JDBC Connecting...")
    }

    override fun disconnect() {
        println("JDBC Disconnecting...")
    }

    override fun preparedStatement(query: String): PreparedStatement {
        println("JDBC Preparing SQL statement...")
        return PreparedStatement(query)
    }
}

data class PreparedStatement(
    val query: String
)

class ConnectionInvocationHandler(conn: Connection) : InvocationHandler {

    private val connection = conn
    private val methods = conn::class.java.declaredMethods
        .map { it.name to it }.toMap()

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        println("Additional stuff are happening here...")
        return methods[method?.name]?.invoke(connection, *(args ?: arrayOfNulls<Any>(0)))
    }
}