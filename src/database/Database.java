package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe modélisant la connexion a la base de données des cours
 * 
 */
public class Database {

	private Connection connexion;

	/**
	 * driver JDBC
	 */
	private String jdbcDriver = "org.hsqldb.jdbcDriver";

	/**
	 * fichiers db
	 */
	private String database = "jdbc:hsqldb:file:db/ubidb";

	/**
	 * utilisateur qui se connecte à la base de données
	 */
	private String user = "sa";

	/**
	 * mot de passe pour se connecter à la base de données
	 */
	private String password = "";

	/**
	 * Connexion à la base de donnée
	 */
	public void connexionBD() {
		try {
			// On commence par charger le driver JDBC d'HyperSQL
			Class.forName(jdbcDriver).newInstance();
		} catch (InstantiationException e) {
			System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			// Puis on se connecte à la base de données
			connexion = DriverManager.getConnection(database, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Arrête correctement HyperSQL
	 * 
	 * @throws SQLException
	 */
	public void arretBD() throws SQLException {
		Statement st = connexion.createStatement();
		// On envoie l'instruction pour arreter proprement HSQLDB
		st.execute("SHUTDOWN");
		// On ferme la connexion
		connexion.close(); // if there are no other open connection

	}

	public Connection getConnexion() {
		return connexion;
	}

	public static void main(String[] args) {
		Database ubidb = new Database();
		ubidb.connexionBD();
		try {
			System.out.println("creation de la table...");
			Statement s = ubidb.getConnexion().createStatement();
			s.executeUpdate("CREATE TABLE eleves (email CHAR(256))");

			System.out.println("destruction de la table...");
			Statement t = ubidb.getConnexion().createStatement();
			t.executeUpdate("DROP TABLE eleves");

			ubidb.arretBD();
			System.out.println("terminé !");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
