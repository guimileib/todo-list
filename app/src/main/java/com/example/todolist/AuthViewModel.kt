package np.com.bimalkafle.firebaseauthdemoapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> = _currentUser

    init {
        checkAuthStatus()
    }


    fun checkAuthStatus(){
        if(auth.currentUser==null){
            _authState.value = AuthState.Unauthenticated
            _currentUser.value = null
        }else{
            _authState.value = AuthState.Authenticated
            _currentUser.value = auth.currentUser
        }
    }

    fun login(email : String,password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("E-mail e senha não podem estar vazios")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{task->
                if (task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                    _currentUser.value = auth.currentUser
                }else{
                    _authState.value = AuthState.Error("E-mail ou senha inválidos!")
                }
            }
    }

    fun signup(email : String,password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("E-mail e senha não podem estar vazios")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{task->
                if (task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                    _currentUser.value = auth.currentUser
                }else{
                    val errorMessage = when {
                        task.exception?.message?.contains("email", ignoreCase = true) == true -> 
                            "E-mail inválido ou já existe!"
                        task.exception?.message?.contains("password", ignoreCase = true) == true -> 
                            "Senha deve ter pelo menos 6 caracteres!"
                        else -> "Erro no cadastro!"
                    }
                    
                    _authState.value = AuthState.Error(errorMessage)
                }
            }
    }

    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
        _currentUser.value = null
    }


}


sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}