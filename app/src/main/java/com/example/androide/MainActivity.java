package com.example.androide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androide.Utilities.AssetsUtil;

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    String currentDir;
    File storage;
    EditText codeEditor;
    AssetsUtil assetsUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        codeEditor = findViewById(R.id.codeEditor);

        storage = getDir("", Context.MODE_PRIVATE);
        System.out.println("Before: "+Arrays.asList(storage.list()));
        assetsUtil = new AssetsUtil(getApplicationContext());
        assetsUtil.copyAssets("Executable.java", storage.getAbsolutePath());
        assetsUtil.copyAssets("android.jar", storage.getAbsolutePath());
        compile();
        d8();
        execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.run_code:

                //Toast.makeText(this, "Run Code", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void compile() {
        try {
            System.out.println("Compiling: ");
            System.out.println(storage.getAbsolutePath());
            System.out.println(Arrays.asList(storage.list()));
            PrintWriter out = new PrintWriter(System.out);
            PrintWriter err = new PrintWriter(System.err);
            CompilationProgress progress = null;
            currentDir = System.getProperty("user.dir");
            System.out.println(currentDir);
            System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath());
            BatchCompiler.compile("-classpath "+storage.getAbsolutePath()+"/android.jar "+storage.getAbsolutePath() + "/Executable.java -d " + storage.getAbsolutePath() + "/", out, err, progress);
            System.out.println("Here Out:");
            System.out.println(System.getProperty("user.dir"));
            System.out.println(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void d8() {
        System.out.println("D8");
        System.out.println(Arrays.asList(storage.list()));
        System.out.println("Here:  "+Arrays.asList(new File(Environment.getExternalStorageDirectory().getAbsolutePath()).list()));
        String outputDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/AndroIDE";
        com.android.tools.r8.D8.main(new String[] {storage.getAbsolutePath()+"/Executable.class",
                "--output", outputDir});
        System.out.println(Arrays.asList(storage.list()));
        try {
            DexClassLoader dexClassLoader = new DexClassLoader(outputDir + "/classes.dex", outputDir, null, getClassLoader());
            Object obj = dexClassLoader.loadClass("Executable").newInstance();
            System.out.println("Result: "+obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        Process process = null;
        BufferedReader reader = null;
        String readerData = "";
        try {
            process = Runtime.getRuntime().exec("dalvikvm -cp /sdcard/AndroIDE/classes.dex Executable");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        } catch (Exception e) {
            readerData = e.toString();
        }
        try {
            String line;
            while ((line = reader.readLine()) != null)
                readerData = readerData + "\n" + line;
        } catch (Exception e) {
            readerData = readerData +"\n\n" +e.toString();
        }
        System.out.println("Result: "+readerData);
    }

    public void copyAssets(String javaFilename) {
       // String javaFilename = "Executable.java";

        System.err.println("copying the android.jar from asssets to the internal storage to make it available to the compiler");
        BufferedInputStream bis = null;
        OutputStream dexWriter = null;
        int BUF_SIZE = 8 * 1024;
        try {
            bis = new BufferedInputStream(getAssets().open(javaFilename));
            dexWriter = new BufferedOutputStream(
                    new FileOutputStream(storage.getAbsolutePath()+"/"+javaFilename));
            byte[] buf = new byte[BUF_SIZE];
            int len;
            while((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
                dexWriter.write(buf, 0, len);
            }
            dexWriter.close();
            bis.close();

        } catch (Exception e) {
            System.err.println("Error while copying from assets: " + e.getMessage());
            e.printStackTrace();
        }

    }

}