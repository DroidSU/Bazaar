package com.bazaar.models

// Sealed class to represent the different states of the CSV upload process.
sealed class UploadState {
    data object Idle : UploadState()
    data class Uploading(val progress: Int) : UploadState()
    data object Success : UploadState()
    data class Error(val message: String) : UploadState()
}