package ru.skillbranch.devintensive.extensions

import ru.skillbranch.devintensive.models.data.User
import ru.skillbranch.devintensive.models.UserView
import ru.skillbranch.devintensive.utils.Utils

fun User.toUserView(): UserView {
    val nickName = Utils.transliteration(fullName())
    val status =
        if (lastVisit == null) "Еще не разу не был"
        else if (isOnline) "online"
        else "Последний раз был ${lastVisit.humanizeDiff()}"
    val initials = Utils.toInitials(firstName, lastName)

    return UserView(
        id,
        fullName = fullName(),
        avatar = avatar,
        nickName = nickName,
        status = status,
        initials = initials
    )
}

fun User.fullName() = "$firstName $lastName"


