import java.io.Serializable;
import java.util.ArrayList;

import javax.management.relation.RelationException;

public class Rel implements Serializable{
    String name = "no name";
    ArrayList<String> column;
    ArrayList<ArrayList<Object>> data;


/// Constructor
    public Rel(){
        setColumn(new ArrayList<>());
        setData(new ArrayList<>());
    }

    public Rel(ArrayList<String> column){
        setColumn(column);
        setData(new ArrayList<>());
        
    }
    public Rel(String name ,ArrayList<String> column){
        setColumn(column);
        setName(name);
        setData(new ArrayList<>());
    }
    public Rel(String name, ArrayList<String> column, ArrayList<ArrayList<Object>> data){
        setName(name);
        setColumn(column);
        setData(data);
    }


/// Getters & setters
    public ArrayList<String> getColumn() {
        return column;
    }public ArrayList<ArrayList<Object>> getData() {
        return data;
    }public String getName() {
        return name;
    }
    public void setColumn(ArrayList<String> column) {
        this.column = column;
    }public void setData(ArrayList<ArrayList<Object>> data) {
        this.data = data;
    }public void setName(String name) {
        this.name = name;
    }

/// Fonctions
    public void insert(ArrayList<String> columns, ArrayList<ArrayList<Object>> addedData) throws Exception{
        int count = 0;
        if(columns.size() != addedData.size() && columns.size() != getColumn().size()){
            return;
        }
        for (int i = 0; i < getColumn().size(); i++) {
            for (int j = 0; j < columns.size(); j++) {
                if(getColumn().get(i).equals(columns.get(j))){
                    for (int j2 = 0; j2 < addedData.get(j).size(); j2++) {
                        this.getData().get(i).add(addedData.get(j).get(j2));
                    }
                    count++;
                }
            }
        }
        //System.out.println(count + " rows added");
    }
    
    public void insert(String[] columns, ArrayList<Object> values) throws Exception{
        if(columns.length != values.size()){
            return;
        }
        ArrayList<Integer> lastModified= new ArrayList<>();
        for (int i = 0; i < getColumn().size(); i++) {
            for (int j = 0; j < columns.length; j++) {

                if(getColumn().get(i).equals(columns[j])){
                    this.getData().get(i).add(values.get(j));
                    lastModified.add(i);
                }
            }
        }
    }



    public Rel union(Rel relation) throws Exception{
        if(sameTable(relation)){
            Rel newR = new Rel("union",getColumn());
            for (int i = 0; i < column.size(); i++) {
                newR.getData().add(new ArrayList<>());
            }
            if(this.getColumn().size() == relation.getColumn().size()){
                newR.insert(this.getColumn(), this.getData());
                newR.insert(relation.getColumn(), relation.getData());
            }
            return newR.removeDouble();

        }

        throw new Exception("Union exc");
    }

    public void resetData(){
        setData(new ArrayList<>());
        for (int i = 0; i < getColumn().size(); i++) {
            getData().add(new ArrayList<>());
        }
    }

    public Rel difference(Rel relation) throws Exception{
        Rel newR = new Rel("diff", getColumn());
        newR.resetData();
        ArrayList<String> concatSplitValues = concatSplit(" ");
        ArrayList<String> concatSplitValues2 = relation.concatSplit(" ");

        for (int i = 0; i < concatSplitValues.size(); i++) {
            for (int j = 0; j < concatSplitValues2.size(); j++) {
                if(concatSplitValues.get(i).equals(concatSplitValues2.get(j))){
                    concatSplitValues.remove(i);
                    concatSplitValues2.remove(j);
                    i--;
                }
            }
        }

        concatSplitValues.addAll(concatSplitValues2);
        for (int l = 0; l < concatSplitValues.size(); l++) {
            String[] split = concatSplitValues.get(l).split(" ");
            for (int i = 0; i < split.length; i++) {
                newR.getData().get(i).add(split[i]);
            }
        }

        return newR;
    }

    public Rel inter(Rel relation) throws Exception{
        if(sameTable(relation)){

            ArrayList<String> concatSplitValues = concatSplit(" ");
            ArrayList<String> concatSplitValues2 = relation.concatSplit(" ");

            ArrayList<String> val = new ArrayList<>();

            for (int i = 0; i < concatSplitValues.size(); i++) {
                boolean flg=false;
                for (int j = 0; j < concatSplitValues2.size(); j++) {
                    if(concatSplitValues.get(i).equals(concatSplitValues2.get(j))){
                        flg=true;
                    }
                }
                if(flg){
                    val.add(concatSplitValues.get(i));
                }    
            }
            Rel newR = unconcat(" ", val, "intersection");
            return newR.removeDouble();
        }
        throw new Exception("inter exc");
    }

    public Rel diff(Rel relation) throws Exception{
        if(sameTable(relation)){

            ArrayList<String> concatSplitValues = concatSplit(" ");
            ArrayList<String> concatSplitValues2 = relation.concatSplit(" ");

            ArrayList<String> val = new ArrayList<>();

            for (int i = 0; i < concatSplitValues.size(); i++) {
                boolean flg=false;
                for (int j = 0; j < concatSplitValues2.size(); j++) {
                    if(concatSplitValues.get(i).equals(concatSplitValues2.get(j))){
                        flg=true;
                    }
                }
                if(!flg){
                    val.add(concatSplitValues.get(i));
                }    
            }
            Rel newR = unconcat(" ", val, "difference");
            return newR.removeDouble();
        }
        throw new Exception("diff exc");
    }

    public Rel join(Rel relation, int columnIndex1, int columnIndex2) throws Exception{
        Rel cartes = produit(relation);

        ArrayList<String> cartSC = cartes.concatSplit(";");
        ArrayList<String> val = new ArrayList<>();

        for (int i = 0; i < cartSC.size(); i++) {
            String[] cartCS = cartSC.get(i).split(";");
            String a ="";
            if(cartCS[columnIndex1].equals(cartCS[getColumn().size()+columnIndex2])){
                val.add(cartSC.get(i));
            }
        }
        
        ArrayList<String> newcol = new ArrayList<>();
        newcol.addAll(getColumn());
        newcol.addAll(relation.getColumn());

        return unconcatJoin(";", val, "join",newcol);
    }   

    public Rel division(Rel relation) throws Exception{
        Rel newR;
        ArrayList<String> cols = columnDifferencet(getColumn(), relation.getColumn());
        ArrayList<String> cols1 = new ArrayList<>();
        cols1.addAll(relation.getColumn());
        cols1.addAll(cols);

        Rel R1 = projection(cols);
        
        
        Rel R2 = relation.produit(R1).diff(this.projection(cols1)).projection(cols);

        newR = R1.diff(R2);
        newR.setName("division");
        return newR;
    }

    public ArrayList<String> columnDifferencet(ArrayList<String> col1, ArrayList<String> col2){
        ArrayList<String> val = new ArrayList<>();
        val.addAll(col1);
        for (int i = 0; i < val.size(); i++) {
            if(col2.contains(val.get(i))){
                val.remove(i);
            }
        }
        return val;
    }

    public Rel removeDouble() throws Exception{
        ArrayList<String> concatSplitValues = concatSplit(";");
        ArrayList<String> val = new ArrayList<>();

        for (int i = 0; i < concatSplitValues.size(); i++) {
            boolean tmp = false;
            for (int j = 0; j < i; j++) {
                if(concatSplitValues.get(i).equals(concatSplitValues.get(j))){
                    tmp=true;
                }
            }
            if(!tmp){
                val.add(concatSplitValues.get(i));
            }
        }
        return unconcat(";", val, getName());
    }



    public ArrayList<Object> concatRel(ArrayList<Object> tab1, ArrayList<Object> tab2){
        ArrayList<Object> val = new ArrayList<>();
        val.addAll(tab1);
        val.addAll(tab2);
        return val;
    }

    public Rel projection(ArrayList<String> columns){
        Rel newR = new Rel("projection" ,columns);
        for (int i = 0; i < newR.getColumn().size(); i++) {
            for (int j = 0; j < this.getColumn().size(); j++) {
                if(newR.getColumn().get(i).equals(this.getColumn().get(j))){
                    newR.getData().add(this.getData().get(j));
                }   
            }
        }
        return newR;
    }
    
    public boolean sameTable(Rel relation){
        if(getColumn().size()!=relation.getColumn().size()){
            System.out.println("NOFQIDIHF");
            return false;
        }
        for (int i = 0; i < getColumn().size(); i++) {
            if(!getColumn().get(i).equals(relation.getColumn().get(i))){
                System.out.println("MISY OLANA");
                return false;
            }
        }
        return true;
    }

    public ArrayList<String> concatSplit(String regex) throws Exception{
        ArrayList<String> val = new ArrayList<>();
        for (int i = 0; i < getData().get(0).size(); i++) {
            String value = "";
            for (int j = 0; j < getData().size(); j++) {
                value += getData().get(j).get(i) + regex; 
            }
            val.add(value);
        }
        return val;
    }

    Rel unconcat(String regex, ArrayList<String> concated,String tabname){
        Rel newR = new Rel(tabname, getColumn());
        newR.resetData();

        for (int k = 0; k < concated.size(); k++) {
            String[] cct = concated.get(k).split(regex);
            for (int i = 0; i < getData().size(); i++) {
                newR.getData().get(i).add(cct[i]);
            }         
        }
        
        return newR;
    }

    Rel unconcatJoin(String regex, ArrayList<String> concated,String tabname, ArrayList<String> newColumn){
        Rel newR = new Rel(tabname, newColumn);
        newR.resetData();

        for (int k = 0; k < concated.size(); k++) {
            String[] cct = concated.get(k).split(regex);
            for (int i = 0; i < newR.getData().size(); i++) {
                newR.getData().get(i).add(cct[i]);
            }         
        }
        
        return newR;
    }


    public Rel produit(Rel relation){
        Rel R = new Rel();
        R.setName("produit cartésienne");
        R.getColumn().addAll(getColumn());
        R.getColumn().addAll(relation.getColumn());
        ArrayList<ArrayList<Object>> newData = new ArrayList<>();
        for (int i = 0; i < R.getColumn().size(); i++) {
            newData.add(new ArrayList<>());
        }
        for (int i = 0; i < getData().size(); i++) {
            for (int j = 0; j < getData().get(i).size(); j++) {
                for (int k = 0; k < relation.getData().get(0).size(); k++) {
                    newData.get(i).add(getData().get(i).get(j));
                }
                
            }
        }
        for (int k = 0; k < relation.getData().size(); k++) {
            for (int i = 0; i < getData().get(0).size(); i++) {
                for (int k2 = 0; k2 < relation.getData().get(k).size(); k2++) {
                    newData.get(getData().size()+k).add(relation.getData().get(k).get(k2));
                    
                }
            }
        }

        R.setData(newData);

        return R;
    }

}
