package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dao.CashConfigDao
import com.example.dao.ExpenseDao
import com.example.dao.JimpitanDao
import com.example.dao.ResidentDao
import com.example.dao.RondaAttendanceDao
import com.example.model.CashConfig
import com.example.model.ExpenseEntry
import com.example.model.JimpitanEntry
import com.example.model.Resident
import com.example.model.RondaAttendance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Resident::class,
        JimpitanEntry::class,
        ExpenseEntry::class,
        CashConfig::class,
        RondaAttendance::class
    ],
    version = 3,
    exportSchema = false
)
abstract class JimpitanDatabase : RoomDatabase() {
    abstract fun residentDao(): ResidentDao
    abstract fun jimpitanDao(): JimpitanDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun cashConfigDao(): CashConfigDao
    abstract fun rondaAttendanceDao(): RondaAttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: JimpitanDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): JimpitanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JimpitanDatabase::class.java,
                    "jimpitan_rt06_kcvri.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        val hasOldResidents = database.residentDao().getResidentByHouseNumber("A-01") != null
                        val count = database.residentDao().getResidentCount()
                        if (hasOldResidents || count < 88) {
                            populateInitialData(database)
                        }
                    }
                }
            }
        }

        suspend fun populateInitialData(database: JimpitanDatabase) {
            val residentDao = database.residentDao()
            val jimpitanDao = database.jimpitanDao()
            val expenseDao = database.expenseDao()
            val cashConfigDao = database.cashConfigDao()

            // Check if old data needs to be cleared
            val hasOldResidents = residentDao.getResidentByHouseNumber("A-01") != null
            val currentCount = residentDao.getResidentCount()
            if (hasOldResidents || currentCount < 88) {
                residentDao.deleteAllResidents()
                jimpitanDao.deleteAllEntries()
            }

            // 1. Initial Cash Balance Config (Saldo Kas Awal 2026)
            cashConfigDao.setConfig(CashConfig("STARTING_BALANCE_2026", "1500000"))
            cashConfigDao.setConfig(CashConfig("COMMUNITY_NAME", "Perumahan KCVRI RT 06 RW 06"))
            cashConfigDao.setConfig(CashConfig("COMMUNITY_SUBTITLE", "Berkoh - Purwokerto Selatan"))
            cashConfigDao.setConfig(CashConfig("DEFAULT_DAILY_AMOUNT", "2000"))

            // 2. Initial List of Residents: Dawis 1, Dawis 2, Dawis 3, Dawis 4 (22 warga per Dawis = 88 warga)
            val sampleResidents = mutableListOf<Resident>()

            // Dawis 1 (22 Warga: DW1-01 s/d DW1-22)
            val dawis1Names = listOf(
                "Bpk. Bambang Wijaya (Ketua RT)" to "Ketua RT 06",
                "Bpk. H. Agus Santoso" to "Sie Pembangunan",
                "Bpk. Sugeng Riyadi" to "Sie Keamanan / Ronda",
                "Bpk. Tri Purwanto" to "Sekretaris",
                "Ibu Hj. Siti Aminah" to "Bendahara / Koord. Dawis 1",
                "Bpk. Joko Priyono" to "Warga Dawis 1",
                "Bpk. Gunawan Wibisono" to "Warga Dawis 1",
                "Ibu Sri Wahyuni" to "Warga Dawis 1",
                "Bpk. Arif Rahman" to "Warga Dawis 1",
                "Bpk. Suryanto" to "Warga Dawis 1",
                "Ibu Endang Susilowati" to "Warga Dawis 1",
                "Bpk. Danang Prasetyo" to "Warga Kontrak Dawis 1",
                "Bpk. Wahid Hasyim" to "Warga Dawis 1",
                "Ibu Nur Hidayah" to "Warga Dawis 1",
                "Bpk. Rudi Hermawan" to "Warga Dawis 1",
                "Bpk. Budi Darmawan" to "Warga Dawis 1",
                "Ibu Tri Lestari" to "Warga Dawis 1",
                "Bpk. Ahmad Fauzi" to "Warga Kontrak Dawis 1",
                "Bpk. Agus Setiawan" to "Warga Dawis 1",
                "Ibu Titik Handayani" to "Warga Dawis 1",
                "Bpk. Sigit Prabowo" to "Warga Dawis 1",
                "Bpk. Yudi Kurniawan" to "Warga Dawis 1"
            )
            dawis1Names.forEachIndexed { index, (name, note) ->
                val num = index + 1
                val houseNo = String.format("DW1-%02d", num)
                val status = if (note.contains("Kontrak")) "Kontrak" else "Tetap"
                sampleResidents.add(
                    Resident(
                        id = num.toLong(),
                        houseNumber = houseNo,
                        residentName = name,
                        phone = String.format("0812345601%02d", num),
                        status = status,
                        defaultAmount = 2000.0,
                        notes = "Dawis 1 - $note"
                    )
                )
            }

            // Dawis 2 (22 Warga: DW2-01 s/d DW2-22)
            val dawis2Names = listOf(
                "Bpk. Dedi Supriyadi" to "Koordinator Ronda",
                "Bpk. Eko Prasetyo" to "Sie Sosial",
                "Bpk. Haryanto" to "Sie Kebersihan",
                "Bpk. Joko Susilo" to "Warga Dawis 2",
                "Ibu Rina Marlina" to "Koord. Dawis 2",
                "Bpk. Supriyono" to "Warga Dawis 2",
                "Bpk. Taufik Hidayat" to "Warga Dawis 2",
                "Ibu Ratna Dewi" to "Warga Dawis 2",
                "Bpk. Hendra Gunawan" to "Warga Dawis 2",
                "Bpk. Sunardi" to "Warga Dawis 2",
                "Ibu Purwanti" to "Warga Dawis 2",
                "Bpk. Didik Subagyo" to "Warga Kontrak Dawis 2",
                "Bpk. Anton Wibowo" to "Warga Dawis 2",
                "Ibu Maya Sari" to "Warga Dawis 2",
                "Bpk. Fajar Ramadhan" to "Warga Dawis 2",
                "Bpk. Slamet Mulyono" to "Warga Dawis 2",
                "Ibu Yuli Astuti" to "Warga Dawis 2",
                "Bpk. Anang Hermansyah" to "Warga Dawis 2",
                "Bpk. Kuswanto" to "Warga Kontrak Dawis 2",
                "Ibu Eni Sumarni" to "Warga Dawis 2",
                "Bpk. Teguh Santoso" to "Warga Dawis 2",
                "Bpk. Bagus Wicaksono" to "Warga Dawis 2"
            )
            dawis2Names.forEachIndexed { index, (name, note) ->
                val num = index + 1
                val id = 22L + num
                val houseNo = String.format("DW2-%02d", num)
                val status = if (note.contains("Kontrak")) "Kontrak" else "Tetap"
                sampleResidents.add(
                    Resident(
                        id = id,
                        houseNumber = houseNo,
                        residentName = name,
                        phone = String.format("0812345602%02d", num),
                        status = status,
                        defaultAmount = 2000.0,
                        notes = "Dawis 2 - $note"
                    )
                )
            }

            // Dawis 3 (22 Warga: DW3-01 s/d DW3-22)
            val dawis3Names = listOf(
                "Bpk. Hendro Siswanto" to "Warga Dawis 3",
                "Bpk. Kusworo" to "Warga Kontrak Dawis 3",
                "Bpk. Muh. Irfan" to "Sie Humas",
                "Bpk. Slamet Raharjo" to "Warga Dawis 3",
                "Bpk. Wahyu Nugroho" to "Sie Kerohanian",
                "Bpk. Hartono" to "Warga Dawis 3",
                "Ibu Sulastri" to "Koord. Dawis 3",
                "Bpk. Purnomo" to "Warga Dawis 3",
                "Bpk. Doni Setiawan" to "Warga Dawis 3",
                "Ibu Wulandari" to "Warga Dawis 3",
                "Bpk. Rohmad" to "Warga Dawis 3",
                "Bpk. Agus Triono" to "Warga Dawis 3",
                "Ibu Indah Permata" to "Warga Dawis 3",
                "Bpk. Harianto" to "Warga Kontrak Dawis 3",
                "Bpk. Tegar Pratama" to "Warga Dawis 3",
                "Ibu Sri Mulyani" to "Warga Dawis 3",
                "Bpk. Edi Suwarno" to "Warga Dawis 3",
                "Bpk. Nanang Kosim" to "Warga Dawis 3",
                "Ibu Dewi Sartika" to "Warga Dawis 3",
                "Bpk. Bayu Aji" to "Warga Dawis 3",
                "Bpk. Lukman Hakim" to "Warga Kontrak Dawis 3",
                "Bpk. Heru Cahyono" to "Warga Dawis 3"
            )
            dawis3Names.forEachIndexed { index, (name, note) ->
                val num = index + 1
                val id = 44L + num
                val houseNo = String.format("DW3-%02d", num)
                val status = if (note.contains("Kontrak")) "Kontrak" else "Tetap"
                sampleResidents.add(
                    Resident(
                        id = id,
                        houseNumber = houseNo,
                        residentName = name,
                        phone = String.format("0812345603%02d", num),
                        status = status,
                        defaultAmount = 2000.0,
                        notes = "Dawis 3 - $note"
                    )
                )
            }

            // Dawis 4 (22 Warga: DW4-01 s/d DW4-22)
            val dawis4Names = listOf(
                "Bpk. Suwardi" to "Sie Kepemudaan",
                "Bpk. Sukirno" to "Warga Dawis 4",
                "Ibu Suparmi" to "Koord. Dawis 4",
                "Bpk. Dimas Saputra" to "Warga Dawis 4",
                "Bpk. Kuncoro" to "Warga Dawis 4",
                "Ibu Siti Maryam" to "Warga Dawis 4",
                "Bpk. Bambang Sutrisno" to "Warga Dawis 4",
                "Bpk. Ari Wibowo" to "Warga Dawis 4",
                "Ibu Yayuk Sri" to "Warga Dawis 4",
                "Bpk. Danuarta" to "Warga Kontrak Dawis 4",
                "Bpk. Ahmad Basuki" to "Warga Dawis 4",
                "Ibu Rini Setyowati" to "Warga Dawis 4",
                "Bpk. Cipto Raharjo" to "Warga Dawis 4",
                "Bpk. Gilang Ramadhan" to "Warga Dawis 4",
                "Ibu Ningsih" to "Warga Dawis 4",
                "Bpk. Wisnu Broto" to "Warga Dawis 4",
                "Bpk. Untung Slamet" to "Warga Dawis 4",
                "Ibu Kartini" to "Warga Dawis 4",
                "Bpk. Panji Asmoro" to "Warga Kontrak Dawis 4",
                "Bpk. Wahyudi" to "Warga Dawis 4",
                "Ibu Tutik Alawiyah" to "Warga Dawis 4",
                "Bpk. Zulfikar" to "Warga Dawis 4"
            )
            dawis4Names.forEachIndexed { index, (name, note) ->
                val num = index + 1
                val id = 66L + num
                val houseNo = String.format("DW4-%02d", num)
                val status = if (note.contains("Kontrak")) "Kontrak" else "Tetap"
                sampleResidents.add(
                    Resident(
                        id = id,
                        houseNumber = houseNo,
                        residentName = name,
                        phone = String.format("0812345604%02d", num),
                        status = status,
                        defaultAmount = 2000.0,
                        notes = "Dawis 4 - $note"
                    )
                )
            }

            residentDao.insertResidents(sampleResidents)

            // 3. Historical Monthly & Daily Jimpitan Entries for 2026
            val sampleJimpitan = mutableListOf<JimpitanEntry>()
            
            // Populate past months (Jan - Aug 2026) for cumulative balance demonstration
            val pastMonths = listOf(
                "2026-01" to 25,
                "2026-02" to 24,
                "2026-03" to 26,
                "2026-04" to 25,
                "2026-05" to 27,
                "2026-06" to 25,
                "2026-07" to 28,
                "2026-08" to 28
            )

            // Sample monthly batch contributions per month
            for ((monthStr, activeDays) in pastMonths) {
                for (r in sampleResidents) {
                    val amount = activeDays * r.defaultAmount
                    sampleJimpitan.add(
                        JimpitanEntry(
                            residentId = r.id,
                            houseNumber = r.houseNumber,
                            residentName = r.residentName,
                            date = "$monthStr-28",
                            amount = amount,
                            collectorName = "Petugas Ronda RT 06",
                            notes = "Setoran jimpitan $activeDays hari"
                        )
                    )
                }
            }

            // Current month (September 2026) daily collections
            val sepDays = listOf("2026-09-01")
            for (day in sepDays) {
                for (r in sampleResidents) {
                    sampleJimpitan.add(
                        JimpitanEntry(
                            residentId = r.id,
                            houseNumber = r.houseNumber,
                            residentName = r.residentName,
                            date = day,
                            amount = r.defaultAmount,
                            collectorName = "Regu Ronda Malam",
                            notes = "Jimpitan koin harian"
                        )
                    )
                }
            }

            jimpitanDao.insertEntries(sampleJimpitan)

            // 4. Sample RT Expenses across 2026 (Pengeluaran Jimpitan RT 06 Berkoh)
            if (expenseDao.getExpenseCount() == 0) {
                val sampleExpenses = listOf(
                    ExpenseEntry(
                        date = "2026-01-15",
                        category = "Ronda & Keamanan",
                        amount = 150000.0,
                        description = "Pembelian senter patroli & baterai pos ronda",
                        receiptNotes = "Sie Keamanan (Bpk. Sugeng)"
                    ),
                    ExpenseEntry(
                        date = "2026-02-20",
                        category = "Kebersihan & Lingkungan",
                        amount = 200000.0,
                        description = "Konsumsi & sewa alat kerja bakti saluran air Dawis 1-4",
                        receiptNotes = "Sie Kebersihan (Bpk. Haryanto)"
                    ),
                    ExpenseEntry(
                        date = "2026-03-10",
                        category = "Sarana & Prasarana",
                        amount = 275000.0,
                        description = "Penggantian 4 unit lampu penerangan jalan gang RT 06",
                        receiptNotes = "Bpk. Hendro Siswanto"
                    ),
                    ExpenseEntry(
                        date = "2026-04-12",
                        category = "Sosial & Kematian",
                        amount = 300000.0,
                        description = "Tali asih & santunan warga rawat inap RS Margono",
                        receiptNotes = "Sie Sosial (Bpk. Eko)"
                    ),
                    ExpenseEntry(
                        date = "2026-05-22",
                        category = "Konsumsi Pertemuan",
                        amount = 175000.0,
                        description = "Snack & konsumsi pertemuan rutin warga triwulan II",
                        receiptNotes = "Ibu Siti Aminah (Bendahara)"
                    ),
                    ExpenseEntry(
                        date = "2026-06-18",
                        category = "Ronda & Keamanan",
                        amount = 120000.0,
                        description = "Pengadaan kopi, gula & teh inventaris pos ronda",
                        receiptNotes = "Sie Ronda (Bpk. Dedi)"
                    ),
                    ExpenseEntry(
                        date = "2026-07-25",
                        category = "Kebersihan & Lingkungan",
                        amount = 250000.0,
                        description = "Pembersihan rumput liar fasum & pemangkasan dahan pohon",
                        receiptNotes = "Sie Lingkungan"
                    ),
                    ExpenseEntry(
                        date = "2026-08-17",
                        category = "Kegiatan RT",
                        amount = 650000.0,
                        description = "Subsidi hadiah lomba anak & tasyakuran HUT RI ke-81",
                        receiptNotes = "Panitia Agustusan RT 06"
                    ),
                    ExpenseEntry(
                        date = "2026-09-01",
                        category = "Ronda & Keamanan",
                        amount = 85000.0,
                        description = "Isi ulang minyak & perawatan lonceng ronda",
                        receiptNotes = "Petugas Ronda"
                    )
                )
                expenseDao.insertExpenses(sampleExpenses)
            }

            // 5. Initial Ronda Attendance & Accumulated Fines (Denda Rp 20.000 / minggu jika tidak hadir)
            val rondaAttendanceDao = database.rondaAttendanceDao()
            if (rondaAttendanceDao.getCount() == 0) {
                val sampleRonda = mutableListOf<RondaAttendance>()

                // Minggu ke-34 (2026-08-22)
                sampleRonda.add(
                    RondaAttendance(
                        residentId = 4L,
                        houseNumber = "DW1-04",
                        residentName = "Bpk. Tri Purwanto",
                        date = "2026-08-22",
                        weekOfYear = 34,
                        year = 2026,
                        status = "TIDAK_HADIR",
                        fineAmount = 20000.0,
                        isFinePaid = false,
                        notes = "Tidak hadir ronda jadwal Sabtu malam",
                        recordedBy = "Sie Keamanan"
                    )
                )
                sampleRonda.add(
                    RondaAttendance(
                        residentId = 34L,
                        houseNumber = "DW2-12",
                        residentName = "Bpk. Didik Subagyo",
                        date = "2026-08-22",
                        weekOfYear = 34,
                        year = 2026,
                        status = "TIDAK_HADIR",
                        fineAmount = 20000.0,
                        isFinePaid = false,
                        notes = "Tidak hadir ronda",
                        recordedBy = "Sie Keamanan"
                    )
                )
                sampleRonda.add(
                    RondaAttendance(
                        residentId = 1L,
                        houseNumber = "DW1-01",
                        residentName = "Bpk. Bambang Wijaya (Ketua RT)",
                        date = "2026-08-22",
                        weekOfYear = 34,
                        year = 2026,
                        status = "HADIR",
                        fineAmount = 0.0,
                        isFinePaid = false,
                        notes = "Hadir ronda malam",
                        recordedBy = "Sie Keamanan"
                    )
                )

                // Minggu ke-35 (2026-08-29) - Terakumulasi denda bila belum dibayarkan!
                sampleRonda.add(
                    RondaAttendance(
                        residentId = 4L,
                        houseNumber = "DW1-04",
                        residentName = "Bpk. Tri Purwanto",
                        date = "2026-08-29",
                        weekOfYear = 35,
                        year = 2026,
                        status = "TIDAK_HADIR",
                        fineAmount = 20000.0,
                        isFinePaid = false,
                        notes = "Tidak hadir ronda (Terakumulasi 2 minggu)",
                        recordedBy = "Sie Keamanan"
                    )
                )
                sampleRonda.add(
                    RondaAttendance(
                        residentId = 46L,
                        houseNumber = "DW3-02",
                        residentName = "Bpk. Kusworo",
                        date = "2026-08-29",
                        weekOfYear = 35,
                        year = 2026,
                        status = "TIDAK_HADIR",
                        fineAmount = 20000.0,
                        isFinePaid = true,
                        finePaidDate = "2026-08-30",
                        notes = "Denda Rp 20.000 telah dilunasi",
                        recordedBy = "Sie Keamanan"
                    )
                )
                sampleRonda.add(
                    RondaAttendance(
                        residentId = 76L,
                        houseNumber = "DW4-10",
                        residentName = "Bpk. Danuarta",
                        date = "2026-08-29",
                        weekOfYear = 35,
                        year = 2026,
                        status = "TIDAK_HADIR",
                        fineAmount = 20000.0,
                        isFinePaid = false,
                        notes = "Tidak hadir ronda malam",
                        recordedBy = "Sie Keamanan"
                    )
                )

                // Minggu ke-36 (2026-09-01) - Tanggal aktif saat ini
                sampleRonda.add(
                    RondaAttendance(
                        residentId = 1L,
                        houseNumber = "DW1-01",
                        residentName = "Bpk. Bambang Wijaya (Ketua RT)",
                        date = "2026-09-01",
                        weekOfYear = 36,
                        year = 2026,
                        status = "HADIR",
                        fineAmount = 0.0,
                        isFinePaid = false,
                        notes = "Hadir pos ronda",
                        recordedBy = "Petugas Ronda"
                    )
                )
                sampleRonda.add(
                    RondaAttendance(
                        residentId = 3L,
                        houseNumber = "DW1-03",
                        residentName = "Bpk. Sugeng Riyadi",
                        date = "2026-09-01",
                        weekOfYear = 36,
                        year = 2026,
                        status = "HADIR",
                        fineAmount = 0.0,
                        isFinePaid = false,
                        notes = "Koordinator ronda hadir",
                        recordedBy = "Petugas Ronda"
                    )
                )
                sampleRonda.add(
                    RondaAttendance(
                        residentId = 12L,
                        houseNumber = "DW1-12",
                        residentName = "Bpk. Danang Prasetyo",
                        date = "2026-09-01",
                        weekOfYear = 36,
                        year = 2026,
                        status = "TIDAK_HADIR",
                        fineAmount = 20000.0,
                        isFinePaid = false,
                        notes = "Alpa ronda malam ini",
                        recordedBy = "Petugas Ronda"
                    )
                )
                sampleRonda.add(
                    RondaAttendance(
                        residentId = 6L,
                        houseNumber = "DW1-06",
                        residentName = "Bpk. Joko Priyono",
                        date = "2026-09-01",
                        weekOfYear = 36,
                        year = 2026,
                        status = "IZIN",
                        fineAmount = 0.0,
                        isFinePaid = false,
                        notes = "Izin sakit / ada kepentingan keluarga",
                        recordedBy = "Petugas Ronda"
                    )
                )

                rondaAttendanceDao.insertBatch(sampleRonda)
            }
        }
    }
}
