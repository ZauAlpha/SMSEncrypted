package com.example.smsencrypted.Screens

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smsencrypted.R

@Composable
fun CommonBottomAppBar(
    modifier: Modifier = Modifier,
    onClickOne: () -> Unit,
    onClickTwo: () -> Unit,
    state: BottomNavigationScreens
) {
    BottomAppBar(
        modifier = modifier,
        contentColor = MaterialTheme.colorScheme.surface,
        containerColor = contentColorFor(MaterialTheme.colorScheme.surface),

        ) {
        Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
            val color =
                if (state == BottomNavigationScreens.NewMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            val alternateColor =
                if (state == BottomNavigationScreens.Inbox) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            IconButton(onClick = onClickOne) {
                Icon(painter = painterResource(id = R.drawable.message), contentDescription ="Message", tint = color )
            }

            IconButton(onClick = onClickTwo) {
                Icon(painter = painterResource(id = R.drawable.inbox), contentDescription ="Message", tint = alternateColor )
            }
        }


    }
}


enum class BottomNavigationScreens {
    NewMessage, Inbox
}

