package com.sujoy.products.models

sealed class UploadState {
    data object Idle : UploadState()
    data class Uploading(val progress: Int) : UploadState()
    data object Success : UploadState()
    data class Error(val message: String) : UploadState()
}
