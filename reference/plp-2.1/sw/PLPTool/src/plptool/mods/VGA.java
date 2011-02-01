/*
    Copyright 2010 David Fritz, Brian Gordon, Wira Mulia

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */

package plptool.mods;

import plptool.PLPSimBusModule;
import plptool.Constants;
import plptool.PLPMsg;

/**
 *
 * @author wira
 */
public class VGA extends PLPSimBusModule {
    MemModule RAM;

    public VGA(long addr, MemModule RAM) {
        super(addr, addr + 4, true);
        this.RAM = RAM;
    }

    public int eval() {
        // No need to eval every cycle
        return Constants.PLP_OK;
    }

    public int gui_eval(Object x) {
        if(!enabled)
            return Constants.PLP_OK;

        if(!super.isInitialized(startAddr + 4))
            return Constants.PLP_SIM_UNINITIALIZED_MEMORY;

        // Check if control register is initialized and whether it's set to one
        if(!super.isInitialized(startAddr) || ((Long) super.read(startAddr) & 0x1) != 1)
            return Constants.PLP_SIM_MODULE_DISABLED;

        long framePointer = (Long) super.read(startAddr + 4);
        PLPMsg.D("Framepointer is at " + String.format("0x%08x", framePointer), 4, this);

        int[][] image = new int[640][480];

        for(int y_coord = 0; y_coord < 480; y_coord++) {
            for(int x_coord = 0; x_coord < 160; x_coord++) {
                long addr = framePointer + (y_coord * 640) + (x_coord * 4);
                long data = 0;
                if(RAM.isInitialized(addr)) {
                    data = RAM.read(addr);
                    PLPMsg.D("Initialized pixel at " + String.format("0x%08x", addr), 4, this);
                }
                if(data == 0)
                    for(int i = 0; i < 4; i++)
                        image[x_coord * 4 + i][y_coord] = 0;
                else {
                    for(int i = 0; i < 4; i++) {
                        int pixel = (((int) data) >> i * 8) & 0xff;
                        int red = ((int) pixel & 0xE0);
                        red = (red == 0xE0) ? 0xFF : red;
                        int green = ((int) pixel & 0x1C) << 3;
                        green = (green == 0xE0) ? 0xFF : green;
                        int blue = ((int) pixel & 0x03) << 6;
                        blue = (blue == 0xC0) ? 0xFF : blue;
                        PLPMsg.D("Colors: " + red + " " + green + " " + blue, 4, this);
                        image[x_coord * 4 + i][y_coord] = (red << 16) | (green << 8) | (blue);
                    }
                }
            }
        }

        ((VGAFrame)x).draw(image);

        return Constants.PLP_OK;
    }

    public String introduce() {
        return "640x480 VGA Module";
    }

    @Override
    public String toString() {
        return "VGA";
    }
}
