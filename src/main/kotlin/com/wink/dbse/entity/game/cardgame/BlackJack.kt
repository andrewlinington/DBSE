package com.wink.dbse.entity.game.cardgame


import com.sun.org.apache.xpath.internal.operations.Bool
import com.wink.dbse.entity.BlackJackEntity
import com.wink.dbse.entity.game.BlackJackResult
import com.wink.dbse.entity.game.cardgame.card.CardRank
import com.wink.dbse.entity.game.cardgame.card.Deck
import com.wink.dbse.entity.game.cardgame.card.Hand
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color
import java.time.LocalDateTime
import java.time.ZoneOffset

class BlackJack(
    player: CardGameUserPlayer
) : CardGame(player, Deck.create(5)) {

    private val dealer = CardGameCpuPlayer()
    private var result: BlackJackResult? = null
    var betAmount = 0L
    //OK
    override fun start() {
        isOver = false // todo: not needed as we will just remove the item from blackjack maybe
        deck.shuffle()
        dealer.hand.addAll(deck.drawFromTop(2))
        player.hand.clear()
        player.hand.addAll(deck.drawFromTop(2))
    }

    //OK
    fun generateBlackJackEntityForGame(): BlackJackEntity {
        return BlackJackEntity(null, player.user.idLong, LocalDateTime.now(ZoneOffset.UTC),
            result!!.value , player.hand.toString(), dealer.hand.toString())
    }

    //OK
    fun showPlayerHand (avatarUrl: String, userColor: Color, title: String): MessageEmbed {
        return assembleEmbedCardDisplay(title, avatarUrl, userColor)
    }

    fun showCPUStartingHand(avatarUrl: String): MessageEmbed {
        return assembleEmbedCardDisplay("Dealer's first card:", avatarUrl, isPlayer = false)
    }

    fun assembleEmbedCardDisplay (title: String, avatarUrl: String, color: Color = Color.YELLOW, isPlayer: Boolean = true): MessageEmbed {
        val eb = EmbedBuilder()
        eb.setTitle(title).setThumbnail(avatarUrl).setColor(color)
        eb.setDescription(generateDescriptionString(isPlayer))
        //add images here TODO://
        return eb.build()
    }

    private fun generateDescriptionString(isPlayer: Boolean): String {
       return when(result) {
            null -> if (isPlayer) player.hand.toString() else dealer.hand[0].toString()
            BlackJackResult.BUST_TIE -> TODO()
            BlackJackResult.BUST -> TODO()
            BlackJackResult.LOW_HAND -> TODO()
            BlackJackResult.TIE -> TODO()
            BlackJackResult.HIGH_HAND -> TODO()
            BlackJackResult.TWENTY_ONE -> TODO()
            BlackJackResult.BLACK_JACK -> "You Won ${betAmount * 2}gc!\n${dealer.hand}"
            BlackJackResult.TWENTY_ONE_DRAW -> "It's a draw, you earned 0gc\n${dealer.hand}"
            else -> TODO()
        }
    }

    //TODO on next few commands
    enum class Move {
        STAND, HIT, DOUBLE
    }
    fun makeMove(move: Move) {
        when (move) {
            Move.STAND -> stand()
            Move.HIT -> hit()
            Move.DOUBLE -> double()
        }
    }
//
    private fun stand() {
        dealerPlays()
    }
//
    private fun hit() {
        player.hand.add(deck.pickTopCard())
    }
//
    private fun double() {
        // TODO
    }
//
    private fun dealerPlays() {
        while (valueOf(dealer.hand) < 17) {
            dealer.hand.add(deck.pickTopCard())
        }
        decideWinner()
    }


    private fun decideWinner() {
        calculatePlayerResult()
        isOver = true
    }

    fun calculatePlayerResult(): BlackJackResult {
        result = BlackJackResult.calculateResult(player.hand, dealer.hand)
        return result!!
    }

    companion object {
        fun valueOf(hand: Hand): Int {
            var value = 0
            var numAces = 0
            for (card in hand) {
                if (card.rank == CardRank.ACE) {
                    numAces++
                }
                value += card.rank.value
            }
            while (numAces > 0 && value > 21) {
                numAces--
                value -= 10
            }
            return value
        }
    }
}