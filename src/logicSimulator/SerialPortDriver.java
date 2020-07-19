/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import com.fazecast.jSerialComm.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import logicSimulator.common.Propertie;
import logicSimulator.common.SerialIO;
import logicSimulator.data.PropertieReader;
import logicSimulator.objects.input.SerialInputTrigger;
import logicSimulator.objects.input.SerialOutputTrigger;

/**
 *
 * @author Martin
 */
public class SerialPortDriver implements LSComponent {

    private SerialPort port;

    private boolean running = true;

    private Thread thread;

    private final byte[] buffer;

    private final List<SerialIO> seriolIOs;

    public SerialPortDriver(int bufferSize) {
        this.buffer = new byte[bufferSize];
        this.seriolIOs = new ArrayList<>();
    }

    public List<SerialIO> getSerialIOs() {
        return this.seriolIOs;
    }

    public SerialPort[] getAvailableSerialPorts() {
        return SerialPort.getCommPorts();
    }

    /**
     * Open serial port for communication
     *
     * @param port Serial port
     */
    public void openSerialPort(SerialPort port) {
        this.port = port;
        if (this.port != null) {
            this.port.setParity(SerialPort.NO_PARITY);
            this.port.setNumDataBits(8);
            this.port.setNumStopBits(1);

            this.port.openPort();
        }
    }

    /**
     * Set baud rate
     *
     * @param baudRate Baud rate
     */
    public void setBaudRate(int baudRate) {
        if (this.port != null) {
            this.port.setBaudRate(baudRate);
        }
    }

    /**
     * Get opened port
     *
     * @return SerialPort
     */
    public SerialPort getPort() {
        return this.port;
    }

    /**
     * Set parity
     *
     * @param parity New parity
     */
    public void setParity(int parity) {
        if (this.port != null) {
            this.port.setParity(parity);
        }
    }

    @Override
    public void init(LogicSimulatorCore core, PropertieReader propt) throws Exception {

        //init propts
        if (propt != null) {
            List<Propertie> propts = propt.readFile();
            propts.stream().forEach((p) -> {
                try {
                    switch (p.getName()) {
                        case "BaudRate":
                            this.setBaudRate(p.getValueInt());
                            break;
                        case "PORT":
                            //this.openSerialPort(p.getValueString());
                            break;
                    }
                } catch (NumberFormatException ex) {
                    ExceptionLogger.getInstance().logException(ex);
                }
            });
        }

        //data recieving thread
        this.thread = new Thread(() -> {
            while (this.running) {
                try {
                    dataReceiver();
                } catch (InterruptedException ex) {
                    ExceptionLogger.getInstance().logException(ex);
                }
            }
        });
    }

    @Override
    public void run() {
        if (this.port != null) {
            if (this.port.isOpen()) {
                this.thread.start();
            }
        }
    }

    @Override
    public void stop() {
        this.running = false;
        if (this.port != null) {
            this.port.closePort();
        }
    }

    public void writeBytes(byte[] bytes) {
        if (this.port != null) {
            this.port.writeBytes(bytes, bytes.length);
        }
    }

    private void dataReceiver() throws InterruptedException {
        if (this.port != null) {
            if (this.port.isOpen()) {
                if (this.port.bytesAvailable() > 0) {
                    //read bytes
                    int bytes = this.port.readBytes(this.buffer, this.buffer.length);

                    //trig
                    for (int i = 0; i < bytes; ++i) {
                        byte bVal = this.buffer[i];
                        this.seriolIOs.forEach((sIO) -> {

                            /*
                            System.out.println(bVal + ", " + Integer.toBinaryString(bVal)
                                    + ", " + sIO.getSetValue() + " / " + sIO.getResetValue());
                             */
                            if (sIO instanceof SerialInputTrigger) {
                                if (sIO.getSetValue() == bVal) {
                                    sIO.set();
                                } else if (sIO.getResetValue() == bVal) {
                                    sIO.reset();
                                }
                            } else if (sIO instanceof SerialOutputTrigger) {
                                this.writeBytes(new byte[]{sIO.getValue() ? sIO.getSetValue() : sIO.getResetValue()});
                            }

                        });
                    }

                } else {
                    Thread.sleep(20);
                }
            }
        }
    }

}
