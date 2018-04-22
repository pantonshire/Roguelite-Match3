package com.game.run

class Run {

    companion object {

        var current = Run()

        fun newRun() {
            current = Run()
        }
    }

    var maxHealth: Int      = 3
    var health: Int         = 3
    var movements: Int      = 8
    var attacks: Int        = 2

    fun loseHeart() {
        --health
    }

    fun addHeart() {
        if(health < maxHealth) {
            ++health
        }
    }

    fun fullyHeal() {
        health = maxHealth
    }

    fun addMaxHealth() {
        ++maxHealth
        ++health
    }

}