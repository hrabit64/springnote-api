package com.springnote.api.domain.post;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = -1940513638L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final com.springnote.api.domain.QBaseDateTimeEntity _super = new com.springnote.api.domain.QBaseDateTimeEntity(this);

    public final com.springnote.api.domain.content.QContent content;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isEnabled = createBoolean("isEnabled");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final ListPath<com.springnote.api.domain.postTag.PostTag, com.springnote.api.domain.postTag.QPostTag> postTags = this.<com.springnote.api.domain.postTag.PostTag, com.springnote.api.domain.postTag.QPostTag>createList("postTags", com.springnote.api.domain.postTag.PostTag.class, com.springnote.api.domain.postTag.QPostTag.class, PathInits.DIRECT2);

    public final com.springnote.api.domain.postType.QPostType postType;

    public final com.springnote.api.domain.series.QSeries series;

    public final StringPath thumbnail = createString("thumbnail");

    public final StringPath title = createString("title");

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.content = inits.isInitialized("content") ? new com.springnote.api.domain.content.QContent(forProperty("content")) : null;
        this.postType = inits.isInitialized("postType") ? new com.springnote.api.domain.postType.QPostType(forProperty("postType")) : null;
        this.series = inits.isInitialized("series") ? new com.springnote.api.domain.series.QSeries(forProperty("series")) : null;
    }

}

