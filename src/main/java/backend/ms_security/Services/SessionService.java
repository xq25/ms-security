package backend.ms_security.Services;

import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.Session;
import backend.ms_security.Repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionService {

    @Autowired
    private SessionRepository theSessionRepository;

    public ApiResponse<List<Session>> find() {
        return ApiResponse.success(this.theSessionRepository.findAll(), "Sesiones obtenidas correctamente");
    }

    public ApiResponse<Session> findById(String id) {
        Session session = this.theSessionRepository.findById(id).orElse(null);
        if (session == null) return ApiResponse.error("Sesión no encontrada");
        return ApiResponse.success(session, "Sesión encontrada");
    }

    public ApiResponse<Session> create(Session newSession) {
        return ApiResponse.success(this.theSessionRepository.save(newSession), "Sesión creada correctamente");
    }

    public ApiResponse<Session> update(String id, Session newSession) {
        Session actual = this.theSessionRepository.findById(id).orElse(null);
        if (actual == null) return ApiResponse.error("Sesión no encontrada");
        actual.setToken(newSession.getToken());
        actual.setExpiration(newSession.getExpiration());
        actual.setCode2FA(newSession.getCode2FA());
        actual.setUser(newSession.getUser());
        actual.setActive(newSession.isActive());
        this.theSessionRepository.save(actual);
        return ApiResponse.success(actual, "Sesión actualizada correctamente");
    }

    public ApiResponse<Void> delete(String id) {
        Session session = this.theSessionRepository.findById(id).orElse(null);
        if (session == null) return ApiResponse.error("Sesión no encontrada");
        this.theSessionRepository.delete(session);
        return ApiResponse.success("Sesión eliminada correctamente");
    }

    public boolean invalidateSession(String id) {
        Session theSession = this.theSessionRepository.findById(id).orElse(null);
        if (theSession == null) return false;
        theSession.setActive(false);
        this.theSessionRepository.save(theSession);
        return true;
    }

    public boolean isTokenAlreadyUsed(String token) {
        return this.theSessionRepository.findInvalidatedSessionByToken(token) != null;
    }
}