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
package logicSimulator.objects.complex;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import logicSimulator.Convert;
import logicSimulator.Project;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.ClickAction;
import logicSimulator.common.LinkASM;
import logicSimulator.common.Memory;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.objects.IOPin;
import logicSimulator.graphics.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.common.SerialDataDriver;
import logicSimulator.graphics.GString;
import window.ROMMemoryEditor;

/**
 *
 * @author Martin
 */
public class CPU extends WorkSpaceObject implements Memory, ClickAction {

    //const
    //###################################################################
    public static final short BITS = 8;
    public static final int REG_ALU_OUT = 8;
    public static final int REG_ALU_STATUS = 9;
    public static final int REG_ADR = 10;
    public static final int REG_TIME = 11;
    //###################################################################

    private class Reg {
        
        boolean[] data;
        
        public Reg(int bits) {
            this.data = new boolean[bits];
        }
        
        public Reg(boolean[] val) {
            this.data = new boolean[val.length];
            for (int i = 0; i < val.length; ++i) {
                this.data[i] = val[i];
            }
        }

        /**
         * Get cloned bit buffer of register
         *
         * @return
         */
        public boolean[] getData() {
            boolean[] copy = new boolean[this.data.length];
            System.arraycopy(this.data, 0, copy, 0, this.data.length);
            return copy;
        }

        /**
         * Set value of this register
         *
         * @param value boolean[]
         */
        public void cloneValue(boolean[] value) {
            for (int i = 0; i < value.length && i < this.data.length; ++i) {
                this.data[i] = value[i];
            }
        }
        
    }

    //registers
    private transient Reg[] registers = new Reg[256];

    //program of this MCU (16b addressing = 65 536 instructions)
    private final byte[] program = new byte[65536];

    //IO pins
    private final IOPin input, output, clk, clr;

    //id for memory
    private String ID = "";

    //adr
    private final GString adr;
    
    public CPU(Point position, String ID) {
        super(position);
        
        this.ID = ID;
        initAllRegisters();

        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);

        //body
        GOList.add(new Line(-28, 56, 28, 56));
        GOList.add(new Line(-28, -56, 28, -56));
        GOList.add(new Line(-28, 56, -28, -56));
        GOList.add(new Line(28, 56, 28, -56));
        
        GOList.add(new GString(new Point2D.Double(-17d, 0d), 18, "MCU"));
        GOList.add(new GString(new Point2D.Double(-17d, 19d), 10, "8-bit"));
        this.adr = new GString(new Point2D.Double(-17d, 32d), 10, "0x0");
        GOList.add(this.adr);
        
        this.input = new IOPin(IOPin.MODE.INPUT, 32, "INPUT", new Point.Double(-28, -28));
        this.clk = new IOPin(IOPin.MODE.INPUT, 1, "CLK", new Point.Double(-28, 0));
        this.clr = new IOPin(IOPin.MODE.INPUT, 1, "CLR", new Point.Double(-28, 28));
        this.output = new IOPin(IOPin.MODE.OUTPUT, 32, "OUTPUT", new Point.Double(29, 0));
        
        model.getIOPins().add(this.input);
        model.getIOPins().add(this.output);
        model.getIOPins().add(this.clk);
        model.getIOPins().add(this.clr);
        
        super.setModel(model);
        model.computeSize();

        //init sdd
        this.initSerilaDataDriver();
    }
    
    private void initAllRegisters() {
        for (int i = 0; i < 256; ++i) {
            this.registers[i] = new Reg(MCU.BITS);
        }
    }
    
    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        this.adr.setString("0x" + Convert.intToHex(this.instructionAddress));
        super.getModel().renderModel(g2, super.getPosition(), offset, screen, super.isSelected());
    }
    
    @Override
    public byte[] getData() {
        return this.program;
    }
    
    @Override
    public String getID() {
        return this.ID;
    }
    
    @Override
    public void changeValue(Point cursor, Component parent, Project project) {
        //edit rom data
        ROMMemoryEditor editor = new ROMMemoryEditor(this, project);
        editor.setLocationRelativeTo(parent);
        editor.setVisible(true);
        editor.setTitle("ROM data editor : [" + this.ID + "]");
    }

    //this call after access to memory, get address of accessed cell (in action event id)
    private transient ActionListener onMemoryAccess = null;
    
    @Override
    public void setOnMemoryAccessListener(ActionListener listener) {
        this.onMemoryAccess = listener;
    }
    
    @Override
    public void uploadProgram(List<Byte> hex, int offset, boolean clear) {
        this.uploading = true;

        //clear
        if (clear) {
            for (int i = 0; i < this.program.length; i++) {
                this.program[i] = 0x0;
            }
        }
        
        int index = 0;
        for (int i = offset; i < this.program.length; i++) {
            //byte from list copy to data of memory
            this.program[i] = hex.get(index++);
            //if all hex data are loaded than break
            if (index == hex.size()) {
                break;
            }
        }
        
        this.uploading = false;
        
        this.instructionAddress = 0;
    }
    
    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("ID", this.ID)
        };
    }
    
    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "ID":
                    this.ID = propt.getValueString();
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }
    
    private boolean edge = false;
    
    private boolean uploading = false;
    
    @Override
    public boolean compute() {
        boolean change = false;
        
        if (this.uploading) {
            this.instructionAddress = 0;
            return false;
        }

        //clk
        if (this.clk.getValue()[0]) {
            if (!this.edge) {

                //WDT
                if (this.WDT_ENABLE) {
                    ++this.WDT_COUNTER;
                }

                //call listener
                if (this.onMemoryAccess != null) {
                    this.onMemoryAccess.actionPerformed(new ActionEvent(this, this.instructionAddress, ""));
                }

                //time register
                boolean[] time = this.registers[MCU.REG_TIME].data;
                boolean carry = true;
                for (int i = 0; i < time.length; ++i) {
                    if (carry) {
                        time[i] = !time[i];
                        carry = !time[i];
                    }
                }
                
                if (this.flush_buffer) {
                    //load bits to out buffer
                    boolean[] outBuf = new boolean[this.buffer_width * MCU.BITS];
                    boolean[] dataCell;
                    for (int i = 0; i < this.buffer_width && !this.buffer.isEmpty(); ++i) {
                        dataCell = this.buffer.pop();
                        for (int j = 0; j < dataCell.length; ++j) {
                            outBuf[j + i * MCU.BITS] = dataCell[j];
                        }
                    }

                    //stop flushing
                    if (this.buffer.isEmpty()) {
                        this.flush_buffer = false;
                    }

                    //write bit buffer to output
                    boolean[] out = this.output.getValue();
                    for (int i = 0; i < outBuf.length; ++i) {
                        out[i] = outBuf[i];
                    }
                    
                    return true;
                }

                //handle current instruction
                change = this.sdd.handleValue(
                        Convert.byteToBits(this.program[this.instructionAddress]));

                //next adress
                ++this.instructionAddress;
            }
            this.edge = true;
        } else {
            this.edge = false;
        }

        //clr or WDT
        if (this.clr.getValue()[0] || this.WDT_COUNTER > this.WDT_MAX) {
            this.WDT_COUNTER = 0;
            this.instructionAddress = 0;
            this.output.setValue(false);
        }
        
        return change;
    }
    
    @Override
    public MCU cloneObject() {
        MCU ret = new MCU(Tools.copy(super.getPosition()), this.ID);
        ret.getModel().rotate(super.getModel().getAngle());
        return ret;
    }
    
    @Override
    public boolean error() {
        return false;
    }

    /**
     * Return all command for vecter screen
     *
     * @return
     */
    public static final LinkASM[] getInstructions() {
        //list with all instruction for this component
        return new LinkASM[]{
            //basic operations
            new LinkASM("RDA", "0x1", "Read all inputs", true),
            new LinkASM("WRA", "0x2", "Write all bites to output", true),
            new LinkASM("MOV", "0x3", "Move value from source register to targer register (2x 8bit address of register)", true),
            new LinkASM("CMP", "0x4", "Compare values of two reisters, if values are different then skip next instruction (2x 8bit address of register)", true),
            new LinkASM("JMP", "0x5", "Jump to spacifisc adderess (2x 8bit)", true),
            new LinkASM("SET", "0x6", "Set value of register (1x 8bit address of register, 1x 8bit value)", true),
            //logic operations
            new LinkASM("NOT", "0x7", "NOT logic operation (1x 8bit address of register", true),
            new LinkASM("OR", "0x8", "OR logic operation (2x 8bit address of register", true),
            new LinkASM("NOR", "0x9", "NOR logic operation (2x 8bit address of register", true),
            new LinkASM("AND", "0xA", "AND logic operation (2x 8bit address of register", true),
            new LinkASM("NAND", "0xB", "NAND logic operation (2x 8bit address of register", true),
            new LinkASM("XOR", "0xC", "XOR logic operation (2x 8bit address of register", true),
            new LinkASM("NXOR", "0xD", "NXOR logic operation (2x 8bit address of register", true),
            //aritmetic operations
            new LinkASM("ADD", "0xE", "ADD aritmetic operation (2x 8bit address of register", true),
            new LinkASM("SUB", "0xF", "ADD aritmetic operation (2x 8bit address of register", true),
            //stream operations
            new LinkASM("MVB", "0x10", "Move byte from register to buffer (1x 8bit address of register", true),
            new LinkASM("MVCB", "0x11", "Move constant byte to buffer (1x 8bit address of register", true),
            new LinkASM("CLRB", "0x12", "Clear buffer", true),
            new LinkASM("FLB", "0x13", "Flush buffer", true),
            new LinkASM("SBW", "0x14", "Set buffer output width (1x 8bit value -> 0x1=8bit, 0x2=16, 0x3=24, 0x4=32)", true),
            //time
            new LinkASM("RST", "0x15", "Reset time", true),
            new LinkASM("ST", "0x16", "Set time (1x 8bit value)", true),
            //watch dog timer
            new LinkASM("WDT", "0x17", "Enable or disable Watch dog timer (1x 8bit value -> 0x0 = false, 0x1 = true)", true),
            new LinkASM("WDTR", "0x18", "Reset Watch dog timer", true),
            new LinkASM("WDTS", "0x19", "Set Watch dog timer reset time (2x 8bit value) [default value: 250 clk ticks]", true),
            //comparing
            new LinkASM("GT", "0x1A", "Compare values of two reisters, if value of first register is not greater than value of second reg. then skip next instruction (2x 8bit address of register)", true),
            new LinkASM("LT", "0x1B", "Compare values of two reisters, if value of first lower is not greater than value of second reg. then skip next instruction (2x 8bit address of register)", true),
            //alu config
            new LinkASM("SCP", "0x1C", "Second constant parameter mode => address of second register will be value(1x 8bit address of register -> 0x0 = false, 0x1 = true)", true),
            //return
            new LinkASM("return", "0x1D", "Return to last jump function", true),
            //aliases inputs registers
            new LinkASM("IN_0", "0x0", "Alias of input register for bits 0 - 7", false),
            new LinkASM("IN_1", "0x1", "Alias of input register for bits 8 - 15", false),
            new LinkASM("IN_2", "0x2", "Alias of input register for bits 16 - 23", false),
            new LinkASM("IN_3", "0x3", "Alias of input register for bits 24 - 31", false),
            //aliases outputs register
            new LinkASM("OUT_0", "0x4", "Alias of output register for bits 0 - 7", false),
            new LinkASM("OUT_1", "0x5", "Alias of output register for bits 0 - 7", false),
            new LinkASM("OUT_2", "0x6", "Alias of output register for bits 0 - 7", false),
            new LinkASM("OUT_3", "0x7", "Alias of output register for bits 0 - 7", false),
            //aliases of system registers
            new LinkASM("ALU_OUT", "0x8", "Alias of ALU output register", false),
            new LinkASM("ALU_STATUS", "0x9", "Alias of ALU status register [0-carry, 1-zero, 2-sign]", false),
            new LinkASM("ADR", "0xA", "Alias of instruction address register", false),
            new LinkASM("TIME", "0xB", "Alias of time register", false)
        };
    }

    /**
     * Get length of instruction
     *
     * @param instructionID Instruction ID
     * @return
     */
    public static int legthOfInstruction(int instructionID) {
        switch (instructionID) {
            case 3:
            case 4:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 25:
            case 26:
            case 27:
                return 3;
            case 7:
            case 16:
            case 17:
            case 20:
            case 22:
            case 23:
            case 28:
                return 2;
        }
        return 1;
    }
    
    @Override
    public void restore() {
        //init serial data driver
        this.sdd = new SerialDataDriver();
        this.initSerilaDataDriver();

        //init registers
        this.registers = new Reg[256];
        initAllRegisters();

        //default instruction address
        this.instructionAddress = 0;

        //buffer
        this.buffer = new LinkedList<>();
        this.buffer_width = 1;
        this.flush_buffer = false;

        //wdt
        this.WDT_ENABLE = false;
        this.WDT_COUNTER = 0;
        this.WDT_MAX = 250;

        //SCP
        this.SCP = false;

        //jmp adr
        this.jmpAdr = new LinkedList<>();
    }

    //serial data driver
    private transient SerialDataDriver sdd = new SerialDataDriver();

    //address of current instruction
    private transient int instructionAddress = 0;

    //work int for processes
    private transient int workInt;

    //work bit array
    private transient boolean[] workBitBuffer;

    //buffer
    private transient int buffer_width = 1;
    private transient boolean flush_buffer = false;
    private transient LinkedList<boolean[]> buffer = new LinkedList<>();

    //Watch dog timer
    private transient boolean WDT_ENABLE = false;
    private transient int WDT_COUNTER = 0;
    private transient int WDT_MAX = 250;

    //SCP
    private transient boolean SCP = false;

    //jump addresses
    private transient LinkedList<Integer> jmpAdr = new LinkedList<>();
    
    private void initSerilaDataDriver() {
        //RDA
        this.sdd.addInstructionListener(1, 0, (ActionEvent e) -> {
            boolean[] inputBuf = this.input.getValue();
            
            boolean[] data;
            for (int j = 0; j < 4; ++j) {
                data = new boolean[MCU.BITS];
                for (int i = 0; i < MCU.BITS; ++i) {
                    data[i] = inputBuf[i + j * MCU.BITS];
                }
                this.registers[j].data = data;
            }
        });

        //WRA
        this.sdd.addInstructionListener(2, 0, (ActionEvent e) -> {
            boolean[] outputBuf = this.output.getValue();
            
            boolean[] data;
            for (int j = 0; j < 4; ++j) {
                data = this.registers[j + 4].data;
                for (int i = 0; i < MCU.BITS; ++i) {
                    outputBuf[i + j * MCU.BITS] = data[i];
                }
            }
        });

        //MOV
        this.sdd.addInstructionListener(3, 2, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 3, (ActionEvent e) -> {
            switch (e.getID()) {
                case 2:
                    this.workInt = this.sdd.getBitBufferValue();
                    break;
                case 1:
                    this.registers[this.sdd.getBitBufferValue()].cloneValue(
                            this.registers[this.workInt].data
                    );
                    break;
            }
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //CMP
        this.sdd.addInstructionListener(4, 2, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 4, (ActionEvent e) -> {
            switch (e.getID()) {
                case 2:
                    this.workInt = this.sdd.getBitBufferValue();
                    break;
                case 1:
                    boolean[] reg1 = this.registers[this.workInt].data;
                    boolean[] reg2 = this.SCP ? Convert.intToBits(this.sdd.getBitBufferValue(), MCU.BITS)
                            : this.registers[this.sdd.getBitBufferValue()].data;
                    boolean equal = true;
                    for (int i = 0; i < MCU.BITS; ++i) {
                        if (reg1[i] != reg2[i]) {
                            equal = false;
                            break;
                        }
                    }
                    if (!equal) {
                        //skip next instruction
                        this.instructionAddress += MCU.legthOfInstruction(
                                this.program[this.instructionAddress + 1]
                        );
                    }
                    break;
            }
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //JMP
        this.sdd.addInstructionListener(5, 2, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 5, (ActionEvent e) -> {
            switch (e.getID()) {
                case 1:
                    //add current adr to lifo stack
                    this.jmpAdr.add(this.instructionAddress);
                    //jump to specific address
                    this.instructionAddress = this.sdd.getBitBufferValue() - 1;
                    //clear buffer
                    this.sdd.getBitBuffer().clear();
                    break;
            }
        });

        //SET
        this.sdd.addInstructionListener(6, 2, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 6, (ActionEvent e) -> {
            switch (e.getID()) {
                case 2:
                    //address of register
                    this.workInt = this.sdd.getBitBufferValue();
                    break;
                case 1:
                    this.registers[this.workInt].cloneValue(
                            Convert.intToBits(this.sdd.getBitBufferValue(), MCU.BITS)
                    );
                    break;
            }
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //NOT
        this.sdd.addInstructionListener(7, 1, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 7, (ActionEvent e) -> {
            //load bits
            boolean[] bits = this.SCP ? Convert.intToBits(this.sdd.getBitBufferValue(), MCU.BITS)
                    : this.registers[this.sdd.getBitBufferValue()].getData();

            //clear buffer
            this.sdd.getBitBuffer().clear();

            //not
            boolean zero = true;
            for (int i = 0; i < bits.length; ++i) {
                bits[i] = !bits[i];
                zero = bits[i] ? false : bits[i];
            }

            //zero
            this.registers[MCU.REG_ALU_STATUS].data[1] = zero;

            //store final value
            this.registers[MCU.REG_ALU_OUT].data = bits;
        });

        //OR
        this.sdd.addInstructionListener(8, 2, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 8, (ActionEvent e) -> {
            switch (e.getID()) {
                case 2:
                    this.workBitBuffer = this.registers[this.sdd.getBitBufferValue()].getData();
                    break;
                case 1:
                    boolean[] bits = this.SCP ? Convert.intToBits(this.sdd.getBitBufferValue(), MCU.BITS)
                            : this.registers[this.sdd.getBitBufferValue()].getData();

                    //or
                    boolean zero = true;
                    for (int i = 0; i < bits.length; ++i) {
                        bits[i] = bits[i] || this.workBitBuffer[i];
                        zero = bits[i] ? false : bits[i];
                    }

                    //zero
                    this.registers[MCU.REG_ALU_STATUS].data[1] = zero;

                    //store final value
                    this.registers[MCU.REG_ALU_OUT].data = bits;
                    break;
            }
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //NOR
        this.sdd.addInstructionListener(9, 2, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 9, (ActionEvent e) -> {
            switch (e.getID()) {
                case 2:
                    this.workBitBuffer = this.registers[this.sdd.getBitBufferValue()].getData();
                    break;
                case 1:
                    boolean[] bits = this.SCP ? Convert.intToBits(this.sdd.getBitBufferValue(), MCU.BITS)
                            : this.registers[this.sdd.getBitBufferValue()].getData();

                    //nor
                    boolean zero = true;
                    for (int i = 0; i < bits.length; ++i) {
                        bits[i] = !(bits[i] || this.workBitBuffer[i]);
                        zero = bits[i] ? false : bits[i];
                    }

                    //zero
                    this.registers[MCU.REG_ALU_STATUS].data[1] = zero;

                    //store final value
                    this.registers[MCU.REG_ALU_OUT].data = bits;
                    break;
            }
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //AND
        this.sdd.addInstructionListener(10, 2, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 10, (ActionEvent e) -> {
            switch (e.getID()) {
                case 2:
                    this.workBitBuffer = this.registers[this.sdd.getBitBufferValue()].getData();
                    break;
                case 1:
                    boolean[] bits = this.SCP ? Convert.intToBits(this.sdd.getBitBufferValue(), MCU.BITS)
                            : this.registers[this.sdd.getBitBufferValue()].getData();

                    //and
                    boolean zero = true;
                    for (int i = 0; i < bits.length; ++i) {
                        bits[i] = bits[i] && this.workBitBuffer[i];
                        zero = bits[i] ? false : bits[i];
                    }

                    //zero
                    this.registers[MCU.REG_ALU_STATUS].data[1] = zero;

                    //store final value
                    this.registers[MCU.REG_ALU_OUT].data = bits;
                    break;
            }
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //NAND
        this.sdd.addInstructionListener(11, 2, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 11, (ActionEvent e) -> {
            switch (e.getID()) {
                case 2:
                    this.workBitBuffer = this.registers[this.sdd.getBitBufferValue()].getData();
                    break;
                case 1:
                    boolean[] bits = this.SCP ? Convert.intToBits(this.sdd.getBitBufferValue(), MCU.BITS)
                            : this.registers[this.sdd.getBitBufferValue()].getData();

                    //nand
                    boolean zero = true;
                    for (int i = 0; i < bits.length; ++i) {
                        bits[i] = !(bits[i] && this.workBitBuffer[i]);
                        zero = bits[i] ? false : bits[i];
                    }

                    //zero
                    this.registers[MCU.REG_ALU_STATUS].data[1] = zero;

                    //store final value
                    this.registers[MCU.REG_ALU_OUT].data = bits;
                    break;
            }
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //XOR
        this.sdd.addInstructionListener(12, 2, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 12, (ActionEvent e) -> {
            switch (e.getID()) {
                case 2:
                    this.workBitBuffer = this.registers[this.sdd.getBitBufferValue()].getData();
                    break;
                case 1:
                    boolean[] bits = this.SCP ? Convert.intToBits(this.sdd.getBitBufferValue(), MCU.BITS)
                            : this.registers[this.sdd.getBitBufferValue()].getData();

                    //xor
                    boolean zero = true;
                    for (int i = 0; i < bits.length; ++i) {
                        bits[i] = bits[i] ^ this.workBitBuffer[i];
                        zero = bits[i] ? false : bits[i];
                    }

                    //zero
                    this.registers[MCU.REG_ALU_STATUS].data[1] = zero;

                    //store final value
                    this.registers[MCU.REG_ALU_OUT].data = bits;
                    break;
            }
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //NXOR
        this.sdd.addInstructionListener(13, 2, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 13, (ActionEvent e) -> {
            switch (e.getID()) {
                case 2:
                    this.workBitBuffer = this.registers[this.sdd.getBitBufferValue()].getData();
                    break;
                case 1:
                    boolean[] bits = this.SCP ? Convert.intToBits(this.sdd.getBitBufferValue(), MCU.BITS)
                            : this.registers[this.sdd.getBitBufferValue()].getData();

                    //nxor
                    boolean zero = true;
                    for (int i = 0; i < bits.length; ++i) {
                        bits[i] = !(bits[i] ^ this.workBitBuffer[i]);
                        zero = bits[i] ? false : bits[i];
                    }

                    //zero
                    this.registers[MCU.REG_ALU_STATUS].data[1] = zero;

                    //store final value
                    this.registers[MCU.REG_ALU_OUT].data = bits;
                    break;
            }
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //ADD
        this.sdd.addInstructionListener(14, 2, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 14, (ActionEvent e) -> {
            switch (e.getID()) {
                case 2:
                    this.workInt = Convert.bitsToInt(this.registers[this.sdd.getBitBufferValue()].data);
                    break;
                case 1:
                    int res = this.workInt + (this.SCP ? this.sdd.getBitBufferValue()
                            : Convert.bitsToInt(this.registers[this.sdd.getBitBufferValue()].data));

                    //carry
                    this.registers[MCU.REG_ALU_STATUS].data[0] = res > 255;
                    //zero
                    this.registers[MCU.REG_ALU_STATUS].data[1] = res == 0;
                    //sign
                    this.registers[MCU.REG_ALU_STATUS].data[2] = res >= 0;

                    //store final value
                    this.registers[MCU.REG_ALU_OUT].data = Convert.intToBits(res, MCU.BITS);
                    break;
            }
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //SUB
        this.sdd.addInstructionListener(15, 2, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 15, (ActionEvent e) -> {
            switch (e.getID()) {
                case 2:
                    this.workInt = Convert.bitsToInt(this.registers[this.sdd.getBitBufferValue()].data);
                    break;
                case 1:
                    int res = this.workInt - (this.SCP ? this.sdd.getBitBufferValue()
                            : Convert.bitsToInt(this.registers[this.sdd.getBitBufferValue()].data));

                    //carry
                    this.registers[MCU.REG_ALU_STATUS].data[0] = Math.abs(res) > 255;
                    //zero
                    this.registers[MCU.REG_ALU_STATUS].data[1] = res == 0;
                    //sign
                    this.registers[MCU.REG_ALU_STATUS].data[2] = res >= 0;

                    //store final value
                    this.registers[MCU.REG_ALU_OUT].data = Convert.intToBits(res, MCU.BITS);
                    break;
            }
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //MVB
        this.sdd.addInstructionListener(16, 1, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 16, (ActionEvent e) -> {
            this.buffer.add(this.registers[this.sdd.getBitBufferValue()].data);
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //MVCB
        this.sdd.addInstructionListener(17, 1, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 17, (ActionEvent e) -> {
            this.buffer.add(Convert.intToBits(this.sdd.getBitBufferValue(), MCU.BITS));
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //CLRB
        this.sdd.addInstructionListener(18, 0, (ActionEvent e) -> {
            this.buffer.clear();
        });

        //FLB
        this.sdd.addInstructionListener(19, 0, (ActionEvent e) -> {
            this.flush_buffer = true;
        });

        //SBW
        this.sdd.addInstructionListener(20, 1, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 20, (ActionEvent e) -> {
            this.buffer_width = Math.min(this.sdd.getBitBufferValue(), 4);
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //RST
        this.sdd.addInstructionListener(21, 0, (ActionEvent e) -> {
            boolean[] bits = this.registers[MCU.REG_TIME].data;
            for (int i = 0; i < bits.length; ++i) {
                bits[i] = false;
            }
        });

        //ST
        this.sdd.addInstructionListener(22, 1, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 22, (ActionEvent e) -> {
            this.registers[MCU.REG_TIME].data = Convert.intToBits(this.sdd.getBitBufferValue(), MCU.BITS);
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //WDT
        this.sdd.addInstructionListener(23, 1, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 23, (ActionEvent e) -> {
            this.WDT_ENABLE = this.sdd.getBitBuffer().get(0);
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //WDTR
        this.sdd.addInstructionListener(24, 0, (ActionEvent e) -> {
            this.WDT_COUNTER = 0;
        });

        //WDTS
        this.sdd.addInstructionListener(25, 2, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 25, (ActionEvent e) -> {
            switch (e.getID()) {
                case 1:
                    this.WDT_MAX = this.sdd.getBitBufferValue();
                    //clear buffer
                    this.sdd.getBitBuffer().clear();
                    break;
            }
        });

        //GT
        this.sdd.addInstructionListener(26, 2, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 26, (ActionEvent e) -> {
            switch (e.getID()) {
                case 2:
                    this.workInt = this.sdd.getBitBufferValue();
                    break;
                case 1:
                    boolean[] reg1 = this.registers[this.workInt].data;
                    boolean[] reg2 = this.SCP ? Convert.intToBits(this.sdd.getBitBufferValue(), MCU.BITS)
                            : this.registers[this.sdd.getBitBufferValue()].data;
                    
                    if (Convert.bitsToInt(reg1) > Convert.bitsToInt(reg2)) {
                        //skip next instruction
                        this.instructionAddress += MCU.legthOfInstruction(
                                this.program[this.instructionAddress + 1]
                        );
                    }
                    break;
            }
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //GT
        this.sdd.addInstructionListener(27, 2, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 27, (ActionEvent e) -> {
            switch (e.getID()) {
                case 2:
                    this.workInt = this.sdd.getBitBufferValue();
                    break;
                case 1:
                    boolean[] reg1 = this.registers[this.workInt].data;
                    boolean[] reg2 = this.SCP ? Convert.intToBits(this.sdd.getBitBufferValue(), MCU.BITS)
                            : this.registers[this.sdd.getBitBufferValue()].data;
                    
                    if (Convert.bitsToInt(reg1) < Convert.bitsToInt(reg2)) {
                        //skip next instruction
                        this.instructionAddress += MCU.legthOfInstruction(
                                this.program[this.instructionAddress + 1]
                        );
                    }
                    break;
            }
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //SCP
        this.sdd.addInstructionListener(28, 1, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 28, (ActionEvent e) -> {
            this.SCP = this.sdd.getBitBuffer().get(0);
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //return
        this.sdd.addInstructionListener(29, 0, (ActionEvent e) -> {
            if (!this.jmpAdr.isEmpty()) {
                this.instructionAddress = this.jmpAdr.getLast();
                this.jmpAdr.removeLast();
            }
        });
        
    }
    
}
