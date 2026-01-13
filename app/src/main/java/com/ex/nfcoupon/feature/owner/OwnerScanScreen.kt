package com.ex.nfcoupon.feature.owner

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ex.nfcoupon.feature.Utility.OwnerNfcClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerScanScreen(onBack: () -> Unit) {
    // MVP: “스캔된 손님”이 있다고 가정한 상태
    var lastAction by remember { mutableStateOf("대기 중") }


    val ctx = LocalContext.current
    val activity = ctx as Activity

    val nfcAdapter = remember {NfcAdapter.getDefaultAdapter(ctx)}
    val client = remember { OwnerNfcClient(aidHex = "F0010203040506")}

    var lastTag by remember { mutableStateOf<Tag?>(null)}
    var showDialog by remember { mutableStateOf(false)}

    var customerId by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("대기중")}

    fun enableReader() {
        status = "스캔 대기..."
        val flags = NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
        nfcAdapter?.enableReaderMode(activity, { tag ->
            // 1) 스캔되면 먼저 READ로 손님ID 읽기

            val readRes = client.readCustomerId(tag)
            val id = if (readRes.ok && readRes.payload != null)
                String(readRes.payload, Charsets.UTF_8)
            else null

            // 2) UI state 업데이트는 UI 스레드에서
            activity.runOnUiThread {
                lastTag = tag
                customerId = id
                status = if (id != null) "손님 감지: $id (처리 대기)" else "손님 감지: ID 읽기 실패(SW=${readRes.sw.toString(16)})"
                showDialog = true
            }
        }, flags, null)
    }

    fun disableReader() {
        nfcAdapter?.disableReaderMode(activity)
    }

    DisposableEffect(Unit) {
        onDispose { disableReader() }
    }

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
                onClick = { enableReader()  },
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

            Text("최근 처리 : $status")
        }
    }

    if (showDialog) {
        val idText = customerId ?: "알 수 없음"
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("손님 발견") },
            text = { Text("손님ID: $idText\n스탬프를 적립하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    val tag = lastTag
                    if (tag == null) {
                        status = "실패: 태그 없음"
                    } else {
                        val res = client.sendStamp(tag)
                        status = if (res.ok)
                            "성공: $idText 스탬프 적립 요청 전송"
                        else
                            "실패: $idText (SW=${res.sw.toString(16)})"
                    }
                    showDialog = false
                }) { Text("쿠폰 적립(+1)") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("취소") }
            }
        )
    }

}
