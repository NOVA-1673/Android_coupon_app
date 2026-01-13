package com.ex.nfcoupon.feature.Utility

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import java.io.IOException

class OwnerNfcClient(
    private val aidHex: String // apduservice.xml에 등록한 AID
) {
    data class Result(val ok: Boolean, val payload: ByteArray?, val sw: Int)

    fun connectAndSelect(tag: Tag): Result {
        val iso = IsoDep.get(tag) ?: return Result(false, null, 0)
        return try {
            iso.connect()
            val select = buildSelectAid(aidHex)
            val resp = iso.transceive(select)
            val sw = parseSw(resp)
            iso.close()
            Result(sw == 0x9000, resp.dropLast(2).toByteArray(), sw)
        } catch (e: IOException) {
            try { iso.close() } catch (_: Exception) {}
            Result(false, null, 0)
        }
    }

    fun sendStamp(tag: Tag): Result {
        val iso = IsoDep.get(tag) ?: return Result(false, null, 0)
        return try {
            iso.connect()
            // 1) SELECT 먼저
            val respSel = iso.transceive(buildSelectAid(aidHex))
            if (parseSw(respSel) != 0x9000) {
                iso.close()
                return Result(false, respSel.dropLast(2).toByteArray(), parseSw(respSel))
            }
            // 2) STAMP 커맨드 전송
            val stamp = byteArrayOf(0x80.toByte(), 0x10.toByte(), 0x00, 0x00)
            val resp = iso.transceive(stamp)
            val sw = parseSw(resp)
            iso.close()
            Result(sw == 0x9000, resp.dropLast(2).toByteArray(), sw)
        } catch (e: IOException) {
            try { iso.close() } catch (_: Exception) {}
            Result(false, null, 0)
        }
    }

    fun readCustomerId(tag: Tag): Result {

        val iso = IsoDep.get(tag) ?: return Result(false, null, 0)
        return try {
            iso.connect()

            val respSel = iso.transceive(buildSelectAid(aidHex))
            if (parseSw(respSel) != 0x9000) {
                iso.close()
                return Result(false, respSel.dropLast(2).toByteArray(), parseSw(respSel))
            }

            // ✅ READ 커맨드: 80 20 00 00
            val read = byteArrayOf(0x80.toByte(), 0x20.toByte(), 0x00, 0x00)
            val resp = iso.transceive(read)
            val sw = parseSw(resp)

            iso.close()
            Result(sw == 0x9000, resp.dropLast(2).toByteArray(), sw)
        } catch (e: IOException) {
            try { iso.close() } catch (_: Exception) {}
            Result(false, null, 0)
        }
    }

    private fun buildSelectAid(aidHex: String): ByteArray {
        val aid = hexToBytes(aidHex)
        // 00 A4 04 00 Lc [AID] 00
        val header = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, aid.size.toByte())
        val le = byteArrayOf(0x00)
        return header + aid + le
    }

    private fun parseSw(resp: ByteArray): Int {
        if (resp.size < 2) return 0
        val sw1 = resp[resp.size - 2].toInt() and 0xFF
        val sw2 = resp[resp.size - 1].toInt() and 0xFF
        return (sw1 shl 8) or sw2
    }

    private fun hexToBytes(s: String): ByteArray {
        val clean = s.replace(" ", "")
        return ByteArray(clean.length / 2) { i ->
            clean.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
    }
}