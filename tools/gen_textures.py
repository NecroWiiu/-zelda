# -*- coding: utf-8 -*-
"""Texturas procedurales (pixel art) de ítems, bloques, entidades, skins, armaduras y GUI."""
import os, random, hashlib
from PIL import Image, ImageDraw
from content import ITEMS, BLOCKS, WARPS

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES = os.path.join(ROOT, "src/main/resources/assets/hyrulecraft")
MC = os.path.join(ROOT, "src/main/resources/assets/minecraft")

def hexc(h, a=255):
    return ((h >> 16) & 255, (h >> 8) & 255, h & 255, a)

def shade(c, f):
    return (max(0, min(255, int(c[0] * f))), max(0, min(255, int(c[1] * f))), max(0, min(255, int(c[2] * f))), c[3] if len(c) > 3 else 255)

def save(img, path):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path)

def new(w=16, h=16):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    return img, ImageDraw.Draw(img)

def outline(img, col=(20, 16, 24, 255)):
    w, h = img.size
    px = img.load()
    add = []
    for y in range(h):
        for x in range(w):
            if px[x, y][3] == 0:
                for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    nx, ny = x + dx, y + dy
                    if 0 <= nx < w and 0 <= ny < h and px[nx, ny][3] > 0 and px[nx, ny] != col:
                        add.append((x, y)); break
    for x, y in add:
        px[x, y] = col
    return img

def noise_fill(img, box, base, amp=0.08, seed=1):
    r = random.Random(seed)
    px = img.load()
    x0, y0, x1, y1 = box
    for y in range(y0, y1):
        for x in range(x0, x1):
            px[x, y] = shade(base, 1 + r.uniform(-amp, amp))

# ---------------------------------------------------------------- ÍTEMS
BLADE = hexc(0xdfe9f2); BLADE_D = hexc(0x8fa3b8)
GOLD = hexc(0xe6b422); GOLD_D = hexc(0x9c7410)
BROWN = hexc(0x6b4423); BROWN_D = hexc(0x45280f)

def spr_sword(blade, blade_d, guard, grip, big=False):
    img, d = new()
    w = 2 if big else 1
    for i in range(3, 13):
        x, y = i + 0, 15 - i - 0
        d.rectangle([x, y - w + 1, x + w - 1, y], fill=blade)
        d.point((x + w, y - w + 1), fill=blade_d)
    d.line([(4, 9), (7, 12)], fill=guard, width=1)
    d.line([(3, 10), (6, 13)], fill=guard, width=1)
    d.line([(2, 13), (3, 12)], fill=grip, width=1)
    d.point((1, 14), fill=guard)
    return outline(img)

def spr_hammer():
    img, d = new()
    for i in range(2, 12):
        d.point((i, 15 - i), fill=BROWN)
        d.point((i + 1, 15 - i), fill=BROWN_D)
    d.rectangle([8, 1, 14, 6], fill=hexc(0x8c9099)); d.rectangle([8, 1, 14, 2], fill=hexc(0xc4c8d0))
    d.rectangle([8, 5, 14, 6], fill=hexc(0x5c606a))
    return outline(img)

def spr_stick():
    img, d = new()
    for i in range(2, 14):
        d.point((i, 15 - i), fill=BROWN); d.point((i + 1, 15 - i), fill=BROWN_D)
    d.point((13, 2), fill=hexc(0xe86a1a)); d.point((14, 1), fill=hexc(0xf2c230))
    return outline(img)

def spr_shield(kind):
    img, d = new()
    if kind == "deku":
        body, edge, emb = hexc(0xa5732d), hexc(0x6b4423), hexc(0x3fa34d)
        d.polygon([(3, 2), (12, 2), (13, 8), (8, 14), (2, 8)], fill=body, outline=edge)
        d.line([(8, 3), (8, 12)], fill=edge); d.rectangle([6, 5, 9, 8], fill=emb)
    elif kind == "hylian":
        body, edge = hexc(0x2f55b0), hexc(0x9aa3b0)
        d.polygon([(3, 1), (12, 1), (13, 8), (8, 14), (2, 8)], fill=body, outline=edge)
        d.polygon([(8, 4), (6, 8), (10, 8)], fill=GOLD); d.point((8, 10), fill=hexc(0xd83030))
    else:
        body, edge = hexc(0xc9d2e0), hexc(0x7a2020)
        d.polygon([(3, 1), (12, 1), (13, 8), (8, 14), (2, 8)], fill=body, outline=edge)
        d.polygon([(8, 3), (6, 8), (10, 8)], fill=hexc(0xe34a4a)); d.line([(5, 3), (5, 8)], fill=hexc(0xffffff))
    return outline(img)

def spr_armor(kind, base, trim):
    img, d = new()
    dark = shade(base, 0.7)
    if kind == "helmet":
        d.rectangle([3, 5, 12, 12], fill=base); d.rectangle([5, 9, 10, 12], fill=(0, 0, 0, 0))
        d.rectangle([3, 5, 12, 6], fill=trim); d.line([(3, 12), (4, 12)], fill=dark); d.line([(11, 12), (12, 12)], fill=dark)
        d.polygon([(3, 5), (12, 5), (14, 3), (12, 2), (4, 2)], fill=base)
    elif kind == "chest":
        d.rectangle([4, 2, 11, 13], fill=base); d.rectangle([1, 2, 4, 8], fill=base); d.rectangle([11, 2, 14, 8], fill=base)
        d.rectangle([5, 2, 10, 3], fill=(0, 0, 0, 0)); d.rectangle([4, 11, 11, 12], fill=hexc(0x6b4423))
        d.rectangle([7, 11, 8, 12], fill=GOLD); d.line([(1, 8), (4, 8)], fill=dark); d.line([(11, 8), (14, 8)], fill=dark)
        d.rectangle([4, 2, 4, 13], fill=dark); d.line([(4, 3), (11, 3)], fill=trim)
    elif kind == "legs":
        d.rectangle([3, 2, 12, 6], fill=base); d.rectangle([3, 7, 6, 13], fill=base); d.rectangle([9, 7, 12, 13], fill=base)
        d.rectangle([3, 2, 12, 3], fill=trim); d.line([(7, 7), (8, 7)], fill=dark)
    else:
        d.rectangle([2, 7, 6, 13], fill=base); d.rectangle([9, 7, 13, 13], fill=base)
        d.rectangle([2, 12, 6, 13], fill=dark); d.rectangle([9, 12, 13, 13], fill=dark)
        d.rectangle([2, 7, 6, 8], fill=trim); d.rectangle([9, 7, 13, 8], fill=trim)
    return outline(img)

def spr_ocarina():
    img, d = new()
    d.ellipse([2, 4, 12, 12], fill=hexc(0x2d6fd1)); d.ellipse([3, 5, 8, 8], fill=hexc(0x5b95ea))
    d.polygon([(10, 3), (14, 2), (13, 6), (11, 6)], fill=hexc(0x2d6fd1))
    for x, y in ((5, 8), (7, 9), (9, 8), (6, 6)):
        d.point((x, y), fill=hexc(0x10244a))
    d.point((8, 11), fill=GOLD); d.point((7, 11), fill=GOLD); d.point((8, 12), fill=GOLD)
    return outline(img)

def spr_hookshot():
    img, d = new()
    for i in range(2, 11):
        d.point((i, 14 - i), fill=hexc(0x8c9099)); d.point((i + 1, 14 - i), fill=hexc(0x5c606a))
    d.polygon([(10, 4), (14, 1), (14, 5), (12, 5)], fill=hexc(0xb0b6c0))
    d.rectangle([1, 12, 4, 14], fill=BROWN)
    return outline(img)

def spr_boomerang():
    img, d = new()
    d.polygon([(2, 3), (5, 3), (9, 9), (13, 9), (13, 12), (8, 12)], fill=hexc(0xb3733a))
    d.line([(3, 3), (9, 10)], fill=hexc(0xe0a05c)); d.point((11, 10), fill=hexc(0x6b4423))
    return outline(img)

def spr_bomb():
    img, d = new()
    d.ellipse([2, 5, 13, 15], fill=hexc(0x22262e)); d.ellipse([4, 7, 6, 9], fill=hexc(0x6a7280))
    d.rectangle([7, 3, 8, 5], fill=hexc(0x9c9c9c)); d.line([(8, 2), (10, 0)], fill=hexc(0xc9a56a))
    d.point((10, 0), fill=hexc(0xff7a1a)); d.point((11, 1), fill=hexc(0xffd23a))
    return outline(img)

def spr_nut():
    img, d = new()
    d.ellipse([3, 4, 12, 13], fill=hexc(0xa5732d)); d.ellipse([4, 5, 7, 8], fill=hexc(0xd29a50))
    d.rectangle([7, 2, 8, 4], fill=hexc(0x3fa34d)); d.point((6, 8), fill=hexc(0x2a1a0a)); d.point((9, 8), fill=hexc(0x2a1a0a))
    return outline(img)

def spr_bottle():
    img, d = new()
    d.rectangle([6, 1, 9, 3], fill=hexc(0x9c6b3a)); d.rectangle([6, 4, 9, 5], fill=hexc(0xc9e6f2))
    d.polygon([(4, 6), (11, 6), (13, 13), (11, 14), (4, 14), (2, 13)], fill=hexc(0xc9e6f2))
    d.ellipse([5, 8, 10, 12], fill=hexc(0xff8fd8)); d.point((7, 9), fill=hexc(0xffffff))
    d.point((4, 8), fill=hexc(0xffffff)); d.point((3, 3), fill=hexc(0xfff0a0))
    return outline(img)

def spr_lens():
    img, d = new()
    d.ellipse([2, 2, 13, 13], fill=hexc(0x9aa3b0)); d.ellipse([4, 4, 11, 11], fill=hexc(0x3a2a52))
    d.ellipse([6, 6, 9, 9], fill=hexc(0xf2d12c)); d.point((7, 7), fill=hexc(0x10101a)); d.point((8, 7), fill=hexc(0x10101a))
    d.rectangle([6, 13, 9, 15], fill=BROWN)
    return outline(img)

def spr_crystal(c):
    img, d = new()
    d.polygon([(8, 1), (13, 7), (8, 14), (3, 7)], fill=c); d.polygon([(8, 1), (3, 7), (8, 8)], fill=shade(c, 1.35))
    d.polygon([(8, 8), (13, 7), (8, 14)], fill=shade(c, 0.7)); d.point((6, 5), fill=(255, 255, 255, 255))
    return outline(img)

def spr_rupee(c):
    img, d = new()
    d.polygon([(8, 0), (12, 4), (12, 11), (8, 15), (4, 11), (4, 4)], fill=c)
    d.polygon([(8, 0), (4, 4), (4, 11), (8, 8)], fill=shade(c, 1.3))
    d.polygon([(8, 8), (12, 11), (8, 15)], fill=shade(c, 0.65)); d.point((6, 4), fill=(255, 255, 255, 255))
    return outline(img)

def spr_heart(piece=False, small=False):
    img, d = new()
    c = hexc(0xe02828)
    if small:
        d.polygon([(8, 12), (3, 7), (3, 5), (5, 4), (8, 6), (11, 4), (13, 5), (13, 7)], fill=c)
        d.point((5, 5), fill=(255, 200, 200, 255))
        return outline(img)
    d.polygon([(8, 14), (1, 7), (1, 4), (4, 2), (8, 5), (12, 2), (15, 4), (15, 7)], fill=c)
    d.point((4, 4), fill=(255, 200, 200, 255)); d.point((3, 5), fill=(255, 200, 200, 255))
    if piece:
        d.rectangle([8, 0, 15, 15], fill=(0, 0, 0, 0)); d.rectangle([0, 9, 15, 15], fill=(0, 0, 0, 0))
        d.line([(8, 5), (8, 9)], fill=GOLD)
    else:
        d.rectangle([0, 0, 15, 15], outline=None)
        d.line([(1, 8), (14, 8)], fill=GOLD_D)
    return outline(img)

def spr_token():
    img, d = new()
    d.ellipse([2, 2, 13, 13], fill=GOLD); d.ellipse([4, 4, 11, 11], fill=hexc(0xf6d860))
    d.point((6, 7), fill=(30, 20, 0, 255)); d.point((9, 7), fill=(30, 20, 0, 255)); d.line([(6, 10), (9, 10)], fill=(30, 20, 0, 255))
    return outline(img)

def spr_energy():
    img, d = new()
    d.ellipse([2, 2, 13, 13], fill=hexc(0x6a2fa8)); d.ellipse([4, 4, 11, 11], fill=hexc(0xb86af0)); d.ellipse([6, 6, 9, 9], fill=(255, 255, 255, 255))
    return outline(img)

def spr_stone(c, shape):
    img, d = new()
    if shape == "round":
        d.ellipse([3, 3, 12, 12], fill=c); d.ellipse([4, 4, 7, 7], fill=shade(c, 1.4)); d.arc([3, 3, 12, 12], 0, 180, fill=shade(c, 0.6))
    elif shape == "oval":
        d.ellipse([4, 2, 11, 13], fill=c); d.ellipse([5, 4, 7, 7], fill=shade(c, 1.4))
    else:
        d.polygon([(8, 1), (13, 6), (11, 13), (5, 13), (3, 6)], fill=c); d.polygon([(8, 1), (3, 6), (8, 7)], fill=shade(c, 1.4))
    d.rectangle([7, 0, 8, 1], fill=GOLD)
    return outline(img)

def spr_medallion(c, glyph):
    img, d = new()
    d.ellipse([1, 1, 14, 14], fill=GOLD_D); d.ellipse([2, 2, 13, 13], fill=GOLD); d.ellipse([4, 4, 11, 11], fill=c)
    g = (255, 255, 255, 255)
    if glyph == "forest": d.polygon([(8, 4), (5, 10), (11, 10)], fill=g)
    elif glyph == "fire": d.polygon([(8, 4), (10, 8), (8, 11), (6, 8)], fill=g)
    elif glyph == "water": d.ellipse([6, 5, 9, 10], fill=g)
    elif glyph == "spirit": d.rectangle([6, 5, 9, 10], fill=g)
    elif glyph == "shadow": d.line([(5, 5), (10, 10)], fill=g); d.line([(10, 5), (5, 10)], fill=g)
    else: d.line([(8, 4), (8, 11)], fill=g); d.line([(5, 7), (11, 7)], fill=g)
    return outline(img)

def item_sprite(i):
    t = {
        "kokiri_sword": lambda: spr_sword(hexc(0xc9d6c0), hexc(0x7d9a74), hexc(0x8a5a2b), hexc(0x4a2a12)),
        "master_sword": lambda: spr_sword(BLADE, BLADE_D, hexc(0x2f55b0), hexc(0x3a2a6b)),
        "biggoron_sword": lambda: spr_sword(hexc(0xe8dfc0), hexc(0xa89a70), GOLD, hexc(0x6b2a1a), True),
        "megaton_hammer": spr_hammer, "deku_stick": spr_stick,
        "deku_shield": lambda: spr_shield("deku"), "hylian_shield": lambda: spr_shield("hylian"), "mirror_shield": lambda: spr_shield("mirror"),
        "kokiri_cap": lambda: spr_armor("helmet", hexc(0x3fa34d), hexc(0x7bd88a)),
        "kokiri_tunic": lambda: spr_armor("chest", hexc(0x3fa34d), hexc(0x7bd88a)),
        "kokiri_trousers": lambda: spr_armor("legs", hexc(0x2e8a3e), hexc(0x7bd88a)),
        "kokiri_boots": lambda: spr_armor("boots", hexc(0x7a4a22), hexc(0xa87038)),
        "goron_tunic": lambda: spr_armor("chest", hexc(0xc83a2a), hexc(0xf08a50)),
        "zora_tunic": lambda: spr_armor("chest", hexc(0x2a6fd0), hexc(0x7ab8f5)),
        "iron_boots": lambda: spr_armor("boots", hexc(0x6c727c), hexc(0xa8aeb8)),
        "hover_boots": lambda: spr_armor("boots", hexc(0x8a5a2b), hexc(0xe6d28a)),
        "ocarina": spr_ocarina, "hookshot": spr_hookshot, "boomerang": spr_boomerang, "bomb": spr_bomb,
        "deku_nut": spr_nut, "fairy_bottle": spr_bottle, "lens_of_truth": spr_lens,
        "dins_fire": lambda: spr_crystal(hexc(0xe0452a)), "nayrus_love": lambda: spr_crystal(hexc(0x2f7ae0)),
        "farores_wind": lambda: spr_crystal(hexc(0x35c46a)),
        "rupee_green": lambda: spr_rupee(hexc(0x2fc24a)), "rupee_blue": lambda: spr_rupee(hexc(0x2f6fe0)),
        "rupee_red": lambda: spr_rupee(hexc(0xe03030)), "rupee_purple": lambda: spr_rupee(hexc(0x9a3fd0)),
        "rupee_gold": lambda: spr_rupee(hexc(0xe6b422)),
        "heart_piece": lambda: spr_heart(piece=True), "heart_container": lambda: spr_heart(),
        "recovery_heart": lambda: spr_heart(small=True),
        "gold_skulltula_token": spr_token, "energy_ball": spr_energy,
        "kokiri_emerald": lambda: spr_stone(hexc(0x2fc24a), "round"), "goron_ruby": lambda: spr_stone(hexc(0xe03030), "oval"),
        "zora_sapphire": lambda: spr_stone(hexc(0x2f6fe0), "round"),
    }
    if i in t: return t[i]()
    for k, c in [("forest", 0x2fa44a), ("fire", 0xe0452a), ("water", 0x2f7ae0), ("spirit", 0xf0a020), ("shadow", 0x8a3fb0), ("light", 0xf6e060)]:
        if i == f"{k}_medallion": return spr_medallion(hexc(c), k)
    return None

# ---------------------------------------------------------------- BLOQUES
def tex_bricks(base, mortar, seed, rows=4, mossy=False):
    img, d = new()
    r = random.Random(seed)
    px = img.load()
    h = 16 // rows
    for row in range(rows):
        off = (row % 2) * 4
        for x in range(16):
            for y in range(row * h, (row + 1) * h):
                bx = (x + off) % 8
                if y == row * h or bx == 0:
                    px[x, y] = mortar
                else:
                    px[x, y] = shade(base, 1 + r.uniform(-0.07, 0.07))
    if mossy:
        for _ in range(26):
            px[r.randrange(16), r.randrange(16)] = hexc(0x4f8a3a)
    return img

def tex_stone(base, seed):
    img, _ = new()
    noise_fill(img, (0, 0, 16, 16), base, 0.1, seed)
    return img

def tex_tiles(base, line, seed, accent=None):
    img, _ = new()
    noise_fill(img, (0, 0, 16, 16), base, 0.05, seed)
    px = img.load()
    for i in range(16):
        for k in (0, 8, 15):
            px[i, k] = line; px[k, i] = line
    if accent:
        for x in range(3, 13):
            for y in range(3, 13):
                if (x + y) % 5 == 0: px[x, y] = accent
    return img

def tex_triforce():
    img = tex_tiles(hexc(0x8a8f98), hexc(0x4a4e56), 5)
    d = ImageDraw.Draw(img)
    d.polygon([(8, 2), (5, 7), (11, 7)], fill=GOLD); d.polygon([(5, 8), (2, 13), (8, 13)], fill=GOLD); d.polygon([(11, 8), (8, 13), (14, 13)], fill=GOLD)
    return img

def tex_cracked():
    img = tex_stone(hexc(0x80858e), 9)
    d = ImageDraw.Draw(img)
    for seg in [[(3, 0), (5, 4), (4, 7), (7, 10), (6, 15)], [(5, 4), (10, 5), (12, 9)], [(7, 10), (11, 12)]]:
        d.line(seg, fill=hexc(0x2a2d33))
    return img

def tex_pot():
    img, d = new()
    c = hexc(0xb5651d)
    d.ellipse([1, 4, 14, 15], fill=c); d.rectangle([4, 1, 11, 5], fill=c); d.rectangle([3, 1, 12, 2], fill=shade(c, 1.2))
    d.rectangle([5, 2, 10, 3], fill=hexc(0x3a2008)); d.line([(2, 9), (13, 9)], fill=shade(c, 0.7)); d.line([(2, 11), (13, 11)], fill=shade(c, 0.7))
    d.ellipse([3, 6, 6, 8], fill=shade(c, 1.35))
    return outline(img, hexc(0x3a2008))

def tex_web():
    img, d = new()
    w = (235, 235, 235, 255)
    d.line([(0, 0), (15, 15)], fill=w); d.line([(15, 0), (0, 15)], fill=w); d.line([(8, 0), (8, 15)], fill=w); d.line([(0, 8), (15, 8)], fill=w)
    for r_ in (3, 6):
        d.arc([8 - r_, 8 - r_, 8 + r_, 8 + r_], 0, 360, fill=w)
    return img

def tex_flame():
    img, _ = new()
    noise_fill(img, (0, 0, 16, 16), hexc(0xf0a020), 0.18, 3)
    d = ImageDraw.Draw(img)
    d.polygon([(8, 1), (12, 8), (10, 14), (6, 14), (4, 8)], fill=hexc(0xffe066))
    d.polygon([(8, 5), (10, 9), (8, 13), (6, 9)], fill=(255, 255, 255, 255))
    return img

def tex_pad(c, rune):
    img, d = new()
    base = hexc(0x6b7078)
    d.rectangle([0, 0, 15, 15], fill=base)
    d.ellipse([1, 1, 14, 14], fill=shade(c, 0.55)); d.ellipse([2, 2, 13, 13], fill=c); d.ellipse([4, 4, 11, 11], fill=shade(c, 0.6))
    d.ellipse([6, 6, 9, 9], fill=shade(c, 1.4))
    if rune: d.line([(8, 3), (8, 12)], fill=(255, 255, 255, 255)); d.line([(4, 8), (12, 8)], fill=(255, 255, 255, 255))
    return img

def tex_pillar_side():
    img = tex_stone(hexc(0xc8ccd2), 21)
    px = img.load()
    for y in range(16):
        px[3, y] = hexc(0x9aa0aa); px[12, y] = hexc(0x9aa0aa)
    for x in range(16):
        px[x, 0] = hexc(0x9aa0aa); px[x, 15] = hexc(0x9aa0aa)
    return img

def tex_pillar_top():
    img = tex_stone(hexc(0xb8bcc4), 22)
    d = ImageDraw.Draw(img)
    d.rectangle([2, 2, 13, 13], outline=hexc(0x8a8f98)); d.rectangle([5, 5, 10, 10], outline=hexc(0x8a8f98))
    return img

def tex_blade():
    img, d = new()
    d.rectangle([6, 0, 9, 15], fill=BLADE); d.rectangle([6, 0, 6, 15], fill=hexc(0xffffff)); d.rectangle([9, 0, 9, 15], fill=BLADE_D)
    d.rectangle([3, 11, 12, 12], fill=hexc(0x2f55b0)); d.rectangle([7, 12, 8, 15], fill=hexc(0x3a2a6b))
    return img

def gen_blocks():
    T = RES + "/textures/block/"
    save(tex_stone(hexc(0x9aa0aa), 1), T + "hyrule_stone.png")
    save(tex_bricks(hexc(0xa9aeb8), hexc(0x6a6f78), 2), T + "hyrule_stone_bricks.png")
    carved = tex_tiles(hexc(0x9aa0aa), hexc(0x5a5f68), 3, hexc(0x7a808a)); save(carved, T + "carved_hyrule_stone.png")
    save(tex_pillar_side(), T + "temple_pillar.png"); save(tex_pillar_top(), T + "temple_pillar_top.png")
    save(tex_triforce(), T + "triforce_tile.png")
    save(tex_bricks(hexc(0x7a8a70), hexc(0x4a5a42), 4, mossy=True), T + "forest_bricks.png")
    save(tex_bricks(hexc(0x7a2f26), hexc(0x3a1410), 5), T + "fire_bricks.png")
    save(tex_tiles(hexc(0x3f8a9a), hexc(0x1c4a58), 6, hexc(0x7ad0e0)), T + "water_tiles.png")
    save(tex_bricks(hexc(0xd8b878), hexc(0x9a7a42), 7), T + "spirit_bricks.png")
    save(tex_bricks(hexc(0x3a3548), hexc(0x14121c), 8), T + "shadow_bricks.png")
    save(tex_cracked(), T + "cracked_wall.png")
    save(tex_pot(), T + "pot.png"); save(tex_web(), T + "deku_web.png"); save(tex_flame(), T + "sacred_flame.png")
    save(tex_pad(hexc(0xff9ae0), False), T + "fairy_fountain_pad.png")
    for k, es, en, c in WARPS:
        save(tex_pad(hexc(c), True), T + f"warp_pad_{k}.png")
    save(tex_stone(hexc(0x8a8f98), 30), T + "pedestal.png"); save(tex_blade(), T + "master_sword_blade.png")

# ---------------------------------------------------------------- ENTIDADES
def face(img, u, v, w, h, base, fn=None, seed=0):
    r = random.Random(seed + u * 31 + v * 17)
    px = img.load()
    for y in range(h):
        for x in range(w):
            c = fn(x, y) if fn else None
            px[u + x, v + y] = c if c else shade(base, 1 + r.uniform(-0.06, 0.06))

def box(img, u, v, w, h, d, base, front=None, top=None, side=None, back=None, seed=0):
    face(img, u + d, v, w, d, top or base, None, seed)
    face(img, u + d + w, v, w, d, shade(base, 0.7), None, seed)
    face(img, u, v + d, d, h, side or shade(base, 0.85), None, seed)
    face(img, u + d, v + d, w, h, base, front, seed)
    face(img, u + d + w, v + d, d, h, side or shade(base, 0.85), None, seed)
    face(img, u + 2 * d + w, v + d, w, h, back or shade(base, 0.9), None, seed)

def player_layout(img, skin, hair, shirt, pants, boots, eye=(30, 30, 40, 255), belt=None, hat=None, trim=None, arm=None, scale=1):
    """Skin 64x64 estilo clásico (brazos de 4)."""
    arm = arm or shirt
    def head_front(x, y):
        if y == 3 and x in (2, 5): return (255, 255, 255, 255)
        if y == 3 and x in (1, 6): return eye
        if y == 4 and x in (2, 5): return eye
        if y == 6 and 3 <= x <= 4: return shade(skin, 0.75)
        if y < 2: return shade(hair, 1.0)
        if y == 2 and x in (0, 7): return shade(hair, 1.0)
        return None
    box(img, 0, 0, 8, 8, 8, skin, front=head_front, top=hair, back=hair, side=shade(hair, 0.9))
    box(img, 16, 16, 8, 12, 4, shirt, front=(lambda x, y: belt if belt and y == 9 else (trim if trim and y == 0 else None)))
    box(img, 40, 16, 4, 12, 4, arm, front=(lambda x, y: skin if y >= 10 else None), side=None)
    box(img, 32, 48, 4, 12, 4, arm, front=(lambda x, y: skin if y >= 10 else None))
    box(img, 0, 16, 4, 12, 4, pants, front=(lambda x, y: boots if y >= 9 else None))
    box(img, 16, 48, 4, 12, 4, pants, front=(lambda x, y: boots if y >= 9 else None))
    if hat:  # capucha/gorro en la capa exterior
        box(img, 32, 0, 8, 8, 8, hat, front=(lambda x, y: (0, 0, 0, 0) if y > 2 else None), top=hat, back=hat)
        px = img.load()
        for yy in range(8, 16):
            for xx in range(32, 64):
                p = px[xx, yy]
        # punta caída del gorro
        for xx in range(55, 60):
            for yy in range(8, 12):
                px[xx, yy] = hat
    return img

SKINS = {
    "link": dict(skin=hexc(0xf0c8a0), hair=hexc(0xe8c040), shirt=hexc(0x3a9a48), pants=hexc(0xe8e0c0), boots=hexc(0x6b4423), belt=hexc(0x6b4423), hat=hexc(0x3a9a48), trim=hexc(0x7bd88a), eye=hexc(0x2a5aa0)),
    "adult_link": dict(skin=hexc(0xf0c8a0), hair=hexc(0xe8c040), shirt=hexc(0x2f8a3e), pants=hexc(0xe8e0c0), boots=hexc(0x5a3a1a), belt=hexc(0x6b4423), hat=hexc(0x2f8a3e), trim=hexc(0xd8d0a0), eye=hexc(0x2a5aa0)),
    "zelda": dict(skin=hexc(0xf4d2b0), hair=hexc(0xd8a050), shirt=hexc(0xf0a0c8), pants=hexc(0xf6e6f0), boots=hexc(0xc87098), belt=GOLD, trim=GOLD, eye=hexc(0x3a7ad0)),
    "sheik": dict(skin=hexc(0xe8c098), hair=hexc(0xe8d078), shirt=hexc(0x3a5a9a), pants=hexc(0x2a3a6a), boots=hexc(0x1a2040), belt=hexc(0xc8c8d8), trim=hexc(0xb8c0d8), eye=hexc(0xd02020)),
    "ganondorf": dict(skin=hexc(0xb8845a), hair=hexc(0xc02a1a), shirt=hexc(0x1e1a24), pants=hexc(0x14121a), boots=hexc(0x2a2020), belt=GOLD, trim=GOLD_D, eye=hexc(0xf0c020)),
    "dark_link": dict(skin=hexc(0x4a4a58), hair=hexc(0xe8e8f0), shirt=hexc(0x14141c), pants=hexc(0x1c1c28), boots=hexc(0x0a0a10), belt=hexc(0x40404c), hat=hexc(0x14141c), trim=hexc(0x40404c), eye=hexc(0xd02020)),
}

def gen_entities():
    E = RES + "/textures/entity/"
    # Ganondorf = skin; Redead = piel gris con harapos
    img, _ = new(64, 64); player_layout(img, **SKINS["ganondorf"]); save(img, E + "ganondorf.png")
    img, _ = new(64, 64)
    player_layout(img, skin=hexc(0x8a8a76), hair=hexc(0x3a3a30), shirt=hexc(0x5a4a3a), pants=hexc(0x3a3a44), boots=hexc(0x2a2a2a), eye=hexc(0xd02020), belt=hexc(0x2a2018))
    r = random.Random(4); px = img.load()
    for _ in range(60):
        px[r.randrange(16, 40), r.randrange(16, 32)] = hexc(0x6a1a1a)
    save(img, E + "redead.png")
    # Stalfos: distribución del esqueleto (64x32)
    img, _ = new(64, 32)
    bone = hexc(0xe0dccb)
    def skull(x, y):
        if y in (3, 4) and x in (1, 2, 5, 6): return hexc(0x101018)
        if y == 6 and 2 <= x <= 5 and x % 2 == 0: return hexc(0x6a6658)
        if y < 1: return hexc(0x5a5a6a)
        return None
    box(img, 0, 0, 8, 8, 8, bone, front=skull)
    box(img, 16, 16, 8, 12, 4, bone, front=(lambda x, y: hexc(0x5a5a6a) if y in (3, 6, 9) else (hexc(0x8a8676) if y in (4, 7, 10) else None)))
    box(img, 40, 16, 2, 12, 2, bone); box(img, 0, 16, 2, 12, 2, bone)
    save(img, E + "stalfos.png")
    # Skulltula (distribución de la araña, 64x32)
    for name, body, mark in (("skulltula", hexc(0x3a2a1a), hexc(0xe8e0c8)), ("skulltula_gold", hexc(0xd8a820), hexc(0xfff2a0))):
        img, _ = new(64, 32)
        def face_(x, y, m=mark):
            if y in (3, 4) and x in (1, 2, 5, 6): return hexc(0xd02020)
            if y == 6 and x in (2, 3, 4, 5): return m
            return None
        box(img, 32, 4, 8, 8, 8, body, front=face_)
        box(img, 0, 0, 6, 6, 6, shade(body, 0.9))
        def mark_(x, y, m=mark):
            return m if (x in (4, 5) and 3 < y < 9) or (y == 5 and 2 < x < 8) else None
        box(img, 0, 12, 10, 8, 12, body, top=None, front=None, back=None)
        px = img.load()
        for x in range(12, 22):  # calavera en el abdomen (cara superior)
            for y in range(12, 24):
                if (x - 12) in (4, 5) and 14 < y < 22 or (y == 17 and 14 <= x <= 19): px[x, y] = mark
        box(img, 18, 0, 16, 2, 2, shade(body, 0.8))
        save(img, E + name + ".png")

def gen_skins():
    S = RES + "/textures/skins/"
    for name, kw in SKINS.items():
        img, _ = new(64, 64); player_layout(img, **kw); save(img, S + name + ".png")
        save(img, os.path.join(ROOT, "skins", name + ".png"))

def armor_layers():
    def make(name, head, body, legs, boots, trim):
        l1, _ = new(64, 32); l2, _ = new(64, 32)
        box(l1, 0, 0, 8, 8, 8, head); box(l1, 32, 0, 8, 8, 8, shade(head, 1.1))
        box(l1, 16, 16, 8, 12, 4, body, front=(lambda x, y: trim if y == 0 or y == 10 else None))
        box(l1, 40, 16, 4, 12, 4, body); box(l1, 0, 16, 4, 12, 4, boots)
        box(l2, 16, 16, 8, 12, 4, legs, front=(lambda x, y: trim if y == 0 else None)); box(l2, 0, 16, 4, 12, 4, legs)
        save(l1, MC + f"/textures/models/armor/hyrulecraft_{name}_layer_1.png")
        save(l2, MC + f"/textures/models/armor/hyrulecraft_{name}_layer_2.png")
    make("kokiri", hexc(0x3fa34d), hexc(0x3fa34d), hexc(0x2e8a3e), hexc(0x7a4a22), hexc(0x7bd88a))
    make("goron", hexc(0xc83a2a), hexc(0xc83a2a), hexc(0xa02a20), hexc(0x6a2018), hexc(0xf08a50))
    make("zora", hexc(0x2a6fd0), hexc(0x2a6fd0), hexc(0x1f56a8), hexc(0x16407a), hexc(0x7ab8f5))
    make("iron_boots", hexc(0x6c727c), hexc(0x6c727c), hexc(0x6c727c), hexc(0x5c626c), hexc(0xa8aeb8))
    make("hover_boots", hexc(0x8a5a2b), hexc(0x8a5a2b), hexc(0x8a5a2b), hexc(0x7a4a22), hexc(0xe6d28a))

# ---------------------------------------------------------------- GUI / PARTÍCULAS / ICONO
def gen_gui():
    G = RES + "/textures/gui/"
    colors = {"a": (0x3aa0f0, "A"), "up": (0xf2c230, "u"), "down": (0xf2c230, "d"), "left": (0xf2c230, "l"), "right": (0xf2c230, "r")}
    for n, (c, g) in colors.items():
        img, d = new()
        d.ellipse([0, 0, 15, 15], fill=shade(hexc(c), 0.55)); d.ellipse([1, 1, 14, 14], fill=hexc(c))
        w = (30, 30, 40, 255)
        if g == "A": d.polygon([(8, 3), (4, 12), (6, 12), (8, 7), (10, 12), (12, 12)], fill=w)
        elif g == "u": d.polygon([(8, 3), (3, 10), (13, 10)], fill=w)
        elif g == "d": d.polygon([(8, 12), (3, 5), (13, 5)], fill=w)
        elif g == "l": d.polygon([(3, 8), (11, 3), (11, 13)], fill=w)
        else: d.polygon([(13, 8), (5, 3), (5, 13)], fill=w)
        d.arc([1, 1, 14, 14], 200, 300, fill=(255, 255, 255, 200))
        save(img, G + f"note_{n}.png")
    img, d = new()
    d.polygon([(8, 15), (1, 3), (15, 3)], fill=(30, 24, 0, 255)); d.polygon([(8, 13), (3, 4), (13, 4)], fill=(255, 220, 40, 255))
    d.polygon([(8, 10), (5, 5), (11, 5)], fill=(255, 245, 160, 255))
    save(img, G + "z_marker.png")
    img, d = new(8, 8)
    d.ellipse([1, 1, 6, 6], fill=(150, 220, 255, 150)); d.ellipse([2, 2, 5, 5], fill=(210, 245, 255, 220)); d.rectangle([3, 3, 4, 4], fill=(255, 255, 255, 255))
    save(img, RES + "/textures/particle/navi.png")
    img, d = new(128, 128)
    d.rectangle([0, 0, 127, 127], fill=(20, 40, 90, 255))
    d.polygon([(64, 14), (36, 60), (92, 60)], fill=GOLD); d.polygon([(36, 66), (8, 112), (64, 112)], fill=GOLD); d.polygon([(92, 66), (64, 112), (120, 112)], fill=GOLD)
    save(img, RES + "/icon.png")

def run():
    for it in ITEMS:
        spr = item_sprite(it["id"])
        if spr is not None:
            save(spr, RES + f"/textures/item/{it['id']}.png")
    gen_blocks(); gen_entities(); gen_skins(); armor_layers(); gen_gui()
    print("  Texturas generadas")

if __name__ == "__main__":
    run()
