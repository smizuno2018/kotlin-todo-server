package com.todo.model

import com.todo.model.ErrorCode.Companion.BAD_REQUEST_ERROR_CODE
import com.todo.model.ErrorCode.Companion.UNKNOWN_ERROR_CODE
import com.todo.model.ErrorMessage.Companion.BAD_REQUEST_ERROR_MESSAGE
import com.todo.model.ErrorMessage.Companion.UNKNOWN_ERROR_MESSAGE
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError

abstract class SystemException(message: String, private val code: Int? = null, ex: Exception? = null) :
    RuntimeException(message, ex) {
    abstract val status: HttpStatusCode
    fun response() = ErrorResponse(
        errorCode = code ?: status.value,
        errorMessage = message ?: "error"
    )
}

class UnknownException : SystemException {
    constructor(message: String = UNKNOWN_ERROR_MESSAGE) : super(message, code = UNKNOWN_ERROR_CODE)

    override val status: HttpStatusCode = InternalServerError
}

class InternalServerErrorException : SystemException {
    constructor(message: String, code: Int) : super(message, code)

    override val status: HttpStatusCode = InternalServerError
}

class BadRequestException : SystemException {
    constructor(message: String = BAD_REQUEST_ERROR_MESSAGE) : super(message, code = BAD_REQUEST_ERROR_CODE)

    override val status: HttpStatusCode = BadRequest
}

class RecordInvalidException : Exception()
