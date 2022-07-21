package com.example.androide.compiler;

import android.os.Environment;

import com.android.tools.r8.D8;

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;
import java.io.PrintWriter;

public class Compiler {

    public void compileJavaCode(String classpath, String source, String outputPath) {
        try {
            PrintWriter out = new PrintWriter(System.out);
            PrintWriter err = new PrintWriter(System.err);
            CompilationProgress progress = null;
            BatchCompiler.compile("-classpath "+classpath+" "+source+" "+"-d " +outputPath, out, err, progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileClassFile(String source, String outputDir) {
        D8.main(new String[] {source, "--output", outputDir});
    }

}
