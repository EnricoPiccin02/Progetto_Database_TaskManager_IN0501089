import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Task_Manager_Util {
  // Metodo per l'input da tastiera
  public static String getInput(String parMsg) throws Exception{
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      System.out.print(parMsg+": ");
      String s = br.readLine();
      return s;
    }

  public static void getTaskByUser(Connection conn) throws Exception{
      PreparedStatement stmt = 
      conn.prepareStatement("SELECT ID, Testo, DataAssegnazione, Completata, DataCompletamento " +
                            "FROM Task WHERE Email = ? AND Username = ?");

      stmt.setString(1,getInput("Inserisci email"));     
      stmt.setString(2,getInput("Inserisci username"));

      ResultSet rs = stmt.executeQuery(); 

      while(rs.next()){
        System.out.println("ID: " + rs.getInt(1));
        System.out.println("Testo: " + rs.getString(2));
        System.out.println("DataAssegnazione: " + rs.getString(3));
        System.out.println("Completata: " + (rs.getInt(4)!=0 ? "Sì" : "No"));
        System.out.println("DataCompletamento: " + rs.getString(5));
        System.out.println();
      }

      stmt.close();
      rs.close();
   }

    public static void insertNewTask(Connection conn) throws Exception{
      // Invocazione della Stored Procedure
      CallableStatement cStm = conn.prepareCall("{call sp_insertTask(?, ?, ?, ?, ?, ?)}");
      cStm.setString(1,getInput("Inserisci testo"));

      int taskPadre = Integer.parseInt(getInput("Inserisci task padre"));
      // Gestione di Task priva di padre
      if(taskPadre > 0)
        cStm.setInt(2, taskPadre);
      else
        cStm.setNull(2, java.sql.Types.NULL);
      cStm.setString(3,getInput("Inserisci sezione"));
      cStm.setInt(4,Integer.parseInt(getInput("Inserisci progetto")));
      cStm.setString(5,getInput("Inserisci email"));
      cStm.setString(6,getInput("Inserisci username"));

      boolean hadResults = cStm.execute();
      cStm.close();
      System.out.println("Inserimento effettuato con successo.");
    }

    public static void getProgByUser(Connection conn) throws Exception{
      PreparedStatement stmt = conn.prepareStatement("SELECT * FROM V_Progetti WHERE Email = ? AND Username = ?");

      stmt.setString(1,getInput("Inserisci email"));     
      stmt.setString(2,getInput("Inserisci username"));

      ResultSet rs = stmt.executeQuery();

      while(rs.next()){
        System.out.println("Email: " + rs.getString(1));
        System.out.println("Username: " + rs.getString(2));
        System.out.println("Codice Progetto: " + rs.getString(3));
        System.out.println("Descrizione Progetto: " + rs.getString(4));
        System.out.println("Data Progetto: " + rs.getString(5));
        System.out.println("Sezione: " + rs.getString(6));
        System.out.println("Task: " + rs.getString(7));
        System.out.println("Data Assegnazione Task: " + rs.getString(8));
        System.out.println("Completata: " + (rs.getInt(9)!=0 ? "Sì" : "No"));
        System.out.println("Data Completamento Task: " + rs.getString(10));
        System.out.println("Codice Commessa: " + rs.getString(11));
        System.out.println("Categoria Commessa: " + rs.getString(12));
        System.out.println("Nome Analisi: " + rs.getString(13));
        System.out.println("Versione Analisi: " + rs.getInt(14));
        System.out.println("Redattore Analisi: " + rs.getString(15));
        System.out.println("Path Oggetto: " + rs.getString(16));
        System.out.println("Nome Oggetto: " + rs.getString(17));
        System.out.println("Dimensione Oggetto: " + rs.getInt(18));
        System.out.println();
      }
    }

    public static void deleteAllObjects(Connection conn) throws Exception{
      PreparedStatement del1, del2, stmt;
      // Cancellazione Allegato
      del1 = conn.prepareStatement("DELETE FROM Allegato WHERE OggettoPath = ? AND OggettoNome = ?");
      // Cancellazione Oggetto
      del2 = conn.prepareStatement("DELETE FROM Oggetto WHERE Path = ? AND Nome = ?");
      // Selezione Oggetti della Commessa
      stmt = conn.prepareStatement("SELECT Path, Nome FROM Oggetto AS O " +
            "INNER JOIN Allegato AS A ON O.Path = A.OggettoPath " +
            "AND O.Nome = A.OggettoNome WHERE Commessa = ?");

      stmt.setString(1,getInput("Inserisci commessa"));
      ResultSet rs = stmt.executeQuery();
      while(rs.next()){
        String path = rs.getString(1);
        String nome = rs.getString(2);

        // Cancellazione Allegato
        del1.setString(1, path);
        del1.setString(2, nome); 
        del1.executeUpdate();

        // Cancellazione Oggetto
        del2.setString(1, path);
        del2.setString(2, nome);
        del2.executeUpdate();
      }

      rs.close();
      stmt.close();
      del1.close();
      del2.close();

      System.out.println("Tutti gli Oggetti sono stati cancellati.");
    }

    public static void updateCompletedTask(Connection conn) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("UPDATE Task SET Completata = 1, DataCompletamento = NOW() " +
          "WHERE Sezione IN (SELECT S.Nome FROM Sezione AS S " +
          "INNER JOIN Progetto AS P ON S.Progetto = P.Codice " +
          "WHERE P.Codice = ?)");

        stmt.setInt(1, Integer.parseInt(getInput("Inserisci progetto")));
        stmt.executeUpdate();

        stmt.close();
        System.out.println("Task completate!");
      }
}
