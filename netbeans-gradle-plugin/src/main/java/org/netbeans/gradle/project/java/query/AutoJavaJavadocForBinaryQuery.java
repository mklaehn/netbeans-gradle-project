package org.netbeans.gradle.project.java.query;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.queries.JavadocForBinaryQuery;
import org.netbeans.gradle.project.query.AbstractJavadocForBinaryQuery;
import org.netbeans.spi.java.queries.JavadocForBinaryQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

@ServiceProviders({@ServiceProvider(service = JavadocForBinaryQueryImplementation.class)})
public final class AutoJavaJavadocForBinaryQuery extends AbstractJavadocForBinaryQuery {
    private static final URL[] NO_ROOTS = new URL[0];
    private static final List<String> JAVADOC_SUFFIXES = Arrays.asList("-javadoc.zip", "-javadoc.jar");

    public static FileObject javadocForJar(FileObject binaryRoot) {
        FileObject dir = binaryRoot.getParent();
        if (dir == null) {
            return null;
        }

        for (String javadocSuffix : JAVADOC_SUFFIXES) {
            String srcFileName = binaryRoot.getName() + javadocSuffix;
            FileObject result = dir.getFileObject(srcFileName);
            
            if (null != result) {
                return FileUtil.getArchiveRoot(result);
            }
        }

        return null;
    }

    @Override
    protected JavadocForBinaryQuery.Result tryFindJavadoc(File binaryRoot) {
        final FileObject binaryRootObj = FileUtil.toFileObject(binaryRoot);
        if (binaryRootObj == null) {
            return null;
        }

        // TODO: Adjust global settings to allow prefer javadoc over sources.
        if (AutoJavaSourceForBinaryQuery.sourceForJar(binaryRootObj) != null) {
            return null;
        }

        if (javadocForJar(binaryRootObj) == null) {
            return null;
        }

        return new JavadocForBinaryQuery.Result() {
            @Override
            public URL[] getRoots() {
                FileObject javadoc = javadocForJar(binaryRootObj);
                if (javadoc == null) {
                    return NO_ROOTS;
                }

                return new URL[]{javadoc.toURL()};
            }

            @Override
            public void addChangeListener(ChangeListener l) {
            }

            @Override
            public void removeChangeListener(ChangeListener l) {
            }
        };
    }
}
