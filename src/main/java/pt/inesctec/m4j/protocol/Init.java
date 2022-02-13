package pt.inesctec.m4j.protocol;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName("init")
public record Init(
        int msg_id,
        String node_id,
        List<String> node_ids
) implements Request {
    @JsonTypeName("init_ok")
    public record Ok(
            Integer msg_id,
            int in_reply_to
    ) {}
}
