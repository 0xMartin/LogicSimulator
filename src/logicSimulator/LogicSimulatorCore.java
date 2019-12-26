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
    public static final int WORK_SPACE_STEP = 20; //must be 20 
    public static final int OBJECT_NULL_POSITION = -9999;

    //files
    public static final String PROJECT_FILE_TYPE = "phl";
    public static final String WORKSPACE_FILE_TYPE = "lwf";
    public static final String MODULE_FILE_TYPE = "mf";
    public static final String PROPT_PROJECTS = "projects.propt";

    public static String getDate(String pattern) {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(pattern);
        return date.format(dateFormat);
    }

    public static enum MessageType {
        INIT_L, //init length 
        INIT, //init mesage
        INFO,
        WARNING,
        ERROR
    }

    //get all main logic simulator components
    public List<LSComponent> getLSComponents();

    //send message from lscomponent to core
    public void sendMessage(MessageType type, String message);

}
