package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ModerationDao
import com.example.data.local.dao.PostDao
import com.example.data.local.dao.ReactionDao
import com.example.data.local.dao.RoastDao
import com.example.data.local.dao.SocialDao
import com.example.data.local.dao.UserProfileDao
import com.example.data.local.entity.BadgeEntity
import com.example.data.local.entity.BannedTermEntity
import com.example.data.local.entity.BlockMuteEntity
import com.example.data.local.entity.FollowEntity
import com.example.data.local.entity.ModerationAuditEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.ReactionEntity
import com.example.data.local.entity.RemovalRequestEntity
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.RoastEntity
import com.example.data.local.entity.SaveEntity
import com.example.data.local.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        PostEntity::class,
        RoastEntity::class,
        ReactionEntity::class,
        FollowEntity::class,
        BlockMuteEntity::class,
        SaveEntity::class,
        NotificationEntity::class,
        ReportEntity::class,
        RemovalRequestEntity::class,
        ModerationAuditEntity::class,
        BannedTermEntity::class,
        BadgeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun postDao(): PostDao
    abstract fun roastDao(): RoastDao
    abstract fun reactionDao(): ReactionDao
    abstract fun socialDao(): SocialDao
    abstract fun moderationDao(): ModerationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "exroast_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
