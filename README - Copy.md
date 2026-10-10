# HyruleCraft: Ocarina Edition — Guía completa
Mod para **Minecraft 1.20.1 · Fabric · Java 17**. Contenido inspirado en *The Legend of Zelda: Ocarina of Time*.

> **Estado honesto:** el código y los recursos están completos, pero el mod se escribió en un entorno **sin internet**, así que **no se pudo compilar ni probar dentro de Minecraft**. Se revisó la sintaxis de los 64 archivos Java y que cada ítem/bloque tenga textura, modelo y traducción. Es normal que la primera compilación muestre algún error pequeño de nombre de método: mándame el mensaje y lo corrijo.

## 1. Instalar (Windows + PrismLauncher)
1. Instancia de PrismLauncher: **Minecraft 1.20.1 + Fabric Loader 0.15+**, con **Fabric API 0.92.2+1.20.1** (Fabulously Optimized ya lo incluye).
2. Descomprime el zip y haz doble clic en **`build.bat`**. Descarga Java 17 y Gradle solo la primera vez, compila y te deja elegir la instancia donde instalarlo.
3. Si prefieres a mano: copia `dist/hyrulecraft-1.0.0.jar` a la carpeta `mods` de la instancia.
4. Para abrirlo como proyecto: IntelliJ IDEA → *Open* → carpeta del mod (usa Gradle/Loom).

## 2. Controles (reasignables en Opciones → Controles → HyruleCraft)
| Tecla | Acción |
|---|---|
| **Z** (mantener) | Z-Targeting: fija la cámara en un enemigo; WASD te mueve en círculos a su alrededor |
| **R** | Cambiar al siguiente objetivo |
| **V** | Esquive: voltereta (W), salto atrás (S o quieto con Z), salto lateral (A/D con Z) — dan un instante de invulnerabilidad |
| **K** | Menú de HyruleCraft: cambiar skin (Link niño/adulto, Zelda, Sheik, Ganondorf, Link Oscuro), modo Z alternar, bandas de cine |
| Clic derecho + ocarina | Abre el panel de la ocarina |

**Espadas:** mantén clic derecho ~1 s y suelta → **ataque giratorio** (con escudo en la mano izquierda no se activa). Golpear cayendo → **tajo en salto**.

## 3. Ocarina y canciones
En el panel: **Espacio = A**, **flechas o WASD = botones C**, **Tab** muestra/oculta la lista. Notas reales de OoT (A=Re, C↓=Fa, C→=La, C←=Si, C↑=Re agudo).
| Canción | Botones | Efecto |
|---|---|---|
| Nana de Zelda | ← ↑ → ← ↑ → | Calma monstruos y cura aliados |
| Canción de Epona | ↑ ← → ↑ ← → | Invoca a Epona (caballo ensillado) |
| Canción de Saria | ↓ → ← ↓ → ← | Resplandor en criaturas a 32 bloques |
| Canción del Sol | → ↓ ↑ → ↓ ↑ | Día↔noche y paraliza no-muertos |
| Canción del Tiempo | → A ↓ → A ↓ | Abre el pedestal de la Espada Maestra, o rebobina 10 s |
| Canción de la Tormenta | A ↓ ↑ A ↓ ↑ | Alterna lluvia/sol |
| Minueto del Bosque | A ↑ ← → ← → | Teletransporte a la plataforma del Bosque |
| Bolero del Fuego | ↓ A ↓ A → ↓ → ↓ | … del Fuego |
| Serenata del Agua | A ↓ → → ← | … del Agua |
| Réquiem del Espíritu | A ↓ A → ↓ A | … del Espíritu |
| Nocturno de las Sombras | ← → → A ← → ↓ | … de las Sombras |
| Preludio de la Luz | ↑ → ↑ → ← ↑ | … de la Luz |

Para teletransportarte antes debes **activar la plataforma** (clic derecho). Hay una en cada santuario.

## 4. Camino recomendado
1. Busca el **Templo del Tiempo** (llanuras/prados): el cofre tiene la **Ocarina**, una espada Kokiri y un escudo Deku.
2. Consigue las tres piedras: **Santuario del Bosque** (Esmeralda Kokiri), **del Fuego** (Rubí Goron; desierto/mesa) y **del Agua** (Zafiro Zora; pantanos, el cofre está bajo el agua: Túnica Zora recomendada).
3. Vuelve al Templo del Tiempo, toca la **Canción del Tiempo** junto al pedestal → **Espada Maestra**.
4. Equípate con lo que sueltan los cofres (Gancho, Bumerán, Botas, Martillo, hechizos) y busca la **Arena de Ganondorf** (montañas). Devuelve sus esferas de energía con la espada para aturdirlo.
5. La **Fuente de la Gran Hada** (bosques/taigas) cura al pisar sus plataformas.

Localizar con comandos: `/locate structure hyrulecraft:temple_of_time` (también `forest_shrine`, `fire_shrine`, `water_shrine`, `fairy_fountain`, `ganons_arena`).

## 5. Objetos
- **Espadas:** Kokiri, Maestra (giro 10 de daño), Biggoron (lenta, 14), Martillo Megatón (golpe al suelo), Palo Deku (prende fuego).
- **Escudos:** Deku, Hyliano, **Espejo** (refleja proyectiles).
- **Armadura:** set Kokiri (gorro, túnica, pantalón, botas), **Túnica Goron** (resistencia al fuego), **Túnica Zora** (respirar bajo el agua + delfín), **Botas de Hierro** (te hunden), **Botas de Aire** (agáchate en el aire para flotar).
- **Equipo:** Gancho (24 bloques), Bumerán, Bombas (rompen Muros Agrietados y vasijas, no el terreno), Nueces Deku (aturden), Hada en Botella, Lente de la Verdad.
- **Hechizos:** Fuego de Din, Amor de Nayru, Viento de Farore.
- **Corazones:** Pieza (4 = +1 corazón) y Contenedor; máximo +10 corazones y se conservan al morir.
- **Rupias:** 1/5/20/50/200. Contador en pantalla. Se pueden convertir entre ellas crafteando. Los monstruos de Minecraft también las sueltan.
- **Fichas de Skulltula Dorada:** 9 = Pieza de corazón (crafteo).
- **Piedras y medallones:** objetos de misión.
- **Bloques:** piedra y ladrillos de Hyrule (con escaleras y losas), pilar, baldosa de la Trifuerza, ladrillos de cada templo, Muro Agrietado, vasija, telaraña Deku, Llama Sagrada, plataformas de teletransporte y de hada, pedestal.

## 6. Enemigos
**Stalfos** (salta con espada), **ReDead** (su grito/mordisco paraliza), **Skulltula** (trepa; 12 % doradas sueltan ficha), **Ganondorf** (jefe con barra de vida). Todos tienen huevo generador en la pestaña creativa *HyruleCraft*. Aparecen de forma natural en el Overworld.

## 7. Personalizar
Todo el contenido sale de `tools/content.py`. Tras editarlo ejecuta `python tools/generate_all.py` (necesita Python 3, Pillow y ffmpeg) y recompila. Las skins sueltas están en `skins/` para usarlas también en el launcher.

## 8. Problemas frecuentes
- *No arranca:* revisa Fabric API 0.92.2+1.20.1 y Java 17.
- *Error al compilar:* copia el mensaje completo y envíamelo.
- *Z-Targeting no fija nada:* los objetivos válidos son criaturas vivas visibles a menos de 24 bloques (no jugadores).
- *Conflicto de tecla:* cámbiala en Controles → HyruleCraft.

## 9. Fuera de esta versión
Navi como entidad, Deku Baba/Scrub, Keese, Octorok, Poe, Like Like, Rey Dodongo, los demás templos completos (Árbol Deku, Jabu-Jabu, Espíritu, Sombras…), caballos con montura propia y animación de escalada. La base (manifiesto + generadores) está pensada para ir añadiéndolos.
