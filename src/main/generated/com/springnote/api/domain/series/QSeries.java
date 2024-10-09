package com.springnote.api.domain.series;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSeries is a Querydsl query type for Series
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSeries extends EntityPathBase<Series> {

    private static final long serialVersionUID = -675466438L;

    public static final QSeries series = new QSeries("series");

    public final com.springnote.api.domain.QBaseDateTimeEntity _super = new com.springnote.api.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath name = createString("name");

    public final StringPath thumbnail = createString("thumbnail");

    public QSeries(String variable) {
        super(Series.class, forVariable(variable));
    }

    public QSeries(Path<? extends Series> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSeries(PathMetadata metadata) {
        super(Series.class, metadata);
    }

}

