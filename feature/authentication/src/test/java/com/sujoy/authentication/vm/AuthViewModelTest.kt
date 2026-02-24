package com.sujoy.authentication.vm

import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.AuthCredential
import com.sujoy.authentication.AuthUiState
import com.sujoy.authentication.repository.AuthRepository
import com.sujoy.authentication.repository.AuthResult
import com.sujoy.data.models.PhoneAuthEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: AuthRepository

    @Mock
    private lateinit var mockCredential: AuthCredential

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sendOtp success updates states and starts timer`() = runTest {
        val phoneNumber = "+911234567890"
        val verificationId = "test_v_id"
        
        val flow = MutableSharedFlow<PhoneAuthEvent>()
        `when`(repository.sendVerificationCode(phoneNumber)).thenReturn(flow)

        viewModel.sendOtp(phoneNumber)
        
        testDispatcher.scheduler.runCurrent()
        assertThat(viewModel.uiState.value).isInstanceOf(AuthUiState.Loading::class.java)
        
        flow.emit(PhoneAuthEvent.CodeSent(verificationId))
        testDispatcher.scheduler.runCurrent()

        assertThat(viewModel.isOTPSent.value).isTrue()
        assertThat(viewModel.verificationId.value).isEqualTo(verificationId)
        assertThat(viewModel.uiState.value).isEqualTo(AuthUiState.Idle)
        // Check initial timer value without advancing time
        assertThat(viewModel.timerValue.value).isEqualTo(60)
    }

    @Test
    fun `sendOtp failure updates error state`() = runTest {
        val phoneNumber = "+911234567890"
        val errorMsg = "Network error"
        `when`(repository.sendVerificationCode(phoneNumber))
            .thenReturn(flowOf(PhoneAuthEvent.Error(errorMsg)))

        viewModel.sendOtp("1234567890")
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value).isInstanceOf(AuthUiState.Error::class.java)
        assertThat((viewModel.uiState.value as AuthUiState.Error).message).isEqualTo(errorMsg)
    }

    @Test
    fun `timer counts down correctly`() = runTest {
        `when`(repository.sendVerificationCode("+911234567890"))
            .thenReturn(flowOf(PhoneAuthEvent.CodeSent("id")))

        viewModel.sendOtp("+911234567890")
        
        // runCurrent() starts the coroutine and sets the first value (60)
        testDispatcher.scheduler.runCurrent()
        assertThat(viewModel.timerValue.value).isEqualTo(60)

        // Advance by 1 second
        advanceTimeBy(1000)
        testDispatcher.scheduler.runCurrent() 
        assertThat(viewModel.timerValue.value).isEqualTo(59)

        // Advance by remaining 59 seconds
        advanceTimeBy(59000)
        testDispatcher.scheduler.runCurrent()
        assertThat(viewModel.timerValue.value).isEqualTo(0)
    }

    @Test
    fun `verifyOtp success updates state to Success`() = runTest {
        `when`(repository.sendVerificationCode("+911234567890"))
            .thenReturn(flowOf(PhoneAuthEvent.CodeSent("vid")))
        viewModel.sendOtp("+911234567890")
        testDispatcher.scheduler.advanceUntilIdle()

        val otp = "123456"
        `when`(repository.getPhoneAuthCredential("vid", otp)).thenReturn(mockCredential)
        `when`(repository.signInWithCredentials(mockCredential)).thenReturn(flowOf(AuthResult.Success))

        viewModel.verifyOtp(otp)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value).isEqualTo(AuthUiState.Success)
        assertThat(viewModel.isOTPSent.value).isFalse()
    }

    @Test
    fun `resetAuthFlow resets all states and cancels timer`() = runTest {
        `when`(repository.sendVerificationCode("+911234567890"))
            .thenReturn(flowOf(PhoneAuthEvent.CodeSent("vid")))
        viewModel.sendOtp("1234567890")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.resetAuthFlow()

        assertThat(viewModel.uiState.value).isEqualTo(AuthUiState.Idle)
        assertThat(viewModel.isOTPSent.value).isFalse()
        assertThat(viewModel.verificationId.value).isEmpty()
        assertThat(viewModel.phoneNumber.value).isEmpty()
        assertThat(viewModel.timerValue.value).isEqualTo(60)
    }
}
