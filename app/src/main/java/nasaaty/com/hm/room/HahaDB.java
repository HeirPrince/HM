package nasaaty.com.hm.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import nasaaty.com.hm.model.Favorite;
import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.model.User;

@Database(entities = {User.class, Product.class, Order.class, Favorite.class}, version = 7)
public abstract class HahaDB extends RoomDatabase {

	private static HahaDB Instance;

	public static HahaDB getInstance(Context context) {
		if (Instance == null){
			Instance = Room.databaseBuilder(context, HahaDB.class, "haha_db").fallbackToDestructiveMigration().build();//.addMigrations(MIGRATION_5_6)
		}
		return Instance;
	}

	static final Migration MIGRATION_5_6 = new Migration(5, 6) {
		@Override
		public void migrate(SupportSQLiteDatabase database) {
			database.execSQL("ALTER TABLE orders ADD COLUMN order_id TEXT");
		}
	};

	public abstract UserDao userEntity();
	public abstract ProductDao productEntity();
	public abstract OrderDao orderEntity();
	public abstract FavDao favoriteEntity();
}
