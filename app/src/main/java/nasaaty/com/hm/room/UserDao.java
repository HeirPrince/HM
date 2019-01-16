package nasaaty.com.hm.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

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

	@Query("UPDATE User SET name = :username WHERE id = :id ")
	void updateUser(int id, String username);

	@Query("UPDATE User SET name = :username, photoUrl =:photo, email =:email WHERE id = :id ")
	void updateUserPhone(int id, String username, String photo, String email);

	@Query("UPDATE User SET name = :username, phoneNum =:phone WHERE id = :id ")
	void updateUserEmail(int id, String username, String phone);

	@Query("UPDATE User SET phoneNum =:phone WHERE id = :id ")
	void updateUserProvider(int id, String phone);

	@Query("UPDATE User SET photoUrl =:photo WHERE id = :id ")
	void updateUserPhoto(int id, String photo);

	@Delete
	void deleteUser(User user);


}
