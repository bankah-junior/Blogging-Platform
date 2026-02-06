package com.amalitech.SpringBootBloggingApp.model.dto.response;

import java.util.List;

/**
 * Paginated response envelope: content plus page metadata.
 *
 * @param <T> type of each item in the page
 */
public class PageResponse<T> {

    private List<T> content;
    private int page;           // 0-based
    private int size;
    private long totalElements;
    private int totalPages;

    public PageResponse() {}

    public PageResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
    }

    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
