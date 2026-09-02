# Guion de sustentación — Entrega 4

Sistema de gestión de drones · Ingeniería de Software 2 · Universidad POLI

> Cómo usar este documento: la sección 1 es el recorrido que conviene seguir en pantalla. De la 2 en adelante son preguntas probables con la respuesta preparada. No hace falta memorizarlo; sí haber abierto antes cada archivo que se menciona.

---

## 1. Recorrido del código, en orden

Un orden que cuenta una historia: de la entidad hacia afuera, terminando por donde el usuario entra.

### 1.º — `modelo/Dron.java`

El corazón del dominio. Qué señalar:

- Es **abstracta**: en el mundo real no existe "un dron" a secas, existe uno de agricultura o uno de vigilancia.
- Dos métodos abstractos: `getTipo()` y `descripcionOperativa()`. El primero es la clave de la persistencia; el segundo, el ejemplo de polimorfismo puro.
- `sensores` es `private final` y `getSensores()` devuelve una vista no modificable. La composición está encapsulada: solo se puede tocar con `agregarSensor` y `removerSensor`.
- `setPiloto()` tiene **visibilidad de paquete** a propósito: la relación bidireccional solo se altera desde `Piloto.asignarDron()`, que mantiene los dos extremos sincronizados. Como efecto secundario, la capa de servicios no puede asignar piloto — y por eso un clon nunca puede robárselo al original.
- **Lo que NO hay aquí:** ni `implements Prototipo`, ni `copiar()`, ni constructor copia. El modelo no participa en ningún patrón. Merece señalarse explícitamente, porque es una decisión, no un olvido.

### 2.º — `modelo/Agricultura.java` y `modelo/Vigilancia.java`

Cortas, se ven de un vistazo. Cada una:
- declara su atributo propio,
- implementa `getTipo()` devolviendo su constante,
- implementa `descripcionOperativa()` con su propio texto.

Y nada más: son clases de dominio puro, sin lógica de patrones.

### 3.º — `modelo/TipoDron.java`

El enum que el diagrama no dibuja pero que sostiene el mapeo de herencia. Señalar `getCodigo()` y `desdeCodigo()`: los dos sentidos de la traducción entre Java y la columna de la base.

### 4.º — `servicios/ConexionBD.java`

El Singleton. Qué señalar:
- constructor privado, `getInstancia()` con doble comprobación y `volatile`;
- el campo `conexion`: **una sola para toda la aplicación**;
- `getConexion()`, que reconecta si la encuentra cerrada, y su Javadoc en negrita: *quien la recibe no debe cerrarla*;
- `cerrarConexion()`, que pone el campo a `null` para que el Singleton nunca quede inservible.

### 5.º — `servicios/GenericDAO.java` y `servicios/DronDAOImpl.java`

Primero el contrato genérico `<T, ID>`, luego la implementación. En el DAO:
- **el detalle crítico**: `try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql))`. La conexión no está dentro del try-with-resources. El comentario justo encima lo explica.
- `asignarAtributosEspecificos()`: el único punto que distingue subclase al escribir, y por qué pone en NULL la columna del otro subtipo.
- `mapearDron()`: la columna discriminadora decide qué fábrica llamar.

### 6.º — Las dos fábricas

`AgriculturaFactory` y `VigilanciaFactory`. Ambas caben en pantalla. Señalar el constructor privado y que **cada una recibe solo los atributos que su subtipo usa**.

### 7.º — `servicios/DronBuilder.java`

Empezar por el ejemplo de uso del Javadoc de la clase, luego `build()`: primero valida, después delega en la fábrica.

### 8.º — `servicios/DronPrototypeManager.java`

La pieza que más preguntas va a atraer. Qué señalar, en este orden:

- `clonar(Dron)`: el método público que copia cualquier dron. Su Javadoc lista qué se copia y qué no, con el motivo de cada decisión.
- El bucle de sensores: lista nueva **y** un `Sensor` nuevo por cada uno. Ahí está la copia profunda.
- `copiarAtributos()`: el **único** punto del proyecto que distingue subtipo al copiar, con el `switch` exhaustivo. Su Javadoc explica por qué es inevitable habiendo sacado el patrón del modelo.
- Que **copia en las dos direcciones**: al registrar guarda una copia y al entregar devuelve otra. Nadie puede contaminar la plantilla.

### 9.º — `servicios/InformeDeIdentidad.java`

La evidencia que se ve en pantalla. Señalar el uso de `System.identityHashCode` y, sobre todo, el Javadoc que explica la trampa del envoltorio de `getSensores()` — ver §10.

### 10.º — `Controlador/DronControlador.java`

Donde todo se junta:
- `registrarDron()` arma el dron con el Builder;
- `registrarPlantillasBase()` **usa el Builder para construir lo que el Prototype va a clonar** — los dos patrones colaborando;
- `clonarDron()` y `construirConBuilder()`: lo que ejecutan los dos botones de demostración;
- `traducir()`: la conversión de fallo técnico a mensaje de usuario.

### 11.º — `Vista/DronVista.java` y el FXML

Cerrar por donde entra el usuario: el selector de tipo que muestra y oculta campos, el selector de configuración base, los botones "Clonar" y "Builder" con el área de evidencia, y `mostrarAlerta()` como único punto de salida de errores.

Detalle que conviene señalar: el botón "Clonar" no comprueba si hay selección al pulsarse — está **atado** a ella con `disableProperty().bind(...)`. El usuario no puede llegar a un estado inválido.

### 12.º — Demostración en vivo

Si se puede ejecutar la aplicación, este es el momento: seleccionar un dron, pulsar **Clonar**, y leer el informe en pantalla. Después pulsar **Builder** y mostrar la secuencia de llamadas encadenadas. Es la prueba más directa de que los patrones funcionan.

---

## 2. Un apartado por patrón

### 2.1 Singleton — `ConexionBD`

**«¿Qué problema resuelve aquí?»**

Dos, y conviene separarlos. Primero, la configuración: la URL, el usuario y la contraseña se leen del `.env` **una sola vez**; sin el Singleton, cada DAO leería el archivo por su cuenta. Segundo, la conexión: mantener una sola compartida evita abrir una conexión TCP contra PostgreSQL en cada operación, que es lo más caro de cada consulta.

**«¿Por qué la doble comprobación con `volatile`?»**

Para que el candado se tome solo en la primera llamada. Sin `volatile`, otro hilo podría ver la referencia antes de que el constructor terminara, y usar un objeto a medio construir.

### 2.2 Factoría — `AgriculturaFactory` / `VigilanciaFactory`

**«¿Qué problema resuelve aquí?»**

Que ninguna capa tenga que saber qué subclase instanciar. El controlador sabe que el usuario eligió "agricultura"; el DAO sabe que la columna dice `'agricultura'`. Ninguno de los dos necesita conocer la clase `Agricultura`, y si mañana esa clase cambiara de constructor, solo se tocaría la fábrica.

**«¿Por qué dos clases y no una con un `switch`?»**

Porque en la fábrica única había que pasar los siete atributos siempre, incluidos los que no aplicaban. Para crear un dron de vigilancia había que enviar una `capacidadTanque` que la fábrica descartaba. Separadas, cada una recibe solo lo suyo.

### 2.3 Builder — `DronBuilder`

**«¿Qué problema resuelve aquí?»**

Dos. El primero, que un constructor con siete parámetros posicionales es fácil de equivocar: intercambiar `modelo` y `fabricante` —dos `String` contiguos— compila perfectamente y guarda los datos cruzados. El segundo, que **valida antes de instanciar**: nunca llega a existir un dron a medio construir con un peso negativo o sin serial.

**«¿Por qué `build()` devuelve `Dron` si es abstracta?»**

Porque devolver un tipo abstracto no tiene nada de raro: lo que no se puede es *instanciarlo*. Y no lo instancia: delega en la fábrica del subtipo según el campo `tipo`.

### 2.4 Prototype — `DronPrototypeManager`

**«¿Qué problema resuelve aquí?»**

Dos. Primero, que dar de alta drones parecidos obliga a repetir los mismos datos: con las configuraciones base, el usuario elige "Fumigador estándar", pulsa Usar, cambia el serial y guarda. Segundo, duplicar un dron que ya está en la tabla, que es lo que hace el botón "Clonar".

**«¿Por qué la copia está en servicios y no en el modelo?»**

Porque copiarse a sí mismo no es una responsabilidad del dominio, sino de la capa que gestiona la creación de objetos. Un `Dron` representa un dron: sus datos y sus reglas de negocio. Saber duplicarse para que la interfaz ofrezca un botón es infraestructura.

Conviene ser honesto en la sustentación: **la primera versión sí lo tenía en el modelo**, con una interfaz `Prototipo` y constructores copia. Técnicamente era más elegante —cada subclase se copiaba a sí misma, con retorno covariante y sin un solo `instanceof`—. Se movió porque el criterio de separación de capas pesa más que la elegancia de una llamada polimórfica.

**«¿Y no se pierde nada al moverlo?»**

Sí, y hay que decirlo: se pierde el polimorfismo de la copia. Como el modelo ya no ofrece un `copiar()`, el servicio tiene que preguntar por el subtipo, porque `capacidadTanque` no tiene equivalente en `Vigilancia` ni `deteccionTermica` en `Agricultura`. Ese `switch` está **confinado a un único método privado**, `copiarAtributos()`, y es exhaustivo sobre el enum: si mañana hay un tercer subtipo, el proyecto no compila hasta cubrirlo.

**«¿Y por qué no `Cloneable`?»**

Porque exigiría tocar el modelo, que es justo lo que se ha evitado. Y aunque no fuera así, tiene un problema concreto: **`super.clone()` hace copia superficial y el campo `sensores` es `final`**, así que no se puede reasignar la lista copiada. Habría que quitarle el `final` a un campo que protege la composición. Además `Cloneable` no declara ningún método, así que no sirve como contrato; `clone()` es `protected` y devuelve `Object`; y lanza una excepción comprobada que aquí nunca puede ocurrir.

**«¿Cómo garantizan que el clon no le roba el piloto al original?»**

Por diseño del lenguaje, no por disciplina: `Dron.setPiloto()` tiene visibilidad de paquete, así que `DronPrototypeManager`, que vive en `servicios`, **no puede invocarlo aunque quisiera**. El clon nace sin piloto necesariamente.

### 2.5 DAO — `GenericDAO` / `DronDAOImpl`

**«¿Qué problema resuelve aquí?»**

Aísla el SQL. El controlador llama a `guardar(dron)` sin saber si detrás hay PostgreSQL, un archivo o un mapa en memoria — de hecho `DronControladorTest` le inyecta un DAO en memoria y funciona igual.

**«¿Por qué genérico?»**

Para que el contrato sirviera a cualquier entidad. En esta entrega solo lo implementa `DronDAOImpl`, porque el diagrama solo muestra ese; en la anterior lo implementaban también los DAO de piloto, sensor y misión, y eso demostró que el contrato era genérico de verdad.

### 2.6 MVC

**«¿Cómo está repartido?»**

- **Modelo** (`modelo`): las entidades y sus reglas propias. No sabe que existe una base de datos ni una interfaz.
- **Vista** (`Vista` + FXML): recoge lo que el usuario escribe y muestra lo que le devuelven. No interpreta errores técnicos: muestra el mensaje que ya viene redactado.
- **Controlador** (`Controlador`): media entre ambos, valida, y traduce fallos técnicos a mensajes.

La capa `servicios` no es una cuarta capa de MVC: es la de persistencia y creación de objetos, a la que el controlador delega.

---

## 3. Cómo se conectan los patrones

**«¿Por qué el Builder y el Prototype no se pisan?»**

Porque resuelven momentos distintos:

- El **Builder** construye **desde datos sueltos**: los siete campos del formulario, que hay que reunir y validar. Es el camino de un dron que todavía no existe en ninguna forma.
- El **Prototype** copia **un objeto que ya existe y ya es válido**. No valida nada, porque no hace falta: la plantilla ya pasó por el Builder cuando se registró.

**Cuándo usar cada uno:** si el usuario escribe los datos, Builder. Si el usuario parte de algo que ya está armado, Prototype.

**El punto que mejor lo demuestra** está en `DronControlador.registrarPlantillasBase()`: el Builder construye la plantilla **una sola vez**, y a partir de ahí el Prototype produce copias sin volver a validar. Colaboran, no compiten.

**«¿Y el Builder con la Factoría?»**

Se reparten el trabajo. El Builder reúne y valida; la Factoría sabe qué subclase corresponde a cada tipo. `build()` no instancia nada: llama a la fábrica. Si se hubiera puesto un `new Agricultura(...)` dentro del builder, habría dos sitios sabiendo cómo se construye un dron de agricultura.

---

## 4. El recorrido completo de un dato

### De la pantalla a PostgreSQL

1. **`DronVista.agregarDron()`** — lee los `TextField`, convierte el peso con `leerDecimal()`.
2. **`DronControlador.registrarDron()`** — recibe los datos sueltos.
3. **`DronBuilder`** — encadena los `con...()`, y `build()` valida.
4. **`AgriculturaFactory.crearDron()`** (o `VigilanciaFactory`) — devuelve la instancia concreta.
5. **`DronDAOImpl.guardar(dron)`** — arma el `INSERT`. `dron.getTipo().getCodigo()` escribe la columna discriminadora; `asignarAtributosEspecificos()` pone en NULL la columna del otro subtipo.
6. **`ConexionBD.obtenerConexion()`** — entrega la conexión compartida.
7. **PostgreSQL** — inserta la fila y genera el `id`.
8. **Vuelta atrás inmediata**: `getGeneratedKeys()` recupera ese id y `dron.setId()` lo refleja en el objeto.

### De PostgreSQL a la pantalla

1. **`DronVista.actualizarTabla()`** → `DronControlador.listarDrones()` → `DronDAOImpl.listarTodos()`.
2. **`mapearDron(rs)`** — lee la columna `tipo`, la convierte con `TipoDron.desdeCodigo()` y, según el resultado, llama a una fábrica u otra leyendo solo la columna que ese subtipo usa.
3. La lista de `Dron` sube al controlador, y de ahí al `ObservableList` de la tabla.
4. La columna "Tipo" de la tabla usa `getTipo().getCodigo()`: **el objeto se describe a sí mismo**.

Frase para cerrar: *el tipo baja como código de texto y sube como clase de Java; `TipoDron` es la bisagra en los dos sentidos.*

---

## 5. Herencia y polimorfismo en una base relacional

**«Una base relacional no tiene herencia. ¿Cómo la guardaron?»**

Con **tabla única y columna discriminadora** (*Single Table Inheritance*). Toda la jerarquía vive en la tabla `dron`; la columna `tipo` dice de qué subclase es cada fila; `capacidad_tanque` y `deteccion_termica` quedan en NULL en las filas que no las usan.

**«¿Por qué esa estrategia y no otra?»**

Las tres opciones clásicas son tabla única, una tabla por subclase (*class table inheritance*) y una tabla por clase concreta.

- **Tabla única**: consultar toda la flota es un `SELECT` sin `JOIN`, y añadir un subtipo es añadir columnas. El precio es tolerar NULL.
- **Tabla por subclase**: normaliza mejor, sin NULL, pero cada consulta necesita `JOIN` y cada alta escribe en dos tablas.
- **Tabla por clase concreta**: sin NULL ni `JOIN`, pero listar la flota completa obliga a un `UNION` y el serial único deja de poder garantizarse con una restricción.

Con dos subtipos, tres atributos propios entre ambos y una operación tan frecuente como "listar todos los drones", la tabla única es la que menos complejidad aporta.

**«¿Y los NULL no son un problema?»**

Están controlados por una restricción:

```sql
CONSTRAINT chk_dron_atributos_por_tipo CHECK (
    (tipo = 'agricultura' AND capacidad_tanque IS NOT NULL AND deteccion_termica IS NULL)
 OR (tipo = 'vigilancia'  AND deteccion_termica IS NOT NULL AND capacidad_tanque IS NULL)
)
```

La base **no permite** una fila incoherente. Un dron de agricultura con detección térmica es rechazado por PostgreSQL, no solo por Java.

---

## 6. Dónde hay `instanceof` y por qué no contradice el polimorfismo

Hay **tres** usos de `instanceof` más un `switch` con cast, y conviene tenerlos localizados:

| Dónde | Forma | Para qué |
|---|---|---|
| `DronDAOImpl.asignarAtributosEspecificos()` | `instanceof` | Saber qué columna específica rellenar. |
| `DronVista.cargarEnFormulario()` | `instanceof` | Saber qué campo del formulario mostrar. |
| `DronVista.actualizarDronSeleccionado()` | `instanceof` | Saber qué campo leer al guardar. |
| `DronPrototypeManager.copiarAtributos()` | `switch` + cast | Saber qué atributo propio copiar. |

(Hay otros dos `instanceof` en el proyecto, en `Dron.equals()` y en `ServicioException`, pero no distinguen subtipos de dron: comprueban tipos para comparar y para extraer un código SQL.)

**La respuesta.** El polimorfismo resuelve el caso en el que **todos los subtipos responden a la misma pregunta de forma distinta**. `descripcionOperativa()` es eso: se recorre la colección y cada dron contesta lo suyo, sin preguntar por su clase. Igual `getTipo()`, que es lo que permite al DAO escribir el discriminador sin inspeccionar nada.

Pero los tres puntos de la tabla no son ese caso: ahí los subtipos **no tienen el mismo dato**. `Agricultura` tiene `capacidadTanque` y `Vigilancia` no la tiene ni tiene un equivalente. No hay una pregunta común que hacerles.

**«¿No se podría evitar de todas formas?»**

Sí, y conviene decir cómo y por qué no se hizo. Se podría dar a `Dron` un método `escribirseEn(PreparedStatement)`, y cada subclase escribiría sus columnas. Se elimina el `instanceof`, pero a cambio **el modelo pasaría a conocer JDBC**, que es justo lo que la capa DAO existe para evitar. El `instanceof` está confinado a un método privado de la capa que ya conoce la base de datos; el intercambio no compensa.

Frase para cerrar: *cuatro puntos distinguen subtipo, todos en el borde del sistema —donde se traduce a SQL, a formulario o a copia—, ninguno en el dominio.*

---

## 7. Manejo de excepciones: la cadena de traducción

Cada capa habla su idioma y traduce al de arriba.

| Capa | Clase de excepción | Contenido del mensaje |
|---|---|---|
| JDBC | `java.sql.SQLException` | Técnico. Trae el código SQLSTATE. |
| Servicios | `ServicioException` | Envuelve la anterior. Guarda el SQLSTATE y lo interpreta con `esDuplicado()`, `esViolacionDeRegla()`, `esReferenciaInvalida()`. |
| Control | `OperacionFallidaException` | Redactado para el usuario. Sin códigos ni nombres de tabla. |
| Vista | *ninguna* | Solo captura y muestra el `getMessage()` en un `Alert`. |

**El recorrido de un serial duplicado:**

1. PostgreSQL rechaza el `INSERT` por la restricción UNIQUE y devuelve **SQLSTATE 23505**.
2. `DronDAOImpl.guardar()` captura la `SQLException` y lanza `new ServicioException("Error al guardar el dron", e)`. El constructor extrae el `23505`.
3. `DronControlador.registrarDron()` la captura y llama a `traducir()`, que consulta `esDuplicado()` y devuelve `OperacionFallidaException("Ya existe un dron con el serial AGR-001.")`.
4. `DronVista.agregarDron()` la captura y muestra ese texto en un `Alert`.

**«¿Por qué tres clases y no propagar la `SQLException`?»**

Porque la vista tendría que conocer JDBC para saber qué mostrar, y porque un `SQLState 23505` no le dice nada a un usuario. Además, si mañana se cambiara de motor, o de JDBC a un ORM, la vista y el controlador no se enterarían.

**«¿Se pierde la información técnica?»**

No: cada eslabón conserva el anterior como causa. La traza completa sigue disponible para diagnóstico; lo que no llega a pantalla es el texto técnico.

**«¿Por qué son excepciones no comprobadas?»**

Porque no hay nada que el llamador pueda hacer para recuperarse de un fallo de base de datos salvo informar. Obligar a un `try/catch` en cada llamada solo añadiría ruido. `DronBuilder` sigue el mismo criterio con `IllegalStateException`.

---

## 8. Sobre la conexión compartida

**«¿Por qué el Singleton guarda una única `Connection`?»**

Porque JavaFX atiende toda la interfaz en un solo hilo. Cuando el usuario pulsa Agregar, ese manejador corre completo antes de que pueda empezar cualquier otro: **nunca hay dos operaciones simultáneas** compitiendo por la conexión. Y abrir una conexión TCP contra PostgreSQL es la parte más costosa de cada operación, así que reutilizarla se nota.

**Dato concreto:** al hacer el cambio, la clase de pruebas de manejo de errores pasó de 0,638 s a 0,036 s.

**«¿Por qué los DAO no la cierran?»**

Porque no es suya. La regla general de JDBC —cerrar lo que abres, con try-with-resources— sigue vigente, y por eso el DAO **sí** cierra el `PreparedStatement` y el `ResultSet`, que son suyos. La conexión la abrió el Singleton.

Si un DAO la cerrara, el fallo no aparecería en esa operación —que ya terminó— sino en **la siguiente**. Ese es exactamente el motivo de que exista `FlujoCrudDronTest`: encadena tres altas, una actualización y tres bajas, porque un test que hace una sola operación no lo detectaría.

En el código se ve así:

```java
// La conexión no entra en el try-with-resources: pertenece al Singleton
// y cerrarla dejaría inservibles las operaciones siguientes.
try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
```

**«¿Quién la cierra entonces?»**

`MainApp.stop()`, que JavaFX invoca al cerrarse la última ventana. Es el único punto autorizado, y solo tiene sentido ahí: mientras la aplicación viva, cualquier operación puede necesitarla.

**«¿En qué escenario este diseño dejaría de servir?»**

Esta es la pregunta importante y conviene responderla sin defenderse:

1. **En cuanto haya un segundo hilo.** Si una consulta larga se moviera a un `Task` en segundo plano para no congelar la interfaz, dos hilos usarían la misma `Connection`. JDBC no garantiza que sea segura entre hilos: los resultados podrían mezclarse.
2. **Con transacciones concurrentes.** Una `Connection` tiene un único contexto transaccional. Dos operaciones que necesitaran confirmar o deshacer por separado se pisarían.
3. **En una aplicación web.** Cada petición es un hilo; una conexión compartida sería un cuello de botella inmediato.
4. **Ante una caída de red prolongada.** `getConexion()` reconecta si la encuentra cerrada, pero no reintenta ni distingue una caída transitoria.

**La salida es conocida: un pool de conexiones** (HikariCP, por ejemplo). Cada operación pide una conexión al pool, la usa y la devuelve — volviendo al try-with-resources. Lo interesante es que **el cambio quedaría confinado a `ConexionBD`**: `getConexion()` devolvería una del pool y los DAO no se enterarían. No se hizo porque el enunciado no pide dependencias nuevas y la aplicación es monohilo.

---

## 8 bis. La evidencia en pantalla

Es lo que el evaluador va a mirar primero, así que conviene dominarlo.

**«¿Por qué `System.identityHashCode` y no `hashCode()`?»**

Porque `Dron` **sobrescribe** `hashCode()` a partir del id de negocio. Dos objetos distintos con el mismo id devuelven el mismo valor, así que no sirve para demostrar que son objetos separados — que es justo lo que queremos demostrar. `identityHashCode` devuelve la identidad que la JVM asigna a cada objeto, independientemente de lo que la clase haya sobrescrito. Hay una prueba que lo verifica: construye dos drones con el mismo id, comprueba que `hashCode()` los da iguales, y que el informe no se apoya en él.

**«¿Es esa la dirección de memoria real?»**

No exactamente, y conviene no afirmarlo. Es el *identity hash code*: un valor que la JVM asocia a cada objeto y que se comporta como su identidad. En la práctica sirve para lo que aquí se necesita —dos objetos distintos dan valores distintos—, pero no es un puntero: el recolector de basura puede mover el objeto en memoria y este valor no cambia.

**«¿Qué demuestra cada línea del informe?»**

- `identityHashCode` distinto → la JVM les dio identidades separadas: son dos objetos, no dos nombres del mismo.
- `original == copia -> false` → lo mismo, comparando referencias en vez de contenido.
- `original.equals(copia)` → compara el **id de negocio**, no la memoria. Da `false` porque el clon nace sin id, esperando el que le asigne PostgreSQL. Es un buen momento para explicar la diferencia entre identidad de objeto e identidad de negocio.
- El recuento de sensores → demuestra que la copia es profunda.

**«La línea de las listas de sensores, ¿qué prueba?»**

Aquí la respuesta honesta suma puntos: **por sí sola, nada**, y el propio informe lo advierte en pantalla.

`getSensores()` devuelve la lista envuelta con `Collections.unmodifiableList()`, y ese envoltorio **se crea en cada llamada**. Dos vistas de la *misma* lista ya dan identidades distintas, así que esa línea saldría igual aunque la copia fuera superficial. Se imprime porque el enunciado la pide, con la advertencia al lado.

**Lo que sí demuestra la copia profunda** es la comprobación de debajo: se agrega un sensor **solo al clon** y se muestran los recuentos de ambas listas. Si se compartieran, el original también crecería. El sensor de prueba se retira después, así que ninguno de los dos drones queda alterado — y hay una prueba que lo verifica.

**«¿Por qué en un TextArea y no en consola?»**

Porque la consola no forma parte de la aplicación: quien la usa no la ve. Una evidencia que hay que ir a buscar al terminal no es evidencia para el usuario. Además, el texto lo redacta la capa de servicios (`InformeDeIdentidad`), lo que permite **probar su contenido sin levantar JavaFX**: hay ocho pruebas sobre él.

---

## 9. Preguntas incómodas

**«¿Por qué una tabla única y no una por subclase?»**

Ver §5. Resumen: con dos subtipos, tres atributos propios y "listar toda la flota" como operación más frecuente, la tabla única evita un `JOIN` en cada consulta. El precio son los NULL, y están controlados por un CHECK que impide filas incoherentes. Si la jerarquía creciera a seis o siete subtipos con muchos atributos propios, la tabla se llenaría de columnas casi siempre vacías y convendría revisar la decisión.

**«¿Qué tocarías si agregas un tercer tipo de dron?»**

Conviene contestar en orden, porque demuestra que se conoce el sistema:

1. `modelo/` — la clase nueva, extendiendo `Dron`, con `getTipo()` y `descripcionOperativa()`. Nada más: el modelo no lleva lógica de patrones.
2. `TipoDron` — la constante nueva con su código.
3. `servicios/` — su fábrica.
4. `DronBuilder.build()`, `DronDAOImpl.mapearDron()` y `DronPrototypeManager.copiarAtributos()` — el caso nuevo en cada `switch`.
5. `DronDAOImpl.asignarAtributosEspecificos()` — su rama.
6. `db/schema.sql` — la columna propia, el código nuevo en `chk_dron_tipo` y la rama en `chk_dron_atributos_por_tipo`.
7. `DronVista` — mostrar y leer su campo.

**El detalle que conviene añadir:** el paso 4 **no hay que recordarlo**. Los tres `switch` sobre `TipoDron` son expresiones exhaustivas, así que en cuanto se añade la constante **el proyecto deja de compilar** hasta cubrir el caso nuevo en los tres sitios. El compilador hace de lista de tareas.

*(Si preguntan por qué no se usa despacho polimórfico en vez de `switch`: ver §6.)*

**«¿Por qué Builder y Prototype si ya tienes factorías?»**

Porque las tres resuelven cosas distintas:

- La **Factoría** decide **qué clase** instanciar. No valida ni reúne datos.
- El **Builder** reúne los datos **paso a paso** y los **valida** antes de construir. Después delega en la fábrica: no duplica lo que ella hace.
- El **Prototype** no construye nada: **copia** algo que ya existe.

Y no están sueltos: `build()` llama a la fábrica, y las plantillas del Prototype se arman con el Builder. Cada patrón usa al anterior en lugar de repetirlo.

**«¿No es sobre-ingeniería para una aplicación académica?»**

Respuesta honesta: para dos subtipos, sí, un constructor bastaría. Los patrones están porque el enunciado los pide y porque el proyecto es el vehículo para demostrar que se entienden. Dicho eso, dos de ellos ya se ganan el sitio: el Builder evitó duplicar la validación entre el controlador y el modelo, y las fábricas separadas eliminaron los parámetros ignorados que arrastraba la fábrica única.

**«El `DronPrototypeManager` aparece dos veces en el diagrama. ¿Por qué solo hay una clase?»**

Porque es un error de dibujo. Dos cajas idénticas con el mismo nombre y los mismos métodos no representan dos clases distintas: no habría forma de distinguirlas ni motivo para tener dos registros de prototipos.

**«Eliminaron `PilotoDAO`, `SensorDAO` y `MisionDAO`. ¿No hacían falta?»**

Estaban completos y funcionaban. Se eliminaron porque el diagrama de esta entrega solo muestra `DronDAOImpl` implementando `GenericDAO`. Sus tablas siguen en el esquema y sus clases en el modelo; si una entrega posterior necesita persistir pilotos o misiones, hay que reintroducir el acceso a datos. Es una decisión de alcance, no de calidad del código.

**«¿Por qué el DAO no usa el Builder, si el controlador sí?»**

Porque reconstruir una fila existente no debe pasar por las validaciones de un alta. Si mañana el builder exigiera un campo nuevo, el sistema no podría releer las filas antiguas guardadas antes de esa regla. Son dos caminos distintos: uno crea algo que no existía, el otro reconstruye algo que ya está guardado.

**«¿Las pruebas dependen de la base de datos? ¿No deberían ser unitarias?»**

Parte sí y parte no, a propósito. `DronBuilderTest`, `DronPrototypeTest` y `FactoriasDronTest` son unitarias puras: no tocan la base. `DronControladorTest` usa un DAO en memoria — que es justamente lo que permite la inyección por constructor.

`ConexionBDTest`, `ManejoErroresTest` y `FlujoCrudDronTest` **son de integración a propósito**: verifican cosas que solo la base real puede confirmar, como que una restricción UNIQUE produce el SQLSTATE 23505 o que la conexión compartida sobrevive a una cadena de operaciones. Con dobles de prueba se estaría verificando el doble, no el sistema.

En un entorno de integración continua habría que separar ambos grupos o levantar una base efímera. Es un pendiente reconocido.

**«¿Cómo saben que la copia del Prototype es realmente profunda?»**

Hay una prueba que lo comprueba, y además **se verificó que la prueba funciona**: se rompió el constructor copia a propósito, haciendo que compartiera los objetos `Sensor`, y `modificarUnSensorDelClon_noDebeAfectarAlDelOriginal` falló. Después se restauró el código. Una prueba que nunca se ha visto fallar no demuestra gran cosa.

**«¿Por qué el clon conserva el serial, si es único en la base?»**

Es una decisión de dos niveles. El **servicio** copia el serial porque es un dato del dron y permite reconocer de qué dron proviene la copia. La **vista** lo borra al cargar el formulario, porque es único y debe escribirlo quien da el alta. Si el servicio lo dejara en `null`, se estaría metiendo una regla de presentación en la capa equivocada.

**«Tienen un `switch` sobre el tipo dentro del Prototype. ¿No es eso lo que los patrones deberían evitar?»**

Sí, y es el precio consciente de sacar la copia del modelo. Con `copiar()` en las subclases no haría falta ningún `switch`: cada dron se copiaría a sí mismo. Al mover el patrón a servicios, la capa pierde el acceso polimórfico a los atributos que solo existen en una subclase.

Tres cosas acotan el daño: está en **un solo método privado**, es **exhaustivo sobre el enum** —así que el compilador exige el caso nuevo si aparece un tercer subtipo—, y la construcción se **delega en las fábricas**, de modo que ni siquiera ahí se llama a un constructor del modelo.

**«¿Cómo evitan que alguien vuelva a meter la lógica de copia en el modelo?»**

Con una prueba. `elModelo_noDebeConocerElPatronPrototype` usa reflexión para comprobar que `Dron` no expone `copiar()`, que ninguna clase del modelo tiene constructor copia y que `Dron` no implementa ninguna interfaz. Si alguien lo reintroduce, la compilación pasa pero **la prueba falla**. El requisito quedó convertido en una verificación automática en vez de en una nota en un documento.

**«¿Por qué `Vista` y `Controlador` siguen con mayúscula si renombraron los otros?»**

Porque el diagrama nombra `modelo` y `servicios`. Por coherencia con la convención de Java, los cuatro deberían ir en minúscula; se dejó así para no apartarse del diagrama. Es un cambio de dos commits si se quiere completar.
