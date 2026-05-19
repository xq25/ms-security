package backend.ms_security.Services;

import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.Profile;
import backend.ms_security.Repositories.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository theProfileRepository;

    public ApiResponse<List<Profile>> find() {
        return ApiResponse.success(this.theProfileRepository.findAll(), "Perfiles obtenidos correctamente");
    }

    public ApiResponse<Profile> findById(String id) {
        Profile profile = this.theProfileRepository.findById(id).orElse(null);
        if (profile == null) return ApiResponse.error("Perfil no encontrado");
        return ApiResponse.success(profile, "Perfil encontrado");
    }

    public ApiResponse<Profile> findProfileByUser(String user_id) {
        Profile profile = this.theProfileRepository.findProfileByUserID(user_id);
        if (profile == null) return ApiResponse.error("Perfil no encontrado para ese usuario");
        return ApiResponse.success(profile, "Perfil encontrado");
    }

    public ApiResponse<Profile> create(Profile newProfile) {
        return ApiResponse.success(this.theProfileRepository.save(newProfile), "Perfil creado correctamente");
    }

    public ApiResponse<Profile> update(String id, Profile newProfile) {
        Profile actual = this.theProfileRepository.findById(id).orElse(null);
        if (actual == null) return ApiResponse.error("Perfil no encontrado");
        actual.setPhone(newProfile.getPhone());
        actual.setPhoto(newProfile.getPhoto());
        this.theProfileRepository.save(actual);
        return ApiResponse.success(actual, "Perfil actualizado correctamente");
    }

    public ApiResponse<Void> delete(String id) {
        Profile profile = this.theProfileRepository.findById(id).orElse(null);
        if (profile == null) return ApiResponse.error("Perfil no encontrado");
        this.theProfileRepository.delete(profile);
        return ApiResponse.success("Perfil eliminado correctamente");
    }
}