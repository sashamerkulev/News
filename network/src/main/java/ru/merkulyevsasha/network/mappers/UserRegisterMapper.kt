package ru.merkulyevsasha.network.mappers

import ru.merkulyevsasha.core.mappers.Mapper
import ru.merkulyevsasha.core.models.UserRegister
import ru.merkulyevsasha.network.models.UserRegisterResponse

class UserRegisterMapper : Mapper<UserRegisterResponse, UserRegister> {
    override fun map(item: UserRegisterResponse): UserRegister {
        return UserRegister()
    }
}