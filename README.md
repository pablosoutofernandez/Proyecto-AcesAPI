# Proyecto-AcesAPI

## API (AcesAPI)

La aplicación obtiene los datos de [AcesAPI](https://mcp.acesapi.com/v1/). Todas las peticiones son `POST` con un cuerpo JSON y la API key en las cabeceras (se configura en `config.properties`, ver `config.properties.example`).

### `search_players`: buscar tenistas por nombre

```json
{ "name": "sinner" }
```

Devuelve `player_id`, `name`, `country`, `date_of_birth`, `matches`, `last_match`, `current_rank` y `plays`.

![Petición search_players en Postman](docs/postman-search-players.png)

### `get_head_to_head`: enfrentamientos entre dos tenistas

```json
{ "player_id": "3623", "opponent_id": "3782" }
```

Devuelve los dos jugadores y un `summary` con el historial de partidos entre ellos.

![Petición get_head_to_head en Postman](docs/postman-head-to-head.png)

### `get_player_stats`: estadísticas de un tenista por temporada

```json
{ "player_id": "3623", "season": 2025 }
```

Devuelve en `by_season` las victorias y derrotas, el porcentaje de victorias y los sets y juegos ganados/perdidos.

![Petición get_player_stats en Postman](docs/postman-player-stats.png)

## Base de datos

Diagrama entidad-relación (notación Crow's Foot) de la BD `tenisdle`:

![Diagrama ER de la BD tenisdle](docs/diagrama-er.png)

- **tenistas**: datos de cada tenista descargados de la API (PK `id`).
- **partidas**: cada partida jugada, asociada a un tenista mediante la FK `tenista_id` (`ON DELETE CASCADE`).
- Relación 1:N: un tenista puede tener 0..N partidas; cada partida pertenece a exactamente un tenista.

## Conclusiones

Pablo Souto: Ha sido un proyecto entretenido de realizar y sin mucha complejidad técnica ya que las técnicas utilizadas no son nuevas para mí. Desarrollar el backend me llevó un par de días, y la calificación que espero ronda el 7,5.
