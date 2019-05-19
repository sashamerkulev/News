package ru.merkulyevsasha.network.mappers

import ru.merkulyevsasha.core.mappers.Mapper
import ru.merkulyevsasha.core.models.UserInfo
import ru.merkulyevsasha.network.models.UserInfoResponse

class UserInfoMapper : Mapper<UserInfoResponse, UserInfo> {
    override fun map(item: UserInfoResponse): UserInfo {
        return UserInfo(
            item.name,
            item.phone,
            item.fileName
        )
    }
}