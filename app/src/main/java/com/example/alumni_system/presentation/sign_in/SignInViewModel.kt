package com.example.alumni_system.presentation.sign_in

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel :ViewModel ()
{
    private  val  _state = MutableStateFlow(SignInState())


    fun onSignInResult(result: SignInResult)

    {
        _state.update {

            it.copy(

                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage
            )

        }



    }


}









