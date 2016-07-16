package jdk7.example.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The try-with-resources Statement (在 try 之后自动关闭资源).
 * <p/>
 * The try-with-resources statement is a try statement that declares one or more resources (声明若干个资源).
 * A resource is as an object that must be closed after the program is finished with it
 * (一个资源必须在结束后被关闭).
 * The try-with-resources statement ensures that each resource is closed at the end of the statement
 * (try-with-resources 语句确保每个资源在语句末被关闭).
 * Any object that implements {@link AutoCloseable java.lang.AutoCloseable},
 * which includes all objects which implement {@link java.io.Closeable},
 * can be used as a resource (任何实现 AutoCloseable 或 Closeable 接口的对象都可以作为一个资源使用).
 *
 * @author xingle
 * @since 2016年07月09日 11:17
 */
public class TryWithResources {

    private static final Logger logger = LoggerFactory.getLogger(TryWithResources.class);


    // Java SE 7 and later, implements the interface java.lang.AutoCloseable
    // 强烈推荐使用！
    /**
     * The following example reads the first line from a file.
     * It uses an instance of BufferedReader to read data from the file.
     * BufferedReader is a resource that must be closed after the program is finished with it:
     */
    public static String readFirstLineFromFile(String path)
            throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) { // 区别点
            return br.readLine(); // the exception thrown
        }
    }

    // Prior to Java SE 7, use a finally block to ensure that a resource is closed
    // JDK 6及之前版本，不推荐使用！
    public static String readFirstLineFromFileWithFinallyBlock(String path)
            throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally { // the exception thrown
            br.close();
        }
    }


    // declare one or more resources in a try-with-resources statement
    // 声明多个资源
    public static void writeToFileZipFileContents(String zipFileName, String outputFileName)
            throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        Path outputFilePath = Paths.get(outputFileName);

        // Open zip file and create output file with try-with-resources statement
        try ( // 区别点
                ZipFile zf = new ZipFile(zipFileName);
                BufferedWriter writer = Files.newBufferedWriter(outputFilePath, charset)
        ) {
            String lineSeparator = System.lineSeparator();

            // Enumerate each entry
            Enumeration<?> entries = zf.entries();
            while (entries.hasMoreElements()) {
                // Get the entry name and write it to the output file
                String zipEntryName = ((ZipEntry) entries.nextElement()).getName() + lineSeparator;
                writer.write(zipEntryName, 0, zipEntryName.length());
            }
        }
    }


    // uses a try-with-resources statement to automatically close a java.sql.Statement object
    public static void viewTable(Connection conn) throws SQLException {
        String query = "select COF_NAME, SUP_ID, PRICE, SALES, TOTAL from COFFEES";

        try (Statement stmt = conn.createStatement()) { // 区别点
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String coffeeName = rs.getString("COF_NAME");
                int supplierId = rs.getInt("SUP_ID");
                BigDecimal price = rs.getBigDecimal("PRICE");
                int sales = rs.getInt("SALES");
                int total = rs.getInt("TOTAL");

                logger.info("{}, {}, {}, {}, {}", coffeeName, supplierId, price, sales, total);
            }
        }
    }

}
