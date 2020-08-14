package eu.zkkn.android.disruptions.data

import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import eu.zkkn.android.disruptions.utils.RoomTypeConverters


@Database(entities = [Subscription::class, Disruption::class], version = 2)
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun disruptionDao(): DisruptionDao

    companion object {

        private val TAG = AppDatabase::class.simpleName

        @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
        internal val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""CREATE TABLE `disruption` (
                    |`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    |`guid` TEXT NOT NULL,
                    |`received` INTEGER NOT NULL,
                    |`line_names` TEXT NOT NULL,
                    |`title` TEXT NOT NULL,
                    |`time_info` TEXT NOT NULL);""".trimMargin())
                database.execSQL("CREATE UNIQUE INDEX `index_disruption_guid` ON `disruption` (`guid`);")
                database.execSQL("CREATE INDEX `index_disruption_received` ON `disruption` (`received`);")
            }
        }


        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }

            }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_database.db")
                .addMigrations(MIGRATION_1_2)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.d(TAG, "Create App Database (with Room)")
                    }
                })
                .build()
        }

    }

}
