package com.ex.nfcoupon.feature.Data

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import com.ex.nfcoupon.feature.Utility.NfcNotification
import java.nio.charset.StandardCharsets

class CouponHceService : HostApduService() {

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        Log.d("HCE", "APDU <- ${commandApdu.toHex()}")

        // 1) 손님 화면이 열려있지 않으면 거부
        /*if (!FlagStore.isReady()) {
            Log.d("HCE", "Not ready -> reject")
            return SW_CONDITIONS_NOT_SATISFIED // 0x6985
        }*/

        // 2) 최소: SELECT AID 처리 (리더가 처음 보내는 경우가 많음)
        if (isSelectAid(commandApdu)) {
            Log.d("HCE", "SELECT AID -> OK")
            return SW_OK
        }

        // ✅ 테스트: STAMP 커맨드 수신 시 알림 띄우기
        if (isStampCommand(commandApdu)) {
            NfcNotification.show(
                applicationContext,
                "NFCoupon",
                "사장님이 스탬프 적립을 요청했어요! (+1) (테스트)"
            )
            return "STAMP_OK".toByteArray(StandardCharsets.UTF_8) + SW_OK
        }

        // ✅ READ: 손님ID 응답
        if (isReadCommand(commandApdu)) {
            val payload = userIdentity.CUSTOMER_ID.toByteArray(Charsets.UTF_8)
            return payload + SW_OK
        }

        // ✅ STAMP: 알림 테스트
        if (isStampCommand(commandApdu)) {
            NfcNotification.show(applicationContext, "NFCoupon", "사장님이 스탬프 적립을 요청했어요! (+1) (테스트)")
            return "STAMP_OK".toByteArray(Charsets.UTF_8) + SW_OK
        }

        // 3) (임시) 나머지 커맨드는 그냥 "OK" + payload 예시
        // 나중에 READ/COMMIT 커맨드로 확장하면 됨.
        val payload = "READY_OK".toByteArray(StandardCharsets.UTF_8)
        return payload + SW_OK
    }

    override fun onDeactivated(reason: Int) {
        Log.d("HCE", "Deactivated. reason=$reason")
    }

    private fun isSelectAid(apdu: ByteArray): Boolean {
        // SELECT by AID: 00 A4 04 00 [Lc] [AID...] 00
        if (apdu.size < 4) return false
        return apdu[0] == 0x00.toByte() &&
                apdu[1] == 0xA4.toByte() &&
                apdu[2] == 0x04.toByte() &&
                apdu[3] == 0x00.toByte()
    }

    /**
     * ✅ 매우 단순한 커맨드 정의(테스트용)
     * CLA=0x80, INS=0x10, P1=0x00, P2=0x00, Lc 없음
     * => 80 10 00 00
     */
    private fun isStampCommand(apdu: ByteArray): Boolean {
        return apdu.size >= 4 &&
                apdu[0] == 0x80.toByte() &&
                apdu[1] == 0x10.toByte() &&
                apdu[2] == 0x00.toByte() &&
                apdu[3] == 0x00.toByte()
    }



    // CouponHceService.kt (추가/변경 부분만)
    private fun isReadCommand(apdu: ByteArray): Boolean {
        // 80 20 00 00
        return apdu.size >= 4 &&
                apdu[0] == 0x80.toByte() &&
                apdu[1] == 0x20.toByte() &&
                apdu[2] == 0x00.toByte() &&
                apdu[3] == 0x00.toByte()
    }

    private val SW_OK = byteArrayOf(0x90.toByte(), 0x00.toByte())
    private val SW_CONDITIONS_NOT_SATISFIED = byteArrayOf(0x69.toByte(), 0x85.toByte())


}

// Hex util
private fun ByteArray.toHex(): String =
    joinToString(" ") { "%02X".format(it) }