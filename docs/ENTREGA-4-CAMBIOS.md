# Entrega 4 — Reporte técnico de cambios

Sistema de gestión de drones · Ingeniería de Software 2 · Universidad POLI
Java 17 · Maven · JavaFX 21.0.2 · PostgreSQL (JDBC) · MVC + DAO

---

## 1. Resumen ejecutivo

Esta entrega alinea el código con el diagrama UML de la entrega 4 y suma los tres patrones de creación que pedía el enunciado.

La estructura se reorganizó: los paquetes `Modelo` y `Servicio` pasaron a `modelo` y `servicios`, en minúscula y en plural, y se eliminaron las clases que el diagrama no contempla (`PilotoDAO`, `SensorDAO`, `MisionDAO` y la factoría única `DronFactory`).

`ConexionBD` cambió de diseño: el Singleton ya no entrega una conexión nueva por llamada, sino que mantiene una única `Connection` compartida. Con ello, los DAO dejaron de cerrarla y `MainApp.stop()` pasó a ser el único punto que lo hace.

Se implementaron tres patrones, **todos ellos alojados en `servicios`**. La factoría única se dividió en `AgriculturaFactory` y `VigilanciaFactory`, una por subtipo. `DronBuilder` construye drones paso a paso, validando antes de instanciar, y lo usa el controlador en cada alta desde el formulario. `DronPrototypeManager` copia drones y guarda configuraciones base —"Fumigador estándar", "Vigilancia nocturna"— de las que el usuario puede partir.

**El paquete `modelo` no participa en ningún patrón.** Una primera versión daba a `Dron` una interfaz `Prototipo` y constructores copia; se retiró por completo para que el dominio quede libre de responsabilidades de infraestructura. Comparado con su estado previo al Prototype, el modelo solo difiere hoy en el Javadoc.

La interfaz suma dos botones de demostración, "Clonar" y "Builder", que escriben su evidencia en un `TextArea` de solo lectura —nunca en consola—, con las identidades de memoria de original y copia.

Las pruebas pasaron de 18 a 65, y `mvn javadoc:javadoc` de 67 avisos a ninguno. Por el camino se corrigió un error que impedía montar la base de datos desde cero.

---

## 2. Tabla de archivos

### 2.1 Creados

| Archivo | Motivo |
|---|---|
| `servicios/AgriculturaFactory.java` | Fábrica del subtipo Agricultura, según el diagrama. |
| `servicios/VigilanciaFactory.java` | Fábrica del subtipo Vigilancia, según el diagrama. |
| `servicios/DronBuilder.java` | Patrón Builder: construcción paso a paso con validación. |
| `servicios/DronPrototypeManager.java` | Patrón Prototype: copia de drones y registro de configuraciones base. |
| `servicios/InformeDeIdentidad.java` | Redacta la evidencia que los botones vuelcan en el `TextArea`. |
| `servicios/DemostracionPatron.java` | Record que devuelve a la vista el objeto producido y su informe. |
| `test/DronBuilderTest.java` | 14 pruebas del Builder. |
| `test/DronPrototypeTest.java` | 17 pruebas del Prototype, incluida la de copia superficial. |
| `test/InformeDeIdentidadTest.java` | 8 pruebas del contenido de los informes, sin levantar JavaFX. |
| `test/FlujoCrudDronTest.java` | Encadena altas, actualización y bajas reales para detectar un cierre indebido de la conexión. |
| `docs/ENTREGA-4-CAMBIOS.md` | Este reporte. |
| `docs/GUION-SUSTENTACION.md` | Guion de sustentación. |

### 2.2 Modificados

| Archivo | Motivo |
|---|---|
| `servicios/ConexionBD.java` | Pasa a mantener una única `Connection` compartida: `getConexion()`, `cerrarConexion()`. Se elimina `abrirConexion()` y `getUrlSegura()`, sin uso. |
| `servicios/DronDAOImpl.java` | La `Connection` sale del try-with-resources; `mapearDron()` usa las fábricas por subtipo. |
| `Controlador/DronControlador.java` | Construye con el Builder al registrar; sirve las configuraciones base; expone `clonarDron()` y `construirConBuilder()` para los botones de demostración. |
| `Vista/DronVista.java` | Selector de configuración base, botones "Clonar" y "Builder", y el área de evidencia. |
| `Vista/DrownView.fxml` | ComboBox de plantillas, botones de demostración y `TextArea` de evidencia. |
| `Vista/styles.css` | Estilos del área de evidencia (monoespaciada) y de los botones deshabilitados. |
| `MainApp.java` | Sobrescribe `stop()` para cerrar la conexión al terminar. |
| `Main.java` | Javadoc; explicación de por qué existe como lanzador aparte. |
| `modelo/*.java` | **Solo Javadoc.** Ningún cambio estructural respecto al estado previo al Prototype. |
| `servicios/ServicioException.java`, `Controlador/OperacionFallidaException.java` | Javadoc completo. |
| `test/ConexionBDTest.java` | Reescrito: verifica reutilización y restablecimiento. |
| `test/FactoriasDronTest.java` | Antes `DronFactoryTest`; cubre las dos fábricas nuevas. |
| `test/ManejoErroresTest.java` | Prueba del tipo nulo, trasladada desde el test de la fábrica. |
| `test/DronControladorTest.java` | Cuatro pruebas de la integración del Prototype. |
| `db/schema.sql` | Corregido un `COMMENT` sobre una columna inexistente que rompía el script. |
| `db/README.md` | Documentaba `db.properties`, que ya no existe; ahora describe `.env`. |

### 2.3 Eliminados

| Archivo | Motivo |
|---|---|
| `servicios/PilotoDAO.java` | El diagrama solo muestra `DronDAOImpl` implementando `GenericDAO`. |
| `servicios/SensorDAO.java` | Ídem. |
| `servicios/MisionDAO.java` | Ídem. |
| `servicios/DronFactory.java` | Sustituida por las dos fábricas por subtipo. |
| `modelo/Prototipo.java` | El modelo no debe conocer los patrones: la copia se trasladó íntegra a `servicios`. |
| `test/Daosrestantestest.java` | Única prueba de los tres DAO eliminados. |

Ningún código de producción referenciaba los tres DAO: solo su propio test.

---

## 3. Los tres patrones nuevos

### 3.1 Factorías por subtipo

**Qué problema resuelve.** La versión anterior era una sola clase con un `switch` que recibía los siete atributos de golpe, incluidos los que no aplicaban al subtipo pedido: para crear un dron de vigilancia había que pasar igualmente una `capacidadTanque` que la fábrica ignoraba. Ese parámetro sobrante es una invitación al error, y crece con cada subtipo nuevo.

**Dónde está.** `servicios/AgriculturaFactory.java` y `servicios/VigilanciaFactory.java`. Ambas tienen constructor privado y un único `crearDron()` estático que devuelve su subtipo concreto.

**Dónde se usa.**
- `DronBuilder.build()`, tras validar los datos.
- `DronDAOImpl.mapearDron()`, al reconstruir un dron desde la columna discriminadora.

**Alternativas descartadas.**
- *Conservar la fábrica única con `switch`.* El diagrama la sustituye explícitamente.
- *Una interfaz `DronFactory` con dos implementaciones y despacho polimórfico.* Elimina el `switch` por completo, pero el diagrama dibuja dos clases con métodos estáticos, no una jerarquía, y añadir una interfaz habría sido inventar requisitos.

**Consecuencia de diseño.** Al desaparecer el parámetro `tipo`, la decisión de qué fábrica usar se desplaza a quien las llama. Quedan dos puntos que la toman, y son distintos a propósito: en `DronBuilder` el tipo lo elige el usuario en el formulario; en `DronDAOImpl` lo dicta la fila de la base de datos. Ambos usan un `switch` de expresión sobre `TipoDron`, que al ser exhaustivo **obliga al compilador a exigir todos los casos**: añadir un tercer tipo rompe la compilación hasta actualizar los dos puntos, en vez de fallar en ejecución.

### 3.2 Builder

**Qué problema resuelve.** Un dron tiene siete datos y el constructor los recibe posicionalmente. Intercambiar dos cadenas contiguas —`modelo` y `fabricante`, por ejemplo— guarda los datos cruzados sin que el compilador se entere. El builder sustituye esa lista por llamadas con nombre y, sobre todo, **valida antes de instanciar**: nunca llega a existir un dron a medio construir.

**Dónde está.** `servicios/DronBuilder.java`. Cada método devuelve `this`; `build()` valida y delega en la fábrica del subtipo.

Validaciones: `tipo`, `serial`, `modelo` y `fabricante` obligatorios y no en blanco; `peso` no negativo; `capacidadTanque` no negativa en drones de agricultura. Cada fallo lanza `IllegalStateException` nombrando el dato concreto.

**Dónde se usa.**
- `DronControlador.registrarDron()` — **cada alta desde el formulario pasa por él**.
- `DronControlador.registrarPlantillasBase()` — arma las configuraciones base que luego clona el Prototype.

**Alternativas descartadas.**
- *Builder abstracto con `AgriculturaBuilder` y `VigilanciaBuilder`.* Elimina el `switch` de `build()`, pero añade tres clases que el diagrama no contiene y obliga a genéricos recursivos (`T extends DronBuilder<T>`) para conservar el encadenamiento.
- *`buildAgricultura()` / `buildVigilancia()`.* Rompe la firma `build(): Dron` del diagrama y devuelve al llamador la decisión del subtipo.
- *Un método `desde(Dron)` para precargar el builder desde un dron existente.* Llegó a escribirse y se retiró: solapaba con el Prototype. El Builder construye desde datos sueltos; el Prototype copia objetos ya formados.

### 3.3 Prototype

**Qué problema resuelve.** Dar de alta drones parecidos obliga a repetir los mismos datos una y otra vez. El Prototype permite registrar configuraciones habituales y partir de una copia.

**Dónde está.** Íntegramente en `servicios/DronPrototypeManager.java`: `clonar`, `registrar`, `obtenerClon` y `eliminar` sobre un `LinkedHashMap`. **El modelo no participa.**

**Dónde se usa.** `DronControlador` registra al arrancar "Fumigador estándar" y "Vigilancia nocturna", y expone `crearDesdePlantilla()` y `clonarDron()`. La vista tiene un selector con botón "Usar" que precarga el formulario, y un botón "Clonar" que duplica el dron elegido en la tabla.

**Qué se copia y qué no** (documentado en el Javadoc de `clonar()`):

| Dato | Decisión | Por qué |
|---|---|---|
| `id` | Queda en 0 | El id es la identidad en la base de datos y la copia aún no existe allí. Arrastrarlo haría que actualizar la copia sobrescribiera la fila del original. |
| `piloto` | Queda sin asignar | La columna `piloto_id` es UNIQUE: un piloto conduce un solo dron. Copiar la referencia le robaría el piloto al original. Ahora el lenguaje lo garantiza: `setPiloto()` tiene visibilidad de paquete y `servicios` no puede invocarlo. |
| `sensores` | Lista nueva **y** `Sensor` nuevo por cada uno | Un sensor es una pieza física montada en un dron concreto; la tabla `sensor` lo confirma con `ON DELETE CASCADE`. Compartir la lista haría que agregar un sensor al clon se lo agregara al original; compartir los objetos haría que editar un sensor del clon editara el del original. |
| `serial` | Sí se copia | Es un dato del dron y permite reconocer de qué plantilla proviene. La **vista** lo deja vacío al precargar, porque es único en la base y debe escribirlo quien da el alta. |

**Alternativas descartadas.**
- *Interfaz `Prototipo` con `copiar()` en el modelo y constructores copia.* **Fue la primera implementación y se retiró.** Era la más elegante en lo técnico —cada subclase se copiaba a sí misma, con retorno covariante y sin un solo `instanceof`—, pero ponía en el dominio una responsabilidad que corresponde a la capa de servicios. El requisito de no tocar el modelo zanja la discusión.
- *`Cloneable` + `clone()`.* El argumento decisivo es concreto: `super.clone()` produce una copia superficial y repararla exige **reasignar el campo `sensores`, que es `final`**. Habría que quitarle el `final` a un campo que protege la composición. Además, `Cloneable` no declara ningún método —no sirve como contrato—, `clone()` es `protected`, devuelve `Object` y lanza una excepción comprobada que aquí nunca puede ocurrir. Y, en cualquier caso, obligaría a tocar el modelo.
- *Copiadores polimórficos en servicios* (`interface CopiadorDron` con una implementación por subtipo y un `Map<TipoDron, CopiadorDron>`). Elimina el `switch` por despacho, pero son tres clases que el diagrama no contiene y cada copiador necesita igualmente un cast interno: el `instanceof` no desaparece, solo se reparte.

**El coste de sacarlo del modelo.** Se pierde el polimorfismo de la copia. Como el dominio ya no ofrece un `copiar()`, el servicio tiene que preguntar por el subtipo para leer los atributos que solo existen en una subclase: `capacidadTanque` no tiene equivalente en `Vigilancia`, ni `deteccionTermica` en `Agricultura`. Ese conocimiento está **confinado a un único método privado**, `DronPrototypeManager.copiarAtributos()`, resuelto con un `switch` exhaustivo sobre `TipoDron` para que el compilador exija el caso nuevo si algún día se añade un tercer subtipo. La construcción se delega en las fábricas, de modo que ni siquiera ahí se invoca un constructor del modelo.

### 3.4 Demostración de los patrones en la interfaz

La ventana incorpora, bajo la tabla, un **área de evidencia**: un `TextArea` de solo lectura, con scroll, tipografía monoespaciada y su propio botón "Limpiar evidencia". Dos botones escriben en ella. **Nada se imprime por consola**: no hay un solo `System.out` en la aplicación.

**Botón "Clonar"** — se habilita solo cuando hay una fila seleccionada. El control se hace atando `disableProperty()` a la propiedad de selección de la tabla, en vez de comprobarlo al pulsar: el usuario no puede llegar a un estado inválido. Copia el dron vía `DronPrototypeManager`, lo carga en el formulario con el id y el serial en blanco, y vuelca el informe.

**Botón "Builder"** — construye con `DronBuilder` encadenando llamadas, carga el resultado en el formulario y escribe la secuencia ejecutada con los valores reales. Toma los datos del formulario y completa con valores de muestra los campos vacíos, para que la demostración funcione también con el formulario recién abierto.

**Por qué `System.identityHashCode` y no `hashCode()`.** `Dron` sobrescribe `hashCode()` a partir del id de negocio: dos objetos distintos con el mismo id devuelven el mismo valor, así que no sirve como evidencia de identidad. `identityHashCode` devuelve lo que la JVM asigna a cada objeto. Hay una prueba que lo verifica.

#### Una advertencia sobre la comparación de las listas de sensores

El formato pedido incluye una línea con la identidad de la lista de sensores de cada dron. **Esa comparación no demuestra nada**, y conviene saberlo antes de que lo pregunte el evaluador: `Dron.getSensores()` devuelve la lista envuelta con `Collections.unmodifiableList()`, y ese envoltorio **se construye en cada llamada**. Comprobado:

```
clase envoltorio : java.util.Collections$UnmodifiableRandomAccessList
vista 1 : 1dbd16a6
vista 2 : 251a69d7
v1 == v2 (misma lista real detrás) -> false
```

Dos vistas de la **misma** `ArrayList` ya dan identidades distintas. Es decir, esa línea saldría igual aunque la copia fuera superficial. Arreglarlo exigiría cambiar `getSensores()`, que el requisito prohíbe.

**Solución adoptada:** la línea se imprime igualmente, porque el enunciado la pide, pero el informe advierte de que no prueba nada y añade debajo la comprobación que sí lo hace: agrega un sensor **solo al clon** y muestra los recuentos de ambas listas. Si se compartieran, el original también crecería. El sensor de prueba se retira después, de modo que ni el original ni la copia quedan alterados; hay una prueba que lo verifica.

Esta comprobación funciona además cuando el dron no tiene sensores, que es el caso habitual: `DronDAOImpl` no los carga, porque `SensorDAO` se eliminó por no aparecer en el diagrama.

---

## 4. Decisiones de diseño

### 4.1 `ConexionBD` con conexión compartida

El Singleton pasó de entregar una conexión nueva por llamada a mantener una única `Connection`. Es viable porque **JavaFX atiende la interfaz en un solo hilo**: nunca hay dos operaciones simultáneas compitiendo por ella. A cambio se evita abrir una conexión TCP contra PostgreSQL en cada operación, que es la parte más costosa de cada consulta.

Cinco cambios acompañan la decisión:

1. `getConexion()` comprueba `isClosed()` y reconecta si hace falta.
2. Los DAO **no cierran la conexión**: sale del try-with-resources, que se queda solo con `PreparedStatement` y `ResultSet`, que sí abre el DAO. Cada método lleva el comentario que lo explica.
3. `MainApp.stop()` la cierra al terminar la aplicación.
4. `ConexionBDTest` se reescribió: donde antes se exigían conexiones independientes, ahora se verifica la reutilización con `assertSame` y el restablecimiento tras cerrarla.
5. El Javadoc de `getConexion()` deja constancia de que la conexión pertenece al Singleton y de que quien la recibe no debe cerrarla.

`cerrarConexion()` pone el campo a `null` en su `finally`, de modo que el Singleton nunca queda inservible: la siguiente petición reabre. Eso permite probar el cierre sin romper las pruebas que corren después en la misma JVM.

Se conservaron la carga desde `.env` y la doble comprobación con `volatile`.

**Efecto medible:** `ManejoErroresTest` pasó de 0,638 s a 0,036 s.

### 4.2 `DronPrototypeManager` duplicado en el diagrama

Aparecía dos veces. Se trató como error de dibujo: existe una sola clase.

### 4.3 `TipoDron` no aparece en el diagrama

**Se conserva.** Es el único lugar donde vive el literal de la columna discriminadora, y lo consumen seis puntos: `Dron.getTipo()`, el DAO al escribir y al leer, `DronBuilder`, el `ComboBox` de la vista y el `CHECK (tipo IN ('agricultura','vigilancia'))` del esquema.

Sin él habría que esparcir literales `String` por el DAO, el controlador y la vista: el compilador dejaría de detectar erratas, un `"agricultra"` reventaría en ejecución contra el CHECK, el ComboBox necesitaría otra fuente de valores y se perdería el mensaje claro de `desdeCodigo()` ante un tipo desconocido.

Es un **detalle de implementación del mapeo de herencia**, no una entidad de dominio; los diagramas de clases no suelen dibujar enums auxiliares.

### 4.4 `DronDAOImpl` no usa el Builder

`mapearDron()` llama directamente a las fábricas. Reconstruir una fila existente no debe someterse a las validaciones de un alta: si mañana el builder exigiera un campo nuevo, el sistema no podría releer las filas antiguas. Son dos caminos con requisitos distintos y conviene que sigan separados.

### 4.5 Dónde quedó la validación

`registrarDron()` ya no llama a `validarDatosComunes()`: valida el builder y el controlador solo traduce `IllegalStateException` a `OperacionFallidaException`. `validarDatosComunes()` sigue vivo porque lo usa `actualizarDron()`, que modifica un dron existente y no pasa por el builder.

### 4.6 Paquetes renombrados en dos pasos

Windows no distingue mayúsculas en nombres de carpeta, así que `Modelo` → `modelo` en un solo `git mv` habría dejado el repositorio inconsistente. El cambio se hizo pasando por un nombre temporal, en dos commits por paquete.

`Vista` y `Controlador` conservan su mayúscula: el diagrama solo nombra `modelo` y `servicios`.

### 4.7 `Piloto.dron`

`Piloto` tiene un atributo `dron` que el diagrama no lista. No es un atributo inventado: es el otro extremo de la asociación bidireccional que el diagrama dibuja como línea. Se conserva.

---

## 5. Cambios en base de datos

**El esquema no cambió de estructura.** El modelo de esta entrega no añadió ni quitó atributos; los patrones nuevos son de creación de objetos y no tocan la persistencia.

Sí se corrigió un error existente:

```sql
-- Antes: la tabla piloto no declara ninguna columna 'experiencia'
COMMENT ON COLUMN piloto.experiencia IS 'Años de experiencia acumulados.';

-- Ahora
COMMENT ON COLUMN piloto.licencia IS 'Número de licencia de vuelo del piloto.';
```

El script fallaba al montar la base desde cero. Con la corrección, `db/schema.sql` vuelve a ser ejecutable de principio a fin.

También se actualizó `db/README.md`, que documentaba credenciales en `db.properties`, archivo desaparecido al migrar la configuración a `.env`.

**Pendiente de verificar:** el script no se ejecutó durante esta entrega porque empieza con `DROP TABLE IF EXISTS` y habría borrado los datos de trabajo. Conviene probarlo sobre una base desechable.

---

## 6. Estado de las pruebas

**Antes: 18. Ahora: 65.**

El recuento bajó momentáneamente a 15 al eliminar `Daosrestantestest` junto con los tres DAO que el diagrama no contempla, y subió desde ahí.

| Clase | Pruebas | Qué cubre |
|---|---:|---|
| `ConexionBDTest` | 5 | Unicidad del Singleton, conexión abierta, **reutilización con `assertSame`**, restablecimiento tras cerrarla, y dos operaciones consecutivas sobre la misma conexión. Integración real contra PostgreSQL. |
| `FactoriasDronTest` | 6 | Cada fábrica construye su subtipo, atributos comunes y específicos, el tipo lo declara el objeto, polimorfismo sobre la colección, igualdad por id. |
| `DronBuilderTest` | 14 | Ambos subtipos, encadenamiento (`assertSame` sobre `this`), reutilización del builder, id en 0 por defecto, y una prueba por cada validación. |
| `DronPrototypeTest` | 17 | **Que el modelo siga ajeno al patrón** (por reflexión), clase concreta conservada, datos del subtipo, id descartado, piloto no robado, identidades distintas, **lista de sensores independiente**, **sensores duplicados**, atributos independientes, y el registro: clones distintos, plantilla inmune a la edición de un clon y a la del objeto registrado, clave desconocida, eliminación, orden de registro. |
| `InformeDeIdentidadTest` | 8 | Identidades en hexadecimal, uso de `identityHashCode` y **no** del `hashCode` sobrescrito, las comparaciones pedidas, la advertencia sobre las listas, la prueba de independencia, que el informe no altere los drones que examina, y el informe del Builder. |
| `DronControladorTest` | 9 | Alta y consulta con DAO en memoria; integración del Prototype; los dos botones de demostración, incluido el caso del formulario vacío. |
| `ManejoErroresTest` | 5 | Serial vacío, **tipo nulo**, peso negativo, serial duplicado y alta correcta. Integración real. |
| `FlujoCrudDronTest` | 1 | Tres altas, una actualización, una relectura y tres bajas **encadenadas**, comprobando que la conexión sigue siendo la misma. Integración real. |

**Sobre la prueba de copia superficial.** `modificarUnSensorDelClon_noDebeAfectarAlDelOriginal` se verificó rompiendo a propósito la copia (compartiendo los objetos `Sensor`): la prueba **falla**, como debe. Después se restauró el código.

**Sobre la prueba por reflexión.** `elModelo_noDebeConocerElPatronPrototype` comprueba que `Dron` no expone `copiar()`, que ninguna clase del modelo tiene constructor copia y que `Dron` no implementa interfaz alguna. Falla si alguien vuelve a meter la lógica del patrón en el dominio: convierte el requisito del profesor en una prueba automática en vez de en una nota en un documento.

**Javadoc:** `mvn javadoc:javadoc` pasó de 67 avisos a 0.

---

## 7. Riesgos y pendientes

1. **Verificación manual de la interfaz pendiente.** El ciclo con clics —crear, listar, actualizar, eliminar; campos que aparecen y desaparecen según el tipo; mensaje legible ante serial duplicado— no se hizo. La aplicación arranca sin excepciones y `FlujoCrudDronTest` y `ManejoErroresTest` cubren la lógica contra la base real, pero la parte visual requiere una pasada a mano.

2. **`db/schema.sql` sin ejecutar tras la corrección.** Ver §5.

3. **Las pruebas de integración necesitan PostgreSQL en marcha.** `ConexionBDTest`, `ManejoErroresTest` y `FlujoCrudDronTest` fallan sin base de datos. Aceptable en un proyecto académico; en un entorno de integración continua habría que separarlas o levantar una base efímera.

4. **La conexión compartida es la decisión más frágil del diseño.** Funciona porque JavaFX es monohilo. Deja de servir en cuanto haya un segundo hilo —una carga en segundo plano con `Task`, por ejemplo— o si dos operaciones necesitaran transacciones independientes. La salida natural sería un pool de conexiones (HikariCP), que no se incorporó por no añadir dependencias que el enunciado no pide. Está desarrollado en el guion de sustentación.

5. **La decisión tipo → subclase vive en dos sitios**, `DronBuilder.build()` y `DronDAOImpl.mapearDron()`. Es deliberado (§3.1), y el `switch` exhaustivo sobre el enum hace que el compilador exija actualizar ambos al añadir un tercer tipo. Aun así, conviene tenerlo presente.

6. **`Piloto`, `Sensor` y `Mision` se quedaron sin persistencia** al eliminar sus DAO. Sus tablas siguen en el esquema y las clases en el modelo, pero ninguna operación de la aplicación las usa. Es lo que pide el diagrama; si la entrega 5 necesita esas entidades, habrá que reintroducir el acceso a datos.

7. ~~**Directorio vacío `src/main/java/co/edu/poli/sw2/Config/`.**~~ Resuelto: era un residuo de cuando `ConexionBD` vivía ahí. Se eliminó. No aparece en el historial de git porque los directorios vacíos no se rastrean.
