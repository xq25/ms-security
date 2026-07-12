package backend.ms_security.Services;

import backend.ms_security.DTOs.Pagination.PageRequestDTO;
import backend.ms_security.DTOs.Response.PagedResponse;
import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.Profile;
import backend.ms_security.Models.Session;
import backend.ms_security.Models.User;
import backend.ms_security.Models.UserRole;
import backend.ms_security.Repositories.ProfileRepository;
import backend.ms_security.Repositories.SessionRepository;
import backend.ms_security.Repositories.UserRepository;
import backend.ms_security.Repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository theUserRepository;
    @Autowired
    private ProfileRepository theProfileRepository;
    @Autowired
    private SessionRepository theSessionRepository;
    @Autowired
    private UserRoleRepository theUserRoleRepository;
    @Autowired
    private EncryptionService theEncryption;
    @Autowired
    private ClasificatorService clasificatorService;

    public ApiResponse<PagedResponse<User>> find(PageRequestDTO pageRequest) {
        Page<User> page = this.theUserRepository.findAll(pageRequest.toPageable());
        return ApiResponse.success(toPagedResponse(page), "Usuarios obtenidos correctamente");
    }

    public ApiResponse<PagedResponse<User>> searchByName(String query, PageRequestDTO pageRequest) {
        Page<User> page = this.theUserRepository.findByNameContainingIgnoreCase(query, pageRequest.toPageable());
        return ApiResponse.success(toPagedResponse(page), "Resultados de búsqueda por nombre");
    }

    public ApiResponse<PagedResponse<User>> searchByEmail(String query, PageRequestDTO pageRequest) {
        Page<User> page = this.theUserRepository.findByEmailContainingIgnoreCase(query, pageRequest.toPageable());
        return ApiResponse.success(toPagedResponse(page), "Resultados de búsqueda por email");
    }

    private PagedResponse<User> toPagedResponse(Page<User> page) {
        return PagedResponse.<User>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    public ApiResponse<User> findById(String id) {
        User theUser = this.theUserRepository.findById(id).orElse(null);
        if (theUser == null) return ApiResponse.error("Usuario no encontrado");
        return ApiResponse.success(theUser, "Usuario encontrado");
    }

    public ApiResponse<User> findByEmail(String email) {
        User theUser = this.theUserRepository.getUserByEmail(email);
        if (theUser == null) return ApiResponse.error("Usuario no encontrado");
        return ApiResponse.success(theUser, "Usuario encontrado");
    }

    public ApiResponse<User> create(User newUser) {
        newUser.setPassword(this.theEncryption.convertSHA256(newUser.getPassword()));
        return ApiResponse.success(this.theUserRepository.save(newUser), "Usuario creado correctamente");
    }

    public ApiResponse<User> update(String id, User newUser) {
        User actualUser = this.theUserRepository.findById(id).orElse(null);
        if (actualUser == null) return ApiResponse.error("Usuario no encontrado");
        actualUser.setName(newUser.getName());
        actualUser.setEmail(newUser.getEmail());
        actualUser.setPassword(this.theEncryption.convertSHA256(newUser.getPassword()));
        this.theUserRepository.save(actualUser);
        return ApiResponse.success(actualUser, "Usuario actualizado correctamente");
    }

    public ApiResponse<Void> delete(String id) {
        User theUser = this.theUserRepository.findById(id).orElse(null);
        if (theUser == null) return ApiResponse.error("Usuario no encontrado");
        if (this.clasificatorService.existRelation(theUser.getId())) {
            return ApiResponse.error("No se puede eliminar el usuario porque tiene un Doctor o Paciente asociado en el sistema");
        }
        Profile userProfile = this.theProfileRepository.findProfileByUserID(theUser.getId());
        if (userProfile != null) this.theProfileRepository.delete(userProfile);
        List<UserRole> roles = this.theUserRoleRepository.getRolesByUser(id);
        if (roles != null && !roles.isEmpty()) this.theUserRoleRepository.deleteAll(roles);
        this.theUserRepository.delete(theUser);
        return ApiResponse.success("Usuario eliminado correctamente");
    }

    public boolean existUserById(String user_id) {
        return this.theUserRepository.findById(user_id).orElse(null) != null;
    }

    public ApiResponse<Void> addProfile(String user_id, String profile_id) {
        User user = this.theUserRepository.findById(user_id).orElse(null);
        Profile profile = this.theProfileRepository.findById(profile_id).orElse(null);
        if (user == null || profile == null) return ApiResponse.error("Usuario o perfil no encontrado");
        profile.setUser(user);
        this.theProfileRepository.save(profile);
        return ApiResponse.success("Perfil asociado correctamente");
    }

    public ApiResponse<Void> removeProfile(String user_id, String profile_id) {
        User user = this.theUserRepository.findById(user_id).orElse(null);
        Profile profile = this.theProfileRepository.findById(profile_id).orElse(null);
        if (user == null || profile == null) return ApiResponse.error("Usuario o perfil no encontrado");
        profile.setUser(null);
        this.theProfileRepository.save(profile);
        return ApiResponse.success("Perfil desvinculado correctamente");
    }

    public ApiResponse<Void> addSession(String user_id, String session_id) {
        User theUser = this.theUserRepository.findById(user_id).orElse(null);
        Session theSession = this.theSessionRepository.findById(session_id).orElse(null);
        if (theUser == null || theSession == null) return ApiResponse.error("Usuario o sesión no encontrado");
        theSession.setUser(theUser);
        this.theSessionRepository.save(theSession);
        return ApiResponse.success("Sesión asociada correctamente");
    }

    public ApiResponse<Void> removeSession(String user_id, String session_id) {
        User theUser = this.theUserRepository.findById(user_id).orElse(null);
        Session theSession = this.theSessionRepository.findById(session_id).orElse(null);
        if (theUser == null || theSession == null) return ApiResponse.error("Usuario o sesión no encontrado");
        theSession.setUser(null);
        this.theSessionRepository.save(theSession);
        return ApiResponse.success("Sesión desvinculada correctamente");
    }
}