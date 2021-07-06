package com.wink.dbse.command.game

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.entity.game.BlackJackResult
import com.wink.dbse.entity.game.cardgame.BlackJack
import com.wink.dbse.entity.game.cardgame.BlackJackList
import com.wink.dbse.entity.game.cardgame.CardGameUserPlayer
import com.wink.dbse.extension.safeName
import com.wink.dbse.repository.BlackJackRepository
import com.wink.dbse.repository.UserRepository
import com.wink.dbse.service.Messenger
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import org.springframework.stereotype.Component
import java.awt.Color

@Component
class Bet(
    private val messenger: Messenger,
    private val blackJackList: BlackJackList,
    private val userRepository: UserRepository,
    private val blackJackRepository: BlackJackRepository
): Command() {

    init {
        name = "Bet"
        help = "todo"

    }
    //OK
    override fun execute(event: CommandEvent) {
        if(verifyValidInput(event)) return
        setUpGame(event, event.args.split(" ")[0].toLong())
    }
    //OK
    private fun verifyValidInput (event: CommandEvent): Boolean {
        return verifyGameIsNotStarted(event) || verifyAmount(event)
    }
    //OK
    private fun verifyGameIsNotStarted(event: CommandEvent): Boolean {
        if(blackJackList.isGameStarted(event.author.idLong)) {
            messenger.sendMessage(event.channel, """
                You are already in a game
                To see your cards, say `!myhand`
            """.trimIndent())
            return true
        }
        return false
    }
    //OK
    private fun verifyAmount(event: CommandEvent): Boolean {
        val betAmount: Long
        try {
            betAmount = event.args.split(" ")[0].toLong()
        } catch (e: Exception ) {
            messenger.sendMessage(event.channel, "To bet for a game of blackjack, say `!bet <amount>`")
            return true
        }

        if (betAmount < 1) {
            messenger.sendMessage(event.channel, "You must bet at least 1 GryphCoin!")
            return true
        }

        return false
    }
    //OK
    private fun setUpGame(event: CommandEvent, betAmount: Long) {
        val user = event.author
        if(verifyPlayerFunds(user.idLong, betAmount, event)) return
        checkBlackJackGameStatus(startBlackJackGame(user, event, betAmount), event)
    }
    //OK
    private fun verifyPlayerFunds (userId: Long, betAmount: Long, event: CommandEvent): Boolean {
        val wallet = userRepository.getOne(userId).wallet
        if(wallet < betAmount) {
            val message = "You do not have enough money to make that bet!\n" +
                    "Your wallet contains $wallet GryphCoins"
            messenger.sendMessage(event.channel, message)
            return true
        }
        return false
    }
    //OK
    private fun startBlackJackGame(user: User, event: CommandEvent, betAmount: Long): BlackJack {
        val blackJack =  BlackJack(CardGameUserPlayer(user))
        blackJack.betAmount = betAmount
        blackJack.start()
        messenger.sendMessage(event.channel, blackJack.showPlayerHand(user.avatarUrl!!,
            event.member?.color ?: Color.LIGHT_GRAY,"${user.safeName()} received their first 2 cards:"))
        messenger.sendMessage(event.channel, blackJack.showCPUStartingHand(event.selfUser.effectiveAvatarUrl))
        return blackJack
    }

    private fun checkBlackJackGameStatus(blackJack: BlackJack, event: CommandEvent) {
        val result = blackJack.calculatePlayerResult()

        if(result == BlackJackResult.BLACK_JACK || result ==  BlackJackResult.TWENTY_ONE_DRAW ) {
            val gc = BlackJackResult.calculatePlayerReward(result, blackJack.betAmount)
            printBlackJackResult(event, blackJack)
            performBlackJackResult(blackJack, gc,event.author.idLong)
        } else {
            blackJackList.putGame(event.author.idLong, blackJack)
            blackJackList.removeGame(event.author.idLong)
        }
    }

    private fun printBlackJackResult(event: CommandEvent, blackJack: BlackJack){
        messenger.sendMessage(event.channel, assembleBlackJackResultEmbed(event, blackJack))
    }

    private fun assembleBlackJackResultEmbed(event: CommandEvent, blackJack: BlackJack): MessageEmbed {
        return blackJack.assembleEmbedCardDisplay("${event.author.safeName()} got 21!",
            event.author.defaultAvatarUrl, event.member?.color ?: Color.LIGHT_GRAY)
    }

    private fun performBlackJackResult (blackJack: BlackJack, gc: Long, userId: Long) {
        userRepository.addMoney(userId, gc)
        blackJackRepository.save(blackJack.generateBlackJackEntityForGame())
    }

}
