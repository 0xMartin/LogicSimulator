/*
 * Logic simlator
 * Author: Martin Krcma
 */
package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.projectFile.HEXEditor;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.projectFile.ModuleEditor;
import logicSimulator.PFMode;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.projectFile.WorkSpace;
import logicSimulator.common.Propertie;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.LogicModule;
import logicSimulator.objects.displays.VectorScreen;

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
        //set location of project main file
        this.project.setFile(file);

        //create project main file
        PropertieWriter writer = new PropertieWriter(file.toString());
        List<Propertie> propts = new ArrayList<>();

        //project file linker
        for (int i = 0; i < this.project.getProjectFiles().size(); i++) {
            ProjectFile pf = this.project.getProjectFiles().get(i);
            if (pf == null) {
                continue;
            }
            //file type of this item
            String type = "";
            if (pf instanceof WorkSpace) {
                type = LogicSimulatorCore.WORKSPACE_FILE_TYPE;
            } else if (pf instanceof ModuleEditor) {
                type = LogicSimulatorCore.MODULE_FILE_TYPE;
            } else if (pf instanceof HEXEditor) {
                type = LogicSimulatorCore.HEX_FILE_TYPE;
            }
            //add to linker
            if (pf.getComp() != null) {
                propts.add(
                        new Propertie(
                                type + i,
                                pf.getComp().getName() + "." + type
                        )
                );
            }
        }

        //visible (on the top of tabbed pane view)
        String sel = "";
        for (int i = 0; i < this.project.getProjectFiles().size(); i++) {
            ProjectFile pf = this.project.getProjectFiles().get(i);
            if (pf.isVisible()) {
                sel += i + ",";
            }
        }

        propts.add(new Propertie("visible", sel));
        writer.writeFile(propts);

        //opened project files (in some tabbed pane)
        String opened_left = "", opened_right = "";
        for (int i = 0; i < this.project.getProjectFiles().size(); i++) {
            ProjectFile pf = this.project.getProjectFiles().get(i);
            if (pf.getPFMode().OPENED) {
                if (pf.getPFMode().LEFT_SIDE) {
                    opened_left += i + ",";
                } else {
                    opened_right += i + ",";
                }
            }
        }
        propts.add(new Propertie("opened_left", opened_left));
        propts.add(new Propertie("opened_right", opened_right));

        //write propt file
        writer.writeFile(propts);

        //create files for all project file
        for (ProjectFile pf : project.getProjectFiles()) {
            if (pf instanceof WorkSpace) {
                //WORKSPACE
                WorkSpace ws = (WorkSpace) pf;
                FileOutputStream fileOut = new FileOutputStream(
                        new File(file.getAbsoluteFile().getParent() + "/"
                                + ws.getName() + "." + LogicSimulatorCore.WORKSPACE_FILE_TYPE)
                );

                //delete all useless data and not serializable data
                deleteDateBeforeSave(ws.getObjects());

                try (ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
                    //all object of workspace write to one file
                    objectOut.writeObject(ws.getObjects());
                }
            } else if (pf instanceof ModuleEditor) {
                //MODULE EDITOR
                ModuleEditor me = (ModuleEditor) pf;
                FileOutputStream fileOut = new FileOutputStream(
                        new File(file.getAbsoluteFile().getParent() + "/"
                                + me.getName() + "." + LogicSimulatorCore.MODULE_FILE_TYPE)
                );
                try (ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
                    //write module to file
                    LogicModule logicModule = me.getModule();

                    //delete all useless data and not serializable data
                    deleteDateBeforeSave(logicModule.getLogicModel());

                    objectOut.writeObject(logicModule);
                }
            } else if (pf instanceof HEXEditor) {
                //HEX EDITOR
                HEXEditor he = (HEXEditor) pf;
                FileOutputStream fileOut = new FileOutputStream(
                        new File(file.getAbsoluteFile().getParent() + "/"
                                + he.getName() + "." + LogicSimulatorCore.HEX_FILE_TYPE)
                );

                try (ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
                    //all object of workspace write to one file
                    objectOut.writeObject(he.getSaveData());
                }
            }
        }

        addToLastProjectList();
    }

    /**
     * Add link on this project to last opened project list
     *
     * @throws Exception
     */
    public void addToLastProjectList() throws Exception {
        if (this.project == null) {
            return;
        }
        if (this.project.getFile() == null) {
            return;
        }

        //add to last project list
        PropertieWriter writer = new PropertieWriter(LogicSimulatorCore.PROPT_PROJECTS);
        List<Propertie> propts = new ArrayList<>();
        try {
            PropertieReader reader = new PropertieReader(LogicSimulatorCore.PROPT_PROJECTS);
            propts = reader.readFile();
            //if this project is in the list then must delete it
            for (int i = 0; i < propts.size(); i++) {
                Propertie propt = propts.get(i);
                if (propt.getValueString().split(";")[0].equals(this.project.getFile().toString())) {
                    propts.remove(propt);
                    i = -1;
                }
            }
        } catch (Exception ex) {
        }
        //name: project name value: project location;time
        propts.add(new Propertie(this.project.getName(), this.project.getFile().toString() + ";"
                + LogicSimulatorCore.getDate("HH:mm:ss - dd.MM.yyyy")));
        writer.writeFile(propts);
    }

    /**
     * Open project
     *
     * @param projectFile File - main project file
     * @throws Exception
     */
    public void open(String projectFile) throws Exception {
        //read project file
        PropertieReader reader = new PropertieReader(projectFile);
        List<Propertie> subfiles = reader.readFile();

        this.project.setFile(new File(projectFile));

        List<Integer> visible = new ArrayList<>();
        String opened_left = "", opened_right = "";

        String dirPath = (new File(projectFile)).getAbsoluteFile().getParent() + "\\";

        //load all project files
        for (Propertie propt : subfiles) {
            File f = new File(dirPath + propt.getValueString());
            if (propt.getName().startsWith(LogicSimulatorCore.WORKSPACE_FILE_TYPE)) {
                //workspace
                Tools.loadWorkspaceFromFile(f, project);
            } else if (propt.getName().startsWith(LogicSimulatorCore.MODULE_FILE_TYPE)) {
                //module
                Tools.loadModuleEditorFromFile(f, project);
            } else if (propt.getName().startsWith(LogicSimulatorCore.HEX_FILE_TYPE)) {
                //hex
                Tools.loadHEXEditorFromFile(f, project);
            } else if (propt.getName().equals("visible")) {
                //store visible index
                String[] arr = propt.getValueString().split(",");
                for (String arr1 : arr) {
                    try {
                        visible.add(Integer.parseInt(arr1));
                    } catch (NumberFormatException ex) {
                    }
                }
            } else if (propt.getName().equals("opened_left")) {
                //store all opened workspaces
                opened_left = propt.getValueString();
            } else if (propt.getName().equals("opened_right")) {
                //store all opened workspaces
                opened_right = propt.getValueString();
            }
        }

        //set visble for project files
        visible.stream()
                .filter((v) -> (v < this.project.getProjectFiles().size() && v >= 0))
                .forEachOrdered((v) -> {
                    this.project.getProjectFiles().get(v).setVisible(true);
                });

        //set open for all opened workspaces 
        //left
        String[] indexes = opened_left.split(",");
        for (String index : indexes) {
            try {
                int i = Integer.parseInt(index);
                if (i >= 0 && i < this.project.getProjectFiles().size()) {
                    PFMode pfm = this.project.getProjectFiles().get(i).getPFMode();
                    pfm.OPENED = true;
                    pfm.LEFT_SIDE = true;
                }
            } catch (NumberFormatException ex) {
            }
        }
        //right
        indexes = opened_right.split(",");
        for (String index : indexes) {
            try {
                int i = Integer.parseInt(index);
                if (i >= 0 && i < this.project.getProjectFiles().size()) {
                    PFMode pfm = this.project.getProjectFiles().get(i).getPFMode();
                    pfm.OPENED = true;
                    pfm.LEFT_SIDE = false;
                }
            } catch (NumberFormatException ex) {
            }
        }

        //add logic models (workspaces) for all modules
        for (ProjectFile pf : this.project.getProjectFiles()) {
            if (pf instanceof ModuleEditor) {
                //logic module name of last logic model
                LogicModule module = ((ModuleEditor) pf).getModule();
                String lmName = module.getNameOfLastLogicModel();
                //find workspace
                for (ProjectFile pf2 : this.project.getProjectFiles()) {
                    if (pf == pf2) {
                        continue;
                    }
                    if (pf2 instanceof WorkSpace) {
                        if (lmName.equals(((WorkSpace) pf2).getName())) {
                            //set pf2 workspace as logic model for "module"
                            module.setLogicModel((WorkSpace) pf2);
                            //set pf2 workspace for module editor
                            ((ModuleEditor) pf).setWorkSpace((WorkSpace) pf2);
                            break;
                        }
                    }
                }
            }
        }

    }

    /**
     * Rename file on disk
     *
     * @param oldName Old name of file
     * @param newName New name of file
     */
    public void renameFile(String oldName, String newName) {
        try {
            //rename file
            File f = new File(this.project.getFile().getAbsoluteFile().getParent() + "/" + oldName);
            f.renameTo(new File(this.project.getFile().getAbsoluteFile().getParent() + "/" + newName));

            //chage link in main project file
            PropertieReader reader = new PropertieReader(this.project.getFile().toString());
            List<Propertie> propts = reader.readFile();
            for (Propertie p : propts) {
                //if old name = fName -> rename
                if (p.getValueString().equals(oldName)) {
                    p.setValue(newName);
                    break;
                }
            }
            //save
            PropertieWriter write = new PropertieWriter(this.project.getFile().toString());
            write.writeFile(propts);
        } catch (Exception ex) {
        }
    }

    /**
     * Delete file of project from disk and from project link list
     *
     * @param file File name and file type
     */
    public void deleteFile(String file) {
        try {
            //rename file
            File f = new File(this.project.getFile().getAbsoluteFile().getParent() + "/" + file);
            System.out.println(">" + f);
            f.delete();
            //remove file from link list of project
            PropertieReader reader = new PropertieReader(this.project.getFile().toString());
            List<Propertie> propts = reader.readFile();
            for (Propertie p : propts) {
                if (p.getValueString().equals(file)) {
                    propts.remove(p);
                    break;
                }
            }
            //save
            PropertieWriter write = new PropertieWriter(this.project.getFile().toString());
            write.writeFile(propts);
        } catch (Exception ex) {
        }
    }

    /**
     * Delete all useless data and non serializable data
     *
     * @param objects list with objects
     */
    private void deleteDateBeforeSave(List<WorkSpaceObject> objects) {
        objects.stream().forEach((obj) -> {
            if (obj instanceof VectorScreen) {
                ((VectorScreen) obj).clearData();
            }
        });
    }

}
