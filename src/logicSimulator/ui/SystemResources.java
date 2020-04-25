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
package logicSimulator.ui;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import window.components.Icon16;
import window.components.Icon20;

/**
 * All system resources are here, instance of this must be create before system
 * start. Resources: Images, Icons, Text data, scripts, ...
 *
 * @author Martin
 */
public class SystemResources {

    public static BufferedImage PROJECT_WIZARD_BG,
            ICON;

    public static Icon16 LWF_ICON,
            MF_ICON,
            HEF_ICON,
            PACKAGE_ICON,
            DF_ICON;

    public static Icon20 TOOLBAR_CURCOR,
            TOOLBAR_CLICKER,
            TOOLBAR_NEWFILE,
            TOOLBAR_NEWPROJECT,
            TOOLBAR_OPENPROJECT,
            TOOLBAR_REDO,
            TOOLBAR_UNDO,
            TOOLBAR_SETTINGS,
            TOOLBAR_SAVE,
            TOOLBAR_RUN,
            TOOLBAR_STOP,
            TOOLBAR_STEP,
            TOOLBAR_HELP,
            TOOLBAR_TXT,
            TOOLBAR_DELETE,
            TOOLBAR_ROTATE,
            TOOLBAR_DELETE_WIRE,
            TOOLBAR_REPLACE,
            TOOLBAR_ALIGN_VERTICAL,
            TOOLBAR_ALIGN_HORISONTAL,
            TOOLBAR_LINE,
            TOOLBAR_SELECT_ALL,
            TOOLBAR_ZOOM_IN,
            TOOLBAR_ZOOM_OUT,
            TOOLBAR_RECTANGLE,
            TOOLBAR_CIRCLE,
            TOOLBAR_CURVE,
            TOOLBAR_COMMENT,
            TOOLBAR_UNCOMMENT,
            TOOLBAR_TRASLATOR_CONFIG,
            TOOLBAR_UPLOAD,
            TOOLBAR_APPLY,
            ICON_ROM,
            TOOLBAR_TO_HEX,
            TOOLBAR_ALIGN_LEFT,
            TOOLBAR_ALIGN_CENTER,
            TOOLBAR_ALIGN_RIGHT,
            TOOLBAR_ALIGN_JUSTIFIED,
            TOOLBAR_TABLE, 
            TOOLBAR_HORISONTAL_LINE;

    public SystemResources() throws Exception {

        //icon
        SystemResources.ICON = ImageIO.read(this.getClass().getResource("/src/img/Icons/icon.png"));

        //project wizzard
        SystemResources.PROJECT_WIZARD_BG = ImageIO.read(this.getClass().getResource("/src/img/projectWizardBg.png"));

        //project file icons
        SystemResources.LWF_ICON = new Icon16(this.getClass().getResource("/src/img/Icons/lwfIcon.png"));
        SystemResources.MF_ICON = new Icon16(this.getClass().getResource("/src/img/Icons/mfIcon.png"));
        SystemResources.HEF_ICON = new Icon16(this.getClass().getResource("/src/img/Icons/hefIcon.png"));
        SystemResources.DF_ICON = new Icon16(this.getClass().getResource("/src/img/Icons/docIcon.png"));

        //main toolbar
        SystemResources.TOOLBAR_CURCOR = new Icon20(this.getClass().getResource("/src/img/toolbar/cursor.png"));
        SystemResources.TOOLBAR_CLICKER = new Icon20(this.getClass().getResource("/src/img/toolbar/clicker.png"));
        SystemResources.TOOLBAR_NEWFILE = new Icon20(this.getClass().getResource("/src/img/toolbar/new_file.png"));
        SystemResources.TOOLBAR_NEWPROJECT = new Icon20(this.getClass().getResource("/src/img/toolbar/new_project.png"));
        SystemResources.TOOLBAR_OPENPROJECT = new Icon20(this.getClass().getResource("/src/img/toolbar/open_project.png"));
        SystemResources.TOOLBAR_SAVE = new Icon20(this.getClass().getResource("/src/img/toolbar/save.png"));
        SystemResources.TOOLBAR_REDO = new Icon20(this.getClass().getResource("/src/img/toolbar/redo.png"));
        SystemResources.TOOLBAR_UNDO = new Icon20(this.getClass().getResource("/src/img/toolbar/undo.png"));
        SystemResources.TOOLBAR_SETTINGS = new Icon20(this.getClass().getResource("/src/img/toolbar/settings.png"));
        SystemResources.TOOLBAR_RUN = new Icon20(this.getClass().getResource("/src/img/toolbar/run.png"));
        SystemResources.TOOLBAR_STOP = new Icon20(this.getClass().getResource("/src/img/toolbar/stop.png"));
        SystemResources.TOOLBAR_STEP = new Icon20(this.getClass().getResource("/src/img/toolbar/step.png"));
        SystemResources.TOOLBAR_HELP = new Icon20(this.getClass().getResource("/src/img/toolbar/help.png"));

        //workspace
        SystemResources.TOOLBAR_TXT = new Icon20(this.getClass().getResource("/src/img/toolbar/txt.png"));
        SystemResources.TOOLBAR_DELETE = new Icon20(this.getClass().getResource("/src/img/toolbar/delete.png"));
        SystemResources.TOOLBAR_ROTATE = new Icon20(this.getClass().getResource("/src/img/toolbar/rotate.png"));
        SystemResources.TOOLBAR_DELETE_WIRE = new Icon20(this.getClass().getResource("/src/img/toolbar/deleteWire.png"));
        SystemResources.TOOLBAR_REPLACE = new Icon20(this.getClass().getResource("/src/img/toolbar/deleteWire.png"));
        SystemResources.TOOLBAR_ALIGN_VERTICAL = new Icon20(this.getClass().getResource("/src/img/toolbar/align_vertical.png"));
        SystemResources.TOOLBAR_ALIGN_HORISONTAL = new Icon20(this.getClass().getResource("/src/img/toolbar/align_horisontal.png"));
        SystemResources.TOOLBAR_ZOOM_IN = new Icon20(this.getClass().getResource("/src/img/toolbar/zoom_in.png"));
        SystemResources.TOOLBAR_ZOOM_OUT = new Icon20(this.getClass().getResource("/src/img/toolbar/zoom_out.png"));
        SystemResources.ICON_ROM = new Icon20(this.getClass().getResource("/src/img/toolbar/rom.png"));

        //model editor
        SystemResources.TOOLBAR_LINE = new Icon20(this.getClass().getResource("/src/img/toolbar/line.png"));
        SystemResources.TOOLBAR_SELECT_ALL = new Icon20(this.getClass().getResource("/src/img/toolbar/select_all.png"));
        SystemResources.TOOLBAR_RECTANGLE = new Icon20(this.getClass().getResource("/src/img/toolbar/rectangle.png"));
        SystemResources.TOOLBAR_CIRCLE = new Icon20(this.getClass().getResource("/src/img/toolbar/circle.png"));
        SystemResources.TOOLBAR_CURVE = new Icon20(this.getClass().getResource("/src/img/toolbar/curve.png"));
        SystemResources.TOOLBAR_APPLY = new Icon20(this.getClass().getResource("/src/img/toolbar/apply.png"));

        //hex editor
        SystemResources.TOOLBAR_COMMENT = new Icon20(this.getClass().getResource("/src/img/toolbar/comment.png"));
        SystemResources.TOOLBAR_UNCOMMENT = new Icon20(this.getClass().getResource("/src/img/toolbar/uncomment.png"));
        SystemResources.TOOLBAR_TRASLATOR_CONFIG = new Icon20(this.getClass().getResource("/src/img/toolbar/translatorConfig.png"));
        SystemResources.TOOLBAR_UPLOAD = new Icon20(this.getClass().getResource("/src/img/toolbar/upload.png"));
        SystemResources.TOOLBAR_TO_HEX = new Icon20(this.getClass().getResource("/src/img/toolbar/to_hex.png"));

        //documentation
        SystemResources.TOOLBAR_ALIGN_LEFT = new Icon20(this.getClass().getResource("/src/img/toolbar/align_left.png"));
        SystemResources.TOOLBAR_ALIGN_CENTER = new Icon20(this.getClass().getResource("/src/img/toolbar/align_center.png"));
        SystemResources.TOOLBAR_ALIGN_RIGHT = new Icon20(this.getClass().getResource("/src/img/toolbar/align_right.png"));
        SystemResources.TOOLBAR_ALIGN_JUSTIFIED = new Icon20(this.getClass().getResource("/src/img/toolbar/align_justified.png"));
        SystemResources.TOOLBAR_TABLE = new Icon20(this.getClass().getResource("/src/img/toolbar/table.png"));
        SystemResources.TOOLBAR_HORISONTAL_LINE = new Icon20(this.getClass().getResource("/src/img/toolbar/horisontal_line.png"));
    }

}
