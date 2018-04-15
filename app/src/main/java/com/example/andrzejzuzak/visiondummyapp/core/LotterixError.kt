package com.example.andrzejzuzak.visiondummyapp.core

data class LotterixError(val code: Int, override val message: String? = null): Exception() {

    fun isUnauthorizedError(): Boolean {
        return code == 401
    }

    fun isBadRequestError(): Boolean {
        return code == 400
    }

    fun isForbiddenError(): Boolean {
        return code == 403
    }

    fun isNotFoundError(): Boolean {
        return code == 404
    }

}