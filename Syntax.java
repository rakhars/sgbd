import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Syntax implements Serializable{
    KeySyntax syntax;
    String[] Keyword;


    public Syntax(){
        
    }
    public Syntax(String[] keyword){
        setKeyword(keyword);
    }
    public String[] getKeyword() {
        return Keyword;
    }public void setKeyword(String[] Keyword) {
        this.Keyword = Keyword;
    }
}