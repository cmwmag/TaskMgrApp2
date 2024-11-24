package testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.Test;

import exception.preDateException;

public class ExceptionTest {
	 @Test
	    public void preDateCheck_pastDate_exceptionThrown() {
	        Date date = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
	        Exception exception = assertThrows(preDateException.class, () -> preDateException.preDateCheck(date));
	        assertEquals("You cannot assign a task to a past date.", exception.getMessage());
	    }

}
