# -*- coding: utf-8 -*-
"""Genera las estructuras (.nbt) + datos de worldgen (jigsaw) de HyruleCraft."""
import gzip, io, json, math, os, struct

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA = os.path.join(ROOT, "src/main/resources/data/hyrulecraft")
N = "hyrulecraft"

# ----------------------------------------------------------------- NBT mínimo
class T:
    def __init__(s, i, v): s.i, s.v = i, v
def B(v): return T(1, v)
def SH(v): return T(2, v)
def I(v): return T(3, v)
def D(v): return T(6, v)
def STR(v): return T(8, v)
def LST(tid, items): return T(9, (tid, items))
def C(d): return T(10, d)

def _pay(b, t):
    i = t.i
    if i == 1: b.write(struct.pack(">b", t.v))
    elif i == 2: b.write(struct.pack(">h", t.v))
    elif i == 3: b.write(struct.pack(">i", t.v))
    elif i == 6: b.write(struct.pack(">d", t.v))
    elif i == 8:
        e = t.v.encode("utf-8"); b.write(struct.pack(">H", len(e))); b.write(e)
    elif i == 9:
        tid, items = t.v
        b.write(struct.pack(">b", tid)); b.write(struct.pack(">i", len(items)))
        for it in items: _pay(b, it)
    elif i == 10:
        for k, v in t.v.items():
            b.write(struct.pack(">b", v.i)); e = k.encode("utf-8"); b.write(struct.pack(">H", len(e))); b.write(e); _pay(b, v)
        b.write(b"\x00")

def write_nbt(path, root):
    b = io.BytesIO(); b.write(b"\x0a"); b.write(struct.pack(">H", 0)); _pay(b, root)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with gzip.open(path, "wb") as f: f.write(b.getvalue())

# ----------------------------------------------------------------- Constructor
AIR = "minecraft:air"
class Struct:
    def __init__(self, sx, sy, sz, ground):
        self.size = (sx, sy, sz); self.ground = ground
        self.blocks = {}; self.entities = []
    def set(self, x, y, z, name, props=None, nbt=None):
        if 0 <= x < self.size[0] and 0 <= y < self.size[1] and 0 <= z < self.size[2]:
            self.blocks[(x, y, z)] = (name, props or {}, nbt)
    def fill(self, x1, y1, z1, x2, y2, z2, name, props=None):
        for x in range(min(x1, x2), max(x1, x2) + 1):
            for y in range(min(y1, y2), max(y1, y2) + 1):
                for z in range(min(z1, z2), max(z1, z2) + 1):
                    self.set(x, y, z, name, props)
    def shell(self, x1, y1, z1, x2, y2, z2, wall):
        for x in range(x1, x2 + 1):
            for y in range(y1, y2 + 1):
                for z in range(z1, z2 + 1):
                    edge = x in (x1, x2) or z in (z1, z2) or y in (y1, y2)
                    self.set(x, y, z, wall if edge else AIR)
    def disc(self, cx, cz, r, y, name, props=None, hollow=False):
        for x in range(cx - r, cx + r + 1):
            for z in range(cz - r, cz + r + 1):
                d = math.hypot(x - cx, z - cz)
                if d <= r + 0.3 and (not hollow or d >= r - 0.9):
                    self.set(x, y, z, name, props)
    def chest(self, x, y, z, loot, facing="south"):
        self.set(x, y, z, "minecraft:chest", {"facing": facing, "type": "single", "waterlogged": "false"},
                 C({"id": STR("minecraft:chest"), "LootTable": STR(f"{N}:chests/{loot}")}))
    def spawner(self, x, y, z, mob):
        self.set(x, y, z, "minecraft:spawner", None, C({
            "id": STR("minecraft:mob_spawner"),
            "SpawnData": C({"entity": C({"id": STR(mob)})}),
            "SpawnCount": SH(2), "SpawnRange": SH(4), "RequiredPlayerRange": SH(14), "MaxNearbyEntities": SH(5),
            "MinSpawnDelay": SH(240), "MaxSpawnDelay": SH(600), "Delay": SH(20)}))
    def entity(self, x, y, z, nbt):
        self.entities.append((x, y, z, nbt))
    def save(self, name):
        palette, index, blocks = [], {}, []
        for (x, y, z), (nm, props, nbt) in sorted(self.blocks.items()):
            key = (nm, tuple(sorted(props.items())))
            if key not in index:
                index[key] = len(palette)
                e = {"Name": STR(nm)}
                if props: e["Properties"] = C({k: STR(v) for k, v in props.items()})
                palette.append(C(e))
            b = {"state": I(index[key]), "pos": LST(3, [I(x), I(y), I(z)])}
            if nbt: b["nbt"] = nbt
            blocks.append(C(b))
        ents = []
        for x, y, z, nbt in self.entities:
            ents.append(C({"pos": LST(6, [D(x + 0.5), D(y), D(z + 0.5)]), "blockPos": LST(3, [I(x), I(y), I(z)]), "nbt": nbt}))
        root = C({"size": LST(3, [I(v) for v in self.size]), "palette": LST(10, palette), "blocks": LST(10, blocks),
                  "entities": LST(10, ents), "DataVersion": I(3465)})
        write_nbt(os.path.join(DATA, "structures", name + ".nbt"), root)

H = f"{N}:hyrule_stone_bricks"; HC = f"{N}:carved_hyrule_stone"; PIL = f"{N}:temple_pillar"
FLAME = f"{N}:sacred_flame"; TRI = f"{N}:triforce_tile"; POT = f"{N}:pot"; CRACK = f"{N}:cracked_wall"
def pad(k): return f"{N}:warp_pad_{k}"
def facing(f): return {"facing": f}

def door(s, x, y, z, w, h, axis="x"):
    for i in range(w):
        for j in range(h):
            if axis == "x": s.set(x + i, y + j, z, AIR)
            else: s.set(x, y + j, z + i, AIR)

# ----------------------------------------------------------------- Estructuras
def temple_of_time():
    G = 2; s = Struct(21, G + 14, 21, G)
    s.fill(0, G, 0, 20, G + 13, 20, AIR)
    s.fill(0, 0, 0, 20, G - 1, 20, H)
    s.fill(7, G - 1, 7, 13, G - 1, 13, HC); s.fill(8, G - 1, 8, 12, G - 1, 12, TRI)
    s.shell(3, G, 3, 17, G + 8, 17, H)
    door(s, 9, G, 17, 3, 4)
    s.fill(2, G + 9, 2, 18, G + 9, 18, H); s.fill(3, G + 10, 3, 17, G + 10, 17, f"{N}:hyrule_stone_brick_slab", {"type": "bottom", "waterlogged": "false"})
    for x, z in [(2, 2), (18, 2), (2, 18), (18, 18), (2, 10), (18, 10), (10, 2), (10, 18)]:
        s.fill(x, G, z, x, G + 8, z, PIL, {"axis": "y"})
    for x in (6, 14):
        for z in (6, 14): s.set(x, G, z, FLAME)
    s.set(10, G, 8, f"{N}:master_sword_pedestal", {"has_sword": "true"})
    s.set(10, G, 14, pad("light"))
    s.chest(5, G, 5, "ocarina", "south")
    for dx in (-1, 1): s.set(10 + dx, G + 7, 17, FLAME)
    s.save("temple_of_time")
    return s

def fairy_fountain():
    G = 2; s = Struct(15, G + 9, 15, G)
    s.fill(0, G, 0, 14, G + 8, 14, AIR)
    s.disc(7, 7, 7, 0, H); s.disc(7, 7, 7, 1, H)
    s.disc(7, 7, 6, G - 1, "minecraft:pink_concrete"); s.disc(7, 7, 4, G - 1, "minecraft:pink_wool")
    for y in range(G, G + 5): s.disc(7, 7, 7, y, f"{N}:hyrule_stone" if y % 2 else H, hollow=True)
    for a in range(0, 360, 45):
        x = 7 + round(6.5 * math.cos(math.radians(a))); z = 7 + round(6.5 * math.sin(math.radians(a)))
        s.set(x, G + 2, z, "minecraft:pink_stained_glass"); s.set(x, G + 3, z, "minecraft:pink_stained_glass")
    for y in (G + 5, G + 6): s.disc(7, 7, 7 - (y - G - 4), y, H)
    s.disc(7, 7, 4, G + 5, "minecraft:glass"); s.disc(7, 7, 3, G + 6, "minecraft:glass")
    for dx in (-1, 0, 1):
        for dz in (-1, 0, 1): s.set(7 + dx, G, 7 + dz, f"{N}:fairy_fountain_pad")
    s.set(7, G, 7, f"{N}:fairy_fountain_pad")
    s.set(7, G + 4, 7, FLAME)
    s.chest(7, G, 11, "fairy", "north"); s.set(7, G, 3, pad("spirit"))
    door(s, 6, G, 0, 3, 3, "x")
    s.save("fairy_fountain")
    return s

def forest_shrine():
    G = 2; s = Struct(15, G + 10, 15, G)
    s.fill(0, G, 0, 14, G + 9, 14, AIR)
    s.fill(0, 0, 0, 14, G - 1, 14, f"{N}:forest_bricks")
    s.shell(1, G, 1, 13, G + 6, 13, f"{N}:forest_bricks")
    door(s, 6, G, 13, 3, 3)
    for x, z in [(3, 3), (11, 3), (3, 11), (11, 11)]:
        s.fill(x, G, z, x, G + 5, z, PIL, {"axis": "y"})
    s.fill(0, G + 7, 0, 14, G + 7, 14, f"{N}:forest_bricks"); s.fill(4, G + 7, 4, 10, G + 7, 10, "minecraft:oak_leaves", {"persistent": "true", "distance": "1", "waterlogged": "false"})
    for dx, dz in [(-2, -2), (2, -2), (-2, 2), (2, 2)]: s.set(7 + dx, G, 7 + dz, FLAME)
    s.set(7, G, 5, f"{N}:carved_hyrule_stone"); s.chest(7, G + 1, 5, "stone_forest", "south")
    s.set(4, G, 10, pad("forest")); s.spawner(10, G, 10, f"{N}:skulltula"); s.spawner(4, G, 4, f"{N}:stalfos")
    for x, z in [(2, 7), (12, 7), (7, 2)]: s.set(x, G, z, POT)
    s.chest(12, G, 2, "shrine_common", "west")
    s.save("forest_shrine")
    return s

def fire_shrine():
    G = 2; s = Struct(15, G + 10, 15, G)
    s.fill(0, G, 0, 14, G + 9, 14, AIR)
    s.fill(0, 0, 0, 14, G - 1, 14, f"{N}:fire_bricks")
    s.shell(1, G, 1, 13, G + 7, 13, f"{N}:fire_bricks")
    door(s, 6, G, 13, 3, 3)
    s.fill(4, G - 1, 4, 10, G - 1, 10, "minecraft:lava"); s.fill(6, G - 1, 4, 8, G - 1, 10, "minecraft:obsidian")
    s.fill(4, 0, 4, 10, 0, 10, "minecraft:obsidian")
    s.chest(7, G, 4, "stone_fire", "south"); s.set(7, G - 1, 4, "minecraft:obsidian")
    for x, z in [(2, 2), (12, 2), (2, 12), (12, 12)]: s.set(x, G, z, FLAME)
    s.set(3, G, 11, pad("fire")); s.spawner(11, G, 11, f"{N}:stalfos"); s.spawner(3, G, 3, f"{N}:redead")
    s.chest(12, G, 7, "shrine_common", "west"); s.set(2, G, 7, POT)
    s.save("fire_shrine")
    return s

def water_shrine():
    G = 2; s = Struct(15, G + 10, 15, G)
    s.fill(0, G, 0, 14, G + 9, 14, AIR)
    s.fill(0, 0, 0, 14, G - 1, 14, f"{N}:water_tiles")
    s.shell(1, G, 1, 13, G + 6, 13, f"{N}:water_tiles")
    door(s, 6, G, 13, 3, 3)
    s.fill(3, G - 1, 3, 11, G - 1, 11, f"{N}:water_tiles"); s.fill(4, G - 1, 4, 10, G + 2, 10, "minecraft:water", {"level": "0"})
    s.fill(4, G - 2, 4, 10, G - 2, 10, "minecraft:prismarine")
    s.fill(4, G - 2 + 0, 4, 10, G - 2, 10, "minecraft:prismarine")
    s.chest(7, G - 1, 7, "stone_water", "south")
    for x, z in [(2, 2), (12, 2), (2, 12), (12, 12)]: s.set(x, G, z, "minecraft:sea_lantern")
    s.set(2, G, 7, pad("water")); s.spawner(12, G, 12, f"{N}:skulltula")
    s.chest(12, G, 7, "shrine_common", "west")
    s.save("water_shrine")
    return s

def ganons_arena():
    G = 3; s = Struct(27, G + 18, 27, G)
    s.fill(0, G, 0, 26, G + 17, 26, AIR)
    s.disc(13, 13, 13, 0, f"{N}:shadow_bricks"); s.disc(13, 13, 13, 1, f"{N}:shadow_bricks"); s.disc(13, 13, 13, 2, f"{N}:shadow_bricks")
    s.disc(13, 13, 10, G - 1, f"{N}:carved_hyrule_stone"); s.disc(13, 13, 6, G - 1, f"{N}:shadow_bricks"); s.disc(13, 13, 3, G - 1, TRI)
    for y in range(G, G + 9): s.disc(13, 13, 12, y, f"{N}:shadow_bricks", hollow=True)
    for a in range(0, 360, 45):
        x = 13 + round(9 * math.cos(math.radians(a))); z = 13 + round(9 * math.sin(math.radians(a)))
        s.fill(x, G, z, x, G + 8, z, PIL, {"axis": "y"}); s.set(x, G + 9, z, FLAME)
    door(s, 12, G, 25, 3, 4); door(s, 12, G, 24, 3, 4); door(s, 12, G, 23, 3, 4)
    s.set(13, G, 22, pad("shadow"))
    s.chest(8, G, 19, "shrine_rare", "north"); s.chest(18, G, 19, "shrine_rare", "north")
    s.entity(13, G, 10, C({"id": STR(f"{N}:ganondorf"), "PersistenceRequired": B(1),
        "HandItems": LST(10, [C({"id": STR(f"{N}:biggoron_sword"), "Count": B(1)}), C({})])}))
    s.save("ganons_arena")
    return s

# ----------------------------------------------------------------- Datos de worldgen
STRUCTS = {
    # nombre: (constructor, biomas, espaciado, separación, sal, heightmap)
    "temple_of_time": (temple_of_time, ["minecraft:plains", "minecraft:sunflower_plains", "minecraft:meadow"], 22, 8, 1470001, "WORLD_SURFACE_WG"),
    "fairy_fountain": (fairy_fountain, ["#minecraft:is_forest", "minecraft:flower_forest", "#minecraft:is_taiga"], 20, 7, 1470002, "WORLD_SURFACE_WG"),
    "forest_shrine": (forest_shrine, ["minecraft:dark_forest", "minecraft:forest", "minecraft:birch_forest"], 18, 6, 1470003, "WORLD_SURFACE_WG"),
    "fire_shrine": (fire_shrine, ["#minecraft:is_badlands", "minecraft:desert", "minecraft:savanna"], 18, 6, 1470004, "WORLD_SURFACE_WG"),
    "water_shrine": (water_shrine, ["minecraft:swamp", "minecraft:mangrove_swamp", "minecraft:river"], 16, 6, 1470005, "OCEAN_FLOOR_WG"),
    "ganons_arena": (ganons_arena, ["#minecraft:is_mountain", "#minecraft:is_hill"], 26, 8, 1470006, "WORLD_SURFACE_WG"),
}

def wj(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f: json.dump(obj, f, indent=2)

def run():
    for name, (fn, biomes, spacing, sep, salt, hm) in STRUCTS.items():
        st = fn()
        wj(f"{DATA}/tags/worldgen/biome/has_structure/{name}.json", {"values": biomes})
        wj(f"{DATA}/worldgen/structure/{name}.json", {
            "type": "minecraft:jigsaw", "biomes": f"#{N}:has_structure/{name}", "step": "surface_structures",
            "spawn_overrides": {}, "terrain_adaptation": "beard_thin", "start_pool": f"{N}:{name}/start", "size": 1,
            "start_height": {"absolute": -st.ground}, "project_start_to_heightmap": hm,
            "max_distance_from_center": 80, "use_expansion_hack": False})
        wj(f"{DATA}/worldgen/template_pool/{name}/start.json", {"name": f"{N}:{name}/start", "fallback": "minecraft:empty", "elements": [{
            "weight": 1, "element": {"element_type": "minecraft:single_pool_element", "location": f"{N}:{name}",
                                     "processors": "minecraft:empty", "projection": "rigid"}}]})
        wj(f"{DATA}/worldgen/structure_set/{name}.json", {"structures": [{"structure": f"{N}:{name}", "weight": 1}],
            "placement": {"type": "minecraft:random_spread", "spacing": spacing, "separation": sep, "salt": salt}})
    print(f"  Estructuras: {', '.join(STRUCTS)}")

if __name__ == "__main__":
    run()
