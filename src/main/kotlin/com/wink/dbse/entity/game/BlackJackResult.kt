package com.wink.dbse.entity.game

import com.wink.dbse.entity.game.cardgame.BlackJack
import com.wink.dbse.entity.game.cardgame.card.Hand

enum class BlackJackResult(val value: Int) {
    BUST_TIE(-3),
    BUST(-2),
    LOW_HAND(-1),
    TIE(0),
    HIGH_HAND(1),
    TWENTY_ONE(2),
    BLACK_JACK(3),
    TWENTY_ONE_DRAW(4);

    companion object {
        fun of(num: Int): BlackJackResult {
            return when(num) {
                -3 -> BUST_TIE
                -2 -> BUST
                -1 -> LOW_HAND
                0 -> TIE
                1 -> HIGH_HAND
                2 -> TWENTY_ONE
                3 -> BLACK_JACK
                4 -> TWENTY_ONE_DRAW
                else -> throw IllegalArgumentException()
            }
        }

        fun calculateResult (dealer: Hand, player: Hand): BlackJackResult {
            val playerValue = BlackJack.valueOf(player)
            val dealerValue = BlackJack.valueOf(dealer)

            if( playerValue == 21) {
                if(dealerValue == 21) return TWENTY_ONE_DRAW
                return if(player.size == 2) BLACK_JACK else TWENTY_ONE
            }

            if( playerValue > 21) {
                return if(dealerValue > 21 ) BUST_TIE else BUST
            }

            return calculatePlayerHandAgainstDealer(playerValue, dealerValue)
        }

        private fun calculatePlayerHandAgainstDealer(playerValue:Int, dealerValue: Int): BlackJackResult{
            if(playerValue == dealerValue) return TIE
            return if(playerValue > dealerValue) HIGH_HAND else LOW_HAND
        }

        fun calculatePlayerReward(blackJackResult: BlackJackResult, amount:Long): Long {
             return when(blackJackResult) {
                TWENTY_ONE, HIGH_HAND -> amount
                 TIE, BUST_TIE, TWENTY_ONE_DRAW -> 0L
                 BLACK_JACK -> amount * 2
                 LOW_HAND, BUST -> -amount
             }
        }

    }
}