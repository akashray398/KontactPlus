package com.akash.kontactplus.core.demo

import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.recents.domain.model.RecentCall
import com.akash.kontactplus.feature.recents.domain.model.RecentCallType

object DemoData {
    val contacts = listOf(
        Contact(1, "aarav", "Aarav Sharma", listOf("+91 ••••• ••101")),
        Contact(2, "meera", "Meera Kapoor", listOf("+91 ••••• ••202")),
        Contact(3, "rohan", "Rohan Verma", listOf("+91 ••••• ••303")),
        Contact(4, "priya", "Priya Singh", listOf("+91 ••••• ••404"))
    )

    val recentCalls = listOf(
        RecentCall(1, "+91 ••••• ••101", "Aarav Sharma", "aarav", "Aarav Sharma", RecentCallType.Incoming, System.currentTimeMillis() - 3600000, 120, false),
        RecentCall(2, "+91 ••••• ••202", "Meera Kapoor", "meera", "Meera Kapoor", RecentCallType.Outgoing, System.currentTimeMillis() - 7200000, 45, false),
        RecentCall(3, "+91 ••••• ••505", "Unknown", null, null, RecentCallType.Missed, System.currentTimeMillis() - 86400000, 0, true)
    )
}
