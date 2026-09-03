package com.akash.kontactplus.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.akash.kontactplus.feature.favourites.data.local.FavouriteContactDao
import com.akash.kontactplus.feature.favourites.data.local.FavouriteContactEntity
import com.akash.kontactplus.feature.relationship.data.local.*

/**
 * Main database for the KontactPlus application.
 */
@Database(
    entities = [
        FavouriteContactEntity::class,
        ContactRelationshipEntity::class,
        RelationshipTagEntity::class,
        ContactTagCrossRef::class,
        ImportantDateEntity::class,
        RelationshipReminderEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class KontactPlusDatabase : RoomDatabase() {
    abstract fun favouriteContactDao(): FavouriteContactDao
    abstract fun relationshipDao(): RelationshipDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create contact_relationships table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `contact_relationships` (
                        `lookupKey` TEXT NOT NULL, 
                        `privateNote` TEXT NOT NULL, 
                        `createdAtEpochMillis` INTEGER NOT NULL, 
                        `updatedAtEpochMillis` INTEGER NOT NULL, 
                        PRIMARY KEY(`lookupKey`)
                    )
                """.trimIndent())

                // Create relationship_tags table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `relationship_tags` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `name` TEXT NOT NULL, 
                        `normalizedName` TEXT NOT NULL, 
                        `colorKey` TEXT NOT NULL, 
                        `createdAtEpochMillis` INTEGER NOT NULL
                    )
                """.trimIndent())
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_relationship_tags_normalizedName` ON `relationship_tags` (`normalizedName`)")

                // Create contact_tag_cross_ref table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `contact_tag_cross_ref` (
                        `lookupKey` TEXT NOT NULL, 
                        `tagId` INTEGER NOT NULL, 
                        PRIMARY KEY(`lookupKey`, `tagId`), 
                        FOREIGN KEY(`tagId`) REFERENCES `relationship_tags`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE 
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_contact_tag_cross_ref_tagId` ON `contact_tag_cross_ref` (`tagId`)")

                // Create important_dates table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `important_dates` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `lookupKey` TEXT NOT NULL, 
                        `title` TEXT NOT NULL, 
                        `dateEpochDay` INTEGER NOT NULL, 
                        `type` TEXT NOT NULL, 
                        `repeatsYearly` INTEGER NOT NULL, 
                        `createdAtEpochMillis` INTEGER NOT NULL, 
                        `updatedAtEpochMillis` INTEGER NOT NULL
                    )
                """.trimIndent())

                // Create relationship_reminders table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `relationship_reminders` (
                        `id` TEXT NOT NULL, 
                        `lookupKey` TEXT NOT NULL, 
                        `title` TEXT NOT NULL, 
                        `note` TEXT NOT NULL, 
                        `scheduledAtEpochMillis` INTEGER NOT NULL, 
                        `status` TEXT NOT NULL, 
                        `workRequestId` TEXT, 
                        `createdAtEpochMillis` INTEGER NOT NULL, 
                        `updatedAtEpochMillis` INTEGER NOT NULL, 
                        `completedAtEpochMillis` INTEGER, 
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
            }
        }
    }
}
