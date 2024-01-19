package io.dexproject.achatservice.generic.entity;

import lombok.Value;

import java.util.List;

@Value
public class PagedResponse<R extends BaseReponseDto> {
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
