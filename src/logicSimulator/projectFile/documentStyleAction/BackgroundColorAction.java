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
