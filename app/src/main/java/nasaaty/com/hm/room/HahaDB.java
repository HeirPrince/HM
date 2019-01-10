package nasaaty.com.hm.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.model.User;

@Database(entities = {User.class, Product.class}, version = 1)
public abstract class HahaDB extends RoomDatabase {

	private static HahaDB Instance;

	public static HahaDB getInstance(Context context) {
		if (Instance == null){
			Instance = Room.databaseBuilder(context, HahaDB.class, "haha_db").build();
		}
		return Instance;
	}

	public abstract UserDao userEntity();
}
