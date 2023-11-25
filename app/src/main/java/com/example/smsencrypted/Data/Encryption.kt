package com.example.smsencrypted.Data
import java.math.BigInteger
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import kotlin.math.sqrt

class Encryption {
    companion object {
        fun generateKeys(): LongArray {
            var p = 0L
            var q = 0L
            while (p == 0L || q == 0L) {
                val randomNum = 1009 + (Math.random() * 8964).toInt()
                if (isPrime(randomNum.toLong())) {
                    if (p == 0L) {
                        p = randomNum.toLong()
                    } else {
                        q = randomNum.toLong()
                    }
                }
            }

            val n = p * q
            val totientFunction = n - (p + q - 1)
            val e = 5483L
            val d = modinv(e, totientFunction)
            val dP = modinv(e, (p - 1))
            val dQ = modinv(e, q - 1)
            val qInv = modinv(q, p)

            return longArrayOf(e, n, d, p, q, dP, dQ, qInv)
        }
        private fun isPrime(num: Long): Boolean {
            if (num <= 1) return false
            if (num == 2L) return true
            for (i in 2..sqrt(num.toDouble()).toInt()) {
                if (num % i == 0L) return false
            }
            return true
        }

        private fun egcd(a: Long, b: Long): Egcd {
            val newegcd = Egcd()
            return if (a == 0L) {
                newegcd.a = b
                newegcd.b = 0
                newegcd.c = 1
                newegcd
            } else {
                val negcd = egcd((b % a), a)
                val x = negcd.c
                val y = negcd.b
                negcd.c = y
                negcd.b = (x - (b / a) * y)
                negcd
            }
        }

        private fun modinv(a: Long, m: Long): Long {
            val newegcd = egcd(a, m)
            return if (newegcd.a != 1L) {
                0
            } else {
                ((m + newegcd.b) % m) % m
            }
        }

        private fun power(a: Long, b: Long): BigInteger {
            val B = b.toInt()
            var result = BigInteger("1")
            result = result.multiply(BigInteger.valueOf(a))
            result = result.pow(B)
            return result
        }

        private fun encryptDecrypt(a: Long, b: Long, n: Long): Long {
            var result = power(a, b)
            val bigN = BigInteger.valueOf(n)
            result = result.remainder(bigN)
            return result.toLong()
        }
        private fun decryptStandard(cipher: LongArray, d: Long, n: Long): String {
            val messageLength = cipher.size
            val decMessage = CharArray(messageLength)

            for (j in 0 until messageLength) {
                val m = encryptDecrypt(cipher[j], d, n).toInt()
                decMessage[j] = m.toChar()
            }

            return String(decMessage)
        }
        fun encrypt(message: String, e: Long, n: Long): LongArray {
            val messageLength = message.length
            val cipher = LongArray(messageLength)

            for (j in 0 until messageLength) {
                val character = message[j]
                val messageArrayInt = character.code.toLong()
                cipher[j] = encryptDecrypt(messageArrayInt, e, n)
            }

            return cipher
        }

        private fun decrypt(cipher: LongArray, dP: Long, dQ: Long, qInv: Long, p: Long, q: Long): String {
            val messageLength = cipher.size
            val decMessage = CharArray(messageLength)

            for (j in 0 until messageLength) {
                val m1 = encryptDecrypt(cipher[j], dP, p)
                val m2 = encryptDecrypt(cipher[j], dQ, q)
                val h = (qInv * (m1 - m2)) % p
                val m = (m2 + h * q).toInt()
                decMessage[j] = m.toChar()
            }

            return String(decMessage)
        }


    }
    fun example() {
        val keys = generateKeys()
        val e = keys[0]
        val n = keys[1]
        val d = keys[2]
        val p = keys[3]
        val q = keys[4]
        val dP = keys[5]
        val dQ = keys[6]
        val qInv = keys[7]

        println("\nPublic key(e, n) = ($e, $n)")
        println("\nPrivate key(p, q, dP, dQ, qInv) = ($p, $q, $dP, $dQ, $qInv)")
        print("\nEnter Message m: ")
        val message = readLine() ?: ""
        val cipher = encrypt(message, e, n)

        print("\nEncrypted Message: ")
        //cipher.forEach { print("$it ") }

        val finalMessage = decrypt(cipher, dP, dQ, qInv, p, q)
        println("\nFinal Message CRT: $finalMessage")
        val finalMessageStandard = decryptStandard(cipher, d, n)
        println("\nFinal Message Standard: $finalMessageStandard")
    }






    class Egcd {
        var a: Long = 0
        var b: Long = 0
        var c: Long = 0
    }
}