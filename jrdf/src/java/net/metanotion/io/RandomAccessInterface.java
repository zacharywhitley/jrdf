/*
Copyright (c) 2006, Matthew Estes
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
* Neither the name of Metanotion Software nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package net.metanotion.io;

import java.io.IOException;

public interface RandomAccessInterface {
    long getFilePointer() throws IOException;

    long length() throws IOException;

    int read() throws IOException;

    int read(byte[] b) throws IOException;

    int read(byte[] b, int off, int len) throws IOException;

    void seek(long pos) throws IOException;

    void setLength(long newLength) throws IOException;

    //Readeable Methods
    void close() throws IOException;

    //Input Methods
    boolean readBoolean() throws IOException;

    byte readByte() throws IOException;

    char readChar() throws IOException;

    double readDouble() throws IOException;

    float readFloat() throws IOException;

    void readFully(byte[] b) throws IOException;

    void readFully(byte[] b, int off, int len) throws IOException;

    int readInt() throws IOException;

    String readLine() throws IOException;

    long readLong() throws IOException;

    short readShort() throws IOException;

    int readUnsignedByte() throws IOException;

    int readUnsignedShort() throws IOException;

    String readUTF() throws IOException;

    int skipBytes(int n) throws IOException;

    // DataOutput Methods
    void write(int b) throws IOException;

    void write(byte[] b) throws IOException;

    void write(byte[] b, int off, int len) throws IOException;

    void writeBoolean(boolean v) throws IOException;

    void writeByte(int v) throws IOException;

    void writeShort(int v) throws IOException;

    void writeChar(int v) throws IOException;

    void writeInt(int v) throws IOException;

    void writeLong(long v) throws IOException;

    void writeFloat(float v) throws IOException;

    void writeDouble(double v) throws IOException;

    void writeBytes(String s) throws IOException;

    void writeChars(String s) throws IOException;

    void writeUTF(String str) throws IOException;
}
