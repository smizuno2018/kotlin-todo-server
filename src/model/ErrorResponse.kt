package com.todo.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorResponse(
    @JsonProperty("error_code")
    override val errorCode: Int,
    @JsonProperty("error_message")
    override val errorMessage: String
) : BaseResponse

class ErrorCode {
    companion object {
        const val UNKNOWN_ERROR_CODE = 1
        const val BAD_REQUEST_ERROR_CODE = 2
        const val GET_ERROR_CODE = 3
        const val POST_ERROR_CODE = 4
        const val PUT_ERROR_CODE = 5
        const val DELETE_ERROR_CODE = 6
    }
}

class ErrorMessage {
    companion object {
        const val UNKNOWN_ERROR_MESSAGE = "サーバー内で不明なエラーが発生しました"
        const val BAD_REQUEST_ERROR_MESSAGE = "リクエストの形式が不正です"
        const val GET_ERROR_MESSAGE = "一覧の取得に失敗しました"
        const val POST_ERROR_MESSAGE = "登録に失敗しました"
        const val PUT_ERROR_MESSAGE = "更新に失敗しました"
        const val DELETE_ERROR_MESSAGE = "削除に失敗しました"
    }
}
