package io.dexproject.achatservice.generic.page;

import java.util.ArrayList;
import java.util.List;
import io.dexproject.achatservice.generic.entity.GenericEntity;
import lombok.Value;

@Value
public class PagedResponse<E extends GenericEntity<E>> {
    private List<E> content = new ArrayList<>();
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
