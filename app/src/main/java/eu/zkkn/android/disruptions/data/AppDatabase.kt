package eu.zkkn.android.disruptions.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import eu.zkkn.android.disruptions.utils.ioThread


@Database(entities = [Subscription::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subscriptionDao(): SubscriptionDao

    companion object {

        private val TAG = AppDatabase::class.simpleName

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }

            }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_database.db")
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.d(TAG, "Create App Database (with Room)")
                        ioThread {
                            val subscriptionDao = getInstance(context).subscriptionDao()
                            for (topic in Preferences.getTopics(context)) {
                                subscriptionDao.insert(Subscription(0, topic.toUpperCase()))
                            }
                        }
                    }
                })
                .build()
        }

    }

}
