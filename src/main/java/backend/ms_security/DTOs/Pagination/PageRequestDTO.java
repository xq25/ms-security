package backend.ms_security.DTOs.Pagination;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class PageRequestDTO {

    @Min(0)
    private int page = 0;

    @Min(1)
    @Max(value = 100, message = "El tamaño de página no puede superar 100 registros")
    private int size = 10;

    private String sortBy    = "id";
    private String direction = "asc";

    public Pageable toPageable() {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }
}
