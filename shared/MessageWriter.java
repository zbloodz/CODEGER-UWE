package magicUWE.shared;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.GUILog;

/**
 * printing a messages or errors to the GUI. Logs every message with log4j, if
 * logger Parameter is not null.
 * 
 * 
 * @author PST LMU
 */
public abstract class MessageWriter {

	private static final GUILog guiLog = Application.getInstance().getGUILog();

	/**
	 * Output: "MagicUWE: " + msg in GUILog Window. Be careful: when MagicDraw
	 * is loading, the GuiLog isn't available!
	 * 
	 * @param msg
	 * @param logger
	 */
	public static void log(String msg, Logger logger) {
		msg = "MagicUWE: " + msg;
		logWithLog4j(logger, msg);
		try {
			guiLog.log(msg);
		} catch (RuntimeException e) {
			logger.error("Wanted to write the following Message in the GUILog Window, "
					+ "but it wasn't initialized: \"" + msg + "\"");
			e.printStackTrace();
		}
	}

	/**
	 * @see GUILog#showError(String)
	 * @param msg
	 * @param logger
	 */
	public static void showError(String msg, Logger logger) {
		logWithLog4j(logger, msg);
		guiLog.showError(msg);
	}

	/**
	 * @see GUILog#showMessage(String)
	 * @param msg
	 * @param logger
	 */
	public static void showMessage(String msg, Logger logger) {
		logWithLog4j(logger, msg);
		guiLog.showMessage(msg);
	}

	/**
	 * @see GUILog#showQuestion(String)
	 * @param msg
	 * @param logger
	 * @return boolean answer
	 */
	public static boolean showQuestion(String msg, Logger logger) {
		boolean answer = guiLog.showQuestion(msg);
		logWithLog4j(logger, msg, answer);
		return answer;
	}

	/**
	 * @see GUILog#showQuestionOkCancel(String)
	 * @param msg
	 * @param logger
	 * @return boolean answer
	 */
	public static boolean showQuestionOkCancel(String msg, Logger logger) {
		boolean answer = guiLog.showQuestionOkCancel(msg);
		logWithLog4j(logger, msg, answer);
		return answer;
	}

	/**
	 * @see GUILog#showWarning(String)
	 * @param msg
	 * @param logger
	 */
	public static void showWarning(String msg, Logger logger) {
		logWithLog4j(logger, msg);
		guiLog.showWarning(msg);
	}

	/**
	 * Message is written to log (prefix "msg to user: ")with the logger from
	 * the caller
	 * 
	 * @param logger
	 *            Logger
	 * @param msg
	 *            Message
	 */
	private static void logWithLog4j(Logger logger, String msg) {
		if (logger != null) {
			logger.debug("Msg to user: \"" + msg + "\"");
		}
	}

	private static void logWithLog4j(Logger logger, String msg, boolean answer) {
		if (logger != null) {
			logger.debug("Msg to user: \"" + msg + "\" Answer from user is: " + answer);
		}
	}
}
