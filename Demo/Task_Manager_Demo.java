import java.sql.Connection;
import java.sql.DriverManager;

public class Task_Manager_Demo {
  private static final String DB_NAME = "taskmanager";
  private static final String DB_USER = "appUser";
  private static final String DB_PASS = "passUser";
  private static Connection conn;

  public static void main(String[] args) {
    try {
      // Carico il driver  
      Class.forName("com.mysql.cj.jdbc.Driver");
      // Connessione al database
      conn = DriverManager.getConnection("jdbc:mysql://localhost/" + DB_NAME + "?" +
                                             "user=" + DB_USER +"&password=" + DB_PASS);
      String cmd;
      do{
        // Stampa il menu
        cmd = printMenu();
        switch(cmd.toLowerCase()){
          // Ottieni le Task di un dato Utente
          case "1":
            Task_Manager_Util.getTaskByUser(conn);
          break;

          // Inserisci una nuova Task
          case "2":
            Task_Manager_Util.insertNewTask(conn);
          break;

          // Seleziona tutti i Progetti di un dato Utente, con Sezioni, Task, Commesse e Allegati
          case "3":
            Task_Manager_Util.getProgByUser(conn);
          break;

          // Cancella Oggetti di una Commessa
          case "4":
            Task_Manager_Util.deleteAllObjects(conn);
          break;

          // Completa tutte le Task di un dato Progetto
          case "5":
            Task_Manager_Util.updateCompletedTask(conn);
          break;

          case "exit":
            System.out.println("Uscita...");
          break;
        }
      }while(!cmd.toLowerCase().equals("exit"));

      conn.close();
      } catch (Exception e) {
        System.out.println("Errore nell'operazione!");
      }
  }

  private static String printMenu() throws Exception{
    System.out.println("\nInserisci il numero della funzione che vuoi eseguire:");
    System.out.println("1.Seleziona le Task di un dato Utente;");
    System.out.println("2.Inserisci una nuova Task per un dato Utente;");
    System.out.println("3.Seleziona tutti i Progetti di un dato Utente, con Sezioni, Task, Commesse e Allegati;");
    System.out.println("4.Cancella tutti gli Oggetti di una data Commessa;");
    System.out.println("5.Completa tutte le Task di un dato Progetto;");
    System.out.println("'Exit' per uscire.");
    return Task_Manager_Util.getInput("Scegli");
  }

}
