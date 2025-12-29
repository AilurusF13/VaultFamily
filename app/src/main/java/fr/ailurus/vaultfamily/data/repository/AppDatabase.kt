package fr.ailurus.vaultfamily.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.ailurus.vaultfamily.data.dao.EntryDao
import fr.ailurus.vaultfamily.data.model.*
import fr.ailurus.vaultfamily.data.dao.*
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Database(
    entities = [
        Entry::class,
        EntrySecret::class,
        Group::class,
        GroupSecret::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao
    abstract fun entrySecretDao(): EntrySecretDao
    abstract fun groupDao(): GroupDao
    abstract fun groupSecretDao(): GroupSecretDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, passphrase: ByteArray): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val factory = SupportOpenHelperFactory(passphrase)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "secure-vaultfamily-db"
                )
                    .openHelperFactory(factory)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("INSERT INTO groups (groupName) VALUES ('Self')")
                            db.execSQL("INSERT INTO groupsecret (groupId, groupKey) VALUES (1, 'key-self')")
                            // TODO inserer les données liées au group self
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun clearInstance() {
            synchronized(this) {
                INSTANCE?.close() // On ferme la connexion proprement
                INSTANCE = null   // On efface la référence pour forcer une recréation
            }
        }
    }
}