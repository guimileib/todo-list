package com.example.todolist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.todolist.navigation.AddEditRoute
import com.example.todolist.navigation.ListRoute
import com.example.todolist.ui.feature.addedit.AddEditScreen
import com.example.todolist.ui.feature.list.ListScreen
import np.com.bimalkafle.firebaseauthdemoapp.AuthState
import np.com.bimalkafle.firebaseauthdemoapp.AuthViewModel
import np.com.bimalkafle.firebaseauthdemoapp.pages.HomePage
import np.com.bimalkafle.firebaseauthdemoapp.pages.LoginPage
import np.com.bimalkafle.firebaseauthdemoapp.pages.SignupPage
import np.com.bimalkafle.firebaseauthdemoapp.pages.WelcomePage

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    
    // Observa o estado de autenticação para navegar automaticamente
    val authState by authViewModel.authState.observeAsState()
    
    // Define a tela inicial baseada no estado de autenticação
    val startDestination = if (authState is AuthState.Authenticated) "todo_list" else "login"

    NavHost(navController = navController, startDestination = startDestination, builder = {
        composable("login") {
            LoginPage(modifier,navController,authViewModel)
        }
        composable("signup") {
            SignupPage(modifier,navController,authViewModel)
        }
        composable("welcome") {
            WelcomePage(modifier,navController,authViewModel)
        }
        composable("home") {
            HomePage(modifier,navController,authViewModel)
        }
        
        // Rotas da lista de tarefas
        composable("todo_list") {
            ListScreen(
                navigateToAddEditScreen = { id ->
                    navController.navigate("add_edit_todo/${id ?: -1}")
                },
                authViewModel = authViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("add_edit_todo/{id}") { backStackEntry ->
            val idString = backStackEntry.arguments?.getString("id")
            val id = if (idString == "-1") null else idString?.toLongOrNull()
            AddEditScreen(
                id = id,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    })
}

object AppScreen {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val TODO_LIST = "todo_list"
    const val ADD_EDIT_TODO = "add_edit_todo"
}