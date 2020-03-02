import com.aradipatrik.domain.interfaces.auth.Authenticator
import com.aradipatrik.domain.model.UserCredentials
import io.reactivex.Completable

class FirebaseAuthenticator : Authenticator {
    override fun registerUserWithCredentials(userCredentials: UserCredentials): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}