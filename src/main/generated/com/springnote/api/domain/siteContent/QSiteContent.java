package com.springnote.api.domain.siteContent;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSiteContent is a Querydsl query type for SiteContent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSiteContent extends EntityPathBase<SiteContent> {

    private static final long serialVersionUID = 945387682L;

    public static final QSiteContent siteContent = new QSiteContent("siteContent");

    public final StringPath key = createString("key");

    public final StringPath value = createString("value");

    public QSiteContent(String variable) {
        super(SiteContent.class, forVariable(variable));
    }

    public QSiteContent(Path<? extends SiteContent> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSiteContent(PathMetadata metadata) {
        super(SiteContent.class, metadata);
    }

}

