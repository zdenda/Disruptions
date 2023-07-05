package eu.zkkn.android.disruptions.data

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


private const val TEST_DB = "room_db_migration_test.db"


@RunWith(AndroidJUnit4::class)
class RoomDbMigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )


    @Test
    @Throws(IOException::class)
    fun migrateFrom1To2() {
        var db = helper.createDatabase(TEST_DB, 1).apply {

            assertEquals(1, this.version)

            // db has schema version 1. insert some data using SQL queries.
            // You cannot use DAO classes because they expect the latest schema.
            //execSQL(...)

            // Prepare for the next version.
            close()
        }
        assertFalse("DB connection should be closed", db.isOpen)

        // Re-open the database with version 2 and provide
        // AppDatabase.MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, AppDatabase.MIGRATION_1_2).apply {
            assertEquals(2, this.version)
            assertFalse(needUpgrade(2))

            close()
        }
        assertFalse("DB connection should be closed", db.isOpen)

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.

        // Version 2 just created a new table, the original data wasn't changed in any way,
        // so scheme verification should be enough
    }

}
