/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * LogicSimulatorCore core of program In core are all LSComponents = render
 * core, compute core, window, ...
 *
 * @author Martin
 */
public interface LogicSimulatorCore {

    //properties
    public static final int WORK_SPACE_STEP = 14;   //default 14 if you change this you must remodeling all components
    public static final int OBJECT_NULL_POSITION = -9999;

    //file types
    public static final String PROJECT_FILE_TYPE = "phl";
    public static final String WORKSPACE_FILE_TYPE = "lwf";
    public static final String MODULE_FILE_TYPE = "mf";
    public static final String HEX_FILE_TYPE = "hef";

    //properties files
    public static final String PROPT_PROJECTS = "projects.propt";
    public static final String PROPT_WINDOW = "window.propt";
    public static final String PROPT_COMPUTING = "computing.propt";

    /**
     * Get system time
     *
     * @param pattern DateTimeFormatter pattern
     * @return String - time
     */
    public static String getDate(String pattern) {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(pattern);
        return date.format(dateFormat);
    }

    public static enum MessageType {
        INFO,
        WARNING,
        ERROR
    }

    //get all main logic simulator components
    public List<LSComponent> getLSComponents();

    //send message from lscomponent to core
    public void sendMessage(MessageType type, String message);

}
