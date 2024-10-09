package com.springnote.api.utils.tag;


import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class TagComparisonResult<T> {

    private Integer diffCnt;
    private List<T> removedTag;
    private List<Long> addedTag;

    public TagComparisonResult() {
        this.diffCnt = 0;
        this.removedTag = new LinkedList<>();
        this.addedTag = new LinkedList<>();
    }

    public void insertRemoved(T postTag) {
        diffCnt++;
        removedTag.add(postTag);
    }

    public void insertAdded(List<Long> ids) {
        diffCnt += ids.size();
        addedTag.addAll(ids);
    }

    public boolean isChanged() {
        return diffCnt > 0;
    }

}
