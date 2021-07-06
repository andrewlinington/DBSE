package com.wink.dbse.entity.game.cardgame

import org.springframework.stereotype.Component
import kotlin.collections.HashMap

@Component
class BlackJackList {

    private val blackJackList = HashMap<Long,BlackJack>()

    fun putGame (userId: Long, blackJack: BlackJack) {
        blackJackList[userId] = blackJack
    }

    fun isGameStarted (userId: Long) : Boolean {
        return blackJackList.containsKey(userId)
    }

    fun getGame (userId: Long): BlackJack {
        return blackJackList[userId]!!
    }

    fun removeGame (userId: Long) {
        blackJackList.remove(userId)
    }

}