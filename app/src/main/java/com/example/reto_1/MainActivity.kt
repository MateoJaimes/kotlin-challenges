package com.example.reto_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reto_1.ui.theme.Reto_1Theme
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt


// RETO 1: Lógica de login y captcha (sin cambios)

private fun calcularTermino2(codigo: String): Int {

    val d = codigo.map { it.digitToInt() }

    val objetivo = d[d.size - 2] // Penúltimo dígito

    for (a in d) {
        for (b in d) {
            for (c in d) {

                if (a * b - c == objetivo)
                    return objetivo

                if (a + b - c == objetivo)
                    return objetivo

                if (a + b + c == objetivo)
                    return objetivo

                if (a * b + c == objetivo)
                    return objetivo

                if (a + b * c == objetivo)
                    return objetivo
            }
        }
    }

    return (objetivo + objetivo) - objetivo
}

private fun generarCaptcha(codigo: String): Pair<Int, Int> {

    val termino1 = codigo.takeLast(3).toInt()

    val termino2 = calcularTermino2(codigo)

    return Pair(termino1, termino2)
}


// RETO 2: Lógica del menú recurrente y adaptativo

private fun calcularDigitosClave(codigo: String): Pair<Int, Int> {
    val d = codigo.map { it.digitToInt() }
    val penultimo = d[d.size - 2]
    val ultimo = d[d.size - 1]
    return Pair(penultimo, ultimo)
}

private data class OpcionMenu(val id: Int, val texto: String)

private val MENU_POR_DEFECTO = listOf(
    OpcionMenu(1, "Cambiar contraseña"),
    OpcionMenu(2, "Ingresar coordenadas actuales"),
    OpcionMenu(3, "Ubicar zona wifi más cercana"),
    OpcionMenu(4, "Guardar archivo con ubicación cercana"),
    OpcionMenu(5, "Actualizar registros de zonas wifi desde archivo")
)

private const val TEXTO_OPCION_FAVORITA = "Elegir opción de menú favorita"
private const val TEXTO_OPCION_CERRAR = "Cerrar sesión"

private enum class PantallaMenu {
    MENU,
    ELEGIR_FAVORITO,
    ADIVINANZA_1,
    ADIVINANZA_2,
    CAMBIAR_CONTRASENA_CONFIRMAR,
    CAMBIAR_CONTRASENA_NUEVA,
    ENTRAR_COORDENADA,
    ACTUALIZAR_COORDENADA_MENU,
    ACTUALIZAR_COORDENADA_LAT,
    ACTUALIZAR_COORDENADA_LON,
    ELEGIR_UBICACION_ACTUAL,
    ZONAS_CERCANAS,
    INDICACIONES_LLEGADA
}


// RETO 3: Tablas de validación de coordenadas e información clave

private data class RangoCoordenada(
    val municipio: String,
    val latSup: Double,
    val latInf: Double,
    val lonOr: Double,
    val lonOcc: Double
)

private val TABLA_RANGOS: Map<Int, RangoCoordenada> = mapOf(
    0 to RangoCoordenada("Leticia, Amazonas", -3.002, -4.227, -69.714, -70.365),
    1 to RangoCoordenada("Betulia, Antioquia", 6.284, 6.077, -75.841, -76.049),
    2 to RangoCoordenada("Calamar, Bolívar", 10.362, 10.103, -74.918, -75.088),
    3 to RangoCoordenada("Chita, Boyacá", 6.306, 5.888, -72.321, -72.552),
    4 to RangoCoordenada("Cajibio, Cauca", 2.766, 2.548, -76.493, -76.879),
    5 to RangoCoordenada("La Paz, Cesar", 10.462, 9.757, -72.987, -73.623),
    6 to RangoCoordenada("Tadó, Chocó", 5.413, 5.119, -76.132, -76.619),
    7 to RangoCoordenada("Suaza, Huila", 1.998, 1.740, -75.689, -75.950),
    8 to RangoCoordenada("Ortega, Tolima", 4.120, 3.746, -75.075, -75.443),
    9 to RangoCoordenada("Curití, Santander", 6.690, 6.532, -72.872, -73.120)
)

private enum class TipoInfo { NORTE, SUR, ORIENTE, OCCIDENTE, PROMEDIO }

private val TABLA_INFO_CLAVE: Map<Int, Pair<TipoInfo, TipoInfo>> = mapOf(
    0 to (TipoInfo.NORTE to TipoInfo.SUR),
    1 to (TipoInfo.NORTE to TipoInfo.ORIENTE),
    2 to (TipoInfo.NORTE to TipoInfo.OCCIDENTE),
    3 to (TipoInfo.NORTE to TipoInfo.PROMEDIO),
    4 to (TipoInfo.SUR to TipoInfo.PROMEDIO),
    5 to (TipoInfo.SUR to TipoInfo.ORIENTE),
    6 to (TipoInfo.SUR to TipoInfo.OCCIDENTE),
    7 to (TipoInfo.ORIENTE to TipoInfo.OCCIDENTE),
    8 to (TipoInfo.ORIENTE to TipoInfo.PROMEDIO),
    9 to (TipoInfo.OCCIDENTE to TipoInfo.PROMEDIO)
)

private fun promedioCoordenadas(coords: List<Pair<Double, Double>>): Pair<Double, Double> {
    val promLat = coords.sumOf { it.first } / coords.size
    val promLon = coords.sumOf { it.second } / coords.size
    return Pair(promLat, promLon)
}

private fun textoInfoClave(tipo: TipoInfo, coords: List<Pair<Double, Double>>): String {
    return when (tipo) {
        TipoInfo.NORTE -> {
            val idx = coords.indices.maxByOrNull { coords[it].first } ?: 0
            "La coordenada ${idx + 1} es la que está más al norte"
        }
        TipoInfo.SUR -> {
            val idx = coords.indices.minByOrNull { coords[it].first } ?: 0
            "La coordenada ${idx + 1} es la que está más al sur"
        }
        TipoInfo.ORIENTE -> {
            val idx = coords.indices.maxByOrNull { coords[it].second } ?: 0
            "La coordenada ${idx + 1} es la que está más al oriente"
        }
        TipoInfo.OCCIDENTE -> {
            val idx = coords.indices.minByOrNull { coords[it].second } ?: 0
            "La coordenada ${idx + 1} es la que está más al occidente"
        }
        TipoInfo.PROMEDIO -> {
            val (lat, lon) = promedioCoordenadas(coords)
            "El promedio de las coordenadas es: lat=%.3f, lon=%.3f".format(lat, lon)
        }
    }
}

private val NOMBRES_SITIOS = listOf("trabajo", "casa", "parque")


// RETO 4: Zonas wifi predefinidas, distancia y ubicación

private data class ZonaWifi(val lat: Double, val lon: Double, val usuariosProm: Int)

// Tabla de zonas wifi predefinidas según el penúltimo dígito del código
// del grupo (documento del reto 4)
private val TABLA_ZONAS: Map<Int, List<ZonaWifi>> = mapOf(
    0 to listOf(
        ZonaWifi(-3.777, -70.302, 91), ZonaWifi(-4.134, -69.983, 233),
        ZonaWifi(-4.006, -70.132, 149), ZonaWifi(-3.846, -70.222, 211)
    ),
    1 to listOf(
        ZonaWifi(6.124, -75.946, 1035), ZonaWifi(6.125, -75.966, 109),
        ZonaWifi(6.135, -75.976, 31), ZonaWifi(6.144, -75.836, 151)
    ),
    2 to listOf(
        ZonaWifi(10.127, -74.950, 0), ZonaWifi(10.196, -74.935, 0),
        ZonaWifi(10.305, -75.040, 2490), ZonaWifi(10.196, -74.935, 101)
    ),
    3 to listOf(
        ZonaWifi(6.211, -72.482, 2), ZonaWifi(6.212, -72.470, 25),
        ZonaWifi(6.105, -72.342, 25), ZonaWifi(6.210, -72.442, 50)
    ),
    4 to listOf(
        ZonaWifi(2.698, -76.680, 63), ZonaWifi(2.724, -76.693, 20),
        ZonaWifi(2.606, -76.742, 680), ZonaWifi(2.698, -76.690, 15)
    ),
    5 to listOf(
        ZonaWifi(10.348, -73.051, 0), ZonaWifi(10.171, -73.136, 0),
        ZonaWifi(10.259, -73.069, 67), ZonaWifi(10.350, -73.043, 45)
    ),
    6 to listOf(
        ZonaWifi(5.273, -76.579, 390), ZonaWifi(5.311, -76.413, 333),
        ZonaWifi(5.354, -76.204, 240), ZonaWifi(5.306, -76.332, 793)
    ),
    7 to listOf(
        ZonaWifi(1.811, -75.820, 58), ZonaWifi(1.919, -75.843, 1290),
        ZonaWifi(1.875, -75.877, 110), ZonaWifi(1.938, -75.764, 114)
    ),
    8 to listOf(
        ZonaWifi(3.942, -75.152, 59), ZonaWifi(3.482, -75.259, 45),
        ZonaWifi(3.989, -75.181, 165), ZonaWifi(3.966, -75.128, 97)
    ),
    9 to listOf(
        ZonaWifi(6.632, -72.984, 285), ZonaWifi(6.564, -73.061, 127),
        ZonaWifi(6.531, -73.002, 15), ZonaWifi(6.623, -72.978, 56)
    )
)

// Distancia entre dos puntos geográficos en km.
private fun distanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6372.795477598
    val lat1Rad = Math.toRadians(lat1)
    val lat2Rad = Math.toRadians(lat2)
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).let { it * it } +
            cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2).let { it * it }
    return 2 * r * asin(sqrt(a))
}

// Velocidades promedio (m/s) por medio de transporte.
private val VELOCIDADES_MS: Map<String, Double> = mapOf(
    "bus" to 16.67,
    "a pie" to 0.483,
    "bicicleta" to 3.33,
    "moto" to 19.44,
    "auto" to 20.83
)

// Según el último dígito del código de grupo, qué dos medios se muestran.
private val TABLA_TRANSPORTE: Map<Int, Pair<String, String>> = mapOf(
    0 to ("bus" to "moto"),
    1 to ("bus" to "a pie"),
    2 to ("bus" to "auto"),
    3 to ("bus" to "bicicleta"),
    4 to ("moto" to "bicicleta"),
    5 to ("moto" to "a pie"),
    6 to ("moto" to "auto"),
    7 to ("a pie" to "auto"),
    8 to ("a pie" to "bicicleta"),
    9 to ("auto" to "bicicleta")
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Reto_1Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { padding ->
                    AppRoot(Modifier.padding(padding))
                }
            }
        }
    }
}

//Controla el flujo general

@Composable
fun AppRoot(modifier: Modifier = Modifier) {

    var sesionIniciada by remember { mutableStateOf(false) }
    var codigoGrupo by remember { mutableStateOf("") }

    if (!sesionIniciada) {
        LoginScreen(
            modifier = modifier,
            onLoginSuccess = { codigo ->
                codigoGrupo = codigo
                sesionIniciada = true
            }
        )
    } else {
        MenuScreen(
            modifier = modifier,
            codigoGrupo = codigoGrupo,
            onVolverAlLogin = { sesionIniciada = false }
        )
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier, onLoginSuccess: (String) -> Unit) {

    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    var captcha by remember { mutableStateOf("") }
    var termino1 by remember { mutableStateOf<Int?>(null) }
    var termino2 by remember { mutableStateOf<Int?>(null) }

    var mostrarCaptcha by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }


    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            "Bienvenido al sistema de ubicación para zonas públicas WIFI"
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = usuario,
            onValueChange = {
                usuario = it
                mostrarCaptcha = false
                mensaje = ""
            },
            label = { Text("Usuario") }
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
            },
            label = { Text("Contraseña") }
        )

        Spacer(Modifier.height(20.dp))

        if (mostrarCaptcha) {

            Text("$termino1 + $termino2 = ?")

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = captcha,
                onValueChange = {
                    captcha = it
                },
                label = {
                    Text("Resultado del CAPTCHA")
                }
            )

            Spacer(Modifier.height(20.dp))
        }


        Button(
            onClick = {

                if (!mostrarCaptcha) {

                    if (
                        usuario.isEmpty() ||
                        contrasena != usuario.reversed()
                    ) {
                        mensaje = "Error"
                        return@Button
                    }

                    val resultado = generarCaptcha(usuario)

                    termino1 = resultado.first
                    termino2 = resultado.second

                    mostrarCaptcha = true
                    mensaje = ""

                } else {

                    val correcto =
                        (termino1 ?: 0) + (termino2 ?: 0)

                    if (captcha.toIntOrNull() == correcto) {
                        mensaje = "Sesión iniciada"
                        onLoginSuccess(usuario)
                    } else {
                        mensaje = "Error"
                    }
                }
            }
        ) {

            Text(
                if (mostrarCaptcha)
                    "Validar CAPTCHA"
                else
                    "Iniciar sesión"
            )
        }


        Spacer(Modifier.height(20.dp))

        Text(mensaje)
    }
}

/**
 * Pantalla del menú (reto 2) con las funcionalidades de contraseña y
 * coordenadas (reto 3), y ubicación de zona wifi más cercana (reto 4).
 */
@Composable
fun MenuScreen(
    modifier: Modifier = Modifier,
    codigoGrupo: String,
    onVolverAlLogin: () -> Unit
) {
    var opciones by remember { mutableStateOf(MENU_POR_DEFECTO) }
    var pantalla by remember { mutableStateOf(PantallaMenu.MENU) }
    var mensaje by remember { mutableStateOf("Elija una opción") }
    var entrada by remember { mutableStateOf("") }
    var erroresSeguidos by remember { mutableStateOf(0) }
    var favoritoCandidato by remember { mutableStateOf<Int?>(null) }
    var terminado by remember { mutableStateOf(false) }

    val (digitoRango, digitoInfo) = remember(codigoGrupo) {
        calcularDigitosClave(codigoGrupo)
    }
    val rango = remember(digitoRango) { TABLA_RANGOS.getValue(digitoRango) }
    val infoClave = remember(digitoInfo) { TABLA_INFO_CLAVE.getValue(digitoInfo) }
    val zonasDisponibles = remember(digitoRango) { TABLA_ZONAS.getValue(digitoRango) }
    val transportes = remember(digitoInfo) { TABLA_TRANSPORTE.getValue(digitoInfo) }

    var contrasenaActual by remember(codigoGrupo) { mutableStateOf(codigoGrupo.reversed()) }

    var coordenadas by remember { mutableStateOf<List<Pair<Double, Double>>?>(null) }
    var coordEnProgreso by remember { mutableStateOf(listOf<Double>()) }
    var indiceActualizar by remember { mutableStateOf<Int?>(null) }
    var latTemporal by remember { mutableStateOf<Double?>(null) }

    var ubicacionActualIndice by remember { mutableStateOf<Int?>(null) }
    var zonasCercanas by remember { mutableStateOf<List<Pair<ZonaWifi, Double>>>(emptyList()) }
    var direccionTexto by remember { mutableStateOf("") }
    var tiempoLineas by remember { mutableStateOf(listOf<String>()) }

    val menuCompleto: List<String> = remember(opciones) {
        val lista = opciones.map { it.texto } + TEXTO_OPCION_FAVORITA + TEXTO_OPCION_CERRAR
        lista.mapIndexed { index, texto -> "${index + 1}. $texto" }
    }

    fun finalizar() {
        terminado = true
    }

    if (terminado) {
        Column(modifier = modifier.padding(16.dp)) {
            Text(mensaje)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onVolverAlLogin) {
                Text("Volver al inicio de sesión")
            }
        }
        return
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        when (pantalla) {
            PantallaMenu.MENU -> {

                Text("Menú TicNet")
                Spacer(Modifier.height(10.dp))

                menuCompleto.forEach { linea -> Text(linea) }

                Spacer(Modifier.height(16.dp))
                Text(mensaje)

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = entrada,
                    onValueChange = { entrada = it },
                    label = { Text("Número de opción") }
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val opcion = entrada.toIntOrNull()
                    entrada = ""

                    if (opcion == null || opcion !in 1..7) {
                        erroresSeguidos++
                        mensaje = "Error"
                        if (erroresSeguidos >= 3) finalizar()
                        return@Button
                    }

                    erroresSeguidos = 0

                    when (opcion) {
                        in 1..5 -> {
                            when (opciones[opcion - 1].id) {
                                1 -> {
                                    mensaje = "Ingrese su contraseña actual para confirmar"
                                    pantalla = PantallaMenu.CAMBIAR_CONTRASENA_CONFIRMAR
                                }
                                2 -> {
                                    if (coordenadas == null) {
                                        coordEnProgreso = listOf()
                                        mensaje = "Ingrese la latitud de ${NOMBRES_SITIOS[0]} " +
                                                "(coordenada 1)"
                                        pantalla = PantallaMenu.ENTRAR_COORDENADA
                                    } else {
                                        pantalla = PantallaMenu.ACTUALIZAR_COORDENADA_MENU
                                    }
                                }
                                3 -> {
                                    if (coordenadas == null) {
                                        mensaje = "Error sin registro de coordenadas"
                                        finalizar()
                                    } else {
                                        pantalla = PantallaMenu.ELEGIR_UBICACION_ACTUAL
                                    }
                                }
                                else -> {
                                    mensaje = "Usted ha elegido la opción número $opcion"
                                    finalizar()
                                }
                            }
                        }
                        6 -> {
                            mensaje = "Seleccione opción favorita"
                            pantalla = PantallaMenu.ELEGIR_FAVORITO
                        }
                        7 -> {
                            mensaje = "Hasta pronto"
                            finalizar()
                        }
                    }
                }) {
                    Text("Elegir opción")
                }
            }

            PantallaMenu.ELEGIR_FAVORITO -> {

                Text(mensaje)
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = entrada,
                    onValueChange = { entrada = it },
                    label = { Text("Opción favorita (1-5)") }
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val opcion = entrada.toIntOrNull()
                    entrada = ""

                    if (opcion == null || opcion !in 1..5) {
                        mensaje = "Error"
                        finalizar()
                        return@Button
                    }

                    favoritoCandidato = opcion
                    mensaje = "Para confirmar por favor responda: Si me giras " +
                            "pierdo tres unidades por eso debes colocarme siempre " +
                            "de pie, la respuesta es"
                    pantalla = PantallaMenu.ADIVINANZA_1
                }) {
                    Text("Continuar")
                }
            }

            PantallaMenu.ADIVINANZA_1 -> {

                Text(mensaje)
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = entrada,
                    onValueChange = { entrada = it },
                    label = { Text("Respuesta") }
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val respuesta = entrada.toIntOrNull()
                    entrada = ""

                    if (respuesta == digitoRango) {
                        mensaje = "Para confirmar por favor responda: Me " +
                                "separaron de mi hermano siamés, antes era un " +
                                "ocho y ahora soy un… la respuesta es"
                        pantalla = PantallaMenu.ADIVINANZA_2
                    } else {
                        opciones = MENU_POR_DEFECTO
                        mensaje = "Error. Elija una opción"
                        pantalla = PantallaMenu.MENU
                    }
                }) {
                    Text("Confirmar")
                }
            }

            PantallaMenu.ADIVINANZA_2 -> {

                Text(mensaje)
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = entrada,
                    onValueChange = { entrada = it },
                    label = { Text("Respuesta") }
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val respuesta = entrada.toIntOrNull()
                    entrada = ""

                    val posicion = favoritoCandidato

                    if (respuesta == digitoInfo && posicion != null) {
                        val elegida = opciones[posicion - 1]
                        opciones = listOf(elegida) + opciones.filterIndexed { index, _ ->
                            index != posicion - 1
                        }
                        mensaje = "Elija una opción"
                    } else {
                        opciones = MENU_POR_DEFECTO
                        mensaje = "Error. Elija una opción"
                    }

                    favoritoCandidato = null
                    pantalla = PantallaMenu.MENU
                }) {
                    Text("Confirmar")
                }
            }

            PantallaMenu.CAMBIAR_CONTRASENA_CONFIRMAR -> {

                Text(mensaje)
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = entrada,
                    onValueChange = { entrada = it },
                    label = { Text("Contraseña actual") }
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val valor = entrada
                    entrada = ""

                    if (valor != contrasenaActual) {
                        mensaje = "Error"
                        finalizar()
                        return@Button
                    }

                    mensaje = "Ingrese la nueva contraseña"
                    pantalla = PantallaMenu.CAMBIAR_CONTRASENA_NUEVA
                }) {
                    Text("Confirmar")
                }
            }

            PantallaMenu.CAMBIAR_CONTRASENA_NUEVA -> {

                Text(mensaje)
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = entrada,
                    onValueChange = { entrada = it },
                    label = { Text("Nueva contraseña") }
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val nueva = entrada
                    entrada = ""

                    if (nueva.isEmpty() || nueva == contrasenaActual) {
                        mensaje = "Error"
                        finalizar()
                        return@Button
                    }

                    contrasenaActual = nueva
                    mensaje = "Elija una opción"
                    pantalla = PantallaMenu.MENU
                }) {
                    Text("Guardar")
                }
            }

            PantallaMenu.ENTRAR_COORDENADA -> {

                Text(mensaje)
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = entrada,
                    onValueChange = { entrada = it },
                    label = { Text("Valor (use punto decimal)") }
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val valor = entrada.toDoubleOrNull()
                    entrada = ""

                    val paso = coordEnProgreso.size
                    val esLatitud = paso % 2 == 0

                    if (valor == null) {
                        mensaje = "Error"
                        finalizar()
                        return@Button
                    }

                    val dentroDeRango = if (esLatitud)
                        valor in rango.latInf..rango.latSup
                    else
                        valor in rango.lonOcc..rango.lonOr

                    if (!dentroDeRango) {
                        mensaje = "Error coordenada"
                        finalizar()
                        return@Button
                    }

                    coordEnProgreso = coordEnProgreso + valor

                    if (coordEnProgreso.size < 6) {
                        val siguientePaso = coordEnProgreso.size
                        val siguienteEsLatitud = siguientePaso % 2 == 0
                        val siguienteIndice = siguientePaso / 2
                        val etiqueta = if (siguienteEsLatitud) "latitud" else "longitud"
                        mensaje = "Ingrese la $etiqueta de ${NOMBRES_SITIOS[siguienteIndice]} " +
                                "(coordenada ${siguienteIndice + 1})"
                    } else {
                        val nuevas = (0 until 3).map { i ->
                            Pair(coordEnProgreso[i * 2], coordEnProgreso[i * 2 + 1])
                        }
                        coordenadas = nuevas
                        coordEnProgreso = listOf()
                        mensaje = "Coordenadas guardadas. Elija una opción"
                        pantalla = PantallaMenu.MENU
                    }
                }) {
                    Text("Guardar valor")
                }
            }

            PantallaMenu.ACTUALIZAR_COORDENADA_MENU -> {

                val coords = coordenadas ?: emptyList()

                Text("Coordenadas actuales:")
                coords.forEachIndexed { i, (lat, lon) ->
                    Text("Coordenada ${i + 1}: [$lat, $lon]")
                }

                Spacer(Modifier.height(10.dp))

                Text(textoInfoClave(infoClave.first, coords))
                Text(textoInfoClave(infoClave.second, coords))

                Spacer(Modifier.height(10.dp))

                Text("Presione 1, 2 o 3 para actualizar la respectiva coordenada. " +
                        "Presione 0 para regresar al menú")

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = entrada,
                    onValueChange = { entrada = it },
                    label = { Text("Opción") }
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val opcion = entrada.toIntOrNull()
                    entrada = ""

                    if (opcion == null || opcion !in 0..3) {
                        mensaje = "Error actualización"
                        finalizar()
                        return@Button
                    }

                    if (opcion == 0) {
                        mensaje = "Elija una opción"
                        pantalla = PantallaMenu.MENU
                        return@Button
                    }

                    indiceActualizar = opcion - 1
                    mensaje = "Ingrese la nueva latitud de la coordenada $opcion"
                    pantalla = PantallaMenu.ACTUALIZAR_COORDENADA_LAT
                }) {
                    Text("Continuar")
                }
            }

            PantallaMenu.ACTUALIZAR_COORDENADA_LAT -> {

                Text(mensaje)
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = entrada,
                    onValueChange = { entrada = it },
                    label = { Text("Nueva latitud") }
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val valor = entrada.toDoubleOrNull()
                    entrada = ""

                    if (valor == null || valor !in rango.latInf..rango.latSup) {
                        mensaje = "Error coordenada"
                        finalizar()
                        return@Button
                    }

                    latTemporal = valor
                    mensaje = "Ingrese la nueva longitud"
                    pantalla = PantallaMenu.ACTUALIZAR_COORDENADA_LON
                }) {
                    Text("Continuar")
                }
            }

            PantallaMenu.ACTUALIZAR_COORDENADA_LON -> {

                Text(mensaje)
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = entrada,
                    onValueChange = { entrada = it },
                    label = { Text("Nueva longitud") }
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val valor = entrada.toDoubleOrNull()
                    entrada = ""

                    val indice = indiceActualizar
                    val lat = latTemporal

                    if (valor == null || indice == null || lat == null ||
                        valor !in rango.lonOcc..rango.lonOr
                    ) {
                        mensaje = "Error coordenada"
                        finalizar()
                        return@Button
                    }

                    val actuales = coordenadas ?: emptyList()
                    coordenadas = actuales.toMutableList().also {
                        it[indice] = Pair(lat, valor)
                    }

                    indiceActualizar = null
                    latTemporal = null
                    mensaje = "Elija una opción"
                    pantalla = PantallaMenu.MENU
                }) {
                    Text("Guardar")
                }
            }

            PantallaMenu.ELEGIR_UBICACION_ACTUAL -> {

                val coords = coordenadas ?: emptyList()

                Text("Sus coordenadas frecuentes:")
                coords.forEachIndexed { i, (lat, lon) ->
                    Text("coordenada [latitud,longitud] ${i + 1}: ['$lat', '$lon']")
                }

                Spacer(Modifier.height(10.dp))
                Text("Por favor elija su ubicación actual (1,2 ó 3) para calcular " +
                        "la distancia a los puntos de conexión")

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = entrada,
                    onValueChange = { entrada = it },
                    label = { Text("Opción") }
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val opcion = entrada.toIntOrNull()
                    entrada = ""

                    if (opcion == null || opcion !in 1..3 || coords.isEmpty()) {
                        mensaje = "Error ubicación"
                        finalizar()
                        return@Button
                    }

                    ubicacionActualIndice = opcion - 1
                    val (latUsuario, lonUsuario) = coords[opcion - 1]

                    val cercanas = zonasDisponibles
                        .map { zona -> zona to distanciaKm(latUsuario, lonUsuario, zona.lat, zona.lon) }
                        .sortedBy { it.second }
                        .take(2)
                        .sortedBy { it.first.usuariosProm }

                    zonasCercanas = cercanas
                    mensaje = "Zonas wifi cercanas con menos usuarios"
                    pantalla = PantallaMenu.ZONAS_CERCANAS
                }) {
                    Text("Continuar")
                }
            }

            PantallaMenu.ZONAS_CERCANAS -> {

                Text(mensaje)
                Spacer(Modifier.height(10.dp))

                zonasCercanas.forEachIndexed { i, (zona, dist) ->
                    val metros = (dist * 1000).roundToInt()
                    Text(
                        "La zona wifi ${i + 1}: ubicada en ['${zona.lat}','${zona.lon}'] " +
                                "a $metros metros , tiene en promedio ${zona.usuariosProm} usuarios"
                    )
                }

                Spacer(Modifier.height(10.dp))
                Text("Elija 1 o 2 para recibir indicaciones de llegada")

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = entrada,
                    onValueChange = { entrada = it },
                    label = { Text("Opción") }
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val opcion = entrada.toIntOrNull()
                    entrada = ""

                    val indiceUbicacion = ubicacionActualIndice
                    val coords = coordenadas

                    if (opcion == null || opcion !in 1..2 || indiceUbicacion == null || coords == null) {
                        mensaje = "Error zona wifi"
                        finalizar()
                        return@Button
                    }

                    val (zona, dist) = zonasCercanas[opcion - 1]
                    val (latUsuario, lonUsuario) = coords[indiceUbicacion]

                    val horizontal = if (zona.lon >= lonUsuario) "oriente" else "occidente"
                    val vertical = if (zona.lat >= latUsuario) "norte" else "sur"
                    direccionTexto = "Para llegar a la zona wifi dirigirse primero al " +
                            "$horizontal y luego hacia el $vertical"

                    val distanciaMetros = dist * 1000
                    tiempoLineas = listOf(transportes.first, transportes.second).map { modo ->
                        val velocidad = VELOCIDADES_MS.getValue(modo)
                        val minutos = (distanciaMetros / velocidad) / 60
                        "Tiempo en $modo: %.1f min".format(minutos)
                    }

                    mensaje = direccionTexto
                    pantalla = PantallaMenu.INDICACIONES_LLEGADA
                }) {
                    Text("Continuar")
                }
            }

            PantallaMenu.INDICACIONES_LLEGADA -> {

                Text(direccionTexto)
                Spacer(Modifier.height(10.dp))

                tiempoLineas.forEach { linea -> Text(linea) }

                Spacer(Modifier.height(10.dp))
                Text("Presione 0 para salir")

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = entrada,
                    onValueChange = { entrada = it },
                    label = { Text("Opción") }
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val opcion = entrada.toIntOrNull()
                    entrada = ""

                    if (opcion == 0) {
                        mensaje = "Elija una opción"
                        pantalla = PantallaMenu.MENU
                    } else {
                        mensaje = "Error"
                        finalizar()
                    }
                }) {
                    Text("Confirmar")
                }
            }
        }
    }
}