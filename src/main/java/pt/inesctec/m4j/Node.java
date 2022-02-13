package pt.inesctec.m4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.inesctec.m4j.protocol.Init;
import pt.inesctec.m4j.protocol.Message;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Generic Maelstrom node. Handles init message and uses a thread pool to
 * handle messages, timers, and other asynchronous events.
 */
public class Node implements Runnable, Consumer<Message> {

    protected static final Logger logger = LoggerFactory.getLogger(Node.class);

    private final Network net;
    private final ScheduledExecutorService ses;

    private String node_id = null;
    private List<String> node_ids = null;
    private int next_id = 1;

    /**
     * Initialize a node with a network interface and an executor service.
     *
     * @param net a Maelstrong network interface
     * @param ses an executor service
     */
    public Node(Network net, ScheduledExecutorService ses) {
        this.net = net;
        this.ses = ses;
    }

    /**
     * Schedule event after a delay. This is a wrapper for
     * {@link java.util.concurrent.ScheduledExecutorService#schedule(Runnable, long, TimeUnit)}
     * that catches and logs unhandled exceptions, causing the node to stop/fail.
     *
     * @param task the task to run
     * @param time the detail
     * @param unit the unit of the delay
     */
    public void schedule(Runnable task, long time, TimeUnit unit) {
        ses.schedule(()->{
            try {
                task.run();
            } catch(Exception e) {
                logger.error("unexpected exception", e);
                stop();
            }
        }, time, unit);
    }

    /**
     * Schedule event. This is a wrapper for
     * {@link java.util.concurrent.ScheduledExecutorService#submit(Runnable)}
     * that catches and logs unhandled exceptions, causing the node to stop/fail.
     *
     * @param task the task to run
     */
    public void submit(Runnable task) {
        ses.submit(()->{
            try {
                task.run();
            } catch(Exception e) {
                logger.error("unexpected exception", e);
                stop();
            }
        });
    }

    /**
     * Send a message. This is a shortcut to {@link Network#send(Message)}}.
     *
     * @param message a message
     */
    public void send(Message message) {
        net.send(message);
    }

    /**
     * Run the node, receiving and processing incoming messages. This needs to be called
     * by a single thread, that is used for the remainder of the lifetime of the node.
     */
    public void run() {
        logger.debug("process started");
        while(true) {
            final var m = net.receive();
            if (m == null)
                break;
            ses.execute(() -> {
                try {
                    accept(m);
                } catch (Exception e) {
                    logger.error("unexpected exception", e);
                    stop();
                }
            });
        }
        logger.debug("process terminated");
    }

    private synchronized void stop() {
        logger.debug("stopping process");
        try {
            System.out.close();
            System.in.close();
            ses.shutdown();
        } catch(IOException ioe) {
            // don't care
        }
    }

    /**
     * Generates unique ids for outgoing messages.
     *
     * @return next message id
     */
    public synchronized int next_id() {
        return next_id++;
    }

    /**
     * The local node id, after initialization.
     *
     * @return local node id
     */
    public synchronized String node_id() {
        return node_id;
    }

    /**
     * The node list, after initialization.
     *
     * @return node list
     */
    protected synchronized List<String> node_ids() {
        return node_ids;
    }

    /**
     * Default message handler. Sub-classes should override this method and
     * call it for unhandled messages, including the initialization message.
     *
     * @param message message to be handled
     */
    @Override
    public synchronized void accept(Message message) {
        if (message.body() instanceof Init body) {
            node_id = body.node_id();
            node_ids = Collections.unmodifiableList(body.node_ids());
            send(new Message(node_id(), message.src(), new Init.Ok(next_id(), body.msg_id())));

            logger.info("node {} initialized", node_id);
        } else
            logger.warn("message ignored: {}", message);
    }
}
