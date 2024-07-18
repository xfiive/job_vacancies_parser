package org.parser.parsermail.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "messages")
public class JobMessage {

    @Id
    private String id;

    @Field
    private String messageId;

    @Field
    private String jobId;

    @Field
    private boolean isLiked;
}
