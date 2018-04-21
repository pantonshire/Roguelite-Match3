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
    var movements: Int      = 4

}