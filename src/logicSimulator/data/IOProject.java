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
package logicSimulator.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import logicSimulator.ExceptionLogger;
import logicSimulator.projectFile.HexEditor;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.projectFile.ModuleEditor;
import logicSimulator.PFMode;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.projectFile.WorkSpace;
import logicSimulator.common.Propertie;
import logicSimulator.projectFile.DocumentationEditor;
import logicSimulator.projectFile.Library;
import window.ComponentChooser;
import window.MainWindow;

/**
 * Save or open project
 *
 * @author Martin
 */
public class IOProject {

    //project
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

            //only none library files
            if (pf.isLibFile) {
                continue;
            }

            //add to linker
            if (pf.getComp() != null) {
                String type = Tools.getFileType(pf);
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
            if (pf.getPFMode().VISIBLE) {
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

        //ref components
        List<WorkSpaceObject> l1 = this.project.getRefComponents();
        l1.forEach((obj) -> {
            propts.add(new Propertie("RefComponent", Tools.getComponentName(obj)));
        });

        //write propt file
        writer.writeFile(propts);

        //create backup files for all project files (with IOPF)
        this.project.getProjectFiles().stream()
                .forEach((pf) -> {
                    //only non lib files
                    if (!pf.isLibFile) {
                        try {
                            pf.backUpData(file.getAbsoluteFile().getParent() + "/");
                        } catch (Exception ex) {
                            ExceptionLogger.getInstance().logException(ex);
                        }
                    }
                });

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
        LinkedList<Propertie> propts = new LinkedList<>();
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
        propts.addFirst(new Propertie(this.project.getName(), this.project.getFile().toString() + ";"
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
            try {
                if (propt.getName().startsWith(LogicSimulatorCore.WORKSPACE_FILE_TYPE)) {
                    //workspace ################################################
                    ProjectFile pf = new WorkSpace(Tools.fileName(f.getName()), this.project);
                    pf.restoreData(f);
                    this.project.getProjectFiles().add(pf);

                } else if (propt.getName().startsWith(LogicSimulatorCore.MODULE_FILE_TYPE)) {
                    //module ################################################
                    ProjectFile pf = new ModuleEditor(Tools.fileName(f.getName()), this.project, null);
                    pf.restoreData(f);
                    this.project.getProjectFiles().add(pf);

                } else if (propt.getName().startsWith(LogicSimulatorCore.HEX_FILE_TYPE)) {
                    //hex ################################################
                    ProjectFile pf = new HexEditor(Tools.fileName(f.getName()), this.project);
                    pf.restoreData(f);
                    this.project.getProjectFiles().add(pf);

                } else if (propt.getName().startsWith(LogicSimulatorCore.DOCUMENTATION_FILE_TYPE)) {
                    //doc ################################################
                    ProjectFile pf = new DocumentationEditor(Tools.fileName(f.getName()), this.project);
                    pf.restoreData(f);
                    this.project.getProjectFiles().add(pf);

                } else if (propt.getName().startsWith(LogicSimulatorCore.LIB_FILE_TYPE)) {
                    //lib ################################################
                    ProjectFile pf = new Library(Tools.fileName(f.getName()), this.project);
                    pf.restoreData(f);
                    this.project.getProjectFiles().add(pf);

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
                } else if (propt.getName().equals("RefComponent")) {
                    //ref components
                    this.project.getRefComponents().add(
                            ComponentChooser.selectComponent(propt.getValueString())
                    );
                }
            } catch (Exception ex) {
                ExceptionLogger.getInstance().logException(ex);
            }
        }

        //set visble for project files
        visible.stream()
                .filter((v) -> (v < this.project.getProjectFiles().size() && v >= 0))
                .forEachOrdered((v) -> {
                    this.project.getProjectFiles().get(v).getPFMode().VISIBLE = true;
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

        //link logic models (workspaces) with modules
        for (ProjectFile pf : this.project.getProjectFiles()) {
            if (pf instanceof ModuleEditor) {
                //get name of logic model
                ModuleEditor mEditor = ((ModuleEditor) pf);
                String lmName = mEditor.getLogicModelName();
                //find workspace
                for (ProjectFile pf2 : this.project.getProjectFiles()) {
                    if (pf == pf2) {
                        continue;
                    }
                    if (pf2 instanceof WorkSpace) {
                        if (lmName.equals(((WorkSpace) pf2).getName())) {
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
     * Delete all project from disk
     */
    public void deleteAll() {
        //delete all files of project
        this.project.getProjectFiles().stream().forEach((pf) -> {
            deleteFile(pf.getName() + "." + Tools.getFileType(pf));
        });

        //delete project main file
        deleteFile(this.project.getName() + "." + LogicSimulatorCore.PROJECT_FILE_TYPE);
    }

    /**
     * Delete file of project from disk and from project link list
     *
     * @param file File name and file type (name.hex)
     */
    public void deleteFile(String file) {
        try {
            //rename file
            File f = new File(this.project.getFile().getAbsoluteFile().getParent() + "/" + file);
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
     * Export this project as library
     *
     * @param path Path of jar
     * @param description Description of library
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void exportAsLibrary(File path, String description) throws FileNotFoundException, IOException {
        //add jar to the end (if is needed)
        if (!path.toString().endsWith("." + LogicSimulatorCore.LIBRARY)) {
            path = new File(path.toString() + "." + LogicSimulatorCore.LIBRARY);
        }

        //export project as jar file
        byte[] buffer = new byte[1024];
        try {
            FileOutputStream fos = new FileOutputStream(path);
            try (ZipOutputStream zos = new ZipOutputStream(fos)) {

                //write info file
                zos.putNextEntry(new ZipEntry("info.txt"));
                byte[] infoBuf = description.getBytes();
                zos.write(infoBuf, 0, infoBuf.length);

                //write all project files
                this.project.getProjectFiles().stream().forEach((pf) -> {
                    //only workspace and modules
                    if (pf instanceof WorkSpace || pf instanceof ModuleEditor) {

                        //check if this is modul
                        boolean isModule = pf instanceof ModuleEditor;

                        if (!isModule) {
                            for (ProjectFile pf2 : this.project.getProjectFiles()) {
                                if (pf2 instanceof ModuleEditor) {
                                    if (((ModuleEditor) pf2).getLogicModelName().equals(pf.getName())) {
                                        isModule = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (isModule) {
                            String pfName = pf.getName() + "." + Tools.getFileType(pf);
                            try {
                                //set entry
                                zos.putNextEntry(new ZipEntry(pfName));

                                //read data from project file and write to the jar file (lib file)
                                FileInputStream in;
                                try {
                                    //path of current file in disk
                                    in = new FileInputStream(new File(this.project.getFile().
                                            getAbsoluteFile().getParent() + "/" + pfName));

                                    //read file and write to the lib file
                                    int len;
                                    while ((len = in.read(buffer)) > 0) {
                                        zos.write(buffer, 0, len);
                                    }
                                    in.close();

                                } catch (FileNotFoundException ex) {
                                    ExceptionLogger.getInstance().logException(ex);
                                } catch (IOException ex) {
                                    ExceptionLogger.getInstance().logException(ex);
                                }

                            } catch (IOException ex) {
                                ExceptionLogger.getInstance().logException(ex);
                            }
                        }
                    }
                });

                //close entry
                zos.closeEntry();
            }
        } catch (IOException ex) {
            ExceptionLogger.getInstance().logException(ex);
        }
    }

}
