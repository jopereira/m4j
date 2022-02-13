package pt.inesctec.m4j.workload;

import com.fasterxml.jackson.annotation.JsonTypeName;
import pt.inesctec.m4j.protocol.Request;

// RPC:Echo! service
@JsonTypeName("echo")
public record Echo(
        Object echo,
        int msg_id
) implements Request {
        @JsonTypeName("echo_ok")
        public record Ok(
                Object echo,
                Integer msg_id,
                int in_reply_to
        ) {}
}
