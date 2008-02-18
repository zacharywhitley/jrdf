package org.jrdf.util.test;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Given a block/closure expects that the code will throw the expected exception with the expected message.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public class AssertThrows {

    public static void assertThrows(Class exceptionClass, String message, Block block) {
        try {
            block.execute();
            Assert.fail("Failed to throw exception: " + exceptionClass + " with message: " + message);
        }
        catch (AssertionFailedError e) {
            throw e;
        }
        catch (Throwable t) {
            checkExceptionClass(exceptionClass, t);
            Assert.assertEquals(message, t.getMessage());
        }
    }

    public static void assertThrows(Class exceptionClass, Block block) {
        try {
            block.execute();
            Assert.fail("Failed to throw exception: " + exceptionClass);
        }
        catch (AssertionFailedError e) {
            throw e;
        }
        catch (Throwable t) {
            checkExceptionClass(exceptionClass, t);
        }
    }

    private static void checkExceptionClass(Class exceptionClass, Throwable t) {
        StringWriter writer = new StringWriter();
        PrintWriter s = new PrintWriter(writer);
        t.printStackTrace(s);
        Assert.assertEquals("Got exception: " + writer.toString(), exceptionClass, t.getClass());
    }

    public interface Block {

        void execute() throws Throwable;
    }
}