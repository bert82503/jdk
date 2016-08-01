package jdk7.enhancements.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
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

/**
 * Catching Multiple Exception Types and Rethrowing Exceptions with Improved Type Checking.
 * 捕获多个类型的异常并重新抛出改进的类型检查异常。
 * <p/>
 * 涵盖以下主题：
 * <ul>
 *     <li>处理多个异常的类型</li>
 *     <li>重新抛出更具包容性的类型检查的异常</li>
 * </ul>
 *
 * @author xingle
 * @since 2016年07月16日 22:40
 */
public class CatchingMultipleExceptionAndRethrowingExceptions {

    private static final Logger logger = LoggerFactory.getLogger(CatchingMultipleExceptionAndRethrowingExceptions.class);


    /// Handling More Than One Type of Exception.
    public static void viewTable(Connection conn, String outputFileName)
            throws SQLException, IOException {
        Charset charset = StandardCharsets.UTF_8;
        Path outputFilePath = Paths.get(outputFileName);
        try (
                BufferedWriter writer = Files.newBufferedWriter(outputFilePath, charset);
                Statement stmt = conn.createStatement()
        ) {
            String query = "select COF_NAME, SUP_ID, PRICE, SALES, TOTAL from COFFEES";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String coffeeName = rs.getString("COF_NAME");
                int supplierId = rs.getInt("SUP_ID");
                BigDecimal price = rs.getBigDecimal("PRICE");
                int sales = rs.getInt("SALES");
                int total = rs.getInt("TOTAL");

                writer.write(coffeeName + ", " + supplierId + ", " + price + ", " + sales + ", " + total);
            }

        // contains duplicate code in each of the catch blocks
//        } catch (IOException e) {
//            logger.error("IO Exception", e);
//            throw e;
//        } catch (SQLException e) {
//            logger.error("SQL Exception", e);
//            throw e;
//        }
        // 减少代码重复、减少捕获一个过于宽泛的异常的诱惑
        } catch (IOException | SQLException e) { // 区别点
            logger.error("Handling More Than One Type of Exception", e);
            throw e;
        }
    }


    /// Rethrowing Exceptions with More Inclusive Type Checking

    /**
     * Even though the exception parameter of the catch clause, e, is type Exception,
     * the compiler can determine that it is an instance of either FirstException or SecondException:
     */
    public void rethrowException(String exceptionName)
            throws FirstException, SecondException { // 区别点
        try {
            // try block could throw either FirstException or SecondException
            if (exceptionName.equals("First")) {
                throw new FirstException();
            } else {
                throw new SecondException();
            }
        } catch (Exception e) {
            logger.error("rethrow Exception", e);
            throw e;
        }
    }

//    // prior to Java SE 7 (不推荐！)
//    public void rethrowException(String exceptionName)
//            throws Exception { // can only specify the exception type Exception in the throws clause
//        try {
//            // try block could throw either FirstException or SecondException
//            if (exceptionName.equals("First")) {
//                throw new FirstException();
//            } else {
//                throw new SecondException();
//            }
//        } catch (Exception e) {
//            throw e;
//        }
//    }

    static class FirstException extends Exception {
        private static final long serialVersionUID = 4081404095251950996L;
        // First
    }

    static class SecondException extends Exception {
        private static final long serialVersionUID = 6491586964725847382L;
        // Second
    }

}
