package com.ex.nfcoupon.feature.role

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectScreen(
    onUser: () -> Unit,
    onOwner: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("NFCoupon") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,      // ⬅️ 세로 가운데
            horizontalAlignment = Alignment.CenterHorizontally // ⬅️ 가로 가운데
        ) {
            Text("역할을 선택하세요", style = MaterialTheme.typography.titleLarge)
            Button(onClick = onUser, modifier = Modifier.fillMaxWidth()) {
                Text("손님 화면")
            }
            OutlinedButton(onClick = onOwner, modifier = Modifier.fillMaxWidth()) {
                Text("카페 사장 화면")
            }
        }
    }
}
