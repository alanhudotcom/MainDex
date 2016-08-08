package com.android.multidex;

import com.android.patch.RefScan;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * @version V1.0
 * @author: lizhangqu
 * @date: 2016-08-07 17:22
 */
public class RefTest {

    @Test
    public void testDependencies() throws IOException {
        Path path = new Path(Constant.TEST_JAR.getAbsolutePath());
        ClassReferenceListBuilder builder = new ClassReferenceListBuilder(path);
        builder.addRoots(Constant.TEST_CLASS);
        //此时能拿到该class的调用类
        Set<String> classNames = builder.getClassNames();
        System.out.println(classNames);
    }


    @Test
    public void testPatch() throws IOException {
        Path path = new Path(Constant.TEST_JAR.getAbsolutePath());
        ClassReferenceListBuilder builder = new ClassReferenceListBuilder(path);
        //加入patch,寻找依赖
        builder.addRoots(Constant.PATCH_CLASS);
        Set<String> patchClasses = new HashSet<>(builder.getClassNames());
        System.out.println("patchClasses:\n" + patchClasses);


        builder.clearAllForReuse();

        JarFile file = new JarFile(Constant.TEST_JAR);
        Enumeration enumeration = file.entries();

        Set<String> callClasses = new HashSet<>();

        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement();
            String entryName = jarEntry.getName();
            builder.addRoots(entryName);
            Set<String> refClasses = builder.getClassNames();
            //System.out.println("refClasses:" + refClasses);
            boolean hasCallPatch = RefScan.hasCallPatch(entryName, refClasses, patchClasses);
            if (hasCallPatch) {
                callClasses.add(entryName);
                System.out.println("add:" + entryName);
            }
            builder.clearAllForReuse();
        }

        System.out.println("callClasses:\n" + callClasses);
        System.out.println("patchClasses:\n" + patchClasses);


        Set<String> allClass = new HashSet<>();
        allClass.addAll(callClasses);

        for (String p : patchClasses) {
            allClass.add(p + ".class");
        }

        File patchJar = Constant.DEST_PATCH;
        if (patchJar.exists()) {
            patchJar.delete();
        }
        JarFile fileForPatch = new JarFile(Constant.TEST_JAR);
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(patchJar));
        Enumeration enumerationForPatch = fileForPatch.entries();

        while (enumerationForPatch.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumerationForPatch.nextElement();
            if (jarEntry == null) {
                continue;
            }
            String entryName = jarEntry.getName();
            if (allClass.contains(entryName)) {
                InputStream inputStream = file.getInputStream(jarEntry);
                byte[] bytes = inputStreamToByteArray(inputStream);
                jarOutputStream.putNextEntry(new ZipEntry(entryName));
                jarOutputStream.write(bytes);
                jarOutputStream.closeEntry();
            }
        }
        jarOutputStream.close();
    }

    public static byte[] inputStreamToByteArray(InputStream inStream) {
        if (inStream == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = inStream.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, read);
            }
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return bytes;
        } catch (Exception e) {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }
}
