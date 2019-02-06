package com.incwellventure.auth

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AuthUser(
    val displayName: String?,
    val email: String?,
    val photoUrl: String?
) : Parcelable