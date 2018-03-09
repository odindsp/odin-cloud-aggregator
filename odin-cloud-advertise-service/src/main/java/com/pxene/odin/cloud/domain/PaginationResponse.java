package com.pxene.odin.cloud.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.pagehelper.Page;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationResponse
{
    @JsonProperty("items")
    private List<?> items;

    @JsonIgnore
    private Page<?> page;

    @JsonProperty("pager")
    private Pager pager;


    public List<?> getItems()
    {
        return items;
    }
    public void setItems(List<?> items)
    {
        this.items = items;
    }
    public Page<?> getPage()
    {
        return page;
    }
    public void setPage(Page<?> page)
    {
        this.page = page;
    }


    public PaginationResponse(List<?> items, Page<?> page)
    {
        super();
        this.items = items;
        this.page = page;

        if (page != null)
        {
            this.pager = new Pager(page.getPageNum(), page.getPageSize(), page.getTotal());
        }
    }


    @Override
    public String toString()
    {
        return "PaginationResult [items=" + items + ", pager=" + pager + "]";
    }

    public class Pager
    {
        private int pageNo;
        private int pageSize;
        private long total;


        public int getPageNo()
        {
            return pageNo;
        }
        public void setPageNo(int page)
        {
            this.pageNo = page;
        }
        public int getPageSize()
        {
            return pageSize;
        }
        public void setPageSize(int pageSize)
        {
            this.pageSize = pageSize;
        }
        public long getTotal() {
            return total;
        }
        public void setTotal(long total) {
            this.total = total;
        }
        public Pager(int page, int pageSize, long total)
        {
            super();
            this.pageNo = page;
            this.pageSize = pageSize;
            this.total = total;
        }


        @Override
        public String toString()
        {
            return "Pager [pageNo=" + pageNo + ", pageSize=" + pageSize + ", total=" + total + "]";
        }
    }
}
