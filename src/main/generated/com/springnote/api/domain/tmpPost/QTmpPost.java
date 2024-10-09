package com.springnote.api.domain.tmpPost;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTmpPost is a Querydsl query type for TmpPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTmpPost extends EntityPathBase<TmpPost> {

    private static final long serialVersionUID = 1967691756L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTmpPost tmpPost = new QTmpPost("tmpPost");

    public final com.springnote.api.domain.QBaseDateTimeEntity _super = new com.springnote.api.domain.QBaseDateTimeEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath id = createString("id");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final com.springnote.api.domain.postType.QPostType postType;

    public final com.springnote.api.domain.series.QSeries series;

    public final StringPath thumbnail = createString("thumbnail");

    public final StringPath title = createString("title");

    public final ListPath<com.springnote.api.domain.tmpPostTag.TmpPostTag, com.springnote.api.domain.tmpPostTag.QTmpPostTag> tmpPostTags = this.<com.springnote.api.domain.tmpPostTag.TmpPostTag, com.springnote.api.domain.tmpPostTag.QTmpPostTag>createList("tmpPostTags", com.springnote.api.domain.tmpPostTag.TmpPostTag.class, com.springnote.api.domain.tmpPostTag.QTmpPostTag.class, PathInits.DIRECT2);

    public QTmpPost(String variable) {
        this(TmpPost.class, forVariable(variable), INITS);
    }

    public QTmpPost(Path<? extends TmpPost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTmpPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTmpPost(PathMetadata metadata, PathInits inits) {
        this(TmpPost.class, metadata, inits);
    }

    public QTmpPost(Class<? extends TmpPost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.postType = inits.isInitialized("postType") ? new com.springnote.api.domain.postType.QPostType(forProperty("postType")) : null;
        this.series = inits.isInitialized("series") ? new com.springnote.api.domain.series.QSeries(forProperty("series")) : null;
    }

}

