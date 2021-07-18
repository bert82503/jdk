
package java.lang;

/**
 * A resource that must be closed when it is no longer needed.
 * <p>
 * 资源在它不再被需要时，必须被关闭。
 * [资源释放] 可自动关闭的资源（资源在不需要时必须关闭）
 *
 * @author Josh Bloch
 * @since 1.7
 */
public interface AutoCloseable {

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * <p>
     * 关闭此资源，放弃任何底层的资源。
     * 在由 try-with-resources 语句管理的对象上，此方法会被自动调用。
     *
     * <p>While this interface method is declared to throw {@code
     * Exception}, implementers are <em>strongly</em> encouraged to
     * declare concrete implementations of the {@code close} method to
     * throw more specific exceptions, or to throw no exception at all
     * if the close operation cannot fail.
     * <p>
     * 强烈推荐：抛出更具体的异常
     *
     * <p><em>Implementers of this interface are also strongly advised
     * to not have the {@code close} method throw {@link
     * InterruptedException}.</em>
     *
     * This exception interacts with a thread's interrupted status,
     * and runtime misbehavior is likely to occur if an {@code
     * InterruptedException} is {@linkplain Throwable#addSuppressed
     * suppressed}.
     *
     * More generally, if it would cause problems for an
     * exception to be suppressed, the {@code AutoCloseable.close}
     * method should not throw it.
     * <p>
     * 强烈建议：不要抛出中断异常（{@link InterruptedException}）
     *
     * <p>Note that unlike the {@link java.io.Closeable#close close}
     * method of {@link java.io.Closeable}, this {@code close} method
     * is <em>not</em> required to be idempotent.  In other words,
     * calling this {@code close} method more than once may have some
     * visible side effect, unlike {@code Closeable.close} which is
     * required to have no effect if called more than once.
     *
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     * <p>
     * 注意：不像 {@link java.io.Closeable#close Closeable.close()} 方法，
     * 此方法不需要是幂等的。
     * 换句话说，多次调用此方法可能会有一些明显的副作用；不像 {@code Closeable.close} 被调用多次需要没有影响。
     * 但是，强烈建议此接口的实现者让他们的 {@code close} 方法幂等。
     *
     * @throws Exception if this resource cannot be closed (无法关闭此资源)
     */
    void close() throws Exception;
}
