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
package logicSimulator.common;

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

    /**
     * Center of copy pattern "center of mass"
     */
    public final Point centerOfCopy;

    public CopyObjectVector(List<WorkSpaceObject> objects) {
        //center of copy
        this.centerOfCopy = new Point(0, 0);
        
        //copy all selected object
        this.objects = new ArrayList<>();
        objects.stream().forEach((obj) -> {
            if (obj.isSelected()) {
                WorkSpaceObject copy = obj.cloneObject();
                if (copy != null) {
                    //add to vector
                    this.objects.add(copy);
                    
                    //computing of center
                    this.centerOfCopy.x += copy.getPosition().x;
                    this.centerOfCopy.y += copy.getPosition().y;
                }
            }
        });
        
        this.centerOfCopy.x /= this.objects.size();
        this.centerOfCopy.y /= this.objects.size();
    }

    public List<WorkSpaceObject> getObjects() {
        List<WorkSpaceObject> ret = new ArrayList<>();
        objects.stream().forEach((obj) -> {
            WorkSpaceObject copy = obj.cloneObject();
            if (copy != null) {
                ret.add(copy);
            }
        });
        return ret;
    }

}
