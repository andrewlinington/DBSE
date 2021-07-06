package com.wink.dbse.repository

import com.wink.dbse.entity.BlackJackEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlackJackRepository: JpaRepository<BlackJackEntity, Long> {

}