/*
 * Copyright (c) 2005, 2010, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


/**
 * A simple service-provider loading facility.
 *
 * <p/>
 * 一个简单的"服务-提供者"加载设施。
 *
 * <p> A <i>service</i> is a well-known set of interfaces and (usually
 * abstract) classes.  A <i>service provider</i> is a specific implementation
 * of a service.  The classes in a provider typically implement the interfaces
 * and subclass the classes defined in the service itself.  Service providers
 * can be installed in an implementation of the Java platform in the form of
 * extensions, that is, jar files placed into any of the usual extension
 * directories.  Providers can also be made available by adding them to the
 * application's class path or by some other platform-specific means.
 *
 * <p/>
 * 服务是接口和抽象类的集合，服务提供者是服务的特定实现。
 * 提供者中的类实现了服务本身定义的接口和子类，服务提供者可以通过扩展目录的 jar 文件或
 * 应用类路径被安装。
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
 *
 * <p/>
 * 对于加载的目的，一个服务表示单个类型，即单个接口或抽象类。
 * (可以使用一个具体的类，但建议不要这样做。)
 * 给定服务的提供者包含一个或多个具体的类，扩展此服务类型的数据和代码。
 * 提供者类可以按需要创建实际的提供者。
 * 该设施唯一强制要求的是，提供者类必须具有一个无参数的构造函数，以便在加载期间可以被实例化。
 * ({@link java.lang.Class#newInstance()})
 *
 * <p><a name="format"> A service provider is identified by placing a
 * <i>provider-configuration file</i> in the resource directory
 * <tt>META-INF/services</tt>.  The file's name is the fully-qualified <a
 * href="../lang/ClassLoader.html#name">binary name</a> of the service's type.
 * The file contains a list of fully-qualified binary names of concrete
 * provider classes, one per line.  Space and tab characters surrounding each
 * name, as well as blank lines, are ignored.  The comment character is
 * <tt>'#'</tt> (<tt>'&#92;u0023'</tt>, <font size="-1">NUMBER SIGN</font>); on
 * each line all characters following the first comment character are ignored.
 * The file must be encoded in UTF-8.
 *
 * <p/>
 * 服务提供者是由在资源目录META-INF/services中放置提供者配置文件的标识。
 * 该文件的名称是服务类型的完全限定二进制名称，
 * 该文件包含一个具体的提供者类的完全限定二进制名称的列表，每行一个。
 * 每个名称周围的空格和制表符，以及空行，都将被忽略。
 * 在每一行，第一个注释字符后的所有字符都将被忽略。
 * 该文件必须被编码成UTF-8。
 *
 * <p> If a particular concrete provider class is named in more than one
 * configuration file, or is named in the same configuration file more than
 * once, then the duplicates are ignored.  The configuration file naming a
 * particular provider need not be in the same jar file or other distribution
 * unit as the provider itself.  The provider must be accessible from the same
 * class loader that was initially queried to locate the configuration file;
 * note that this is not necessarily the class loader from which the file was
 * actually loaded.
 *
 * <p/>
 * 重复的具体提供者类都会被忽略。
 *
 * <p> Providers are located and instantiated lazily, that is, on demand.  A
 * service loader maintains a cache of the providers that have been loaded so
 * far.  Each invocation of the {@link #iterator iterator} method returns an
 * iterator that first yields all of the elements of the cache, in
 * instantiation order, and then lazily locates and instantiates any remaining
 * providers, adding each one to the cache in turn.  The cache can be cleared
 * via the {@link #reload reload} method.
 *
 * <p/>
 * 提供者在需要时被延迟地定位和实例化(延迟迭代器)。
 * 服务加载器维护一个到目前为止已加载实例化的提供者的缓存。
 * 迭代器方法({@link #iterator iterator})的每次调用返回一个迭代器，它首先会以实例化顺序生成缓存中的所有元素，
 * 然后延迟地定位并实例化剩余的提供者，依次添加每个到缓存中。
 * 通过重新加载方法({@link #reload reload})可清除缓存(热加载机制)。
 *
 * <p> Service loaders always execute in the security context of the caller.
 * Trusted system code should typically invoke the methods in this class, and
 * the methods of the iterators which they return, from within a privileged
 * security context.
 *
 * <p/>
 * 服务加载器始终在调用者的安全上下文中执行。
 * 受信任的系统代码通常应调用此类中的方法，从特权的安全范围内。
 *
 * <p> Instances of this class are not safe for use by multiple concurrent
 * threads.
 *
 * <p/>
 * 本类的实例在多个并发线程中使用是不安全的。
 *
 * <p> Unless otherwise specified, passing a <tt>null</tt> argument to any
 * method in this class will cause a {@link NullPointerException} to be thrown.
 *
 *
 * <p><span style="font-weight: bold; padding-right: 1em">Example (示例)</span>
 * Suppose we have a service type <tt>com.example.CodecSet</tt> which is
 * intended to represent sets of encoder/decoder pairs for some protocol (一些协议的编码器/解码器对).  In
 * this case it is an abstract class (抽象类) with two abstract methods:
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
 * <tt>CodecSet</tt> service then its jar file also contains a file named (1. 包含该命名的文件)
 *
 * <blockquote><pre>
 * META-INF/services/com.example.CodecSet</pre></blockquote>
 *
 * <p> This file contains the single line (2. 此文件包含一行):
 *
 * <blockquote><pre>
 * com.example.impl.StandardCodecs    # Standard codecs</pre></blockquote>
 *
 * <p> The <tt>CodecSet</tt> class creates and saves a single service instance
 * at initialization (3. 在初始化时创建并保存服务实例):
 *
 * <blockquote><pre>
 * private static ServiceLoader&lt;CodecSet&gt; codecSetLoader
 *     = ServiceLoader.load(CodecSet.class);</pre></blockquote>
 *
 * <p> To locate an encoder for a given encoding name it defines a static
 * factory method which iterates through the known and available providers (4. 静态工厂方法：循环访问已知可用的提供者列表),
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
 * <p/>
 * 使用说明：类加载器的类路径包含的远程网络 URLs 会在提供者配置文件搜索过程处理中被取消
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
 * <p/>
 * 当本类尝试将 HTML页面解析为提供者配置文件时，会抛出一个服务配置错误异常。
 *
 * @param  <S>
 *         The type of the service to be loaded by this loader (加载的服务的类型)
 *
 * @author Mark Reinhold
 * @since 1.6
 */
// [SPI机制] 服务加载器
public final class ServiceLoader<S>
        implements Iterable<S>
{

    /**
     * 服务资源目录(提供者配置文件)
     */
    private static final String PREFIX = "META-INF/services/";


    /**
     * 表示正在加载的服务的类或接口
     */
    // The class or interface representing the service being loaded
    private Class<S> service;

    /**
     * 用于定位、加载和实例化服务提供者的类加载器
     */
    // The class loader used to locate, load, and instantiate providers
    private ClassLoader loader;

    /**
     * 以实例化顺序缓存的服务提供者(链式哈希表)
     */
    // Cached providers, in instantiation order
    private Map<String, S> providers = new LinkedHashMap<>();

    /**
     * 延迟查找的迭代器
     */
    // The current lazy-lookup iterator
    private LazyIterator lookupIterator;


    /**
     * Clear this loader's provider cache so that all providers will be
     * reloaded.
     *
     * <p> After invoking this method, subsequent invocations of the {@link
     * #iterator() iterator} method will lazily look up and instantiate
     * providers from scratch, just as is done by a newly-created loader.
     *
     * <p> This method is intended for use in situations in which new providers
     * can be installed into a running Java virtual machine.
     *
     * <p/>
     * 清除该加载器的提供者缓存，以便所有提供者将被重新加载。
     * <p/>
     * 在调用此方法之后，随后迭代器方法的调用将从头开始延迟地查找并实例化提供者，就像是通过一个新创建的加载器。
     * <p/>
     * 本方法适用于这样场景：新的提供者可以安装到一个运行的Java虚拟机中(热部署机制)。
     */
    // 核心方法 清除该加载器的提供者缓存，重新加载所有提供者(热加载机制)
    public void reload() {
        // 清除提供者缓存
        providers.clear();
        // 实例化一个延迟查找的迭代器
        lookupIterator = new LazyIterator(service, loader);
    }

    private ServiceLoader(Class<S> svc, ClassLoader cl) {
        service = svc;
        loader = cl;
        reload(); // 加载所有服务提供者
    }

    private static void fail(Class service, String msg, Throwable cause)
        throws ServiceConfigurationError
    {
        throw new ServiceConfigurationError(service.getName() + ": " + msg,
                                            cause);
    }

    private static void fail(Class service, String msg)
        throws ServiceConfigurationError
    {
        throw new ServiceConfigurationError(service.getName() + ": " + msg);
    }

    private static void fail(Class service, URL u, int line, String msg)
        throws ServiceConfigurationError
    {
        fail(service, u + ":" + line + ": " + msg);
    }

    // Parse a single line from the given configuration file, adding the name
    // on the line to the names list.
    // 解析给定配置文件中的一行，添加行中的名称到名称列表
    private int parseLine(Class service, URL u, BufferedReader r, int lc,
                          List<String> names)
        throws IOException, ServiceConfigurationError
    {
        String ln = r.readLine(); // 单行内容(服务提供者实现类)
        if (ln == null) {
            return -1;
        }
        int ci = ln.indexOf('#'); // 注释字符
        if (ci >= 0) ln = ln.substring(0, ci); // 过滤注释内容
        ln = ln.trim(); // 过滤前后空白字符
        int n = ln.length();
        if (n != 0) {
            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0)) // 中间是否存在空格/制表符
                fail(service, u, lc, "Illegal configuration-file syntax"); // 配置文件的语法非法
            // 校验"服务提供者实现类"名称是否有效
            int cp = ln.codePointAt(0); // 码位
            if (!Character.isJavaIdentifierStart(cp))
                fail(service, u, lc, "Illegal provider-class name: " + ln);
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && (cp != '.'))
                    fail(service, u, lc, "Illegal provider-class name: " + ln);
            }
            // 添加到名称列表
            if (!providers.containsKey(ln) && !names.contains(ln)) // 未实例化或重复名称
                names.add(ln);
        }
        return lc + 1;
    }

    // Parse the content of the given URL as a provider-configuration file.
    // 解析给定的URL的内容(作为提供者配置文件)。
    //
    // @param  service
    //         The service type for which providers are being sought; (正在寻找的提供者的服务类型)
    //         used to construct error detail strings
    //
    // @param  u
    //         The URL naming the configuration file to be parsed (要解析的URL命名配置文件)
    //
    // @return A (possibly empty) iterator that will yield the provider-class
    //         names in the given configuration file that are not yet members
    //         of the returned set ((可能为空)迭代器，将产生给定配置文件中的提供者类名称列表)
    //
    // @throws ServiceConfigurationError
    //         If an I/O error occurs while reading from the given URL, or
    //         if a configuration-file format error is detected (URL 读取异常或配置文件格式错误)
    //
    private Iterator<String> parse(Class service, URL u)
        throws ServiceConfigurationError
    {
        InputStream in = null;
        BufferedReader r = null;
        List<String> names = new ArrayList<>(); // 服务提供者类型名称列表
        try {
            in = u.openStream(); // 文件输入流
            r = new BufferedReader(new InputStreamReader(in, "utf-8"));
            int lc = 1; // 第一行
            while ((lc = parseLine(service, u, r, lc, names)) >= 0); // 循环地读取每一行
        } catch (IOException x) {
            fail(service, "Error reading configuration file", x); // 读取配置文件时出错
        } finally {
            try {
                if (r != null) r.close();
                if (in != null) in.close();
            } catch (IOException y) {
                fail(service, "Error closing configuration file", y); // 关闭配置文件时出错
            }
        }
        return names.iterator();
    }

    // Private inner class implementing fully-lazy provider lookup
    // 私有内部类，实现完全延迟地提供者查找
    private class LazyIterator
            implements Iterator<S>
    {

        /**
         * 服务类型
         */
        Class<S> service;
        /**
         * 类加载器
         */
        ClassLoader loader;
        /**
         * 提供者配置文件列表
         */
        Enumeration<URL> configs = null;
        /**
         * 每个配置文件的提供者类型名称迭代器
         */
        Iterator<String> pending = null;
        /**
         * 下一个提供者类型名称
         */
        String nextName = null;

        private LazyIterator(Class<S> service, ClassLoader loader) {
            this.service = service;
            this.loader = loader;
        }

        // 核心方法 加载所有与该服务相关的提供者配置文件
        @Override
        public boolean hasNext() {
            if (nextName != null) {
                return true;
            }
            if (configs == null) { // 加载所有与该服务相关的提供者配置文件
                try {
                    String fullName = PREFIX + service.getName(); // 配置文件完全路径名称
                    if (loader == null)
                        configs = ClassLoader.getSystemResources(fullName); // 系统类加载器
                    else
                        configs = loader.getResources(fullName);
                } catch (IOException x) {
                    fail(service, "Error locating configuration files", x);
                }
            }
            while ((pending == null) || !pending.hasNext()) { // 一个配置文件已处理好
                if (!configs.hasMoreElements()) {
                    return false;
                }
                pending = parse(service, configs.nextElement()); // 解析下一个提供者配置文件
            }
            nextName = pending.next();
            return true;
        }

        // 核心方法 实例化一个服务提供者对象，并缓存在本地(基于Class.newInstance()反射实现)
        @Override
        public S next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            String cn = nextName; // 下一个服务提供者类型名称
            nextName = null;
            Class<?> c = null;
            try {
                c = Class.forName(cn, false, loader); // 加载类型
            } catch (ClassNotFoundException x) {
                fail(service,
                     "Provider " + cn + " not found"); // 未找到提供者实现类
            }
            if (!service.isAssignableFrom(c)) {
                fail(service,
                     "Provider " + cn  + " not a subtype"); // 提供者不是服务类型的一个子类型
            }
            try {
                // 实例化一个服务提供者对象，并缓存在本地
                S p = service.cast(c.newInstance()); // 基于Class.newInstance()反射实现
                providers.put(cn, p);
                return p;
            } catch (Throwable x) {
                fail(service,
                     "Provider " + cn + " could not be instantiated", // 提供者不能被实例化
                     x);
            }
            throw new Error();          // This cannot happen
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(); // 不支持移除操作
        }

    }

    /**
     * Lazily loads the available providers of this loader's service.
     *
     * <p/>
     * 延迟地加载该服务的可用提供者。
     *
     * <p> The iterator returned by this method first yields all of the
     * elements of the provider cache, in instantiation order.  It then lazily
     * loads and instantiates any remaining providers, adding each one to the
     * cache in turn.
     *
     * <p/>
     * 本方法返回的迭代器会首先产生提供者缓存中的所有元素，以实例化顺序。
     * 然后，才延迟地加载并实例化任何剩余的提供者，并依次添加他们到缓存中。
     *
     * <p> To achieve laziness the actual work of parsing the available
     * provider-configuration files and instantiating providers must be done by
     * the iterator itself.  Its {@link Iterator#hasNext hasNext} and
     * {@link Iterator#next next} methods can therefore throw a
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
     * Invoking its {@link Iterator#remove() remove} method will
     * cause an {@link UnsupportedOperationException} to be thrown.
     *
     * @return  An iterator that lazily loads providers for this loader's
     *          service
     */
    // 核心方法 返回延迟地加载该服务的可用提供者的迭代器
    @Override
    public Iterator<S> iterator() {
        return new Iterator<S>() {

            // 已实例化的服务提供者列表
            Iterator<Map.Entry<String, S>> knownProviders
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

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

    /**
     * Creates a new service loader for the given service type and class
     * loader.
     *
     * <p/>
     * 创建一个给定的服务类型和类加载器的服务加载器。
     *
     * @param  service
     *         The interface or abstract class representing the service (表示服务的接口或抽象类)
     *
     * @param  loader
     *         The class loader to be used to load provider-configuration files
     *         and provider classes, or <tt>null</tt> if the system class
     *         loader (or, failing that, the bootstrap class loader) is to be
     *         used
     *
     * @return A new service loader
     */
    // 核心方法 创建一个给定的服务类型和类加载器的服务加载器
    public static <S> ServiceLoader<S> load(Class<S> service,
                                            ClassLoader loader)
    {
        return new ServiceLoader<>(service, loader);
    }

    /**
     * Creates a new service loader for the given service type, using the
     * current thread's {@linkplain Thread#getContextClassLoader
     * context class loader}.
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
     * @param  service
     *         The interface or abstract class representing the service (表示服务的接口或抽象类)
     *
     * @return A new service loader
     */
    // 核心方法 创建一个给定的服务类型的服务加载器(使用当前线程上下文类加载)
    public static <S> ServiceLoader<S> load(Class<S> service) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return ServiceLoader.load(service, cl);
    }

    /**
     * Creates a new service loader for the given service type, using the
     * extension class loader (使用扩展的类加载器).
     *
     * <p> This convenience method simply locates the extension class loader,
     * call it <tt><i>extClassLoader</i></tt>, and then returns
     *
     * <blockquote><pre>
     * ServiceLoader.load(<i>service</i>, <i>extClassLoader</i>)</pre></blockquote>
     *
     * <p> If the extension class loader cannot be found then the system class
     * loader is used; if there is no system class loader then the bootstrap
     * class loader is used. (系统类加载器/启动类加载器)
     *
     * <p> This method is intended for use when only installed providers are
     * desired.  The resulting service will only find and load providers that
     * have been installed into the current Java virtual machine; providers on
     * the application's class path will be ignored.
     *
     * @param  service
     *         The interface or abstract class representing the service (表示服务的接口或抽象类)
     *
     * @return A new service loader
     */
    public static <S> ServiceLoader<S> loadInstalled(Class<S> service) {
        ClassLoader cl = ClassLoader.getSystemClassLoader(); // 系统类加载器/启动类加载器
        ClassLoader prev = null;
        while (cl != null) {
            prev = cl;
            cl = cl.getParent();
        }
        return ServiceLoader.load(service, prev);
    }

    /**
     * Returns a string describing this service.
     * <p/>
     * 返回描述此服务的字符串。
     *
     * @return  A descriptive string
     */
    @Override
    public String toString() {
        return "java.util.ServiceLoader[" + service.getName() + "]";
    }

}
