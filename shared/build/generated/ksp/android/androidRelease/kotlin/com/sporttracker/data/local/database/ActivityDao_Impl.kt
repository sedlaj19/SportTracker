package com.sporttracker.`data`.local.database

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ActivityDao_Impl(
  __db: RoomDatabase,
) : ActivityDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfActivityEntity: EntityInsertAdapter<ActivityEntity>

  private val __updateAdapterOfActivityEntity: EntityDeleteOrUpdateAdapter<ActivityEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfActivityEntity = object : EntityInsertAdapter<ActivityEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `activities` (`id`,`name`,`location`,`durationMinutes`,`storageType`,`createdAt`,`lastModified`,`syncStatus`,`isDeleted`,`userId`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ActivityEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.location)
        statement.bindLong(4, entity.durationMinutes.toLong())
        statement.bindText(5, entity.storageType)
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.lastModified)
        statement.bindText(8, entity.syncStatus)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        val _tmpUserId: String? = entity.userId
        if (_tmpUserId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpUserId)
        }
      }
    }
    this.__updateAdapterOfActivityEntity = object : EntityDeleteOrUpdateAdapter<ActivityEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `activities` SET `id` = ?,`name` = ?,`location` = ?,`durationMinutes` = ?,`storageType` = ?,`createdAt` = ?,`lastModified` = ?,`syncStatus` = ?,`isDeleted` = ?,`userId` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ActivityEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.location)
        statement.bindLong(4, entity.durationMinutes.toLong())
        statement.bindText(5, entity.storageType)
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.lastModified)
        statement.bindText(8, entity.syncStatus)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        val _tmpUserId: String? = entity.userId
        if (_tmpUserId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpUserId)
        }
        statement.bindText(11, entity.id)
      }
    }
  }

  public override suspend fun insert(activity: ActivityEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfActivityEntity.insert(_connection, activity)
  }

  public override suspend fun update(activity: ActivityEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfActivityEntity.handle(_connection, activity)
  }

  public override fun observeAll(): Flow<List<ActivityEntity>> {
    val _sql: String = "SELECT * FROM activities WHERE isDeleted = 0 ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("activities")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfDurationMinutes: Int = getColumnIndexOrThrow(_stmt, "durationMinutes")
        val _columnIndexOfStorageType: Int = getColumnIndexOrThrow(_stmt, "storageType")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfLastModified: Int = getColumnIndexOrThrow(_stmt, "lastModified")
        val _columnIndexOfSyncStatus: Int = getColumnIndexOrThrow(_stmt, "syncStatus")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _result: MutableList<ActivityEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ActivityEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpDurationMinutes: Int
          _tmpDurationMinutes = _stmt.getLong(_columnIndexOfDurationMinutes).toInt()
          val _tmpStorageType: String
          _tmpStorageType = _stmt.getText(_columnIndexOfStorageType)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpLastModified: Long
          _tmpLastModified = _stmt.getLong(_columnIndexOfLastModified)
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpUserId: String?
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          }
          _item = ActivityEntity(_tmpId,_tmpName,_tmpLocation,_tmpDurationMinutes,_tmpStorageType,_tmpCreatedAt,_tmpLastModified,_tmpSyncStatus,_tmpIsDeleted,_tmpUserId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAll(): List<ActivityEntity> {
    val _sql: String = "SELECT * FROM activities WHERE isDeleted = 0 ORDER BY createdAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfDurationMinutes: Int = getColumnIndexOrThrow(_stmt, "durationMinutes")
        val _columnIndexOfStorageType: Int = getColumnIndexOrThrow(_stmt, "storageType")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfLastModified: Int = getColumnIndexOrThrow(_stmt, "lastModified")
        val _columnIndexOfSyncStatus: Int = getColumnIndexOrThrow(_stmt, "syncStatus")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _result: MutableList<ActivityEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ActivityEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpDurationMinutes: Int
          _tmpDurationMinutes = _stmt.getLong(_columnIndexOfDurationMinutes).toInt()
          val _tmpStorageType: String
          _tmpStorageType = _stmt.getText(_columnIndexOfStorageType)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpLastModified: Long
          _tmpLastModified = _stmt.getLong(_columnIndexOfLastModified)
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpUserId: String?
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          }
          _item = ActivityEntity(_tmpId,_tmpName,_tmpLocation,_tmpDurationMinutes,_tmpStorageType,_tmpCreatedAt,_tmpLastModified,_tmpSyncStatus,_tmpIsDeleted,_tmpUserId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: String): ActivityEntity? {
    val _sql: String = "SELECT * FROM activities WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfDurationMinutes: Int = getColumnIndexOrThrow(_stmt, "durationMinutes")
        val _columnIndexOfStorageType: Int = getColumnIndexOrThrow(_stmt, "storageType")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfLastModified: Int = getColumnIndexOrThrow(_stmt, "lastModified")
        val _columnIndexOfSyncStatus: Int = getColumnIndexOrThrow(_stmt, "syncStatus")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _result: ActivityEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpDurationMinutes: Int
          _tmpDurationMinutes = _stmt.getLong(_columnIndexOfDurationMinutes).toInt()
          val _tmpStorageType: String
          _tmpStorageType = _stmt.getText(_columnIndexOfStorageType)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpLastModified: Long
          _tmpLastModified = _stmt.getLong(_columnIndexOfLastModified)
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpUserId: String?
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          }
          _result = ActivityEntity(_tmpId,_tmpName,_tmpLocation,_tmpDurationMinutes,_tmpStorageType,_tmpCreatedAt,_tmpLastModified,_tmpSyncStatus,_tmpIsDeleted,_tmpUserId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(id: String) {
    val _sql: String = "DELETE FROM activities WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM activities"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
