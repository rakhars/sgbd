import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.management.relation.Relation;

import manager.FileManager;
import manager.RequestManager;
import relationobj.Rel;

public class Main{
    public static void main(String[] args) throws Exception {
        
        System.out.println((int)'t'); 
        FileManager fm = new FileManager("file.db");
        RequestManager rm = null;
        try {
            rm = fm.getRequestManager();
        } catch (Exception e) {
            System.out.println("caught");
            rm = defaultRM();
        }

        boolean flg = true;
        Scanner scan = null;
        while(flg){
            scan = new Scanner(System.in);
            System.out.print(">>  ");
            String req = scan.nextLine();
            if(req.equals("clear")){
                System.out.flush();
                continue;
            }else if(req.equals("exit")){
                flg = false;
            }
            try {
                System.out.println(" ");
                rm.requ(req);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        //String requestTest2 = "ampidiro @ test1 nom,prenom Saro,bidy";

        /*rm.requ("tables");
        rm.requ("alaivo @ A *");
        rm.requ("alaivo @ B *");

        //String requestTest = "alaivo @ test3;test1;0=0 *";
        String requestTest = "alaivo @ A * diff alaivo @ B *";
        rm.requ(requestTest);*/

        fm.writeObject(rm);

        fm.getFile().delete();
        fm = new FileManager("file.db"); 
        fm.writeObject(rm);

    }

    public static void AfficheRel(Rel r){
        System.out.println("Table: " + r.getName());
        for (int i = 0; i < r.getData().size(); i++) {
            for (int j = 0; j < r.getData().get(i).size(); j++) {
                /* Afficher les columns */
                if(j==0){
                    System.out.print("<<" + r.getColumn().get(i) + ">>  ");    
                }
                /* les données */
                System.out.print("  " + r.getData().get(i).get(j) + "  |");
                
            }
            System.out.println();
        }
        System.out.println();
    }


    public static RequestManager defaultRM() throws Exception{
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        ArrayList<Object> data1 = new ArrayList<>();
        ArrayList<Object> data2 = new ArrayList<>();

        data1.add("Jean"); data1.add("Rakoto"); data1.add("Rabe");
        data2.add("Claude"); data2.add("Bevata"); data2.add("Jary");

        columns.add("nom");     columns.add("prenom");
        data.add(data1);           data.add(data2); 

        Rel DataTest = new Rel("test1", columns, data);
        
        ArrayList<ArrayList<Object>> inserted = new ArrayList<>();
        ArrayList<Object> insertedNom = new ArrayList<>();
        ArrayList<Object> insertedPrenom = new ArrayList<>();
        insertedNom.add("Za");
        insertedNom.add("Ilo");
        
        insertedPrenom.add("Tota");
        insertedPrenom.add("Hity");
        inserted.add(insertedNom); inserted.add(insertedPrenom);
        Rel DataTest2 = new Rel("test2",columns, inserted);

        ArrayList<ArrayList<Object>> test3 = new ArrayList<>();
        ArrayList<Object> testNom = new ArrayList<>();
        ArrayList<Object> testPrenom = new ArrayList<>();
        ArrayList<String> testColumns = new ArrayList<>();

        testColumns.add("nom");
        //testColumns.add("puissance");

        testNom.add("Jean");
        testNom.add("Za");

        //testPrenom.add("rond");
        test3.add(testNom); //test3.add(testPrenom);
        Rel DataTest3 = new Rel("test3",testColumns,test3);

        //AfficheRel(DataTest2);

        ArrayList<String> col = new ArrayList<>();
        col.add("prenom");
        ArrayList<String> colA1 = new ArrayList<>();
        colA1.add("a"); colA1.add("b");

        ArrayList<String> colA2 = new ArrayList<>();
        colA2.add("b");

        ArrayList<ArrayList<Object>> d1 = new ArrayList<>();
        ArrayList<ArrayList<Object>> d2 = new ArrayList<>();

        ArrayList<Object> dA2 = new ArrayList<>();
        dA2.add("b1");
        d2.add(dA2);
        
        ArrayList<Object> dA1 = new ArrayList<>();
        ArrayList<Object> dB1 = new ArrayList<>();
        dA1.add("a1");  dB1.add("b1");
        dA1.add("a2");  dB1.add("b2");
        
        d1.add(dA1);  d1.add(dB1);
        Rel a = new Rel("A", colA1, d1);
        Rel b = new Rel("B",colA2, d2);
        
        
        DataTest.insert(columns, inserted);

        ArrayList<Rel> rels = new ArrayList<>();
        rels.add(DataTest);
        rels.add(DataTest2);
        rels.add(DataTest3);
        rels.add(a);
        rels.add(b);
        return new RequestManager(rels);
    }

}