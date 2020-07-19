/* 
 * Copyright (C) 2020 Martin Krcma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package logicSimulator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.ui.SystemResources;

/**
 * LogicSimulatorCore core of program In core are all LSComponents = render
 * core, compute core, window, ...
 *
 * @author Martin
 */
public abstract class LogicSimulatorCore extends SystemResources {

    //properties
    public static final int WORK_SPACE_STEP = 14;   //default 14 if you change this you must remodel all components
    public static final int OBJECT_NULL_POSITION = -9999;

    //file types
    public static final String PROJECT_FILE_TYPE = "phl";
    public static final String WORKSPACE_FILE_TYPE = "lwf";
    public static final String MODULE_FILE_TYPE = "mf";
    public static final String HEX_FILE_TYPE = "hef";
    public static final String LIB_FILE_TYPE = "lbc";
    public static final String DOCUMENTATION_FILE_TYPE = "html";

    //library
    public static final String LIBRARY = "lib";
    
    //properties files
    public static final String PROPT_PROJECTS = "projects.propt";
    public static final String PROPT_WINDOW = "window.propt";
    public static final String PROPT_COMPUTING = "computing.propt";

    //all main components
    private final List<LSComponent> components;

    public LogicSimulatorCore() throws Exception {
        this.components = new ArrayList<>();
    }

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

    //get all main logic simulator components
    public List<LSComponent> getLSComponents() {
        return this.components;
    }

}
