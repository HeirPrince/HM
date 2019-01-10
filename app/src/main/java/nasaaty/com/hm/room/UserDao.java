package nasaaty.com.hm.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

import nasaaty.com.hm.model.DateConverter;
import nasaaty.com.hm.model.User;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
@TypeConverters(DateConverter.class)
public interface UserDao {

	/*
	 * //this may help when you want to display
	 * all users of the app
	 * on a single device
	 * */
	@Query(("SELECT * FROM User"))
	LiveData<List<User>> getUsers();

	@Insert(onConflict = REPLACE)
	void addUser(User user);

	@Delete
	void deleteUser(User user);

}
