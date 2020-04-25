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
package logicSimulator.projectFile.documentStyleAction;

import java.awt.Color;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import java.awt.event.ActionEvent;

public class BackgroundColorAction extends StyledEditorKit.StyledTextAction {

    private final Color color;
    
    public BackgroundColorAction(Color color) {
        super(StyleConstants.Background.toString());
        this.color = color;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JEditorPane editor = getEditor(actionEvent);
        if (editor != null) {
            SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
            StyleConstants.setBackground(simpleAttributeSet, this.color);
            setCharacterAttributes(editor, simpleAttributeSet, false);
        }
    }
}
