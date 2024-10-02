package com.estholon.running.ui.screen.authentication

import android.provider.CalendarContract.Colors
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.estholon.running.R
import com.estholon.running.ui.theme.Black
import com.estholon.running.ui.theme.White

@Composable
fun SignInScreen(
    signInViewModel: SignInViewModel = hiltViewModel(),
    clearNavigation: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToRecover: () -> Unit
) {

    // VARIABLES

    val context = LocalContext.current

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var isEmail by rememberSaveable {
        mutableStateOf(true)
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var passwordVisibility by rememberSaveable {
        mutableStateOf(false)
    }

    val isLoading = signInViewModel.isLoading.collectAsState().value

    // LAYOUT

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_background),
            contentDescription = "Background image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }

    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.5f)
    ){

    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(Modifier.weight(1f))
        TextField(
            value = email,
            label = { Text(text = stringResource(R.string.email)) },
            onValueChange = {
                email = it
                if(signInViewModel.isEmail(email)){
                    isEmail = true
                } else {
                    isEmail = false
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            isError = !isEmail,
            maxLines = 1,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            label = { Text(text = stringResource(R.string.password))},
            onValueChange = { password = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            maxLines = 1,
            singleLine = true,
            trailingIcon = {
                val image = if(passwordVisibility){
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(imageVector = image, contentDescription = stringResource(R.string.show_hide_password))
                }
            },
            visualTransformation = if(passwordVisibility){
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                signInViewModel.signInEmail(
                    email = email,
                    password = password,
                    navigateToHome = { navigateToHome() },
                    communicateError = { string ->
                        Toast.makeText(context,string,Toast.LENGTH_LONG).show()
                    }
                )
            },
            enabled = (email != "" && password.length > 5),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            ),
            modifier = Modifier
                .width(250.dp)
                .height(50.dp)
        ) {
            Text(text = stringResource(R.string.sign_in).uppercase())
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                navigateToRecover()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.password_forgotten),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.weight(1f))
        TextButton(
            onClick = {
                navigateToSignUp()
            },
            colors = ButtonColors(
                contentColor = Black,
                containerColor = White,
                disabledContentColor = Color.Gray,
                disabledContainerColor = White
            )
        ) { Text(
            text = stringResource(R.string.link_to_sign_up),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )}
    }

    // LOADING

    if(isLoading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }

}