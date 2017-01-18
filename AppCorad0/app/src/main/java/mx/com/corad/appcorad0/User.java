package mx.com.corad.appcorad0;

/**
 * Created by Antonio on 09/01/2017.
 */
import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {
    public String name;
    public String email;
    //public String image;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
