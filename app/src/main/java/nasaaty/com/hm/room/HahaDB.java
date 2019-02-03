package nasaaty.com.hm.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.model.User;

@Database(entities = {User.class, Product.class, Order.class}, version = 4)
public abstract class HahaDB extends RoomDatabase {

	private static HahaDB Instance;

	public static HahaDB getInstance(Context context) {
		if (Instance == null){
			Instance = Room.databaseBuilder(context, HahaDB.class, "haha_db").addMigrations(MIGRATION_3_4).build();//.addMigrations(MIGRATION_4_5)
		}
		return Instance;
	}

	static final Migration MIGRATION_3_4 = new Migration(3, 4) {
		@Override
		public void migrate(SupportSQLiteDatabase database) {
			database.execSQL("ALTER TABLE product ADD COLUMN description TEXT");
		}
	};

	public abstract UserDao userEntity();
	public abstract ProductDao productEntity();
	public abstract OrderDao orderEntity();
}
