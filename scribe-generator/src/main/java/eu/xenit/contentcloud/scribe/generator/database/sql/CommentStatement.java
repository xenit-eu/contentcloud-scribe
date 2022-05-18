package eu.xenit.contentcloud.scribe.generator.database.sql;

import lombok.Value;

@Value
public class CommentStatement implements Statement {
    String comment;

    @Override
    public String toSql() {
        return "-- "+ comment.replaceAll("\\n", "\n--");
    }

    public static CommentStatement comment(String comment) {
        return new CommentStatement(comment);
    }
}
