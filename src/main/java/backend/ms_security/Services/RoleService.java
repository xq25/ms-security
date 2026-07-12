package backend.ms_security.Services;

import backend.ms_security.DTOs.Pagination.PageRequestDTO;
import backend.ms_security.DTOs.Response.PagedResponse;
import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.Role;
import backend.ms_security.Models.RolePermission;
import backend.ms_security.Models.UserRole;
import backend.ms_security.Repositories.RolePermissionRepository;
import backend.ms_security.Repositories.RoleRepository;
import backend.ms_security.Repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository theRoleRepository;
    @Autowired
    private UserRoleRepository theUserRoleRepository;
    @Autowired
    private RolePermissionRepository theRolePermissionRepository;

    public ApiResponse<PagedResponse<Role>> find(PageRequestDTO pageRequest) {
        Page<Role> page = this.theRoleRepository.findAll(pageRequest.toPageable());
        return ApiResponse.success(toPagedResponse(page), "Roles obtenidos correctamente");
    }

    public ApiResponse<PagedResponse<Role>> searchByName(String query, PageRequestDTO pageRequest) {
        Page<Role> page = this.theRoleRepository.findByNameContainingIgnoreCase(query, pageRequest.toPageable());
        return ApiResponse.success(toPagedResponse(page), "Resultados de búsqueda por nombre");
    }

    private PagedResponse<Role> toPagedResponse(Page<Role> page) {
        return PagedResponse.<Role>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    public ApiResponse<Role> findById(String id) {
        Role role = this.theRoleRepository.findById(id).orElse(null);
        if (role == null) return ApiResponse.error("Rol no encontrado");
        return ApiResponse.success(role, "Rol encontrado");
    }

    public ApiResponse<Role> create(Role newRole) {
        return ApiResponse.success(this.theRoleRepository.save(newRole), "Rol creado correctamente");
    }

    public ApiResponse<Role> update(String id, Role newRole) {
        Role actualRole = this.theRoleRepository.findById(id).orElse(null);
        if (actualRole == null) return ApiResponse.error("Rol no encontrado");
        actualRole.setName(newRole.getName());
        actualRole.setDescription(newRole.getDescription());
        this.theRoleRepository.save(actualRole);
        return ApiResponse.success(actualRole, "Rol actualizado correctamente");
    }

    public ApiResponse<Void> delete(String id) {
        Role theRole = this.theRoleRepository.findById(id).orElse(null);
        if (theRole == null) return ApiResponse.error("Rol no encontrado");
        List<UserRole> users = this.theUserRoleRepository.getUsersByRole(theRole.getId());
        List<RolePermission> permissions = this.theRolePermissionRepository.getPermissionsByRole(theRole.getId());
        if (users != null && !users.isEmpty()) this.theUserRoleRepository.deleteAll(users);
        if (permissions != null && !permissions.isEmpty()) this.theRolePermissionRepository.deleteAll(permissions);
        this.theRoleRepository.delete(theRole);
        return ApiResponse.success("Rol eliminado correctamente");
    }
}