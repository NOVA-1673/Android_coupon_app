package com.ex.nfcoupon.feature.owner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerScanScreen(onBack: () -> Unit) {
    // MVP: “스캔된 손님”이 있다고 가정한 상태
    var lastAction by remember { mutableStateOf("대기 중") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("사장 - NFC 처리") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("뒤로") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,      // ⬅️ 세로 가운데
            horizontalAlignment = Alignment.CenterHorizontally // ⬅️ 가로 가운데
        ) {
            Text("NFC로 손님을 스캔해 스탬프를 적립/교환 처리합니다.")
            Text("최근 처리: $lastAction")

            // MVP: 실제 NFC 대신 버튼 2개
            Button(
                onClick = { lastAction = "스탬프 +1 처리됨" },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("테스트: 손님 스탬프 +1")
            }

            OutlinedButton(
                onClick = { lastAction = "무료 음료 교환 처리됨(10개 소진)" },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("테스트: 무료 음료 교환 처리")
            }
        }
    }
}
