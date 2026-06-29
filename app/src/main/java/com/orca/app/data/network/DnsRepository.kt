package com.orca.app.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.xbill.DNS.Lookup
import org.xbill.DNS.PTRRecord
import org.xbill.DNS.Record
import org.xbill.DNS.ReverseMap
import org.xbill.DNS.Type
import javax.inject.Inject
import javax.inject.Singleton

enum class DnsRecordType(val label: String, val typeCode: Int) {
    A("A", Type.A),
    AAAA("AAAA", Type.AAAA),
    MX("MX", Type.MX),
    TXT("TXT", Type.TXT),
    CNAME("CNAME", Type.CNAME),
    NS("NS", Type.NS),
    SOA("SOA", Type.SOA),
    PTR("PTR", Type.PTR),
}

data class DnsRecord(
    val type: String,
    val value: String,
    val ttl: Long?,
)

data class DnsResult(
    val host: String,
    val recordType: String,
    val records: List<DnsRecord>,
)

@Singleton
class DnsRepository @Inject constructor() {

    suspend fun lookup(host: String, recordType: DnsRecordType): Result<DnsResult> = withContext(Dispatchers.IO) {
        val trimmed = host.trim()
        if (trimmed.isBlank()) return@withContext Result.failure(IllegalArgumentException("Enter a hostname or IP"))

        try {
            val queryName = if (recordType == DnsRecordType.PTR) toReverseName(trimmed) else trimmed
            val lookup = Lookup(queryName, recordType.typeCode)
            lookup.run()

            if (lookup.result != Lookup.SUCCESSFUL) {
                val error = lookup.errorString ?: "No records found"
                return@withContext Result.failure(Exception(error))
            }

            val records = lookup.answers?.map { record -> record.toDnsRecord() } ?: emptyList()
            if (records.isEmpty()) {
                return@withContext Result.failure(Exception("No ${recordType.label} records found"))
            }

            Result.success(
                DnsResult(
                    host = trimmed,
                    recordType = recordType.label,
                    records = records,
                ),
            )
        } catch (e: Exception) {
            Result.failure(Exception("DNS lookup failed: ${e.message ?: "Unknown error"}"))
        }
    }

    private fun toReverseName(input: String): String {
        if (input.contains(".in-addr.arpa") || input.contains(".ip6.arpa")) return input
        return ReverseMap.fromAddress(input).toString()
    }

    private fun Record.toDnsRecord(): DnsRecord {
        val typeName = Type.string(type)
        val value = when {
            type == Type.PTR -> (this as PTRRecord).target.toString()
            rdataToString().isNotBlank() -> rdataToString()
            else -> toString()
        }
        return DnsRecord(type = typeName, value = value.trimEnd('.'), ttl = ttl)
    }
}
