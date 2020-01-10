
package java.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * A simple service-provider loading facility.
 * 服务加载器，一个简单的服务提供者加载工具。
 *
 * <p> A <i>service</i> is a well-known set of interfaces and (usually
 * abstract) classes.  A <i>service provider</i> is a specific implementation
 * of a service.  The classes in a provider typically implement the interfaces
 * and subclass the classes defined in the service itself.  Service providers
 * can be installed in an implementation of the Java platform in the form of
 * extensions, that is, jar files placed into any of the usual extension
 * directories.  Providers can also be made available by adding them to the
 * application's class path or by some other platform-specific means.
 * 服务是一组接口和类(抽象)。
 * 服务提供者是服务的特定实现。一个服务提供者中的实现类通常实现服务的接口或子类。
 * 服务提供者可以以扩展的形式安装在Java平台的实现中，将jar文件放置在任何常用扩展目录中。
 * 也可以通过将服务提供者添加到应用程序的类路径或通过其他一些平台特定的方式来使服务提供者可用。
 *
 * <p> For the purpose of loading, a service is represented by a single type,
 * that is, a single interface or abstract class.  (A concrete class can be
 * used, but this is not recommended.)  A provider of a given service contains
 * one or more concrete classes that extend this <i>service type</i> with data
 * and code specific to the provider.  The <i>provider class</i> is typically
 * not the entire provider itself but rather a proxy which contains enough
 * information to decide whether the provider is able to satisfy a particular
 * request together with code that can create the actual provider on demand.
 * The details of provider classes tend to be highly service-specific; no
 * single class or interface could possibly unify them, so no such type is
 * defined here.  The only requirement enforced by this facility is that
 * provider classes must have a zero-argument constructor so that they can be
 * instantiated during loading.
 * 出于加载的目的，服务由单一类型表示，即单一接口或抽象类。(可以使用一个具体的类，但是不建议这样做。)
 * 给定服务的提供者包含一个或多个具体类，这些类通过特定于该提供者的数据和代码扩展这个服务类型。
 * 提供者类通常不是整个提供者本身，而是包含足够信息以决定提供者是否能够满足特定请求的代码，
 * 以及可以按需创建实际提供者的代码的代理。
 * 本功能强制执行的唯一要求是，服务提供者类必须具有零参数/缺省构造函数，以便可以在加载期间实例化它们。
 *
 * <p><a name="format"> A service provider is identified by placing a
 * <i>provider-configuration file</i> in the resource directory
 * <tt>META-INF/services</tt>.</a>  The file's name is the fully-qualified <a
 * href="../lang/ClassLoader.html#name">binary name</a> of the service's type.
 * The file contains a list of fully-qualified binary names of concrete
 * provider classes, one per line.  Space and tab characters surrounding each
 * name, as well as blank lines, are ignored.  The comment character is
 * <tt>'#'</tt> (<tt>'&#92;u0023'</tt>,
 * <font style="font-size:smaller;">NUMBER SIGN</font>); on
 * each line all characters following the first comment character are ignored.
 * The file must be encoded in UTF-8.
 * 通过将提供者配置文件放置在资源目录"META-INF/services/"中来标识服务提供者。
 * 文件名是服务类型的全限定名，该文件包含具体提供者类的全限定名列表，每行一个。
 * 每个名称周围的空格和制表符以及空白行将被忽略。
 * 注释字符是'#'，在每一行中，第一个注释字符之后的所有字符都将被忽略。
 * 该文件必须使用"UTF-8"编码。
 *
 * <p> If a particular concrete provider class is named in more than one
 * configuration file, or is named in the same configuration file more than
 * once, then the duplicates are ignored.  The configuration file naming a
 * particular provider need not be in the same jar file or other distribution
 * unit as the provider itself.  The provider must be accessible from the same
 * class loader that was initially queried to locate the configuration file;
 * note that this is not necessarily the class loader from which the file was
 * actually loaded.
 * 如果一个特定的具体服务提供者类在多个配置文件中被命名，或者在同一个配置文件中被多次命名，则重复项将被忽略。
 * 命名特定提供者的配置文件不必与提供者本身位于同一个jar文件或其他分发单元中。
 * 服务提供者必须可以从最初查询以查找配置文件的同一个类加载器进行访问；
 * 请注意，这不一定是实际从中加载文件的类加载器。
 * (缺点：每个服务最多具有一个提供者，不能做差异化定制化)
 *
 * <p> Providers are located and instantiated lazily, that is, on demand.  A
 * service loader maintains a cache of the providers that have been loaded so
 * far.  Each invocation of the {@link #iterator iterator} method returns an
 * iterator that first yields all of the elements of the cache, in
 * instantiation order, and then lazily locates and instantiates any remaining
 * providers, adding each one to the cache in turn.  The cache can be cleared
 * via the {@link #reload reload} method.
 * 服务提供者被惰性地定位和实例化，即按需实例化。
 * 服务加载器维护到目前为止已加载的提供者的缓存。
 * 每次迭代器方法的调用都会返回一个迭代器，该迭代器首先以实例化顺序生成缓存的所有元素，
 * 然后惰性地定位和实例化任何剩余的提供者列表，依次将每个提供者添加到缓存中。
 * 可以通过reload方法清除缓存。
 *
 * <p> Service loaders always execute in the security context of the caller.
 * Trusted system code should typically invoke the methods in this class, and
 * the methods of the iterators which they return, from within a privileged
 * security context.
 * 服务加载器始终在调用方的安全上下文中执行。
 * 受信任的系统代码通常应从特权安全上下文中调用本类中的方法，以及它们返回的迭代器的方法。
 *
 * <p> Instances of this class are not safe for use by multiple concurrent
 * threads.
 * 本类的实例不适用于多个并发线程。
 *
 * <p> Unless otherwise specified, passing a <tt>null</tt> argument to any
 * method in this class will cause a {@link NullPointerException} to be thrown.
 *
 *
 * <p><span style="font-weight: bold; padding-right: 1em">Example</span>
 * Suppose we have a service type <tt>com.example.CodecSet</tt> which is
 * intended to represent sets of encoder/decoder pairs for some protocol.  In
 * this case it is an abstract class with two abstract methods:
 *
 * <blockquote><pre>
 * public abstract Encoder getEncoder(String encodingName);
 * public abstract Decoder getDecoder(String encodingName);</pre></blockquote>
 *
 * Each method returns an appropriate object or <tt>null</tt> if the provider
 * does not support the given encoding.  Typical providers support more than
 * one encoding.
 *
 * <p> If <tt>com.example.impl.StandardCodecs</tt> is an implementation of the
 * <tt>CodecSet</tt> service then its jar file also contains a file named
 *
 * <blockquote><pre>
 * META-INF/services/com.example.CodecSet</pre></blockquote>
 *
 * <p> This file contains the single line:
 *
 * <blockquote><pre>
 * com.example.impl.StandardCodecs    # Standard codecs</pre></blockquote>
 *
 * <p> The <tt>CodecSet</tt> class creates and saves a single service instance
 * at initialization:
 *
 * <blockquote><pre>
 * private static ServiceLoader&lt;CodecSet&gt; codecSetLoader
 *     = ServiceLoader.load(CodecSet.class);</pre></blockquote>
 *
 * <p> To locate an encoder for a given encoding name it defines a static
 * factory method which iterates through the known and available providers,
 * returning only when it has located a suitable encoder or has run out of
 * providers.
 *
 * <blockquote><pre>
 * public static Encoder getEncoder(String encodingName) {
 *     for (CodecSet cp : codecSetLoader) {
 *         Encoder enc = cp.getEncoder(encodingName);
 *         if (enc != null)
 *             return enc;
 *     }
 *     return null;
 * }</pre></blockquote>
 *
 * <p> A <tt>getDecoder</tt> method is defined similarly.
 *
 *
 * <p><span style="font-weight: bold; padding-right: 1em">Usage Note</span> If
 * the class path of a class loader that is used for provider loading includes
 * remote network URLs then those URLs will be dereferenced in the process of
 * searching for provider-configuration files.
 *
 * <p> This activity is normal, although it may cause puzzling entries to be
 * created in web-server logs.  If a web server is not configured correctly,
 * however, then this activity may cause the provider-loading algorithm to fail
 * spuriously.
 *
 * <p> A web server should return an HTTP 404 (Not Found) response when a
 * requested resource does not exist.  Sometimes, however, web servers are
 * erroneously configured to return an HTTP 200 (OK) response along with a
 * helpful HTML error page in such cases.  This will cause a {@link
 * ServiceConfigurationError} to be thrown when this class attempts to parse
 * the HTML page as a provider-configuration file.  The best solution to this
 * problem is to fix the misconfigured web server to return the correct
 * response code (HTTP 404) along with the HTML error page.
 *
 * @param  <S>
 *         The type of the service to be loaded by this loader
 *
 * @author Mark Reinhold
 * @since 1.6
 */
public final class ServiceLoader<S>
    implements Iterable<S>
{

    /**
     * 提供者配置文件放置的资源目录，用来标识服务提供者。
     */
    private static final String PREFIX = "META-INF/services/";

    // The class or interface representing the service being loaded
    // 表示正在加载的服务的类或接口
    private final Class<S> service;

    // The class loader used to locate, load, and instantiate providers
    // 用于查找、加载和实例化服务提供者的类加载器
    private final ClassLoader loader;

    // The access control context taken when the ServiceLoader is created
    // 创建服务加载器时采取的访问控制上下文
    private final AccessControlContext acc;

    // Cached providers, in instantiation order
    // 缓存的服务提供者列表，按实例化顺序保存
    private LinkedHashMap<String,S> providers = new LinkedHashMap<>();

    // The current lazy-lookup iterator
    // 当前惰性查找的迭代器
    private LazyIterator lookupIterator;

    /**
     * Clear this loader's provider cache so that all providers will be
     * reloaded.
     * 清除这个服务加载器的提供者缓存，以便将重新加载所有提供者。
     *
     * <p> After invoking this method, subsequent invocations of the {@link
     * #iterator() iterator} method will lazily look up and instantiate
     * providers from scratch, just as is done by a newly-created loader.
     *
     * <p> This method is intended for use in situations in which new providers
     * can be installed into a running Java virtual machine.
     */
    public void reload() {
        // 清空服务提供者列表的缓存
        providers.clear();
        // 创建惰性查找的迭代器
        lookupIterator = new LazyIterator(service, loader);
    }

    private ServiceLoader(Class<S> svc, ClassLoader cl) {
        service = Objects.requireNonNull(svc, "Service interface cannot be null");
        // 用于查找、加载和实例化服务提供者的类加载器(系统类加载器)
        loader = (cl == null) ? ClassLoader.getSystemClassLoader() : cl;
        // 创建服务加载器时采取的访问控制上下文
        acc = (System.getSecurityManager() != null) ? AccessController.getContext() : null;
        // 清除服务提供者列表的缓存，重新加载所有提供者
        reload();
    }

    // 服务提供者加载失败

    private static void fail(Class<?> service, String msg, Throwable cause)
        throws ServiceConfigurationError
    {
        throw new ServiceConfigurationError(service.getName() + ": " + msg,
                                            cause);
    }

    private static void fail(Class<?> service, String msg)
        throws ServiceConfigurationError
    {
        throw new ServiceConfigurationError(service.getName() + ": " + msg);
    }

    private static void fail(Class<?> service, URL u, int line, String msg)
        throws ServiceConfigurationError
    {
        fail(service, u + ":" + line + ": " + msg);
    }

    // Parse a single line from the given configuration file, adding the name
    // on the line to the names list.
    // 从给定的配置文件中解析一行，并将该行服务的全限定名添加到名称列表中
    private int parseLine(Class<?> service, URL u, BufferedReader r, int lc,
                          List<String> names)
        throws IOException, ServiceConfigurationError
    {
        // 读取一行
        String ln = r.readLine();
        if (ln == null) {
            return -1;
        }
        // 注释
        int ci = ln.indexOf('#');
        if (ci >= 0) ln = ln.substring(0, ci);
        ln = ln.trim();
        int n = ln.length();
        if (n != 0) {
            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0))
                fail(service, u, lc, "Illegal configuration-file syntax");
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp))
                fail(service, u, lc, "Illegal provider-class name: " + ln);
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && (cp != '.'))
                    fail(service, u, lc, "Illegal provider-class name: " + ln);
            }
            // 服务提供者列表的缓存
            if (!providers.containsKey(ln) && !names.contains(ln))
                // 添加新的服务提供者名称
                names.add(ln);
        }
        // 行计数器
        return lc + 1;
    }

    // Parse the content of the given URL as a provider-configuration file.
    // 将给定URL的内容解析为提供者的配置文件。
    //
    // @param  service
    //         The service type for which providers are being sought;
    //         used to construct error detail strings
    //
    // @param  u
    //         The URL naming the configuration file to be parsed
    //
    // @return A (possibly empty) iterator that will yield the provider-class
    //         names in the given configuration file that are not yet members
    //         of the returned set
    //
    // @throws ServiceConfigurationError
    //         If an I/O error occurs while reading from the given URL, or
    //         if a configuration-file format error is detected
    //
    private Iterator<String> parse(Class<?> service, URL u)
        throws ServiceConfigurationError
    {
        InputStream in = null;
        BufferedReader r = null;
        // 服务提供者名称列表
        ArrayList<String> names = new ArrayList<>();
        try {
            in = u.openStream();
            r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            // 行计数器
            int lc = 1;
            while ((lc = parseLine(service, u, r, lc, names)) >= 0);
        } catch (IOException x) {
            fail(service, "Error reading configuration file", x);
        } finally {
            try {
                if (r != null) r.close();
                if (in != null) in.close();
            } catch (IOException y) {
                fail(service, "Error closing configuration file", y);
            }
        }
        // 服务提供者名称列表的迭代器
        return names.iterator();
    }

    // Private inner class implementing fully-lazy provider lookup
    // 实现完全惰性的服务提供者查找
    private class LazyIterator
        implements Iterator<S>
    {

        /**
         * 服务类
         */
        Class<S> service;
        /**
         * 服务提供者的类加载器
         */
        ClassLoader loader;
        /**
         * 配置文件集
         */
        Enumeration<URL> configs = null;
        /**
         * 具体服务提供者全限定名的迭代器
         */
        Iterator<String> pending = null;
        /**
         * 下一个待加载的服务提供者全限定名
         */
        String nextName = null;

        private LazyIterator(Class<S> service, ClassLoader loader) {
            this.service = service;
            this.loader = loader;
        }

        /**
         * 是否还有下一个服务提供者。
         */
        private boolean hasNextService() {
            if (nextName != null) {
                return true;
            }
            if (configs == null) {
                try {
                    // 服务提供者配置文件全名
                    String fullName = PREFIX + service.getName();
                    if (loader == null)
                        // 从系统类加载器获取配置文件集
                        configs = ClassLoader.getSystemResources(fullName);
                    else
                        // 从当前类加载器获取配置文件集
                        configs = loader.getResources(fullName);
                } catch (IOException x) {
                    fail(service, "Error locating configuration files", x);
                }
            }
            while ((pending == null) || !pending.hasNext()) {
                if (!configs.hasMoreElements()) {
                    return false;
                }
                // 将给定URL的内容解析为提供者的配置文件
                pending = parse(service, configs.nextElement());
            }
            // 下一个待加载的服务提供者全限定名
            nextName = pending.next();
            return true;
        }

        /**
         * 下一个服务提供者。
         */
        private S nextService() {
            if (!hasNextService())
                throw new NoSuchElementException();
            // 下一个待加载的服务提供者全限定名
            String cn = nextName;
            nextName = null;
            // 服务提供者类对象
            Class<?> c = null;
            try {
                // 通过反射Class.forName(className)加载服务提供者类
                c = Class.forName(cn, false, loader);
            } catch (ClassNotFoundException x) {
                fail(service,
                     "Provider " + cn + " not found");
            }
            // 检查是服务的本身或子类
            if (!service.isAssignableFrom(c)) {
                fail(service,
                     "Provider " + cn  + " not a subtype");
            }
            try {
                // 通过Class.newInstance()实例化服务提供者对象
                S p = service.cast(c.newInstance());
                // 插入到服务提供者列表的缓存
                providers.put(cn, p);
                return p;
            } catch (Throwable x) {
                fail(service,
                     "Provider " + cn + " could not be instantiated",
                     x);
            }
            throw new Error();          // This cannot happen
        }

        @Override
        public boolean hasNext() {
            if (acc == null) {
                return hasNextService();
            } else {
                PrivilegedAction<Boolean> action = new PrivilegedAction<Boolean>() {
                    public Boolean run() { return hasNextService(); }
                };
                // 访问控制
                return AccessController.doPrivileged(action, acc);
            }
        }

        @Override
        public S next() {
            if (acc == null) {
                return nextService();
            } else {
                PrivilegedAction<S> action = new PrivilegedAction<S>() {
                    public S run() { return nextService(); }
                };
                // 访问控制
                return AccessController.doPrivileged(action, acc);
            }
        }

        public void remove() {
            // 不支持的操作
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Lazily loads the available providers of this loader's service.
     * 延迟加载可用的服务提供者。
     *
     * <p> The iterator returned by this method first yields all of the
     * elements of the provider cache, in instantiation order.  It then lazily
     * loads and instantiates any remaining providers, adding each one to the
     * cache in turn.
     *
     * <p> To achieve laziness the actual work of parsing the available
     * provider-configuration files and instantiating providers must be done by
     * the iterator itself.  Its {@link java.util.Iterator#hasNext hasNext} and
     * {@link java.util.Iterator#next next} methods can therefore throw a
     * {@link ServiceConfigurationError} if a provider-configuration file
     * violates the specified format, or if it names a provider class that
     * cannot be found and instantiated, or if the result of instantiating the
     * class is not assignable to the service type, or if any other kind of
     * exception or error is thrown as the next provider is located and
     * instantiated.  To write robust code it is only necessary to catch {@link
     * ServiceConfigurationError} when using a service iterator.
     *
     * <p> If such an error is thrown then subsequent invocations of the
     * iterator will make a best effort to locate and instantiate the next
     * available provider, but in general such recovery cannot be guaranteed.
     *
     * <blockquote style="font-size: smaller; line-height: 1.2"><span
     * style="padding-right: 1em; font-weight: bold">Design Note</span>
     * Throwing an error in these cases may seem extreme.  The rationale for
     * this behavior is that a malformed provider-configuration file, like a
     * malformed class file, indicates a serious problem with the way the Java
     * virtual machine is configured or is being used.  As such it is
     * preferable to throw an error rather than try to recover or, even worse,
     * fail silently.</blockquote>
     *
     * <p> The iterator returned by this method does not support removal.
     * Invoking its {@link java.util.Iterator#remove() remove} method will
     * cause an {@link UnsupportedOperationException} to be thrown.
     *
     * @implNote When adding providers to the cache, the {@link #iterator
     * Iterator} processes resources in the order that the {@link
     * java.lang.ClassLoader#getResources(java.lang.String)
     * ClassLoader.getResources(String)} method finds the service configuration
     * files.
     *
     * @return  An iterator that lazily loads providers for this loader's
     *          service
     */
    @Override
    public Iterator<S> iterator() {
        return new Iterator<S>() {

            // 已知的服务提供者列表的迭代器
            Iterator<Map.Entry<String,S>> knownProviders
                = providers.entrySet().iterator();

            @Override
            public boolean hasNext() {
                if (knownProviders.hasNext())
                    return true;
                return lookupIterator.hasNext();
            }

            @Override
            public S next() {
                if (knownProviders.hasNext())
                    return knownProviders.next().getValue();
                return lookupIterator.next();
            }

            public void remove() {
                // 不支持的操作
                throw new UnsupportedOperationException();
            }

        };
    }

    /**
     * Creates a new service loader for the given service type and class
     * loader.
     * 为给定的服务类型和类加载器创建一个新的服务加载器。
     *
     * @param  <S> the class of the service type
     *
     * @param  service
     *         The interface or abstract class representing the service
     *
     * @param  loader
     *         The class loader to be used to load provider-configuration files
     *         and provider classes, or <tt>null</tt> if the system class
     *         loader (or, failing that, the bootstrap class loader) is to be
     *         used
     *
     * @return A new service loader
     */
    public static <S> ServiceLoader<S> load(Class<S> service,
                                            ClassLoader loader)
    {
        // 创建一个新的服务加载器对象
        return new ServiceLoader<>(service, loader);
    }

    /**
     * Creates a new service loader for the given service type, using the
     * current thread's {@linkplain java.lang.Thread#getContextClassLoader
     * context class loader}.
     * 使用当前线程的上下文类加载器。
     *
     * <p> An invocation of this convenience method of the form
     *
     * <blockquote><pre>
     * ServiceLoader.load(<i>service</i>)</pre></blockquote>
     *
     * is equivalent to
     *
     * <blockquote><pre>
     * ServiceLoader.load(<i>service</i>,
     *                    Thread.currentThread().getContextClassLoader())</pre></blockquote>
     *
     * @param  <S> the class of the service type
     *
     * @param  service
     *         The interface or abstract class representing the service
     *
     * @return A new service loader
     */
    public static <S> ServiceLoader<S> load(Class<S> service) {
        // 当前线程的上下文类加载器
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return ServiceLoader.load(service, cl);
    }

    /**
     * Creates a new service loader for the given service type, using the
     * extension class loader.
     * 使用扩展类加载器。
     *
     * <p> This convenience method simply locates the extension class loader,
     * call it <tt><i>extClassLoader</i></tt>, and then returns
     *
     * <blockquote><pre>
     * ServiceLoader.load(<i>service</i>, <i>extClassLoader</i>)</pre></blockquote>
     *
     * <p> If the extension class loader cannot be found then the system class
     * loader is used; if there is no system class loader then the bootstrap
     * class loader is used.
     *
     * <p> This method is intended for use when only installed providers are
     * desired.  The resulting service will only find and load providers that
     * have been installed into the current Java virtual machine; providers on
     * the application's class path will be ignored.
     *
     * @param  <S> the class of the service type
     *
     * @param  service
     *         The interface or abstract class representing the service
     *
     * @return A new service loader
     */
    public static <S> ServiceLoader<S> loadInstalled(Class<S> service) {
        // 系统类加载器
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        // 扩展类加载器
        ClassLoader prev = null;
        while (cl != null) {
            prev = cl;
            // 系统类加载器的祖先(引导类加载器)
            cl = cl.getParent();
        }
        return ServiceLoader.load(service, prev);
    }

    /**
     * Returns a string describing this service.
     *
     * @return  A descriptive string
     */
    @Override
    public String toString() {
        return "java.util.ServiceLoader[" + service.getName() + "]";
    }

}
