package ru.merkulyevsasha.network.mappers

import ru.merkulyevsasha.core.mappers.Mapper
import ru.merkulyevsasha.core.models.UserInfo
import ru.merkulyevsasha.network.BuildConfig
import ru.merkulyevsasha.network.models.UserInfoResponse
import java.util.*

class UserInfoMapper(private val authorization: String) : Mapper<UserInfoResponse, UserInfo> {
    override fun map(item: UserInfoResponse): UserInfo {
        return UserInfo(
            item.name ?: "",
            item.phone ?: "",
            BuildConfig.API_URL + "/users/downloadPhoto?nocache=" +UUID.randomUUID(),
            authorization
        )
    }
}