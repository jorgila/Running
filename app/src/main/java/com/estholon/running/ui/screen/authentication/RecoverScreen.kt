package com.estholon.running.ui.screen.authentication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
fun RecoverScreen(
    recoverViewModel: RecoverViewModel = hiltViewModel(),
    navigateToSignIn: () -> Unit
) {

    // VARIABLES

    val context = LocalContext.current

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var isEmail by rememberSaveable {
        mutableStateOf(true)
    }

    val isLoading = recoverViewModel.isLoading.collectAsState().value

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
            .alpha(0.7f)
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
                if(recoverViewModel.isEmail(email)){
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
        Button(
            onClick = {
                recoverViewModel.resetPassword(
                    email = email,
                    navigateToSignIn = { navigateToSignIn() },
                    communicateError = { string ->
                        Toast.makeText(context,string, Toast.LENGTH_LONG).show()
                    }
                )
            },
            enabled = (email != "" && isEmail == true ),
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
            Text(text = stringResource(R.string.reset_password).uppercase())
        }
        Spacer(modifier = Modifier.height(120.dp))
        Spacer(Modifier.weight(1f))
        TextButton(
            onClick = {
                navigateToSignIn()
            },
            colors = ButtonDefaults.buttonColors(
                contentColor = Black,
                containerColor = White
            ),
            shape = RectangleShape
        ) { Text(
            text = stringResource(R.string.link_to_sign_in),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )}
    }


    // LOADING

    if(isLoading){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    }

}