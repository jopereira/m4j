package pt.inesctec.m4j.protocol;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("error")
public record Error(
        int in_reply_to,
        int code,
        String text
) {
    public final static int
        Timeout = 0,
        NodeNotFound = 1,
        NotSupported = 10,
        TemporarilyUnavailale = 11,
        MalformedRequest = 12,
        Crash = 13,
        Abort = 14,
        KeyDoesNotExists = 20,
        KeyAlreadyExists = 21,
        PreconditionFailed = 22,
        TxnConflict = 30
    ;

}
