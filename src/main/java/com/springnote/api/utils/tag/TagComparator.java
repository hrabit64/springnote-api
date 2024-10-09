package com.springnote.api.utils.tag;

import com.springnote.api.domain.PostAndTag;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
public class TagComparator<T extends PostAndTag> {

    public TagComparisonResult<T> getTagCompareResult(List<T> beforePostTags, List<Long> newTagIds) {
        var compareResult = new TagComparisonResult<T>();

        if (beforePostTags.isEmpty()) {

            if (!newTagIds.isEmpty()) {
                compareResult.setDiffCnt(newTagIds.size());
                compareResult.setAddedTag(newTagIds);
            }

            return compareResult;
        }

        var tagIdsToAdd = new HashSet<>(newTagIds);

        for (var beforePostTag : beforePostTags) {

            var tagId = beforePostTag.getTag().getId();

            if (!tagIdsToAdd.remove(tagId)) {
                compareResult.insertRemoved(beforePostTag);
            }
        }

        if (!tagIdsToAdd.isEmpty()) {
            compareResult.insertAdded(tagIdsToAdd.stream().toList());
        }

        return compareResult;
    }
}
