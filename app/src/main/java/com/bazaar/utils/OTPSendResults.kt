package com.bazaar.utils

sealed class OtpSendResult {
    data class CodeSent(val verificationId: String) : OtpSendResult()
    data class Error(val message: String) : OtpSendResult()
}