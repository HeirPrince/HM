package nasaaty.com.hm.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.model.User;

@Database(entities = {User.class, Product.class}, version = 2)
public abstract class HahaDB extends RoomDatabase {

	private static HahaDB Instance;

	public static HahaDB getInstance(Context context) {
		if (Instance == null){
			Instance = Room.databaseBuilder(context, HahaDB.class, "haha_db").addMigrations(MIGRATION_1_2).build();
		}
		return Instance;
	}

	static final Migration MIGRATION_1_2 = new Migration(1, 2) {
		@Override
		public void migrate(SupportSQLiteDatabase database) {
			database.execSQL("ALTER TABLE user "
					+ " ADD COLUMN providerID TEXT");
		}
	};

	public abstract UserDao userEntity();
}
