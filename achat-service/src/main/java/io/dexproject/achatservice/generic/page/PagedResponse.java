package io.dexproject.achatservice.generic.page;

import java.util.List;
import io.dexproject.achatservice.generic.entity.GenericEntity;
import lombok.Value;

@Value
public class PagedResponse<R extends GenericEntity> {
    private List<R> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PagedResponse(List<R> content, int page, int size, long totalElements, int totalPages, boolean last) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }
}
