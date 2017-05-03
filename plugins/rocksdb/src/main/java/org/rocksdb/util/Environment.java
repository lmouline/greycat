/**
 * Copyright 2017 The GreyCat Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rocksdb.util;

public class Environment {
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static String ARCH = System.getProperty("os.arch").toLowerCase();

    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isMac() {
        return (OS.contains("mac"));
    }

    public static boolean isUnix() {
        return (OS.contains("nix") ||
                OS.contains("nux") ||
                OS.contains("aix"));
    }

    public static boolean isSolaris() {
        return OS.contains("sunos");
    }

    public static boolean is64Bit() {
        return (ARCH.indexOf("64") > 0);
    }

    public static boolean isx86() {
        return !ARCH.equals("arm");
    }

    public static String getSharedLibraryName(final String name) {
        return name + "jni";
    }

    public static String getSharedLibraryFileName(final String name) {
        return appendLibOsSuffix("lib" + getSharedLibraryName(name), true);
    }

    public static String getJniLibraryName(final String name) {
        if (isUnix()) {
            final String arch = (is64Bit()) ? "64" : "32";
            return String.format("%sjni-linux%s", name, arch);
        } else if (isMac()) {
            return String.format("%sjni-osx", name);
        } else if (isSolaris()) {
            return String.format("%sjni-solaris%d", name, is64Bit() ? 64 : 32);
        } else if (isWindows() && is64Bit()) {
            return String.format("%sjni-win64", name);
        }
        throw new UnsupportedOperationException();
    }

    public static String getJniLibraryFileName(final String name) {
        String libName = getJniLibraryName(name);
        if(isx86()) {
            System.out.println("Try to load normal lib");
            return appendLibOsSuffix("lib" + libName, false);
        } else {
            System.out.println("Try to arm normal lib");
            return appendLibOsSuffix("arm/lib" + libName, false);
        }
    }

    private static String appendLibOsSuffix(final String libraryFileName, final boolean shared) {
        if (isUnix() || isSolaris()) {
            return libraryFileName + ".so";
        } else if (isMac()) {
            return libraryFileName + (shared ? ".dylib" : ".jnilib");
        } else if (isWindows()) {
            return libraryFileName + ".dll";
        }
        throw new UnsupportedOperationException();
    }

    public static String getJniLibraryExtension() {
        if (isWindows()) {
            return ".dll";
        }
        return (isMac()) ? ".jnilib" : ".so";
    }
}
