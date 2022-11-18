import java.io.Serializable;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RequestManager implements Serializable{
    Syntax syntax;
    ArrayList<Rel> relations;
    static String[] operator = {"inter", "union", "diff", "division", "produit"};


    public String[] getOperator(){
        return this.operator;
    }

    public RequestManager(ArrayList<Rel> rels){
        setRelations(rels);
        String[] kw = {"alaivo", "mampiditra"};
        setSyntax(new Syntax(kw));
    }

    public void requ(String req) throws Exception{
        String[] splitted = req.split("\s+");
        switch (splitted[0].toLowerCase()) {
            case "alaivo":
                try {
                    AfficheRel(Select(req));
                    
                } catch (Exception e) {
                    System.out.println("There is no result or An error occured, verify your syntax");
                }
                break;

            case "ampidiro":
                insert(req);
                break;

            case "tables":
                listTables(req);
                break;

            default:
                break;
        }
    }

    public void listTables(String req){
        System.out.println("LISITRY NY 'TABLE(S)' MISY:  ");
        System.out.println();
        for (int i = 0; i < getRelations().size(); i++) {
            System.out.print(getRelations().get(i).getName() + "\t\t");
            System.out.println(getRelations().get(i).getColumn() + " ");
        }
        System.out.println();
        System.out.println();
    }

    public void insert(String req) throws Exception{
        String[] splitted = req.split("\s+");
        if(isRelation(splitted[2])){
            Rel R = findRelation(splitted[2]);
            String[] columns = splitted[3].split(",");
            String[] values = splitted[4].split(",");

            ArrayList<Object> valuesArrayL = new ArrayList<>();
            Collections.addAll(valuesArrayL, values);

            R.insert(columns, valuesArrayL);
            System.out.println("inserted");
        }
    }

    public Rel Select(String req) throws Exception{
        Rel R = null;
        if(hasOperator("union",req)){
            ArrayList<String> allSelectReq = checkOperator("union",req);
            ArrayList<Rel> gettedRelation = new ArrayList<>();
            for (int i = 0; i < allSelectReq.size(); i++) {
                gettedRelation.add(selectRequest(allSelectReq.get(i)));
            }
            R = gettedRelation.get(0);
            for (int i = 1; i < gettedRelation.size(); i++) {
                R = R.union(gettedRelation.get(i));
            }
        }else if(hasOperator("inter",req)){
            ArrayList<String> allSelectReq = checkOperator("inter",req);
            ArrayList<Rel> gettedRelation = new ArrayList<>();
            for (int i = 0; i < allSelectReq.size(); i++) {
                gettedRelation.add(selectRequest(allSelectReq.get(i)));
            }
            R = gettedRelation.get(0);
            for (int i = 1; i < gettedRelation.size(); i++) {
                //Method meth = R.getClass().getMethod("inter", gettedRelation.get(i).getClass()).invoke(R, gettedRelation.get(i));
                R = R.inter(gettedRelation.get(i));
            }
        }else if(hasOperator("diff",req)){
            ArrayList<String> allSelectReq = checkOperator("diff",req);
            ArrayList<Rel> gettedRelation = new ArrayList<>();
            for (int i = 0; i < allSelectReq.size(); i++) {
                gettedRelation.add(selectRequest(allSelectReq.get(i)));
            }
            R = gettedRelation.get(0);
            for (int i = 1; i < gettedRelation.size(); i++) {
                R = R.division(gettedRelation.get(i));
            }
            
        }else if(hasOperator("division",req)){
            ArrayList<String> allSelectReq = checkOperator("division",req);
            ArrayList<Rel> gettedRelation = new ArrayList<>();
            for (int i = 0; i < allSelectReq.size(); i++) {
                gettedRelation.add(selectRequest(allSelectReq.get(i)));
            }
            R = gettedRelation.get(0);
            for (int i = 1; i < gettedRelation.size(); i++) {
                R = R.division(gettedRelation.get(i));
            }
        }else if(hasOperator("produit",req)){
            ArrayList<String> allSelectReq = checkOperator("produit",req);
            ArrayList<Rel> gettedRelation = new ArrayList<>();
            for (int i = 0; i < allSelectReq.size(); i++) {
                gettedRelation.add(selectRequest(allSelectReq.get(i)));
            }
            R = gettedRelation.get(0);
            for (int i = 1; i < gettedRelation.size(); i++) {
                R = R.produit(gettedRelation.get(i));
            }
        }else{
            R = selectRequest(req);
        }

        return R;
    }

    

    public Rel selectRequest(String req) throws Exception{
        String[] splitted = req.split("\s+");
        if(hasAnOperator(req)){
            return Select(req);
        }
        if(isRelation(splitted[2])){
            Rel R = null;
            Rel relation = findRelation(splitted[2]);
            switch (splitted[3]) {
                case "*":
                    R = findRelation(splitted[2]);
                    break;
            
                default:
                    String[] columns = splitted[3].split(",");
                    ArrayList<String> columnsArrayL = new ArrayList<>();
                    Collections.addAll(columnsArrayL, columns);
                    
                    R = findRelation(splitted[2]);
                    R = R.projection(columnsArrayL);
                    break;
            }
            return R;
        }
        throw new Exception("Tsy misy");
    }

    public ArrayList<String> checkOperator(String operator ,String req) throws Exception{
        if(hasOperator(operator,req)){
            String[] splitted = req.split("\s+");
            ArrayList<String> selRequ = new ArrayList<>();
            String added="";
            for (int i = 0; i < splitted.length; i++) {
                if(splitted[i].equalsIgnoreCase(operator)){
                    selRequ.add(added);
                    added="";
                    continue;
                }

                added += splitted[i];
                if(i+1 < splitted.length && !splitted[i+1].equalsIgnoreCase(operator)){
                    added += "\s";
                }
            }
            selRequ.add(added);
            return selRequ;
        }
        throw new Exception("checkUnion exception");
    }

    public boolean hasOperator(String operator ,String req){
        String[] splitted = req.split("\s+");
        for (int i = 0; i < splitted.length; i+=2) {
            if(splitted[i].equalsIgnoreCase(operator)){
                return true;
            }
        }
        return false;
    }

    public boolean hasAnOperator(String req){
        for (int i = 0; i < getOperator().length; i++) {
            if(hasOperator(getOperator()[i], req)){
                return true;
            }
        }
        return false;
    }


    public boolean isRelation(String name){
        for (int i = 0; i < getRelations().size(); i++) {
            if(name.equals(getRelations().get(i).getName())){
                return true;
            }
        }
        String[] mayJoin = name.split(";");
        if(mayJoin.length == 3){
            for (int i = 0; i < mayJoin.length-1; i++) {
                if(!isRelation(mayJoin[i])){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public Rel findRelation(String name) throws NumberFormatException, Exception{
       Rel R = null;
       String[] mayJoin = name.split(";");
       if(mayJoin.length==3 && isRelation(name)){
            R = findRelation(mayJoin[0]);
            Rel R1 = findRelation(mayJoin[1]);
            String[] colRef = mayJoin[2].split("="); 
            
            R = R.join(R1, Integer.parseInt(colRef[0]), Integer.parseInt(colRef[1]));
            //R = R.join(R, mayJoin[2]);

            return R;
       }
       for (int i = 0; i < getRelations().size(); i++) {
            if(name.equals(getRelations().get(i).getName())){
                R = new Rel();
                R = getRelations().get(i);
            }
       }
        return R;
    }

    public ArrayList<Rel> getRelations() {
        return relations;
    }public Syntax getSyntax() {
        return syntax;
    }public void setSyntax(Syntax syntax) {
        this.syntax = syntax;
    }public void setRelations(ArrayList<Rel> relations) {
        this.relations = relations;
    }

    public static void AfficheRel(Rel r){
        System.out.println();
        System.out.println("Table: " + r.getName());
        for (int i = 0; i < r.getColumn().size(); i++) {
            System.out.print("["+r.getColumn().get(i) + "]\t\t\t");
        }System.out.println();
        for (int i = 0; i < r.getData().get(0).size(); i++) {
            for (int j = 0; j < r.getData().size(); j++) {
                System.out.print(" "+r.getData().get(j).get(i) + "\t\t\t");
            }
            System.out.println();
        }
        System.out.println();
    }
}