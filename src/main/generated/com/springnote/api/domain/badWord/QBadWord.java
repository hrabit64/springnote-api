package com.springnote.api.domain.badWord;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBadWord is a Querydsl query type for BadWord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBadWord extends EntityPathBase<BadWord> {

    private static final long serialVersionUID = -1890827108L;

    public static final QBadWord badWord = new QBadWord("badWord");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath type = createBoolean("type");

    public final StringPath word = createString("word");

    public QBadWord(String variable) {
        super(BadWord.class, forVariable(variable));
    }

    public QBadWord(Path<? extends BadWord> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBadWord(PathMetadata metadata) {
        super(BadWord.class, metadata);
    }

}

