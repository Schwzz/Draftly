package com.example.data

import com.example.model.Hero
import com.example.model.HeroMeta
import com.example.model.Lane
import com.example.model.Role

class LocalHeroRepository : HeroRepository {

  private val heroes: List<Hero> = listOf(
    // ROAM / SUPPORT / TANK HEROES
    Hero(
      id = "saber",
      name = "Saber",
      roles = listOf(Role.ROAM, Role.JUNGLE),
      lanes = listOf(Lane.ROAM, Lane.JUNGLE),
      counters = mapOf(
        "fanny" to 2.0,   // Point-and-click airborne lockdown instantly shuts down cable dives
        "claude" to 1.5,  // Instant burst catches Claude before teleport/Blazing Duet
        "yve" to 2.0,     // S2 dive + Triple Sweep interrupts and bursts stationary Yve during ult
        "ling" to 2.0,
        "wanwan" to 1.5,
        "beatrix" to 1.5
      ),
      counteredBy = mapOf(
        "khufra" to 1.0,
        "terizla" to 1.5, // Tanky sustain fighters withstand burst
        "baxia" to 1.5
      ),
      synergies = mapOf(
        "xavier" to 1.5,  // Lockdown enables guaranteed Dawning Light snipe
        "julian" to 1.0,  // Dual burst pickoff
        "terizla" to 1.0
      ),
      meta = HeroMeta(winRate = 51.4, pickRate = 2.1, banRate = 1.2)
    ),

    Hero(
      id = "khufra",
      name = "Khufra",
      roles = listOf(Role.ROAM),
      lanes = listOf(Lane.ROAM),
      counters = mapOf(
        "fanny" to 2.5,   // Bouncing Ball hard-counters Fanny cable dashes
        "claude" to 2.0,  // Stops Battle Mirror Image dashes and disrupts Blazing Duet
        "ling" to 2.0,    // Blocks wall jump dashes
        "lancelot" to 2.0,
        "wanwan" to 1.5
      ),
      counteredBy = mapOf(
        "yve" to 0.8,     // Kited and slowed by Real World Manipulation from range
        "diggie" to 2.5,  // Time Journey cleanses all Khufra knockups & bounces
        "karrie" to 1.5   // True damage melts tank stats
      ),
      synergies = mapOf(
        "xavier" to 1.5,  // Group CC sets up Xavier burst
        "terizla" to 1.5, // CC chain lockdown
        "claude" to 1.5
      ),
      meta = HeroMeta(winRate = 52.2, pickRate = 2.3, banRate = 1.8)
    ),

    Hero(
      id = "lolita",
      name = "Lolita",
      roles = listOf(Role.ROAM),
      lanes = listOf(Lane.ROAM),
      counters = mapOf(
        "claude" to 2.5,  // Shield completely absorbs Blazing Duet & basic attacks
        "beatrix" to 2.0, // Blocks projectile sniper and rocket shots
        "wanwan" to 1.5,
        "moskov" to 2.0,
        "fanny" to 1.0    // Shield slam stun stops cable trajectory
      ),
      counteredBy = mapOf(
        "yve" to 1.5,     // Yve's AoE slow field is non-projectile and penetrates shield
        "pharsa" to 1.5,  // Sky bombardments strike from above
        "diggie" to 2.0
      ),
      synergies = mapOf(
        "xavier" to 1.0,
        "terizla" to 1.2
      ),
      meta = HeroMeta(winRate = 53.8, pickRate = 1.6, banRate = 0.8)
    ),

    Hero(
      id = "franco",
      name = "Franco",
      roles = listOf(Role.ROAM),
      lanes = listOf(Lane.ROAM),
      counters = mapOf(
        "fanny" to 2.0,   // Bloody Hunt suppression stops cables through CC immunity
        "claude" to 1.5,  // Hook or suppression cancels Blazing Duet
        "ling" to 1.5
      ),
      counteredBy = mapOf(
        "yve" to 0.8,     // Difficult to close distance against heavy slows
        "terizla" to 1.0  // Hooking Terizla initiates fight for enemy
      ),
      synergies = mapOf(
        "xavier" to 1.2,
        "julian" to 1.2
      ),
      meta = HeroMeta(winRate = 49.5, pickRate = 4.2, banRate = 2.5)
    ),

    Hero(
      id = "tigreal",
      name = "Tigreal",
      roles = listOf(Role.ROAM),
      lanes = listOf(Lane.ROAM),
      counters = mapOf(
        "terizla" to 1.0,
        "beatrix" to 1.0
      ),
      counteredBy = mapOf(
        "yve" to 1.5,     // Extreme kiting and slows prevent Tigreal from closing in for ult
        "claude" to 1.2,  // High mobility easily kites Tigreal
        "diggie" to 2.5,  // Ult cleanses Implosion completely
        "fanny" to 1.0    // Hard to pin down Fanny
      ),
      synergies = mapOf(
        "xavier" to 1.5,
        "julian" to 1.5,
        "terizla" to 1.2
      ),
      meta = HeroMeta(winRate = 50.8, pickRate = 3.5, banRate = 1.9)
    ),

    Hero(
      id = "diggie",
      name = "Diggie",
      roles = listOf(Role.ROAM),
      lanes = listOf(Lane.ROAM),
      counters = mapOf(
        "atlas" to 2.5,   // Time Journey completely nullifies Fatal Links
        "tigreal" to 2.5, // Nullifies Implosion
        "khufra" to 2.0,
        "yve" to 1.2      // Cleanses slow debuffs for team
      ),
      counteredBy = mapOf(
        "fanny" to 1.2,   // Burst damage bypasses cleansing utility
        "saber" to 1.5
      ),
      synergies = mapOf(
        "claude" to 1.5,
        "xavier" to 1.0
      ),
      meta = HeroMeta(winRate = 52.0, pickRate = 1.9, banRate = 3.8)
    ),

    Hero(
      id = "atlas",
      name = "Atlas",
      roles = listOf(Role.ROAM),
      lanes = listOf(Lane.ROAM),
      counters = mapOf(
        "beatrix" to 1.5,
        "yve" to 1.0
      ),
      counteredBy = mapOf(
        "diggie" to 3.0,  // Hard counter: cleanses ultimate
        "claude" to 1.2,  // Easy blink escape
        "fanny" to 1.2
      ),
      synergies = mapOf(
        "xavier" to 2.0,
        "julian" to 1.8,
        "terizla" to 1.5
      ),
      meta = HeroMeta(winRate = 51.1, pickRate = 1.8, banRate = 1.1)
    ),

    Hero(
      id = "kaja",
      name = "Kaja",
      roles = listOf(Role.ROAM, Role.MID),
      lanes = listOf(Lane.ROAM, Lane.MID_LANE),
      counters = mapOf(
        "fanny" to 2.0,   // Suppress ultimate
        "claude" to 1.8,
        "yve" to 1.5
      ),
      counteredBy = mapOf(
        "terizla" to 1.0,
        "baxia" to 1.0
      ),
      synergies = mapOf(
        "xavier" to 1.5,
        "julian" to 1.5
      ),
      meta = HeroMeta(winRate = 51.7, pickRate = 1.1, banRate = 0.5)
    ),

    Hero(
      id = "minotaur",
      name = "Minotaur",
      roles = listOf(Role.ROAM),
      lanes = listOf(Lane.ROAM),
      counters = mapOf(
        "claude" to 1.0
      ),
      counteredBy = mapOf(
        "yve" to 1.2,
        "diggie" to 2.0
      ),
      synergies = mapOf(
        "xavier" to 1.2,
        "terizla" to 1.2
      ),
      meta = HeroMeta(winRate = 52.5, pickRate = 2.0, banRate = 0.9)
    ),

    Hero(
      id = "chou",
      name = "Chou",
      roles = listOf(Role.ROAM, Role.EXP),
      lanes = listOf(Lane.ROAM, Lane.EXP_LANE),
      counters = mapOf(
        "claude" to 1.5,
        "yve" to 1.5,
        "fanny" to 1.0
      ),
      counteredBy = mapOf(
        "terizla" to 1.2,
        "pharsa" to 1.0
      ),
      synergies = mapOf(
        "xavier" to 1.3,
        "julian" to 1.3
      ),
      meta = HeroMeta(winRate = 50.2, pickRate = 3.8, banRate = 1.4)
    ),

    // MID LANE MAGES
    Hero(
      id = "xavier",
      name = "Xavier",
      roles = listOf(Role.MID),
      lanes = listOf(Lane.MID_LANE),
      counters = mapOf(
        "terizla" to 1.5, // Immobilize and range kite heavy frontliners
        "tigreal" to 1.5
      ),
      counteredBy = mapOf(
        "fanny" to 2.2,   // High mobility diver executes Xavier easily
        "saber" to 2.0,   // Point and click burst eliminates squishy mage
        "ling" to 2.0,
        "yve" to 0.8
      ),
      synergies = mapOf(
        "saber" to 1.5,
        "khufra" to 1.5,
        "atlas" to 2.0,
        "terizla" to 1.5,
        "julian" to 1.2
      ),
      meta = HeroMeta(winRate = 51.8, pickRate = 2.4, banRate = 1.0)
    ),

    Hero(
      id = "yve",
      name = "Yve",
      roles = listOf(Role.MID),
      lanes = listOf(Lane.MID_LANE),
      counters = mapOf(
        "tigreal" to 1.8, // Heavy slows cripple slow initiators
        "minotaur" to 1.5,
        "khufra" to 1.0,
        "lolita" to 1.5
      ),
      counteredBy = mapOf(
        "saber" to 2.0,   // Dive and instant aerial burst during Real World Manipulation
        "fanny" to 2.0,   // Cable dive into ult box kills Yve
        "paquito" to 1.8,
        "yu_zhong" to 1.8
      ),
      synergies = mapOf(
        "terizla" to 1.5,
        "claude" to 1.2
      ),
      meta = HeroMeta(winRate = 52.0, pickRate = 1.8, banRate = 1.5)
    ),

    Hero(
      id = "pharsa",
      name = "Pharsa",
      roles = listOf(Role.MID),
      lanes = listOf(Lane.MID_LANE),
      counters = mapOf(
        "lolita" to 1.5,
        "terizla" to 1.2
      ),
      counteredBy = mapOf(
        "fanny" to 2.0,
        "saber" to 2.0,
        "ling" to 2.0
      ),
      synergies = mapOf(
        "tigreal" to 1.5,
        "atlas" to 1.5
      ),
      meta = HeroMeta(winRate = 51.0, pickRate = 2.0, banRate = 0.7)
    ),

    Hero(
      id = "valentina",
      name = "Valentina",
      roles = listOf(Role.MID),
      lanes = listOf(Lane.MID_LANE),
      counters = mapOf(
        "atlas" to 2.0,   // Steals Fatal Links game-changing ult
        "terizla" to 1.5, // Steals Penalty Zone
        "claude" to 1.5   // Steals Blazing Duet
      ),
      counteredBy = mapOf(
        "saber" to 1.5,
        "fanny" to 1.5
      ),
      synergies = mapOf(
        "khufra" to 1.2
      ),
      meta = HeroMeta(winRate = 51.5, pickRate = 1.7, banRate = 2.2)
    ),

    Hero(
      id = "nana",
      name = "Nana",
      roles = listOf(Role.MID),
      lanes = listOf(Lane.MID_LANE),
      counters = mapOf(
        "fanny" to 1.5,   // Molina morph stops cables
        "claude" to 1.2
      ),
      counteredBy = mapOf(
        "yve" to 1.0,
        "xavier" to 1.0
      ),
      synergies = mapOf(
        "saber" to 1.0
      ),
      meta = HeroMeta(winRate = 50.4, pickRate = 3.9, banRate = 2.0)
    ),

    Hero(
      id = "novaria",
      name = "Novaria",
      roles = listOf(Role.MID),
      lanes = listOf(Lane.MID_LANE),
      counters = mapOf(
        "fanny" to 1.2    // Astral vision reveals cables & bushes
      ),
      counteredBy = mapOf(
        "saber" to 1.5,
        "ling" to 1.8
      ),
      synergies = mapOf(
        "xavier" to 1.2
      ),
      meta = HeroMeta(winRate = 51.2, pickRate = 1.5, banRate = 1.0)
    ),

    // JUNGLE ASSASSINS & FIGHTERS
    Hero(
      id = "fanny",
      name = "Fanny",
      roles = listOf(Role.JUNGLE),
      lanes = listOf(Lane.JUNGLE),
      counters = mapOf(
        "xavier" to 2.2,  // Devastating dive onto immobile mages
        "pharsa" to 2.0,
        "yve" to 2.0,
        "beatrix" to 1.8,
        "wanwan" to 1.5
      ),
      counteredBy = mapOf(
        "khufra" to 2.5,  // Bouncing ball shuts down cables
        "saber" to 2.0,   // Airborne lock burst
        "franco" to 2.0,  // Suppression lock
        "kaja" to 2.0     // Suppression lock
      ),
      synergies = mapOf(
        "mathilda" to 1.5,
        "diggie" to 1.2
      ),
      meta = HeroMeta(winRate = 52.8, pickRate = 2.5, banRate = 4.2)
    ),

    Hero(
      id = "julian",
      name = "Julian",
      roles = listOf(Role.JUNGLE, Role.MID, Role.EXP),
      lanes = listOf(Lane.JUNGLE, Lane.MID_LANE, Lane.EXP_LANE),
      counters = mapOf(
        "claude" to 1.2,
        "yve" to 1.2
      ),
      counteredBy = mapOf(
        "saber" to 1.5,
        "baxia" to 1.2
      ),
      synergies = mapOf(
        "xavier" to 1.2,
        "terizla" to 1.5,
        "tigreal" to 1.5
      ),
      meta = HeroMeta(winRate = 51.6, pickRate = 2.2, banRate = 1.5)
    ),

    Hero(
      id = "ling",
      name = "Ling",
      roles = listOf(Role.JUNGLE),
      lanes = listOf(Lane.JUNGLE),
      counters = mapOf(
        "xavier" to 2.0,
        "yve" to 1.8,
        "pharsa" to 2.0
      ),
      counteredBy = mapOf(
        "khufra" to 2.0,
        "saber" to 2.0,
        "franco" to 1.8
      ),
      synergies = mapOf(
        "mathilda" to 1.5
      ),
      meta = HeroMeta(winRate = 52.1, pickRate = 2.0, banRate = 3.0)
    ),

    Hero(
      id = "lancelot",
      name = "Lancelot",
      roles = listOf(Role.JUNGLE),
      lanes = listOf(Lane.JUNGLE),
      counters = mapOf(
        "xavier" to 1.8,
        "yve" to 1.5
      ),
      counteredBy = mapOf(
        "khufra" to 2.0,
        "saber" to 1.8
      ),
      synergies = mapOf(
        "khufra" to 1.0
      ),
      meta = HeroMeta(winRate = 50.9, pickRate = 2.8, banRate = 1.0)
    ),

    Hero(
      id = "baxia",
      name = "Baxia",
      roles = listOf(Role.JUNGLE, Role.ROAM),
      lanes = listOf(Lane.JUNGLE, Lane.ROAM),
      counters = mapOf(
        "claude" to 1.5,  // Innate anti-heal disrupts sustain
        "fanny" to 1.2
      ),
      counteredBy = mapOf(
        "karrie" to 2.0
      ),
      synergies = mapOf(
        "xavier" to 1.2
      ),
      meta = HeroMeta(winRate = 51.3, pickRate = 1.4, banRate = 0.6)
    ),

    Hero(
      id = "martis",
      name = "Martis",
      roles = listOf(Role.JUNGLE, Role.EXP),
      lanes = listOf(Lane.JUNGLE, Lane.EXP_LANE),
      counters = mapOf(
        "claude" to 1.2,
        "yve" to 1.2
      ),
      counteredBy = mapOf(
        "saber" to 1.0,
        "terizla" to 1.2
      ),
      synergies = mapOf(
        "xavier" to 1.0
      ),
      meta = HeroMeta(winRate = 51.0, pickRate = 2.5, banRate = 1.2)
    ),

    Hero(
      id = "hayabusa",
      name = "Hayabusa",
      roles = listOf(Role.JUNGLE),
      lanes = listOf(Lane.JUNGLE),
      counters = mapOf(
        "yve" to 1.8,
        "xavier" to 1.8
      ),
      counteredBy = mapOf(
        "saber" to 1.5,
        "khufra" to 1.5
      ),
      synergies = mapOf(
        "khufra" to 1.0
      ),
      meta = HeroMeta(winRate = 52.4, pickRate = 2.1, banRate = 2.8)
    ),

    // GOLD LANE MARKSMEN
    Hero(
      id = "claude",
      name = "Claude",
      roles = listOf(Role.GOLD),
      lanes = listOf(Lane.GOLD_LANE),
      counters = mapOf(
        "terizla" to 1.5, // Kites and shreds high HP fighters
        "tigreal" to 1.2,
        "minotaur" to 1.2
      ),
      counteredBy = mapOf(
        "lolita" to 2.5,  // Shield negates ult entirely
        "khufra" to 2.0,  // Stops blinks and disrupts Blazing Duet
        "saber" to 1.5,   // Point-and-click burst before escape
        "baxia" to 1.5
      ),
      synergies = mapOf(
        "diggie" to 1.5,  // CC immunity during Blazing Duet
        "khufra" to 1.5
      ),
      meta = HeroMeta(winRate = 51.5, pickRate = 2.6, banRate = 1.9)
    ),

    Hero(
      id = "beatrix",
      name = "Beatrix",
      roles = listOf(Role.GOLD),
      lanes = listOf(Lane.GOLD_LANE),
      counters = mapOf(
        "terizla" to 1.2
      ),
      counteredBy = mapOf(
        "lolita" to 2.0,  // Shield blocks weapon projectiles
        "saber" to 1.5,
        "fanny" to 1.8
      ),
      synergies = mapOf(
        "tigreal" to 1.5
      ),
      meta = HeroMeta(winRate = 50.5, pickRate = 3.2, banRate = 1.1)
    ),

    Hero(
      id = "wanwan",
      name = "Wanwan",
      roles = listOf(Role.GOLD),
      lanes = listOf(Lane.GOLD_LANE),
      counters = mapOf(
        "terizla" to 1.8  // High mobility hops kite slow skillshots easily
      ),
      counteredBy = mapOf(
        "khufra" to 1.8,  // Stops jumping hops
        "phoveus" to 2.5,
        "lolita" to 1.5
      ),
      synergies = mapOf(
        "khufra" to 1.0
      ),
      meta = HeroMeta(winRate = 51.9, pickRate = 2.0, banRate = 2.1)
    ),

    Hero(
      id = "brody",
      name = "Brody",
      roles = listOf(Role.GOLD),
      lanes = listOf(Lane.GOLD_LANE),
      counters = mapOf(
        "fanny" to 1.2    // Stun shot interrupts cable trajectory
      ),
      counteredBy = mapOf(
        "saber" to 1.5,
        "lolita" to 1.5
      ),
      synergies = mapOf(
        "khufra" to 1.2
      ),
      meta = HeroMeta(winRate = 51.2, pickRate = 2.3, banRate = 0.9)
    ),

    Hero(
      id = "karrie",
      name = "Karrie",
      roles = listOf(Role.GOLD),
      lanes = listOf(Lane.GOLD_LANE),
      counters = mapOf(
        "terizla" to 2.0, // True damage shreds tank armor
        "baxia" to 2.0,
        "tigreal" to 1.8
      ),
      counteredBy = mapOf(
        "saber" to 1.8,
        "fanny" to 1.8
      ),
      synergies = mapOf(
        "lolita" to 1.2
      ),
      meta = HeroMeta(winRate = 52.3, pickRate = 2.5, banRate = 1.4)
    ),

    Hero(
      id = "moskov",
      name = "Moskov",
      roles = listOf(Role.GOLD),
      lanes = listOf(Lane.GOLD_LANE),
      counters = mapOf(
        "fanny" to 1.5    // Spear of Misery pins Fanny to wall during dive
      ),
      counteredBy = mapOf(
        "lolita" to 2.0,
        "saber" to 1.5
      ),
      synergies = mapOf(
        "tigreal" to 1.5
      ),
      meta = HeroMeta(winRate = 52.6, pickRate = 3.6, banRate = 2.4)
    ),

    // EXP LANE FIGHTERS
    Hero(
      id = "terizla",
      name = "Terizla",
      roles = listOf(Role.EXP),
      lanes = listOf(Lane.EXP_LANE),
      counters = mapOf(
        "paquito" to 1.2,
        "chou" to 1.2
      ),
      counteredBy = mapOf(
        "claude" to 1.5,  // Heavy kiting and percentage damage
        "karrie" to 2.0,  // True damage destroys passive damage reduction
        "xavier" to 1.5,  // Range control and immobilize prevents gap closing
        "wanwan" to 1.8
      ),
      synergies = mapOf(
        "xavier" to 1.5,  // Penalty Zone holds enemies for Xavier beam
        "julian" to 1.5,  // Combo burst on clustered enemies
        "tigreal" to 1.2,
        "saber" to 1.0
      ),
      meta = HeroMeta(winRate = 52.7, pickRate = 2.8, banRate = 1.6)
    ),

    Hero(
      id = "paquito",
      name = "Paquito",
      roles = listOf(Role.EXP, Role.JUNGLE),
      lanes = listOf(Lane.EXP_LANE, Lane.JUNGLE),
      counters = mapOf(
        "yve" to 1.8,     // Heavy multi-dash burst assassinate
        "xavier" to 1.5
      ),
      counteredBy = mapOf(
        "terizla" to 1.2,
        "khufra" to 1.5
      ),
      synergies = mapOf(
        "xavier" to 1.2
      ),
      meta = HeroMeta(winRate = 51.4, pickRate = 2.4, banRate = 1.0)
    ),

    Hero(
      id = "yu_zhong",
      name = "Yu Zhong",
      roles = listOf(Role.EXP),
      lanes = listOf(Lane.EXP_LANE),
      counters = mapOf(
        "yve" to 1.8,     // Black Dragon form dives backline directly onto stationary Yve
        "xavier" to 1.5
      ),
      counteredBy = mapOf(
        "claude" to 1.2,
        "baxia" to 1.5
      ),
      synergies = mapOf(
        "xavier" to 1.2
      ),
      meta = HeroMeta(winRate = 51.8, pickRate = 2.6, banRate = 1.3)
    ),

    Hero(
      id = "ruby",
      name = "Ruby",
      roles = listOf(Role.EXP, Role.ROAM),
      lanes = listOf(Lane.EXP_LANE, Lane.ROAM),
      counters = mapOf(
        "fanny" to 1.5,   // Scythe hooks and stuns stop cables
        "claude" to 1.2
      ),
      counteredBy = mapOf(
        "baxia" to 1.5,
        "yve" to 1.0
      ),
      synergies = mapOf(
        "xavier" to 1.3
      ),
      meta = HeroMeta(winRate = 52.0, pickRate = 2.2, banRate = 0.8)
    ),

    Hero(
      id = "benedetta",
      name = "Benedetta",
      roles = listOf(Role.EXP, Role.JUNGLE),
      lanes = listOf(Lane.EXP_LANE, Lane.JUNGLE),
      counters = mapOf(
        "yve" to 1.5,
        "xavier" to 1.5
      ),
      counteredBy = mapOf(
        "khufra" to 2.0,  // Bouncing ball blocks all dash attacks
        "saber" to 1.5
      ),
      synergies = mapOf(
        "tigreal" to 1.2
      ),
      meta = HeroMeta(winRate = 51.1, pickRate = 1.8, banRate = 1.1)
    )
  )

  override fun getAllHeroes(): List<Hero> = heroes

  override fun getHeroById(id: String): Hero? = heroes.find { it.id.equals(id, ignoreCase = true) }
}
