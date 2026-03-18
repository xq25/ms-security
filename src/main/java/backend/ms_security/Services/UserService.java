package backend.ms_security.Services;

import backend.ms_security.Models.Profile;
import backend.ms_security.Models.Session;
import backend.ms_security.Models.User;
import backend.ms_security.Repositories.ProfileRepository;
import backend.ms_security.Repositories.SessionRepository;
import backend.ms_security.Repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Indica el espacio de la logica de negocio.(Obligatorio, esto permite instanciar de forma automatica donde la useamos para no tener que usar new UserService)
public class UserService {

    //Inyeccion de Repositorios.
    @Autowired // Instancia automaticamente un objeto de UserRepository
    private UserRepository theUserRepository; // Aqui almacenamos nuestro UserRepository para realizar las consultas. (Inyeccion de dependencias)

    @Autowired
    private ProfileRepository theProfileRepository;

    @Autowired
    private SessionRepository theSessionRepository;

    @Autowired // Inyectamos el servicio de encryptado para a la hora de generar o actualizar un usuario poder encryptar la contraseña.
    private EncryptionService theEncryption;

    //getAll() -> List[User]
    public List<User> find(){

        return this.theUserRepository.findAll();
    }

    // getById() -> User
    public User findById(String id){
        User theUser=this.theUserRepository.findById(id).orElse(null);
        return theUser;
    }

    public User findByEmail(String email){
        User theUser = this.theUserRepository.getUserByEmail(email);
        return theUser;
    }

    // create() -> User -> User.id != null
    public User create(User newUser){
        // Antes de crear un usuario, validar que no exista antes.
        //User knewUser = this.theUserRepository

        newUser.setPassword(this.theEncryption.convertSHA256(newUser.getPassword()));
        return this.theUserRepository.save(newUser);
    }

    public User update(String id, User newUser){
        User actualUser=this.theUserRepository.findById(id).orElse(null);

        if(actualUser!=null){
            actualUser.setName(newUser.getName());
            actualUser.setEmail(newUser.getEmail());
            actualUser.setPassword(this.theEncryption.convertSHA256(newUser.getPassword()));
            this.theUserRepository.save(actualUser);
            return actualUser;
        }else{
            return null;
        }
    }

    public boolean delete(String id){
        User theUser=this.theUserRepository.findById(id).orElse(null);
        if (theUser!=null){
            this.theUserRepository.delete(theUser);
            return true;
        }
        else{
            return false;
        }
    }

// ---- Enlace con los Perfiles ----
    /*Permite Asociar un usuario a un perfil, pero se debe generar previamente el perfil en nuestra base de datos.*/
    public boolean addProfile(String user_id, String profile_id){
        User user = this.theUserRepository.findById(user_id).orElse(null);
        Profile profile = this.theProfileRepository.findById(profile_id).orElse(null);

        if(user != null && profile != null){
            profile.setUser(user);
            this.theProfileRepository.save(profile);
            return true;
        }else{
            return false;
        }

    }
    public boolean removeProfile(String user_id, String profile_id){
        User user = this.theUserRepository.findById(user_id).orElse(null);
        Profile profile = this.theProfileRepository.findById(profile_id).orElse(null);

        if(user != null && profile != null){
            profile.setUser(null);
            this.theProfileRepository.save(profile);
            return true;
        }else{
            return false;
        }
    }
//---- Enlace con las Sessiones -----
    /**
     * Permite asociar un usuario y una sesión. Para que funcione ambos
     * ya deben de existir en la base de datos.
     * @param user_id
     * @param session_id
     * @return
     */
    public boolean addSession(String user_id,String session_id){
        User theUser=this.theUserRepository.findById(user_id).orElse(null);
        Session theSession=this.theSessionRepository.findById(session_id).orElse(null);
        if(theUser!=null && theSession!=null){
            theSession.setUser(theUser);
            this.theSessionRepository.save(theSession);
            return true;
        }else{
            return false;
        }
    }
    /*Des enlazamos la relacion entre un usuario y una session especifica*/
    public boolean removeSession(String user_id,String session_id){
        User theUser=this.theUserRepository.findById(user_id).orElse(null);
        Session theSession=this.theSessionRepository.findById(session_id).orElse(null);
        if(theUser!=null && theSession!=null){
            theSession.setUser(null);
            this.theSessionRepository.save(theSession);
            return true;
        }else{
            return false;
        }
    }

}