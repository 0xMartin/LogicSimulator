/*
 * Logic simlator
 * Author: Martin Krcma
 */
package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.Project;
import logicSimulator.WorkSpace;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.Propertie;
import logicSimulator.common.Tools;

/**
 * Save or open project
 *
 * @author Martin
 */
public class IOProject {

    /**
     * lsw - logic simulator workspace, hlprj - logic simulator project
     */
    private final Project project;

    public IOProject(Project project) {
        this.project = project;
    }

    /**
     * Save project to directory
     *
     * @param file Project main file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void save(File file) throws IOException, Exception {
        //create project main file
        PropertieWriter writer = new PropertieWriter(file.toString());
        List<Propertie> propts = new ArrayList<>();
        //workspaces
        for (int i = 0; i < project.getWorkSpaces().size(); i++) {
            propts.add(
                    new Propertie(
                            LogicSimulatorCore.WORKSPACE_FILE_TYPE + i,
                            project.getWorkSpaces().get(i).getName() + "." + LogicSimulatorCore.WORKSPACE_FILE_TYPE
                    )
            );
        }
        //selected
        propts.add(new Propertie("seleted",
                this.project.getWorkSpaces().indexOf(this.project.getSelectedWorkspace())
        ));
        writer.writeFile(propts);

        //create files for all workspaces
        for (WorkSpace ws : project.getWorkSpaces()) {
            if (ws != null) {
                FileOutputStream fileOut = new FileOutputStream(
                        new File(file.getAbsoluteFile().getParent() + "/"
                                + ws.getName() + "." + LogicSimulatorCore.WORKSPACE_FILE_TYPE)
                );
                //all object(List<String>) of workspace write to one file
                try ( ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
                    //all object(List<String>) of workspace write to one file
                    objectOut.writeObject(ws.getObjects());
                }
            }
        }

        //add to last project list
        writer = new PropertieWriter(LogicSimulatorCore.PROPT_PROJECTS);
        propts.clear();
        try {
            PropertieReader reader = new PropertieReader(LogicSimulatorCore.PROPT_PROJECTS);
            propts = reader.readFile();
            //if this project is in the list then must delete it
            for(Propertie propt : propts){
                if(propt.getValueString().split(";")[0].equals(file.toString())){
                    propts.remove(propt);
                    break;
                }
            }
        } catch (Exception ex) {
        }
        propts.add(new Propertie(this.project.getName(), file.toString() + ";"
                + LogicSimulatorCore.getDate("HH:mm:ss - dd.MM.yyyy")));
        writer.writeFile(propts);
    }

    public void open(String projectFile) throws Exception {
        //read project file
        PropertieReader reader = new PropertieReader(projectFile);
        List<Propertie> subfiles = reader.readFile();

        this.project.setFile(new File(projectFile));
        
        int selected = 0;

        String dirPath = (new File(projectFile)).getAbsoluteFile().getParent() + "\\";

        //load all workspaces
        for (Propertie propt : subfiles) {
            try {
                if (propt.getName().startsWith(LogicSimulatorCore.WORKSPACE_FILE_TYPE)) {
                    //workspace
                    File f = new File(dirPath + propt.getValueString());
                    FileInputStream fileIn = new FileInputStream(f);
                    try ( ObjectInputStream objectOut = new ObjectInputStream(fileIn)) {
                        //objects
                        List<WorkSpaceObject> objects = (List<WorkSpaceObject>) objectOut.readObject();
                        //workspace, name of worksapce is name of file
                        WorkSpace w = new WorkSpace(Tools.fileName(f.getName()), this.project);
                        objects.stream().forEach((obj) -> {
                            w.getObjects().add(obj);
                        });
                        this.project.getWorkSpaces().add(w);
                    } catch (Exception ex) {
                        Logger.getLogger(IOProject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (propt.getName().equals("selected")) {
                    //store select index to variable
                    selected = propt.getValueInt();
                }
            } catch (Exception ex) {
            }
        }

        //set selected workspace
        if (selected < this.project.getWorkSpaces().size() && selected >= 0) {
            this.project.setSelectedWorkspace(this.project.getWorkSpaces().get(selected));
        }

    }

}
