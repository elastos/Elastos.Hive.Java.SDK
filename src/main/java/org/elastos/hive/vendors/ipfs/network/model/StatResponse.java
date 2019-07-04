/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.vendors.ipfs.network.model;

public class StatResponse {

    private String Hash;
    private int Size;
    private int CumulativeSize;
    private int Blocks;
    private String Type;
    private boolean WithLocality;
    private boolean Local;
    private int SizeLocal;

    public String getHash() {
        return Hash;
    }

    public void setHash(String Hash) {
        this.Hash = Hash;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int Size) {
        this.Size = Size;
    }

    public int getCumulativeSize() {
        return CumulativeSize;
    }

    public void setCumulativeSize(int CumulativeSize) {
        this.CumulativeSize = CumulativeSize;
    }

    public int getBlocks() {
        return Blocks;
    }

    public void setBlocks(int Blocks) {
        this.Blocks = Blocks;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public boolean isWithLocality() {
        return WithLocality;
    }

    public void setWithLocality(boolean WithLocality) {
        this.WithLocality = WithLocality;
    }

    public boolean isLocal() {
        return Local;
    }

    public void setLocal(boolean Local) {
        this.Local = Local;
    }

    public int getSizeLocal() {
        return SizeLocal;
    }

    public void setSizeLocal(int SizeLocal) {
        this.SizeLocal = SizeLocal;
    }

    @Override
    public String toString() {
        return "StatResponse{" +
                "Hash='" + Hash + '\'' +
                ", Size=" + Size +
                ", CumulativeSize=" + CumulativeSize +
                ", Blocks=" + Blocks +
                ", Type='" + Type + '\'' +
                ", WithLocality=" + WithLocality +
                ", Local=" + Local +
                ", SizeLocal=" + SizeLocal +
                '}';
    }
}
