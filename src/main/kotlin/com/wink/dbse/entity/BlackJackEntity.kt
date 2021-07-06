package com.wink.dbse.entity

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "blackjack")
class BlackJackEntity(
    @Id
    @GeneratedValue
    val id: Long? = null,
    val userId: Long? = null,
    val timeOccurred: LocalDateTime? = null,
    val result: Int? = null,
    val userHand: String? = null,
    val cpuHand: String? =  null
)