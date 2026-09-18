package com.example.data

import com.example.model.Hero

interface HeroRepository {
  fun getAllHeroes(): List<Hero>
  fun getHeroById(id: String): Hero?
}
