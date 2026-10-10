# -*- coding: utf-8 -*-
"""
Fuente única de verdad del contenido de HyruleCraft.
Desde aquí se generan: ModItems.java, ModBlocks.java, modelos, lang, loot, recetas...
Ejecuta:  python tools/generate_all.py
"""
S = "new FabricItemSettings()"
S1 = "new FabricItemSettings().maxCount(1)"

ITEMS = []
def item(i, es, en, expr, model="generated", tab=True):
    ITEMS.append(dict(id=i, es=es, en=en, expr=expr, model=model, tab=tab))

# ---------------- Armas ----------------
item("kokiri_sword", "Espada Kokiri", "Kokiri Sword",
     f"new HyruleSwordItem(HyruleMaterials.KOKIRI, 3, -2.4f, {S1}, 6f, 2.6f, 2f)", "handheld")
item("master_sword", "Espada Maestra", "Master Sword",
     f"new HyruleSwordItem(HyruleMaterials.MASTER, 4, -2.2f, {S1}.rarity(Rarity.EPIC), 10f, 3.2f, 4f)", "handheld")
item("biggoron_sword", "Espada de Biggoron", "Biggoron's Sword",
     f"new HyruleSwordItem(HyruleMaterials.BIGGORON, 5, -3.0f, {S1}.rarity(Rarity.RARE), 14f, 3.8f, 5f)", "handheld")
item("megaton_hammer", "Martillo Megatón", "Megaton Hammer",
     f"new MegatonHammerItem(HyruleMaterials.HAMMER, 5, -3.1f, {S1}.rarity(Rarity.RARE))", "handheld")
item("deku_stick", "Palo Deku", "Deku Stick", f"new DekuStickItem({S})", "handheld")

# ---------------- Escudos ----------------
item("deku_shield", "Escudo Deku", "Deku Shield",
     "new HyruleShieldItem(new FabricItemSettings().maxDamage(168), () -> Ingredient.fromTag(ItemTags.PLANKS), false)")
item("hylian_shield", "Escudo Hyliano", "Hylian Shield",
     "new HyruleShieldItem(new FabricItemSettings().maxDamage(672).fireproof(), () -> Ingredient.ofItems(Items.IRON_INGOT), false)")
item("mirror_shield", "Escudo Espejo", "Mirror Shield",
     "new HyruleShieldItem(new FabricItemSettings().maxDamage(1008).fireproof().rarity(Rarity.RARE), () -> Ingredient.ofItems(Items.DIAMOND), true)")

# ---------------- Armaduras ----------------
item("kokiri_cap", "Gorro Kokiri", "Kokiri Cap",
     f"new ArmorItem(HyruleMaterials.KOKIRI_SET, ArmorItem.Type.HELMET, {S})")
item("kokiri_tunic", "Túnica Kokiri", "Kokiri Tunic", f"new TunicItem(HyruleMaterials.KOKIRI_SET, {S})")
item("kokiri_trousers", "Pantalón Kokiri", "Kokiri Trousers",
     f"new ArmorItem(HyruleMaterials.KOKIRI_SET, ArmorItem.Type.LEGGINGS, {S})")
item("kokiri_boots", "Botas Kokiri", "Kokiri Boots",
     f"new ArmorItem(HyruleMaterials.KOKIRI_SET, ArmorItem.Type.BOOTS, {S})")
item("goron_tunic", "Túnica Goron", "Goron Tunic",
     "new TunicItem(HyruleMaterials.GORON_SET, new FabricItemSettings().fireproof(), StatusEffects.FIRE_RESISTANCE)")
item("zora_tunic", "Túnica Zora", "Zora Tunic",
     f"new TunicItem(HyruleMaterials.ZORA_SET, {S}, StatusEffects.WATER_BREATHING, StatusEffects.DOLPHINS_GRACE)")
item("iron_boots", "Botas de Hierro", "Iron Boots",
     f"new HyruleBootsItem(HyruleMaterials.IRON_BOOTS, {S}, HyruleBootsItem.Kind.IRON)")
item("hover_boots", "Botas de Aire", "Hover Boots",
     f"new HyruleBootsItem(HyruleMaterials.HOVER_BOOTS, {S}.rarity(Rarity.RARE), HyruleBootsItem.Kind.HOVER)")

# ---------------- Equipo ----------------
item("ocarina", "Ocarina del Tiempo", "Ocarina of Time", f"new OcarinaItem({S1}.rarity(Rarity.EPIC))")
item("hookshot", "Gancho", "Hookshot", f"new HookshotItem({S1})")
item("boomerang", "Bumerán", "Boomerang", f"new BoomerangItem({S1})")
item("bomb", "Bomba", "Bomb", f"new BombItem(new FabricItemSettings().maxCount(16))")
item("deku_nut", "Nuez Deku", "Deku Nut", f"new DekuNutItem(new FabricItemSettings().maxCount(32))")
item("fairy_bottle", "Hada en Botella", "Bottled Fairy", f"new FairyBottleItem({S1})")
item("lens_of_truth", "Lente de la Verdad", "Lens of Truth", f"new LensItem({S1}.rarity(Rarity.UNCOMMON))")
item("dins_fire", "Fuego de Din", "Din's Fire", f"new SpellItem({S1}.rarity(Rarity.UNCOMMON), SpellItem.Spell.DIN, 300)")
item("nayrus_love", "Amor de Nayru", "Nayru's Love", f"new SpellItem({S1}.rarity(Rarity.UNCOMMON), SpellItem.Spell.NAYRU, 900)")
item("farores_wind", "Viento de Farore", "Farore's Wind", f"new SpellItem({S1}.rarity(Rarity.UNCOMMON), SpellItem.Spell.FARORE, 200)")

# ---------------- Coleccionables ----------------
for name, es, val in [("green", "Verde", 1), ("blue", "Azul", 5), ("red", "Roja", 20), ("purple", "Morada", 50), ("gold", "Dorada", 200)]:
    en = {"green": "Green", "blue": "Blue", "red": "Red", "purple": "Purple", "gold": "Gold"}[name]
    item(f"rupee_{name}", f"Rupia {es}" if name != "green" and name != "blue" and name != "gold" else f"Rupia {es}",
         f"{en} Rupee", f"new RupeeItem({S}, {val})")
item("heart_piece", "Pieza de Corazón", "Piece of Heart", f"new HeartItem({S}.rarity(Rarity.UNCOMMON), 0.5)")
item("heart_container", "Contenedor de Corazón", "Heart Container", f"new HeartItem({S}.rarity(Rarity.RARE), 2.0)")
item("recovery_heart", "Corazón de Recuperación", "Recovery Heart", f"new RecoveryHeartItem({S})")
item("energy_ball", "Esfera de Energía", "Energy Ball", f"new Item({S})", tab=False)
item("gold_skulltula_token", "Ficha de Skulltula Dorada", "Gold Skulltula Token", f"new Item({S})")

# ---------------- Objetos de la historia ----------------
item("kokiri_emerald", "Esmeralda Kokiri", "Kokiri's Emerald", f"new QuestItem({S1})")
item("goron_ruby", "Rubí Goron", "Goron's Ruby", f"new QuestItem({S1})")
item("zora_sapphire", "Zafiro Zora", "Zora's Sapphire", f"new QuestItem({S1})")
for k, es, en in [("forest", "del Bosque", "Forest"), ("fire", "del Fuego", "Fire"), ("water", "del Agua", "Water"),
                  ("spirit", "del Espíritu", "Spirit"), ("shadow", "de las Sombras", "Shadow"), ("light", "de la Luz", "Light")]:
    item(f"{k}_medallion", f"Medallón {es}", f"{en} Medallion", f"new QuestItem({S1})")

# ---------------- Mobs ----------------
# id, es, en, color1, color2
MOBS = [
    ("stalfos", "Stalfos", "Stalfos", 0xd8d4c4, 0x4a4a5a),
    ("redead", "Redead", "ReDead", 0x7b6f63, 0x8a1c1c),
    ("skulltula", "Skulltula", "Skulltula", 0x3b2a1a, 0xe0b020),
    ("ganondorf", "Ganondorf", "Ganondorf", 0x2a1a1a, 0xc02020),
]
MOB_ES = {m[0]: m[1] for m in MOBS}

# ---------------- Bloques ----------------
BLOCKS = []
def block(i, es, en, expr, kind="cube", item=True):
    BLOCKS.append(dict(id=i, es=es, en=en, expr=expr, kind=kind, item=item))

BS = "FabricBlockSettings"
block("hyrule_stone", "Piedra de Hyrule", "Hyrule Stone", f"new Block({BS}.copyOf(Blocks.STONE))")
block("hyrule_stone_bricks", "Ladrillos de Piedra de Hyrule", "Hyrule Stone Bricks", f"new Block({BS}.copyOf(Blocks.STONE_BRICKS))")
block("hyrule_stone_brick_stairs", "Escaleras de Ladrillo de Hyrule", "Hyrule Brick Stairs",
      f"new StairsBlock(HYRULE_STONE_BRICKS.getDefaultState(), {BS}.copyOf(Blocks.STONE_BRICKS))", "stairs")
block("hyrule_stone_brick_slab", "Losa de Ladrillo de Hyrule", "Hyrule Brick Slab", f"new SlabBlock({BS}.copyOf(Blocks.STONE_BRICKS))", "slab")
block("carved_hyrule_stone", "Piedra de Hyrule Tallada", "Carved Hyrule Stone", f"new Block({BS}.copyOf(Blocks.STONE_BRICKS))")
block("temple_pillar", "Pilar de Templo", "Temple Pillar", f"new PillarBlock({BS}.copyOf(Blocks.STONE_BRICKS))", "pillar")
block("triforce_tile", "Baldosa de la Trifuerza", "Triforce Tile", f"new Block({BS}.copyOf(Blocks.STONE_BRICKS).luminance(7))")
block("forest_bricks", "Ladrillos del Bosque", "Forest Bricks", f"new Block({BS}.copyOf(Blocks.MOSSY_STONE_BRICKS))")
block("fire_bricks", "Ladrillos del Fuego", "Fire Bricks", f"new Block({BS}.copyOf(Blocks.NETHER_BRICKS))")
block("water_tiles", "Azulejos del Agua", "Water Tiles", f"new Block({BS}.copyOf(Blocks.PRISMARINE_BRICKS))")
block("spirit_bricks", "Ladrillos del Espíritu", "Spirit Bricks", f"new Block({BS}.copyOf(Blocks.SANDSTONE))")
block("shadow_bricks", "Ladrillos de las Sombras", "Shadow Bricks", f"new Block({BS}.copyOf(Blocks.DEEPSLATE_BRICKS))")
block("cracked_wall", "Muro Agrietado", "Bombable Wall", f"new CrackedWallBlock({BS}.copyOf(Blocks.STONE).strength(50.0f, 0.0f))")
block("pot", "Vasija", "Pot", f"new PotBlock({BS}.create().strength(0.0f).nonOpaque().sounds(BlockSoundGroup.DECORATED_POT))", "pot")
block("deku_web", "Telaraña Deku", "Deku Web", f"new CobwebBlock({BS}.copyOf(Blocks.COBWEB))", "cross")
block("sacred_flame", "Llama Sagrada", "Sacred Flame", f"new Block({BS}.copyOf(Blocks.GLOWSTONE).luminance(15))")
block("fairy_fountain_pad", "Plataforma de Hada", "Fairy Fountain Pad", f"new FairyFountainBlock({BS}.create().strength(1.5f).luminance(10).nonOpaque())", "pad")
block("master_sword_pedestal", "Pedestal de la Espada Maestra", "Master Sword Pedestal",
      f"new MasterSwordPedestalBlock({BS}.copyOf(Blocks.STONE_BRICKS).strength(-1.0f, 3600000.0f).luminance(s -> s.get(MasterSwordPedestalBlock.HAS_SWORD) ? 8 : 0).nonOpaque())", "pedestal")
WARPS = [("forest", "Bosque", "Forest", 0x2f9e44), ("fire", "Fuego", "Fire", 0xe03131), ("water", "Agua", "Water", 0x1c7ed6),
         ("spirit", "Espíritu", "Spirit", 0xf08c00), ("shadow", "Sombras", "Shadow", 0x862e9c), ("light", "Luz", "Light", 0xfcc419)]
for k, es, en, _ in WARPS:
    block(f"warp_pad_{k}", f"Plataforma de Teletransporte ({es})", f"Warp Pad ({en})",
          f"new WarpPadBlock({BS}.create().strength(-1.0f, 3600000.0f).luminance(9).nonOpaque(), WarpPadBlock.Warp.{k.upper()})", "warp")

# ---------------- Canciones ----------------
SONGS = [
    ("zeldas_lullaby", "Nana de Zelda", "Zelda's Lullaby", "Calma a los monstruos cercanos y cura a los aliados.", "Calms nearby monsters and heals allies."),
    ("eponas_song", "Canción de Epona", "Epona's Song", "Invoca a Epona, tu fiel yegua.", "Summons Epona, your loyal mare."),
    ("sarias_song", "Canción de Saria", "Saria's Song", "Revela a los seres cercanos (resplandor).", "Reveals nearby creatures (glowing)."),
    ("suns_song", "Canción del Sol", "Sun's Song", "Cambia entre día y noche y paraliza a los no-muertos.", "Switches day/night and freezes the undead."),
    ("song_of_time", "Canción del Tiempo", "Song of Time", "Abre el Pedestal de la Espada Maestra o retrocede 10 s en el tiempo.", "Opens the Master Sword Pedestal or rewinds you 10 s."),
    ("song_of_storms", "Canción de la Tormenta", "Song of Storms", "Alterna entre tormenta y cielo despejado.", "Toggles between storm and clear skies."),
    ("minuet_of_forest", "Minueto del Bosque", "Minuet of Forest", "Teletransporte a la Plataforma del Bosque.", "Warp to the Forest pad."),
    ("bolero_of_fire", "Bolero del Fuego", "Bolero of Fire", "Teletransporte a la Plataforma del Fuego.", "Warp to the Fire pad."),
    ("serenade_of_water", "Serenata del Agua", "Serenade of Water", "Teletransporte a la Plataforma del Agua.", "Warp to the Water pad."),
    ("requiem_of_spirit", "Réquiem del Espíritu", "Requiem of Spirit", "Teletransporte a la Plataforma del Espíritu.", "Warp to the Spirit pad."),
    ("nocturne_of_shadow", "Nocturno de las Sombras", "Nocturne of Shadow", "Teletransporte a la Plataforma de las Sombras.", "Warp to the Shadow pad."),
    ("prelude_of_light", "Preludio de la Luz", "Prelude of Light", "Teletransporte a la Plataforma de la Luz.", "Warp to the Light pad."),
]

# ---------------- Huevos generadores ----------------
for _id, _es, _en, _c1, _c2 in MOBS:
    item(f"{_id}_spawn_egg", f"Huevo generador de {_es}", f"{_en} Spawn Egg",
         f"new SpawnEggItem(ModEntities.{_id.upper()}, 0x{_c1:06x}, 0x{_c2:06x}, {S})", "spawn_egg")
