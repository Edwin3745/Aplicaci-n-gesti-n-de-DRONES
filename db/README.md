# Base de datos — Sistema de Gestión de Drones

Motor: **PostgreSQL**

## Contenido

| Archivo | Qué hace |
|---|---|
| `schema.sql` | Crea las tablas, restricciones e índices. **Borra las tablas existentes.** |
| `datos_prueba.sql` | Inserta datos de ejemplo para desarrollo y pruebas |

## Montaje desde cero

```bash
psql -U postgres -c "CREATE DATABASE dron_bd;"
psql -U postgres -d dron_bd -f db/schema.sql
psql -U postgres -d dron_bd -f db/datos_prueba.sql
```

Desde pgAdmin: crear la base `dron_bd`, abrir el *Query Tool* sobre ella, pegar el contenido de cada archivo y ejecutar (F5), primero `schema.sql` y luego `datos_prueba.sql`.

> `schema.sql` empieza con `DROP TABLE IF EXISTS`. Si ya hay datos que importan, respáldalos antes.

## Conexión desde la aplicación

Las credenciales se leen de un archivo `.env` en la raíz del proyecto, o de las variables de entorno del sistema si el archivo no existe. `.env` está en `.gitignore` y no debe subirse al repositorio.

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/dron_bd
DB_USER=postgres
DB_PASSWORD=<tu_contraseña_local>
```

Quien lee ese archivo es `ConexionBD`, el Singleton de la capa de servicios. Lo hace una sola vez, al crear la instancia, y a partir de ahí mantiene una única conexión compartida por toda la aplicación.

## Modelo de datos

```
piloto 1 ──── 0..1 dron 1 ──── 0..N sensor
                    │
                    └── N ──── mision_dron ──── N ── mision
```

- **piloto ↔ dron**: 1:1. La FK vive en `dron.piloto_id` con `UNIQUE`, que impide que dos drones compartan piloto. `ON DELETE SET NULL`: al borrar un piloto, sus drones quedan sin asignar en vez de borrarse.
- **dron → sensor**: composición 1:N. `ON DELETE CASCADE`: los sensores no existen sin su dron.
- **mision ↔ dron**: N:M mediante la tabla puente `mision_dron`.

## Estrategia de herencia

La jerarquía `Dron → {Agricultura, Vigilancia}` usa **tabla única con columna discriminadora** (*Single Table Inheritance*).

Toda la jerarquía vive en la tabla `dron`. La columna `tipo` indica de qué subclase es cada fila:

| id | tipo | serial | peso | capacidad_tanque | deteccion_termica |
|---|---|---|---|---|---|
| 1 | agricultura | AGR-001 | 38.0 | 40.0 | *NULL* |
| 4 | vigilancia | VIG-001 | 3.7 | *NULL* | true |

**Por qué esta y no otra:** con dos subtipos y un atributo específico cada uno, las columnas nulas son un costo trivial frente a la ventaja de que cada operación del DAO sea una sola sentencia SQL sin JOINs ni transacciones. Las alternativas (*Class Table Inheritance*, *Concrete Table Inheritance*) exigirían dos sentencias coordinadas por operación.

**Correspondencia con el código Java:**

| Base de datos | Java |
|---|---|
| `tipo = 'agricultura'` | `TipoDron.AGRICULTURA` → clase `Agricultura` |
| `tipo = 'vigilancia'` | `TipoDron.VIGILANCIA` → clase `Vigilancia` |

El literal exacto se define en `TipoDron.getCodigo()`. El `CHECK` de la tabla replica esa lista: el valor queda validado en las dos capas.

## Restricciones a tener en cuenta

- **`id` es `SERIAL`** — lo genera PostgreSQL. Los `INSERT` no deben enviarlo; el DAO recupera el valor con `getGeneratedKeys()`.
- **`serial` es `UNIQUE`** — insertar un número de serie repetido lanza `SQLException`. Es la regla del negocio, no un error a evadir.
- **`chk_dron_atributos_por_tipo`** — un dron de agricultura debe traer `capacidad_tanque` y `deteccion_termica` en NULL; el de vigilancia al revés. Impide filas incoherentes con su subtipo.
- **`chk_dron_tipo`** — solo admite `'agricultura'` y `'vigilancia'`. Al agregar un subtipo nuevo hay que ampliar este CHECK además de crear la clase Java.

## Verificación

```sql
-- Estructura de la tabla dron
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 'dron'
ORDER BY ordinal_position;

-- Conteo por tipo de dron
SELECT tipo, COUNT(*) FROM dron GROUP BY tipo;

-- Drones con su piloto y número de sensores
SELECT d.serial, d.tipo, p.nombre AS piloto, COUNT(s.id) AS sensores
FROM dron d
LEFT JOIN piloto p ON p.id = d.piloto_id
LEFT JOIN sensor s ON s.dron_id = d.id
GROUP BY d.serial, d.tipo, p.nombre
ORDER BY d.serial;
```

Con los datos de prueba cargados: 4 pilotos, 6 drones (3 de agricultura, 3 de vigilancia), 7 sensores y 3 misiones.
