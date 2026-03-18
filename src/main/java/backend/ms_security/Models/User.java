package backend.ms_security.Models;
/*Importaciones relacionadas con la base de datos*/
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data // Nos ahorra la implementacion de Accesores y mutadores
@Document // Genera una coleccion automatica dentro de la base de datos en relacion con esta clase
public class User {
    @Id // Define la primary key de la coleccion que se esta generando
    private String id;
    private String name;
    private String email;
    private String password;

    // Constructor vacio
    public User() {
    }

    // Constructor parametrizado
    public User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
