package pt.inesctec.m4j;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.inesctec.m4j.protocol.Init;
import pt.inesctec.m4j.protocol.Message;

import java.io.IOException;
import java.util.Iterator;

/**
 * Generic Maelstrom network interface.
 */
public class Network {

    private static final Logger logger = LoggerFactory.getLogger(Node.class);

    private final SequenceWriter writer;
    private final Iterator<Message> reader;

    /**
     * Initialize a network interface. It needs to known what message bodies
     * to expect, which should be records. Nested classes are also searched
     * for nested record classes.
     *
     * @param msgtypes classes of message bodies to be expected
     */
    public Network(Class<?>... msgtypes) {
        var mapper = new ObjectMapper();

        registerTypes(mapper, Init.class, Error.class);
        registerTypes(mapper, msgtypes);

        try {
            var jg = mapper.createGenerator(System.out);
            jg.setPrettyPrinter(new MinimalPrettyPrinter(""));
            this.writer = mapper.writer().writeValues(jg);

            var jf = new JsonFactory();
            var jp = jf.createParser(System.in);
            this.reader = mapper.reader().readValues(jp, Message.class);
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private static void registerTypes(ObjectMapper mapper, Class<?>... messages) {
        for(var c: messages) {
            if (c.isRecord()) {
                var tn = c.getAnnotation(JsonTypeName.class);
                if (tn != null)
                    logger.debug("registering message type \"{}\" (class {})", tn.value(), c.getCanonicalName());
                else
                    logger.debug("registering message class {}", c.getCanonicalName());
                mapper.registerSubtypes(c);
            }
            registerTypes(mapper, c.getDeclaredClasses());
        }
    }

    /**
     * Send a message. This method is thread-safe.
     *
     * @param message the message
     */
    public synchronized void send(Message message) {
        try {
            logger.trace("sending {}", message);
            writer.write(message);
            System.out.println();
            System.out.flush();
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Receive message from the network.
     *
     * @return the next message or null if closed
     */
    public Message receive() {
        if (reader.hasNext()) {
            var message = reader.next();
            logger.trace("receiving {}", message);
            return message;
        } else
            return null;
    }
}
