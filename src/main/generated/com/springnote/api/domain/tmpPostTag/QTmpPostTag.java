package com.springnote.api.domain.tmpPostTag;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTmpPostTag is a Querydsl query type for TmpPostTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTmpPostTag extends EntityPathBase<TmpPostTag> {

    private static final long serialVersionUID = -948253638L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTmpPostTag tmpPostTag = new QTmpPostTag("tmpPostTag");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.springnote.api.domain.tag.QTag tag;

    public final com.springnote.api.domain.tmpPost.QTmpPost tmpPost;

    public QTmpPostTag(String variable) {
        this(TmpPostTag.class, forVariable(variable), INITS);
    }

    public QTmpPostTag(Path<? extends TmpPostTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTmpPostTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTmpPostTag(PathMetadata metadata, PathInits inits) {
        this(TmpPostTag.class, metadata, inits);
    }

    public QTmpPostTag(Class<? extends TmpPostTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.tag = inits.isInitialized("tag") ? new com.springnote.api.domain.tag.QTag(forProperty("tag")) : null;
        this.tmpPost = inits.isInitialized("tmpPost") ? new com.springnote.api.domain.tmpPost.QTmpPost(forProperty("tmpPost"), inits.get("tmpPost")) : null;
    }

}

