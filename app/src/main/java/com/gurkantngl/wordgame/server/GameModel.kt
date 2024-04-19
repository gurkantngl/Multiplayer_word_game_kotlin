package com.gurkantngl.wordgame.server

data class GameModel(
    var winner : String = "",
    var gameStatus : GameStatus = GameStatus.CREATED
)

enum class GameStatus {
    CREATED,
    WAITING,
    JOINED,
    STARTED,
    INPROGRESS,
    FINISHED
}