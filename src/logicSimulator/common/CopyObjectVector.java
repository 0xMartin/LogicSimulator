/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.common;

import logicSimulator.Tools;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.WorkSpaceObject;

/**
 *
 * @author Martin
 */
public class CopyObjectVector {

    public final List<WorkSpaceObject> objects;

    public final Point cursor;

    public CopyObjectVector(List<WorkSpaceObject> objects, Point cursor) {
        this.cursor = Tools.copy(cursor);
        //copy all selected object
        this.objects = new ArrayList<>();
        objects.stream().forEach((obj) -> {
            if (obj.isSelected()) {
                try {
                    WorkSpaceObject copy = Tools.clone(obj);
                    if (copy != null) {
                        this.objects.add(copy);
                    }
                } catch (CloneNotSupportedException ex) {
                }
            }
        });
    }

    public List<WorkSpaceObject> getObjects() {
        List<WorkSpaceObject> ret = new ArrayList<>();
        objects.stream().forEach((obj) -> {
            try {
                WorkSpaceObject copy = Tools.clone(obj);
                if (copy != null) {
                    ret.add(copy);
                }
            } catch (CloneNotSupportedException ex) {
            }
        });
        return ret;
    }

}
