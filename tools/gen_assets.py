# -*- coding: utf-8 -*-
"""Modelos, blockstates, idioma, loot, recetas, tags, logros y sonidos.json."""
import json, os
from content import ITEMS, BLOCKS, MOBS, SONGS, WARPS

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES = os.path.join(ROOT, "src/main/resources")
A = RES + "/assets/hyrulecraft"
D = RES + "/data/hyrulecraft"
N = "hyrulecraft"

def w(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(obj, f, ensure_ascii=False, indent=2)

# ---------------------------------------------------------------- MODELOS
def models():
    for it in ITEMS:
        i = it["id"]
        if it["model"] == "spawn_egg":
            w(f"{A}/models/item/{i}.json", {"parent": "minecraft:item/template_spawn_egg"})
        else:
            w(f"{A}/models/item/{i}.json", {"parent": f"minecraft:item/{it['model']}", "textures": {"layer0": f"{N}:item/{i}"}})

    for b in BLOCKS:
        i, k = b["id"], b["kind"]
        tex = f"{N}:block/{i}"
        if k == "cube":
            w(f"{A}/models/block/{i}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": tex}})
            w(f"{A}/blockstates/{i}.json", {"variants": {"": {"model": f"{N}:block/{i}"}}})
        elif k == "pillar":
            w(f"{A}/models/block/{i}.json", {"parent": "minecraft:block/cube_column", "textures": {"end": f"{tex}_top", "side": tex}})
            w(f"{A}/models/block/{i}_horizontal.json", {"parent": "minecraft:block/cube_column_horizontal", "textures": {"end": f"{tex}_top", "side": tex}})
            w(f"{A}/blockstates/{i}.json", {"variants": {
                "axis=y": {"model": f"{N}:block/{i}"},
                "axis=z": {"model": f"{N}:block/{i}_horizontal", "x": 90},
                "axis=x": {"model": f"{N}:block/{i}_horizontal", "x": 90, "y": 90}}})
        elif k == "slab":
            t = {"bottom": f"{N}:block/hyrule_stone_bricks", "top": f"{N}:block/hyrule_stone_bricks", "side": f"{N}:block/hyrule_stone_bricks"}
            w(f"{A}/models/block/{i}.json", {"parent": "minecraft:block/slab", "textures": t})
            w(f"{A}/models/block/{i}_top.json", {"parent": "minecraft:block/slab_top", "textures": t})
            w(f"{A}/blockstates/{i}.json", {"variants": {
                "type=bottom": {"model": f"{N}:block/{i}"}, "type=top": {"model": f"{N}:block/{i}_top"},
                "type=double": {"model": f"{N}:block/hyrule_stone_bricks"}}})
        elif k == "stairs":
            t = {"bottom": f"{N}:block/hyrule_stone_bricks", "top": f"{N}:block/hyrule_stone_bricks", "side": f"{N}:block/hyrule_stone_bricks"}
            for suffix, parent in (("", "stairs"), ("_inner", "inner_stairs"), ("_outer", "outer_stairs")):
                w(f"{A}/models/block/{i}{suffix}.json", {"parent": f"minecraft:block/{parent}", "textures": t})
            variants = {}
            for facing, ybase in (("east", 0), ("south", 90), ("west", 180), ("north", 270)):
                for half in ("bottom", "top"):
                    for shape in ("straight", "inner_left", "inner_right", "outer_left", "outer_right"):
                        model = {"straight": "", "inner_left": "_inner", "inner_right": "_inner", "outer_left": "_outer", "outer_right": "_outer"}[shape]
                        y = ybase
                        if shape in ("inner_left", "outer_left"): y = (ybase + 270) % 360
                        entry = {"model": f"{N}:block/{i}{model}"}
                        x = 180 if half == "top" else 0
                        if half == "top" and shape in ("inner_right", "outer_right", "straight"):
                            y = y if shape == "straight" else y
                        if half == "top":
                            if shape in ("inner_left", "outer_left"): y = ybase
                            elif shape in ("inner_right", "outer_right"): y = (ybase + 90) % 360
                        if x: entry["x"] = x
                        if y: entry["y"] = y
                        if x or y: entry["uvlock"] = True
                        variants[f"facing={facing},half={half},shape={shape}"] = entry
            w(f"{A}/blockstates/{i}.json", {"variants": variants})
        elif k == "cross":
            w(f"{A}/models/block/{i}.json", {"parent": "minecraft:block/cross", "textures": {"cross": tex}})
            w(f"{A}/blockstates/{i}.json", {"variants": {"": {"model": f"{N}:block/{i}"}}})
        elif k == "pot":
            w(f"{A}/models/block/{i}.json", {"textures": {"pot": tex, "particle": tex}, "elements": [{
                "from": [3, 0, 3], "to": [13, 14, 13],
                "faces": {d_: {"texture": "#pot", "uv": [0, 0, 16, 16]} for d_ in ("north", "east", "south", "west", "up", "down")}}]})
            w(f"{A}/blockstates/{i}.json", {"variants": {"": {"model": f"{N}:block/{i}"}}})
        elif k in ("pad", "warp"):
            w(f"{A}/models/block/{i}.json", {"textures": {"pad": tex, "particle": tex}, "elements": [{
                "from": [0, 0, 0], "to": [16, 2, 16],
                "faces": {d_: {"texture": "#pad", "uv": [0, 0, 16, 16]} for d_ in ("north", "east", "south", "west", "up", "down")}}]})
            w(f"{A}/blockstates/{i}.json", {"variants": {"": {"model": f"{N}:block/{i}"}}})
        elif k == "pedestal":
            base = {"from": [0, 0, 0], "to": [16, 8, 16], "faces": {d_: {"texture": "#stone", "uv": [0, 0, 16, 16]} for d_ in ("north", "east", "south", "west", "up", "down")}}
            col = {"from": [4, 8, 4], "to": [12, 14, 12], "faces": {d_: {"texture": "#stone", "uv": [0, 0, 16, 16]} for d_ in ("north", "east", "south", "west", "up", "down")}}
            blade = {"from": [7.5, 14, 7], "to": [8.5, 30, 9], "faces": {d_: {"texture": "#blade", "uv": [6, 0, 10, 16]} for d_ in ("north", "east", "south", "west")}}
            guard = {"from": [5, 20, 7.5], "to": [11, 21.5, 8.5], "faces": {d_: {"texture": "#blade", "uv": [3, 11, 13, 12]} for d_ in ("north", "east", "south", "west", "up", "down")}}
            tx = {"stone": f"{N}:block/pedestal", "blade": f"{N}:block/master_sword_blade", "particle": f"{N}:block/pedestal"}
            w(f"{A}/models/block/{i}.json", {"textures": tx, "elements": [base, col, blade, guard]})
            w(f"{A}/models/block/{i}_empty.json", {"textures": tx, "elements": [base, col]})
            w(f"{A}/blockstates/{i}.json", {"variants": {"has_sword=true": {"model": f"{N}:block/{i}"}, "has_sword=false": {"model": f"{N}:block/{i}_empty"}}})
        # modelo de ítem del bloque
        if i == "deku_web":
            w(f"{A}/models/item/{i}.json", {"parent": "minecraft:item/generated", "textures": {"layer0": tex}})
        else:
            w(f"{A}/models/item/{i}.json", {"parent": f"{N}:block/{i}"})
    w(f"{A}/particles/navi.json", {"textures": [f"{N}:navi"]})

# ---------------------------------------------------------------- IDIOMA
def lang():
    es, en = {}, {}
    def both(k, e, n): es[k] = e; en[k] = n
    for it in ITEMS: both(f"item.{N}.{it['id']}", it["es"], it["en"])
    for b in BLOCKS: both(f"block.{N}.{b['id']}", b["es"], b["en"])
    for m in MOBS: both(f"entity.{N}.{m[0]}", m[1], m[2])
    both(f"entity.{N}.projectile", "Proyectil", "Projectile"); both(f"entity.{N}.boomerang", "Bumerán", "Boomerang"); both(f"entity.{N}.bomb", "Bomba", "Bomb")
    both(f"itemGroup.{N}.main", "HyruleCraft", "HyruleCraft")
    both("category.hyrulecraft", "HyruleCraft", "HyruleCraft")
    both("key.hyrulecraft.z_target", "Z-Targeting (fijar objetivo)", "Z-Targeting (lock on)")
    both("key.hyrulecraft.next_target", "Cambiar de objetivo", "Switch target")
    both("key.hyrulecraft.dodge", "Voltereta / salto esquivo", "Roll / dodge")
    both("key.hyrulecraft.menu", "Menú de HyruleCraft", "HyruleCraft menu")
    for s in SONGS:
        both(f"song.{N}.{s[0]}", s[1], s[2])
    for k, e, n, _ in WARPS: both(f"warp.{N}.{k}", e, n)
    both("screen.hyrulecraft.ocarina", "Ocarina", "Ocarina")
    both("screen.hyrulecraft.ocarina.help", "Espacio = A · Flechas/WASD = botones C · Tab = lista de canciones", "Space = A · Arrows/WASD = C buttons · Tab = song list")
    both("screen.hyrulecraft.settings", "HyruleCraft", "HyruleCraft")
    both("screen.hyrulecraft.toggle_mode", "Z-Target alternar", "Z-Target toggle mode")
    both("screen.hyrulecraft.letterbox", "Bandas de cine", "Cinematic bars")
    both("screen.hyrulecraft.skins", "Skins de Ocarina of Time", "Ocarina of Time skins")
    both("skin.hyrulecraft.none", "Mi skin normal", "My normal skin")
    for k, e, n in [("link", "Link niño", "Young Link"), ("adult_link", "Link adulto", "Adult Link"), ("zelda", "Zelda", "Zelda"),
                    ("sheik", "Sheik", "Sheik"), ("ganondorf", "Ganondorf", "Ganondorf"), ("dark_link", "Link Oscuro", "Dark Link")]:
        both(f"skin.hyrulecraft.{k}", e, n)
    T = "tooltip.hyrulecraft."
    both(T + "spin_attack", "Mantén clic derecho y suelta: ataque giratorio", "Hold right-click and release: spin attack")
    both(T + "jump_slash", "Golpea mientras caes: tajo en salto", "Hit while falling: jump slash")
    both(T + "hammer", "Clic derecho: golpe al suelo (rompe vasijas y muros)", "Right-click: ground pound (breaks pots and walls)")
    both(T + "ocarina", "Clic derecho: tocar la ocarina", "Right-click: play the ocarina")
    both(T + "quest", "Objeto de la historia", "Quest item")
    both(T + "rupee", "Vale %s rupia(s)", "Worth %s rupee(s)")
    both(T + "heart", "Aumenta tu vida máxima en %s PV", "Raises max health by %s HP")
    both(T + "fairy", "Clic derecho: cura 10 PV y quita efectos negativos", "Right-click: heal 10 HP and cure bad effects")
    both(T + "lens", "En la mano: revela criaturas cercanas", "Held: reveals nearby creatures")
    both(T + "bomb", "Clic derecho: lanzar (Agáchate para soltar cerca)", "Right-click: throw (sneak to drop near)")
    both(T + "nut", "Clic derecho: lanzar. Aturde a los enemigos cercanos", "Right-click: throw. Stuns nearby enemies")
    both(T + "hookshot", "Clic derecho: engancha bloques o criaturas (24 bloques)", "Right-click: hook blocks or mobs (24 blocks)")
    both(T + "boomerang", "Clic derecho: aturde y recoge objetos", "Right-click: stuns and fetches items")
    both(T + "spell.din", "Anillo de fuego a tu alrededor", "Ring of fire around you")
    both(T + "spell.nayru", "Escudo de resistencia y absorción", "Resistance and absorption shield")
    both(T + "spell.farore", "Marca un punto; usar otra vez te teletransporta de vuelta", "Set a point; use again to warp back")
    M = "message.hyrulecraft."
    both(M + "heart_piece", "Pieza de corazón (%s/4)", "Piece of Heart (%s/4)")
    both(M + "heart_complete", "¡Pieza de corazón completa! +1 corazón", "Heart complete! +1 heart")
    both(M + "heart_container", "¡Contenedor de corazón! +1 corazón", "Heart Container! +1 heart")
    both(M + "hearts_full", "Ya tienes el máximo de corazones", "You already have the maximum hearts")
    both(M + "warp_registered", "Plataforma activada: %s", "Warp pad activated: %s")
    both(M + "warp_unknown", "Aún no has activado esa plataforma de teletransporte", "You haven't activated that warp pad yet")
    both(M + "farore_set", "Punto de Farore marcado", "Farore's point set")
    both(M + "pedestal_empty", "El pedestal está vacío", "The pedestal is empty")
    both(M + "pedestal_need_stones", "Necesitas las tres piedras espirituales", "You need the three Spiritual Stones")
    both(M + "pedestal_given", "¡Has obtenido la Espada Maestra!", "You obtained the Master Sword!")
    both(M + "rewind_none", "No hay nada que rebobinar todavía", "Nothing to rewind yet")
    both("advancement.hyrulecraft.root.title", "HyruleCraft", "HyruleCraft")
    both("advancement.hyrulecraft.root.description", "Recibes la Guía de Hyrule al entrar", "You receive the Hyrule Guide on joining")
    both("subtitles.hyrulecraft.ocarina", "Suena la ocarina", "Ocarina plays")
    for d_ in (es, en): d_.update({})
    w(f"{A}/lang/es_es.json", es); w(f"{A}/lang/en_us.json", en)

# ---------------------------------------------------------------- LOOT
def pool(entries, rolls=1, chance=None):
    p = {"rolls": rolls, "entries": entries}
    if chance is not None: p["conditions"] = [{"condition": "minecraft:random_chance", "chance": chance}]
    return p

def ent(item, weight=1, lo=None, hi=None):
    e = {"type": "minecraft:item", "name": item if ":" in item else f"{N}:{item}", "weight": weight}
    if lo is not None:
        e["functions"] = [{"function": "minecraft:set_count", "count": {"min": lo, "max": hi, "type": "minecraft:uniform"} if hi != lo else lo}]
    return e

def loot():
    BL = D + "/loot_tables/blocks/"
    special = {"cracked_wall", "pedestal", "master_sword_pedestal", "pot", "deku_web", "hyrule_stone_brick_slab"}
    for b in BLOCKS:
        i = b["id"]
        if i in special or i.startswith("warp_pad_"): continue
        w(BL + f"{i}.json", {"type": "minecraft:block", "pools": [{"rolls": 1, "entries": [ent(i)], "conditions": [{"condition": "minecraft:survives_explosion"}]}]})
    w(BL + "hyrule_stone_brick_slab.json", {"type": "minecraft:block", "pools": [{"rolls": 1, "entries": [{
        "type": "minecraft:item", "name": f"{N}:hyrule_stone_brick_slab", "functions": [{"function": "minecraft:set_count", "count": 2, "conditions": [{
            "condition": "minecraft:block_state_property", "block": f"{N}:hyrule_stone_brick_slab", "properties": {"type": "double"}}]}]}]}]})
    w(BL + "pot.json", {"type": "minecraft:block", "pools": [
        pool([ent("rupee_green", 10, 1, 3), ent("rupee_blue", 2), ent("recovery_heart", 5), ent("bomb", 1, 1, 2), ent("deku_nut", 2, 1, 3)], 1, 0.7)]})
    w(BL + "deku_web.json", {"type": "minecraft:block", "pools": [pool([ent("minecraft:string")], 1, 0.5)]})
    w(BL + "cracked_wall.json", {"type": "minecraft:block", "pools": []})

    EL = D + "/loot_tables/entities/"
    w(EL + "stalfos.json", {"type": "minecraft:entity", "pools": [
        pool([ent("minecraft:bone", 1, 0, 2)]), pool([ent("rupee_blue", 3), ent("rupee_green", 6, 1, 3), ent("recovery_heart", 4)], 1, 0.6),
        pool([ent("deku_stick"), ent("bomb", 1, 1, 2)], 1, 0.2)]})
    w(EL + "redead.json", {"type": "minecraft:entity", "pools": [
        pool([ent("minecraft:rotten_flesh", 1, 0, 2)]), pool([ent("rupee_green", 6, 1, 2), ent("rupee_red", 1), ent("recovery_heart", 3)], 1, 0.55)]})
    w(EL + "skulltula.json", {"type": "minecraft:entity", "pools": [
        pool([ent("minecraft:string", 1, 0, 2), ent("minecraft:spider_eye", 1, 0, 1)]), pool([ent("rupee_green", 4, 1, 2), ent("recovery_heart", 2)], 1, 0.4)]})
    w(EL + "ganondorf.json", {"type": "minecraft:entity", "pools": [
        pool([ent("heart_container", 1, 2, 2)]), pool([ent("light_medallion")]), pool([ent("rupee_gold", 1, 3, 5)]), pool([ent("biggoron_sword")], 1, 0.5)]})

    CH = D + "/loot_tables/chests/"
    w(CH + "shrine_common.json", {"type": "minecraft:chest", "pools": [
        pool([ent("rupee_green", 10, 3, 8), ent("rupee_blue", 6, 1, 3), ent("rupee_red", 2), ent("bomb", 6, 2, 5), ent("deku_nut", 5, 3, 8),
              ent("deku_stick", 5, 2, 4), ent("recovery_heart", 6, 2, 4), ent("fairy_bottle", 2)], 5),
        pool([ent("heart_piece"), ent("hookshot"), ent("boomerang"), ent("deku_shield"), ent("lens_of_truth")], 1, 0.6)]})
    w(CH + "shrine_rare.json", {"type": "minecraft:chest", "pools": [
        pool([ent("hylian_shield"), ent("hover_boots"), ent("iron_boots"), ent("megaton_hammer"), ent("goron_tunic"), ent("zora_tunic"),
              ent("dins_fire"), ent("nayrus_love"), ent("farores_wind"), ent("mirror_shield", 1)], 2),
        pool([ent("rupee_purple", 3), ent("rupee_red", 6, 2, 4), ent("heart_piece", 4, 1, 2), ent("fairy_bottle", 3)], 3)]})
    for stone, name in (("kokiri_emerald", "stone_forest"), ("goron_ruby", "stone_fire"), ("zora_sapphire", "stone_water")):
        w(CH + f"{name}.json", {"type": "minecraft:chest", "pools": [pool([ent(stone)]), pool([ent("heart_container")]), pool([ent("rupee_red", 1, 2, 4)])]})
    w(CH + "ocarina.json", {"type": "minecraft:chest", "pools": [pool([ent("ocarina")]), pool([ent("kokiri_sword")]), pool([ent("deku_shield")]), pool([ent("rupee_green", 1, 5, 10)])]})
    w(CH + "fairy.json", {"type": "minecraft:chest", "pools": [pool([ent("fairy_bottle", 1, 2, 3), ent("nayrus_love")], 1), pool([ent("heart_piece", 1, 1, 2)])]})

    # guía en libro
    from guide import PAGES
    def snbt(s): return "'" + s.replace("\\", "\\\\").replace("'", "\\'") + "'"
    pages = ",".join(snbt(json.dumps({"text": p}, ensure_ascii=False)) for p in PAGES)
    tag = '{title:"Guía de Hyrule",author:"HyruleCraft",pages:[' + pages + ']}'
    w(D + "/loot_tables/gameplay/guide_book.json", {"type": "minecraft:generic", "pools": [{"rolls": 1, "entries": [{
        "type": "minecraft:item", "name": "minecraft:written_book", "functions": [{"function": "minecraft:set_nbt", "tag": tag}]}]}]})

# ---------------------------------------------------------------- RECETAS
def shaped(name, pattern, key, result, count=1):
    w(D + f"/recipes/{name}.json", {"type": "minecraft:crafting_shaped", "category": "equipment", "pattern": pattern,
        "key": {k: ({"tag": v[1:]} if v.startswith("#") else {"item": v}) for k, v in key.items()},
        "result": {"item": result if ":" in result else f"{N}:{result}", "count": count}})

def shapeless(name, ingredients, result, count=1):
    ing = []
    for x in ingredients:
        n = 1
        if isinstance(x, tuple): x, n = x
        ing += [{"item": x if ":" in x else f"{N}:{x}"}] * n
    w(D + f"/recipes/{name}.json", {"type": "minecraft:crafting_shapeless", "category": "misc", "ingredients": ing,
        "result": {"item": result if ":" in result else f"{N}:{result}", "count": count}})

def recipes():
    mc = lambda s: "minecraft:" + s
    shaped("kokiri_sword", ["C", "C", "S"], {"C": mc("copper_ingot"), "S": mc("stick")}, "kokiri_sword")
    shapeless("deku_stick", [mc("stick"), mc("oak_leaves")], "deku_stick")
    shaped("deku_shield", ["PPP", "PPP", " P "], {"P": "#minecraft:planks"}, "deku_shield")
    shaped("hylian_shield", ["III", "IDI", " I "], {"I": mc("iron_ingot"), "D": f"{N}:deku_shield"}, "hylian_shield")
    shaped("mirror_shield", [" D ", "DHD", " D "], {"D": mc("diamond"), "H": f"{N}:hylian_shield"}, "mirror_shield")
    shaped("kokiri_cap", ["WWW", "W W"], {"W": mc("green_wool")}, "kokiri_cap")
    shaped("kokiri_tunic", ["W W", "WWW", "WWW"], {"W": mc("green_wool")}, "kokiri_tunic")
    shaped("kokiri_trousers", ["WWW", "W W", "W W"], {"W": mc("green_wool")}, "kokiri_trousers")
    shaped("kokiri_boots", ["W W", "W W"], {"W": mc("green_wool")}, "kokiri_boots")
    shaped("goron_tunic", ["RBR", "RRR", "RRR"], {"R": mc("red_wool"), "B": mc("blaze_powder")}, "goron_tunic")
    shaped("zora_tunic", ["WPW", "WWW", "WWW"], {"W": mc("blue_wool"), "P": mc("prismarine_shard")}, "zora_tunic")
    shaped("iron_boots", ["I I", "I I"], {"I": mc("iron_block")}, "iron_boots")
    shaped("hover_boots", ["M M", "F F"], {"M": mc("phantom_membrane"), "F": mc("feather")}, "hover_boots")
    shaped("ocarina", [" C ", "CNC", " C "], {"C": mc("clay_ball"), "N": mc("note_block")}, "ocarina")
    shaped("hookshot", ["CII", " S ", "S  "], {"C": mc("chain"), "I": mc("iron_ingot"), "S": mc("stick")}, "hookshot")
    shaped("boomerang", ["P P", " I "], {"P": "#minecraft:planks", "I": mc("iron_ingot")}, "boomerang")
    shaped("bomb", [" S ", "GIG", " G "], {"S": mc("string"), "G": mc("gunpowder"), "I": mc("iron_ingot")}, "bomb", 2)
    shapeless("deku_nut", [mc("oak_leaves"), mc("brown_dye")], "deku_nut", 2)
    shapeless("fairy_bottle", [mc("glass_bottle"), mc("glowstone_dust"), mc("sugar")], "fairy_bottle")
    shaped("lens_of_truth", [" G ", "GEG", " G "], {"G": mc("glass"), "E": mc("ender_eye")}, "lens_of_truth")
    shaped("megaton_hammer", ["BBB", " S ", " S "], {"B": mc("iron_block"), "S": mc("stick")}, "megaton_hammer")
    shaped("dins_fire", [" P ", "PDP", " P "], {"P": mc("blaze_powder"), "D": mc("diamond")}, "dins_fire")
    shaped("nayrus_love", ["LLL", "LDL", "LLL"], {"L": mc("lapis_lazuli"), "D": mc("diamond")}, "nayrus_love")
    shaped("farores_wind", [" E ", "EDE", " E "], {"E": mc("emerald"), "D": mc("diamond")}, "farores_wind")
    shapeless("rupee_blue_from_green", [("rupee_green", 5)], "rupee_blue")
    shapeless("rupee_red_from_blue", [("rupee_blue", 4)], "rupee_red")
    shapeless("rupee_purple_from_red", [("rupee_red", 2), ("rupee_blue", 2)], "rupee_purple")
    shapeless("rupee_gold_from_purple", [("rupee_purple", 4)], "rupee_gold")
    shapeless("rupee_green_from_blue", ["rupee_blue"], "rupee_green", 5)
    shapeless("rupee_blue_from_red", ["rupee_red"], "rupee_blue", 4)
    shapeless("heart_piece_from_tokens", [("gold_skulltula_token", 9)], "heart_piece")

# ---------------------------------------------------------------- TAGS / LOGROS / SONIDOS
def tags_adv_sounds():
    w(D + "/tags/blocks/hammer_breakable.json", {"values": [f"{N}:cracked_wall", f"{N}:pot", f"{N}:deku_web"]})
    w(D + "/tags/blocks/bombable.json", {"values": [f"{N}:cracked_wall", f"{N}:pot", f"{N}:deku_web"]})
    stone = [f"{N}:{b['id']}" for b in BLOCKS if b["id"] not in ("pot", "deku_web", "fairy_fountain_pad", "master_sword_pedestal") and not b["id"].startswith("warp_pad")]
    w(RES + "/data/minecraft/tags/blocks/mineable/pickaxe.json", {"values": stone})
    w(D + "/advancements/root.json", {
        "display": {"icon": {"item": f"{N}:ocarina"}, "title": {"translate": "advancement.hyrulecraft.root.title"},
                    "description": {"translate": "advancement.hyrulecraft.root.description"},
                    "background": "minecraft:textures/gui/advancements/backgrounds/stone.png", "show_toast": False, "announce_to_chat": False},
        "criteria": {"tick": {"trigger": "minecraft:tick"}}, "rewards": {"loot": [f"{N}:gameplay/guide_book"]}})
    ev = ["song_ok", "z_target", "navi_ping", "roll", "rupee", "secret", "hookshot", "spin_charge"] + [f"ocarina_{n}" for n in ("a", "down", "right", "left", "up")]
    w(A + "/sounds.json", {e: {"sounds": [{"name": f"{N}:{e}"}], "subtitle": "subtitles.hyrulecraft.ocarina"} for e in ev})

def run():
    models(); lang(); loot(); recipes(); tags_adv_sounds()
    print("  Assets JSON: modelos, blockstates, lang, loot, recetas, tags, logro, sounds.json")

if __name__ == "__main__":
    run()
