package com.example.smsencrypted.Data

object Data  {
    lateinit var keys: LongArray
    /*
    *     e = 0
    *     n = 1
    *     d = 2
    *     p = 3
    *     q = 4
    *     dP = 5
    *     dQ = 6
    *     qInv = 7
    * */
    lateinit var users: Set<User>
}