package com.Kai_Jan_57.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements ColorPickerView.OnColorChangedListener, FileSaveFragment.Callbacks, FileSelectFragment.Callbacks {

    ColorPickerView ColorPickerView1;
    ColorPanelView ColorPanelView1;
    String Note;
    String OpenFileName = "";
    MainActivity thiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thiss = this;
    }

    public void add() {
        RelativeLayout rL = (RelativeLayout) findViewById(R.id.container);
        int count = rL.getChildCount();
        if (count < 1) {
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        LayoutInflater li = LayoutInflater.from(this);
        View pV = li.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setView(pV);
        final EditText i = (EditText) pV.findViewById(R.id.editText1);
        ColorPanelView1 = (ColorPanelView) pV.findViewById(R.id.ColorPanelView1);
        ColorPickerView1 = (ColorPickerView) pV.findViewById(R.id.ColorPicker1);
        ColorPickerView1.setOnColorChangedListener(thiss);
        ColorPickerView1.setAlphaSliderVisible(true);
        ab.setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Note = i.getText().toString();
                        new Note(thiss, Note, ColorPanelView1.getColor(), 0, 0);
                    }
                })
                .setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setTitle(R.string.new_note);

        AlertDialog aD = ab.create();
        aD.show();
    }

    public void delete_all() {
        RelativeLayout rL = (RelativeLayout) findViewById(R.id.container);
        if (rL.getChildCount() > 0) {
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setTitle(getString(R.string.del_all));
            ab.setMessage(getString(R.string.prompt_delete_all));
            ab.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RelativeLayout rL = (RelativeLayout) findViewById(R.id.container);
                    rL.removeAllViews();
                }
            });
            ab.setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog aD = ab.create();
            aD.show();
        } else {
            Toast.makeText(this, getString(R.string.nothing_to_delete), Toast.LENGTH_SHORT).show();
        }
    }

    public void save() {
        RelativeLayout rL = (RelativeLayout) findViewById(R.id.container);
        int count = rL.getChildCount();
        if (count > 0) {
            if (!OpenFileName.equals("") && new File(OpenFileName).exists()) {
                StringBuilder ParsedString = new StringBuilder();
                ParsedString.append("NSysV.1");
                boolean orientation = false;
                if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    orientation = true;
                }
                ParsedString.append("\nOrientation=\"" + orientation + "\"");
                for (int i = 0; i < count; i++) {
                    Button b = (Button) rL.getChildAt(i);
                    ParsedString.append("\n<Note");
                    ParsedString.append("\n|Color|=\"" + b.getCurrentTextColor() + "\"");
                    ParsedString.append("\n|PosX|=\"" + b.getX() + "\"");
                    ParsedString.append("\n|PosY|=\"" + b.getY() + "\"");
                    ParsedString.append("\n|Text|=\"" + asciiToHex("" + b.getText()) + "\"");
                    ParsedString.append(">");
                }
                try {
                    FileOutputStream FOS = new FileOutputStream(OpenFileName);
                    OutputStreamWriter OSW = new OutputStreamWriter(FOS);
                    OSW.write(ParsedString.toString());
                    OSW.close();
                    Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                FileSaveFragment FsD = FileSaveFragment.newInstance(".note", R.string.save, R.string.abort, R.string.save_prompt, R.string.hint_edit, R.drawable.save);
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                FsD.width = size.x;
                FsD.height = size.y;
                FsD.show(getFragmentManager(), "Save_.note");
            }
        } else {
            if (!OpenFileName.equals("")) {
                AlertDialog.Builder aB = new AlertDialog.Builder(this);
                aB.setTitle(getString(R.string.delete));
                aB.setMessage(getString(R.string.delete_file));
                aB.setCancelable(true);
                aB.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File fdelete = new File(OpenFileName);
                        if (fdelete.exists()) {
                            if (fdelete.delete()) {
                                MainActivity.this.setTitle(getString(R.string.app_name));
                                Toast.makeText(MainActivity.this, getString(R.string.deleted), Toast.LENGTH_LONG).show();
                                OpenFileName = "";
                            } else {
                                Toast.makeText(MainActivity.this, getString(R.string.deleted_unsuccessfully), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                aB.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                aB.create().show();
            } else {
                Toast.makeText(this, getString(R.string.nothing_to_add), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void open() {
        RelativeLayout rL = (RelativeLayout) findViewById(R.id.container);
        int count = rL.getChildCount();
        if (count > 0) {
            AlertDialog.Builder aB = new AlertDialog.Builder(this);
            aB.setTitle(getString(R.string.save));
            aB.setMessage(getString(R.string.save_prompt));
            aB.setCancelable(false);
            aB.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    save();
                    dialog.cancel();
                }
            });
            aB.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            aB.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    FileSelectFragment FSF = FileSelectFragment.newInstance(FileSelectFragment.Mode.FileSelector, R.string.open, R.string.abort, R.string.open_note, R.drawable.open, R.drawable.open, R.drawable.file);
                    ArrayList<String> allowedExtensions = new ArrayList<String>();
                    allowedExtensions.add(".note");
                    FSF.setFilter(FileSelectFragment.FiletypeFilter(allowedExtensions));
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    FSF.width = size.x;
                    FSF.height = size.y;
                    FSF.show(getFragmentManager(), "Open_.note");
                }
            });
            aB.create().show();
        } else {
            FileSelectFragment FSF = FileSelectFragment.newInstance(FileSelectFragment.Mode.FileSelector, R.string.open, R.string.abort, R.string.open_note, R.drawable.open, R.drawable.open, R.drawable.file);
            ArrayList<String> allowedExtensions = new ArrayList<String>();
            allowedExtensions.add(".note");
            FSF.setFilter(FileSelectFragment.FiletypeFilter(allowedExtensions));
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            FSF.width = size.x;
            FSF.height = size.y;
            FSF.show(getFragmentManager(), "Open_.note");
        }
    }

    public void createNewFile() {
        final RelativeLayout rL = (RelativeLayout) findViewById(R.id.container);
        int count = rL.getChildCount();
        if (count > 0) {
            AlertDialog.Builder aB = new AlertDialog.Builder(this);
            aB.setTitle(getString(R.string.save));
            aB.setMessage(getString(R.string.save_prompt));
            aB.setCancelable(false);
            aB.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    save();
                    dialog.cancel();
                }
            });
            aB.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            aB.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    rL.removeAllViews();
                }
            });
            aB.create().show();
        }
        setTitle(getString(R.string.app_name));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        OpenFileName = "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_help) {
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_add) {
            add();
        }
        if (id == R.id.action_delete_all) {
            delete_all();
        }
        if (id == R.id.action_save) {
            save();
        }
        if (id == R.id.action_open) {
            open();
        }
        if (id == R.id.action_new_file) {
            createNewFile();
        }
        if (id == R.id.action_about) {
            Intent i = new Intent(this, About.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onColorChanged(int newColor) {
        ColorPanelView1.setColor(ColorPickerView1.getColor());
    }

    @Override
    public void onConfirmSelect(String absolutePath, String fileName) {
        if (absolutePath == null || fileName == null || absolutePath.equals("") || fileName.equals(null)) {
            return;
        }
        RelativeLayout rL = (RelativeLayout) findViewById(R.id.container);
        if (rL.getChildCount() > 0) {
            rL.removeAllViews();
        }
        try {
            File file = new File(absolutePath, fileName);
            StringBuilder sB = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                sB.append(line);
                sB.append('\n');
            }
            br.close();

            String fileContent = sB.toString();
            String[] lines = fileContent.split(System.getProperty("line.separator"));
            if (!lines[0].equals("NSysV.1")) {
                Toast.makeText(this, getString(R.string.other_version) + "\nVersion: " + lines[0] + ".", Toast.LENGTH_SHORT).show();
            }
            List<String> n = new ArrayList<String>();
            int indexnotepr = 0;
            int indexzpr = 0;
            int indexnpr = 0;
            int indexzupr = 0;
            while (true) {
                while (true) {
                    indexnotepr = fileContent.indexOf("<Note", indexnpr) + 5;
                    indexnpr = indexnotepr + 1;
                    if (fileContent.charAt(indexnotepr + 5) != '"') {
                        break;
                    }
                }
                while (true) {
                    indexzpr = fileContent.indexOf(">", indexzupr);
                    indexzupr = indexzpr + 1;
                    if (fileContent.charAt(indexzpr + 1) != '"') {
                        break;
                    }
                }
                if (indexnotepr > 0 && indexzpr > 0) {
                    n.add(fileContent.substring(indexnotepr, indexzpr).replaceAll("[\u0000-\u001f]", ""));
                } else {
                    break;
                }
            }
            String[] note = new String[n.size()];
            note = n.toArray(note);


            for (String sub : note) {
                String Color = "-1";
                String PosX = "0";
                String PosY = "0";
                String Text = "";
                Color = sub.substring(sub.indexOf("|Color|=\"") + 9, sub.indexOf("\"", sub.indexOf("|Color|=\"") + 9));
                PosX = sub.substring(sub.indexOf("|PosX|=\"") + 8, sub.indexOf("\"", sub.indexOf("|PosX|=\"") + 8));
                PosY = sub.substring(sub.indexOf("|PosY|=\"") + 8, sub.indexOf("\"", sub.indexOf("|PosY|=\"") + 8));
                Text = sub.substring(sub.indexOf("|Text|=\"") + 8, sub.indexOf("\"", sub.indexOf("|Text|=\"") + 8));
                try {
                    int Color1 = Integer.parseInt(Color);
                    Color1 = getInverseColor(Color1);
                    Text = hexToASCII(Text);
                    float PosX1 = Float.parseFloat(PosX);
                    float PosY1 = Float.parseFloat(PosY);
                    new Note(this, Text, Color1, PosX1, PosY1);
                } catch (Exception ex) {
                    Toast.makeText(this, getString(R.string.malformed), Toast.LENGTH_SHORT).show();
                }
            }
            if (((RelativeLayout) findViewById(R.id.container)).getChildCount() < 1) {
                Toast.makeText(this, getString(R.string.no_content), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            Toast.makeText(this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (absolutePath != null && fileName != null) {
            OpenFileName = absolutePath + "/" + fileName;
            Toast.makeText(this, getString(R.string.load), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean isValid(String absolutePath, String fileName) {
        if (absolutePath == null || fileName == null || absolutePath.equals("") || fileName.equals("")) {
            Toast.makeText(this, getString(R.string.select_tip), Toast.LENGTH_LONG).show();
            return false;
        }
        boolean orientation = false;
        setTitle(getString(R.string.app_name) + " - " + fileName);
        try {
            File file = new File(absolutePath, fileName);
            StringBuilder sB = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                sB.append(line);
                sB.append('\n');
            }
            br.close();

            String fileContent = sB.toString();
            String[] lines = fileContent.split(System.getProperty("line.separator"));
            if (!lines[0].equals("NSysV.1")) {
                Toast.makeText(this, getString(R.string.other_version) + "\nVersion: " + lines[0] + ".", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!lines[1].equals("Orientation=\"true\"")) {
                if (!lines[1].equals("Orientation=\"false\"")) {
                    Toast.makeText(this, getString(R.string.malformed), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    orientation = false;
                }
            } else {
                orientation = true;
            }
            List<String> n = new ArrayList<String>();
            int indexnotepr = 0;
            int indexzpr = 0;
            int indexnpr = 0;
            int indexzupr = 0;
            while (true) {
                while (true) {
                    indexnotepr = fileContent.indexOf("<Note", indexnpr) + 5;
                    indexnpr = indexnotepr + 1;
                    if (fileContent.charAt(indexnotepr + 5) != '"') {
                        break;
                    }
                }
                while (true) {
                    indexzpr = fileContent.indexOf(">", indexzupr);
                    indexzupr = indexzpr + 1;
                    if (fileContent.charAt(indexzpr + 1) != '"') {
                        break;
                    }
                }
                if (indexnotepr > 0 && indexzpr > 0) {
                    n.add(fileContent.substring(indexnotepr, indexzpr).replaceAll("[\u0000-\u001f]", ""));
                } else {
                    break;
                }
            }
            String[] note = new String[n.size()];
            note = n.toArray(note);


            for (String sub : note) {
                String Color = "-1";
                String PosX = "0";
                String PosY = "0";
                String Text = "";
                Color = sub.substring(sub.indexOf("|Color|=\"") + 9, sub.indexOf("\"", sub.indexOf("|Color|=\"") + 9));
                PosX = sub.substring(sub.indexOf("|PosX|=\"") + 8, sub.indexOf("\"", sub.indexOf("|PosX|=\"") + 8));
                PosY = sub.substring(sub.indexOf("|PosY|=\"") + 8, sub.indexOf("\"", sub.indexOf("|PosY|=\"") + 8));
                Text = sub.substring(sub.indexOf("|Text|=\"") + 8, sub.indexOf("\"", sub.indexOf("|Text|=\"") + 8));
                try {
                    int Color1 = Integer.parseInt(Color);
                    Color1 = getInverseColor(Color1);
                    Text = hexToASCII(Text);
                    float PosX1 = Float.parseFloat(PosX);
                    float PosY1 = Float.parseFloat(PosY);
                    new Note(this, Text, Color1, PosX1, PosY1);
                } catch (Exception ex) {
                    Toast.makeText(this, getString(R.string.malformed), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (((RelativeLayout) findViewById(R.id.container)).getChildCount() < 1) {
                Toast.makeText(this, getString(R.string.no_content), Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (Exception ex) {
            Toast.makeText(this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (orientation) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        return true;
    }

    @Override
    public boolean onCanSave(String absolutePath, String fileName) {
        if (absolutePath == null || fileName == null) {
            Toast.makeText(this, getString(R.string.entered_question), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onConfirmSave(String absolutePath, String fileName) {

        if (absolutePath == null || fileName == null) {
            return;
        }

        this.setTitle(getString(R.string.app_name) + " - " + fileName + ".note");
        RelativeLayout rL = (RelativeLayout) findViewById(R.id.container);
        int count = rL.getChildCount();
        StringBuilder ParsedString = new StringBuilder();
        ParsedString.append("NSysV.1");
        boolean orientation = false;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientation = true;
        }
        ParsedString.append("\nOrientation=\"" + orientation + "\"");
        for (int i = 0; i < count; i++) {
            Button b = (Button) rL.getChildAt(i);
            ParsedString.append("\n<Note");
            ParsedString.append("\n|Color|=\"" + b.getCurrentTextColor() + "\"");
            ParsedString.append("\n|PosX|=\"" + b.getX() + "\"");
            ParsedString.append("\n|PosY|=\"" + b.getY() + "\"");
            ParsedString.append("\n|Text|=\"" + asciiToHex("" + b.getText()) + "\"");
            ParsedString.append(">");
        }
        try {
            FileOutputStream FOS = new FileOutputStream(absolutePath + "/" + fileName + ".note");
            OutputStreamWriter OSW = new OutputStreamWriter(FOS);
            OSW.write(ParsedString.toString());
            OSW.close();
            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
            OpenFileName = absolutePath + "/" + fileName + ".note";
        } catch (Exception ex) {
            Toast.makeText(this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private int getInverseColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = Color.alpha(color);
        return Color.argb(alpha, 255 - red, 255 - green, 255 - blue);
    }

    public String asciiToHex(String s) {
        if (s.length() == 0)
            return "";
        char c;
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            buff.append(Integer.toHexString(c) + " ");
        }
        return buff.toString().trim();
    }

    public String hexToASCII(String s) {
        if (s.length() == 0)
            return "";
        String[] arr = s.split(" ");
        StringBuffer buff = new StringBuffer();
        int i;
        for (String str : arr) {
            i = Integer.valueOf(str, 16).intValue();
            String hs = new Character((char) i).toString();
            buff.append(hs);
        }
        return buff.toString();
    }

    @Override
    public void onStop() {
        super.onStop();
        Toast.makeText(this, getString(R.string.save_remember), Toast.LENGTH_LONG).show();
        if(!OpenFileName.equals("")) {
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor e = SP.edit();
            e.putString("Last_Path", OpenFileName);
            e.commit();
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstance) {
        super.onPostCreate(savedInstance);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        OpenFileName = SP.getString("Last_Path", "");
        File check = new File(OpenFileName);
        if(check.exists()){
            boolean orientation = false;
            String[] pathSplit = OpenFileName.split("/");
            String fileName = pathSplit[pathSplit.length - 1];
            setTitle(getString(R.string.app_name) + " - " + fileName);
            try {
                StringBuilder sB = new StringBuilder();
                BufferedReader br = new BufferedReader(new FileReader(check));
                String line;

                while ((line = br.readLine()) != null) {
                    sB.append(line);
                    sB.append('\n');
                }
                br.close();

                String fileContent = sB.toString();
                String[] lines = fileContent.split(System.getProperty("line.separator"));
                if (!lines[0].equals("NSysV.1")) {
                    Toast.makeText(this, getString(R.string.other_version) + "\nVersion: " + lines[0] + ".", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!lines[1].equals("Orientation=\"true\"")) {
                    if (!lines[1].equals("Orientation=\"false\"")) {
                        Toast.makeText(this, getString(R.string.malformed), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        orientation = false;
                    }
                } else {
                    orientation = true;
                }
                List<String> n = new ArrayList<String>();
                int indexnotepr = 0;
                int indexzpr = 0;
                int indexnpr = 0;
                int indexzupr = 0;
                while (true) {
                    while (true) {
                        indexnotepr = fileContent.indexOf("<Note", indexnpr) + 5;
                        indexnpr = indexnotepr + 1;
                        if (fileContent.charAt(indexnotepr + 5) != '"') {
                            break;
                        }
                    }
                    while (true) {
                        indexzpr = fileContent.indexOf(">", indexzupr);
                        indexzupr = indexzpr + 1;
                        if (fileContent.charAt(indexzpr + 1) != '"') {
                            break;
                        }
                    }
                    if (indexnotepr > 0 && indexzpr > 0) {
                        n.add(fileContent.substring(indexnotepr, indexzpr).replaceAll("[\u0000-\u001f]", ""));
                    } else {
                        break;
                    }
                }
                String[] note = new String[n.size()];
                note = n.toArray(note);


                for (String sub : note) {
                    String Color = "-1";
                    String PosX = "0";
                    String PosY = "0";
                    String Text = "";
                    Color = sub.substring(sub.indexOf("|Color|=\"") + 9, sub.indexOf("\"", sub.indexOf("|Color|=\"") + 9));
                    PosX = sub.substring(sub.indexOf("|PosX|=\"") + 8, sub.indexOf("\"", sub.indexOf("|PosX|=\"") + 8));
                    PosY = sub.substring(sub.indexOf("|PosY|=\"") + 8, sub.indexOf("\"", sub.indexOf("|PosY|=\"") + 8));
                    Text = sub.substring(sub.indexOf("|Text|=\"") + 8, sub.indexOf("\"", sub.indexOf("|Text|=\"") + 8));
                    try {
                        int Color1 = Integer.parseInt(Color);
                        Color1 = getInverseColor(Color1);
                        Text = hexToASCII(Text);
                        float PosX1 = Float.parseFloat(PosX);
                        float PosY1 = Float.parseFloat(PosY);
                        new Note(this, Text, Color1, PosX1, PosY1);
                    } catch (Exception ex) {
                        Toast.makeText(this, getString(R.string.malformed), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (((RelativeLayout) findViewById(R.id.container)).getChildCount() < 1) {
                    Toast.makeText(this, getString(R.string.no_content), Toast.LENGTH_SHORT).show();
                    return;
                }

            } catch (Exception ex) {
                Toast.makeText(this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (orientation) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // This overrides default action
    }
}