package pt.inesctec.m4j.workload;

import com.fasterxml.jackson.annotation.JsonTypeName;
import pt.inesctec.m4j.protocol.Request;

import java.util.Collection;
import java.util.List;
import java.util.Map;

// RPC:Lin-KV! service
@JsonTypeName("broadcast")
public record Broadcast(
        Object message,
        int msg_id
)  implements Request {
    @JsonTypeName("topology")
    public record Topology(
        Map<String, List<String>> topology,
        int msg_id
    )  implements Request {}
    @JsonTypeName("topology_ok")
    public record TopologyOk(
        Integer msg_id,
        int in_reply_to
    ) {}

    @JsonTypeName("broadcast_ok")
    public record Ok(
        Integer msg_id,
        int in_reply_to
    ) {}

    @JsonTypeName("read")
    public record Read(
        int msg_id
    )  implements Request {}
    @JsonTypeName("read_ok")
    public record ReadOk(
        Collection<Object> messages,
        Integer msg_id,
        int in_reply_to
    ) {}

}
