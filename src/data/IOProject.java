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
import logicSimulator.LogicSimulatorCore;
import logicSimulator.ModuleEditor;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.WorkSpace;
import logicSimulator.common.Propertie;
import logicSimulator.Tools;
import logicSimulator.common.LogicModule;

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

        //project file linker
        for (int i = 0; i < project.getProjectFiles().size(); i++) {
            ProjectFile pf = project.getProjectFiles().get(i);
            if (pf == null) {
                continue;
            }
            //file type of this item
            String type = "";
            if (pf instanceof WorkSpace) {
                type = LogicSimulatorCore.WORKSPACE_FILE_TYPE;
            } else if (pf instanceof ModuleEditor) {
                type = LogicSimulatorCore.MODULE_FILE_TYPE;
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

        //selected
        propts.add(new Propertie("selected",
                this.project.getProjectFiles().indexOf(this.project.getSelectedFile())
        ));
        writer.writeFile(propts);

        //opened
        String opened = "";
        for (int i = 0; i < this.project.getProjectFiles().size(); i++) {
            if (this.project.getProjectFiles().get(i).isOpened()) {
                opened += i + ",";
            }
        }
        propts.add(new Propertie("opened", opened));
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
                try ( ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
                    //all object of workspace write to one file
                    objectOut.writeObject(ws.getObjects());
                }
            } else if (pf instanceof ModuleEditor) {
                //MODULE
                ModuleEditor me = (ModuleEditor) pf;
                FileOutputStream fileOut = new FileOutputStream(
                        new File(file.getAbsoluteFile().getParent() + "/"
                                + me.getName() + "." + LogicSimulatorCore.MODULE_FILE_TYPE)
                );
                try ( ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
                    //write module to file
                    LogicModule logicModule = me.getModule();
                    objectOut.writeObject(logicModule);
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
            for (Propertie propt : propts) {
                if (propt.getValueString().split(";")[0].equals(this.project.getFile().toString())) {
                    propts.remove(propt);
                    break;
                }
            }
        } catch (Exception ex) {
        }
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

        int selected = 0;
        String opened = "";

        String dirPath = (new File(projectFile)).getAbsoluteFile().getParent() + "\\";

        //load all workspaces
        for (Propertie propt : subfiles) {
            try {
                File f = new File(dirPath + propt.getValueString());
                if (propt.getName().startsWith(LogicSimulatorCore.WORKSPACE_FILE_TYPE)) {
                    //workspace
                    Tools.loadWorkspaceFromFile(f, project);
                }
                if (propt.getName().startsWith(LogicSimulatorCore.MODULE_FILE_TYPE)) {
                    //module
                    Tools.loadModuleEditorFromFile(f, project);
                } else if (propt.getName().equals("selected")) {
                    //store select index to variable
                    selected = propt.getValueInt();
                } else if (propt.getName().equals("opened")) {
                    //store all opened workspaces
                    opened = propt.getValueString();
                }
            } catch (Exception ex) {
            }
        }

        //set selected workspace
        if (selected < this.project.getProjectFiles().size() && selected >= 0) {
            this.project.setSelectedFile(this.project.getProjectFiles().get(selected));
        }

        //set open for all opened workspaces 
        String[] indexes = opened.split(",");
        for (String index : indexes) {
            try {
                int i = Integer.parseInt(index);
                if (i >= 0 && i < this.project.getProjectFiles().size()) {
                    this.project.getProjectFiles().get(i).setOpened(true);
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
                            ((ModuleEditor)pf).setWorkSpace((WorkSpace) pf2);
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
     * @param fileName File name
     */
    public void deleteFile(String fileName) {
        try {
            //rename file
            File f = new File(this.project.getFile().getAbsoluteFile().getParent() + "/" + fileName);
            f.delete();
            //remove file from link list of project
            PropertieReader reader = new PropertieReader(this.project.getFile().toString());
            List<Propertie> propts = reader.readFile();
            for (Propertie p : propts) {
                if (p.getValueString().equals(fileName)) {
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

}
