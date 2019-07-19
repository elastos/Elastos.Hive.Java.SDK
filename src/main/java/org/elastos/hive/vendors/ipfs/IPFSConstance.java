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

package org.elastos.hive.vendors.ipfs;

public class IPFSConstance {
    public static final String URLFORMAT       = "http://%s/api/v0/";
    public static final String DEFAULT_PORT = "9095";
    public static final String JSON			= "application/json";
    public static final String PREFIX          = "/ipfs/";
    public static final String UID         	= "uid";
    public static final String HASH        	= "hash";
    public static final String PATH        	= "path";
    public static final String SOURCE      	= "source";
    public static final String DEST        	= "dest";
    public static final String ARG = "arg" ;
    public static final String PARENTS = "parents" ;
    public static final String CREATE = "create" ;
    public static final String RESCURSIVE = "recursive" ;

    public static final String NEW		    = "uid/new";
    public static final String LOGIN		= "uid/login";
    public static final String UID_INFO = "uid/info";
    public static final String MKDIR		= "files/mkdir";
    public static final String RM  		= "files/rm";
    public static final String CP  		= "files/cp";
    public static final String MV  		= "files/mv";
    public static final String LS  		= "files/ls";
    public static final String WRITE  		= "files/write";
    public static final String READ  		= "files/read";
    public static final String STAT		= "files/stat";
    public static final String PUBLISH		= "name/publish";
    public static final String NAMERESOLVE = "name/resolve";


    public enum Type{
        GET_INFO , GET_DEFAULT_DRIVE,
        GET_DIR , GET_FILE ,
        CREATE_FILE , MKDIR ,
        GET_STAT , PUBLISH ,
        COPY_TO , DELETE_ITEM,
        MOVE_TO , LIST,
        READ, WRITE, 
        GET_CHILDREN, GET_ITEMINFO
    }

}
