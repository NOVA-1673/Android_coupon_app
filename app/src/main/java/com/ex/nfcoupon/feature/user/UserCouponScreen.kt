package com.ex.nfcoupon.feature.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.ex.nfcoupon.feature.Data.FlagStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCouponScreen(onBack: () -> Unit) {
    // MVP: 로컬 상태로만(나중에 Room/서버로 교체)
    var stamps by remember { mutableIntStateOf(0) }
    val canRedeem = stamps >= 10

    val view = LocalView.current

    // 화면 들어오면 ready=true, 나가면 ready=false
    DisposableEffect(Unit) {
        FlagStore.setReady(true)
        view.keepScreenOn = true
        onDispose {
            FlagStore.setReady(false)
            view.keepScreenOn = false
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding() // 상태바 겹침 방지
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // 🔹 1줄: 뒤로 버튼
                TextButton(onClick = onBack) {
                    Text("뒤로")
                }

                // 🔹 2줄: 제목 (뒤로 버튼 '아래')
                Text(
                    text = "손님 - 쿠폰",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
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
            Text("스탬프: $stamps / 10", style = MaterialTheme.typography.titleLarge)

            LinearProgressIndicator(
                progress = (stamps.coerceIn(0, 10) / 10f),
                modifier = Modifier.fillMaxWidth()
            )

            Text("NFC 태그로 스탬프가 적립됩니다.")
            // MVP: 실제 NFC 이벤트 대신 테스트 버튼
            Button(
                onClick = { stamps = (stamps + 1).coerceAtMost(10) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("테스트: 스탬프 +1")
            }

            Button(
                onClick = { stamps = 0 }, // MVP: 교환하면 0으로 초기화
                enabled = canRedeem,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("무료 음료 교환")
            }
        }
    }
}
