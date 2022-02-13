package pt.inesctec.m4j.workload;

import com.fasterxml.jackson.annotation.JsonTypeName;
import pt.inesctec.m4j.protocol.Request;

// RPC:Lin-KV! service
public interface LinKV extends Request {
    Object key();

    @JsonTypeName("read")
    public record Read(
        Object key,
        int msg_id
    ) implements LinKV {}
    @JsonTypeName("read_ok")
    public record ReadOk(
        Object value,
        Integer msg_id,
        int in_reply_to
    ) {}

    @JsonTypeName("write")
    public record Write(
        Object key,
        Object value,
        int msg_id
    ) implements LinKV {}
    @JsonTypeName("write_ok")
    public record WriteOk(
        Integer msg_id,
        int in_reply_to
    ) {}

    @JsonTypeName("cas")
    public record CAS(
        Object key,
        Object from,
        Object to,
        int msg_id
    ) implements LinKV {}
    @JsonTypeName("cas_ok")
    public record CASOk(
        Integer msg_id,
        int in_reply_to
    ) {}

}
