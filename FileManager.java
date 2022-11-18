import java.awt.*;
import java.io.*;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileManager implements Serializable{
    File file;
    FileOutputStream out;
    FileInputStream in;

    public FileManager(String name) throws FileNotFoundException{
        this.file = new File(name);
        out = new FileOutputStream(file,true);
        in = new FileInputStream(file);
    }
    public RequestManager getRequestManager() throws IOException, ClassNotFoundException{
        ObjectInputStream ois = new ObjectInputStream(in);
        RequestManager rm = (RequestManager)ois.readObject();
        return rm;

    }
    public void writeObject(Object obj) throws IOException{
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(obj);
        oos.flush();
    }
    public void write(String toWrite) throws Exception{
        out.write(toWrite.concat("\n").getBytes());
    }
    public String[] read() throws Exception{
        String k=new String(in.readAllBytes());
        String[]val=k.split("\n");
        return val;    
    }
    public String readSpecLine(int Line, String filename) throws IOException{
        String lines=Files.readAllLines(Paths.get(filename)).get(Line);
        return lines;
    }
    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }
    public FileOutputStream getOut() {
        return out;
    }
    public void setOut(FileOutputStream out) {
        this.out = out;
    }
    public FileInputStream getIn() {
        return in;
    }
    public void setIn(FileInputStream in) {
        this.in = in;
    }
}
