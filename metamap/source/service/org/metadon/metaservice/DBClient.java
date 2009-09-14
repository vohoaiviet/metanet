package org.metadon.metaservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Vector;
import java.io.IOException;
import java.awt.image.BufferedImage;

import org.metadon.metaservice.util.PhotoToolkit;
import org.metadon.metaservice.util.UserRepositoryManager;


public class DBClient {

	// define parameters of database server
	private final String dbUrl = "jdbc:mysql://localhost:3306/tmblog";
	private final String dbUser = "tomcat";
	private final String dbPass = "ToMTMbloG";

	private final String thumbnailPrefix = "thumb_";
	private final String photoExtension = ".jpg";

	private volatile static DBClient uniqueInstance;

	// singletone
	public static DBClient getInstance() {
		if (uniqueInstance == null) {
			synchronized (DBClient.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new DBClient();
					try {
						// register the JDBC driver for MySQL
						Class.forName("com.mysql.jdbc.Driver");
					} catch (ClassNotFoundException cnfe) {
						cnfe.printStackTrace();
					}
				}
			}
		}
		return uniqueInstance;
	}

	/** ***************************************************************** */

	public Connection getConnection() throws SQLException {
		// get a connection to the database
		Connection connection = DriverManager.getConnection(this.dbUrl,
				this.dbUser, this.dbPass);
		return connection;
	}

	/** ***************************************************************** */

	public Vector<String> getDevices(String user) {
		Vector<String> devices = new Vector<String>();
		try {
			Connection connection = this.getConnection();
			// get a statement object
			Statement stmt = connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			// get devices for a user
			ResultSet rs = stmt.executeQuery("SELECT id "
					+ "from device WHERE username='" + user + "'");

			while (rs.next()) {
				devices.add(rs.getString("id"));
			}
			connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return devices;
	}

	/** ***************************************************************** */

	public void trackUser(String username) {
		try {
			Connection connection = this.getConnection();
			// get a statement object
			Statement stmt = connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			// track user session or update session if a previous one exists
			ResultSet rs = stmt.executeQuery("SELECT username "
					+ "from trackeduser WHERE username='" + username + "'");
			if (!rs.next()) {
				stmt.execute("INSERT INTO trackeduser VALUES " + "('"
						+ username + "', null)");
				System.out.println("user " + username + " tracked.");
			} else {
				// update signon time
				stmt.execute("UPDATE trackeduser SET " + "signon = null "
						+ "WHERE username='" + username + "'");
				System.out.println("error: user " + username
						+ " already tracked.");
			}
			connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/** ***************************************************************** */

	public boolean isTrackedUser(String username) {
		if (username == null)
			return false;
		try {
			Connection connection = this.getConnection();
			// get a statement object
			Statement stmt = connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			// check if session exists
			ResultSet rs = stmt.executeQuery("SELECT username "
					+ "from trackeduser WHERE username='" + username + "'");
			if (rs.next()) {
				connection.close();
				return true;
			}
			connection.close();
			return false;
		} catch (SQLException se) {
			se.printStackTrace();
			return false;
		}
	}

	/** ***************************************************************** */

	public boolean isValidUser(String username, String deviceID) {
		if (username == null || deviceID == null)
			return false;
		Vector<String> devices = this.getDevices(username);
		for (int i = 0; i < devices.size(); i++) {
			System.out.println("device for " + username + ": "
					+ devices.elementAt(i));
			if (devices.elementAt(i).equals(deviceID))
				return true;
		}
		return false;
	}

	/** ***************************************************************** */

	public void releaseUser(String username) {
		try {
			Connection connection = this.getConnection();
			// get a statement object
			Statement stmt = connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			// release user if tracked
			ResultSet rs = stmt.executeQuery("SELECT username "
					+ "from trackeduser WHERE username='" + username + "'");
			if (rs.next()) {
				stmt.execute("DELETE FROM trackeduser WHERE " + "username='"
						+ username + "'");
				System.out.println("user " + username + " released.");
			} else {
				System.out.println("error: user " + username + " not tracked.");
			}
			connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/** ***************************************************************** */

	public void storeBlog(Blog blog) throws SQLException, IOException {
		Connection connection = this.getConnection();

		String sql = "insert into blog ("
				+ "id, timestamp, username, journeyname, message, longitude, latitude, elevation, photo_id) "
				+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(sql);

		// System.out.println("prepare sql statement...");
		pstmt.setNull(1, java.sql.Types.INTEGER);
		pstmt.setString(2, new Long(blog.getTimestamp()).toString());
		pstmt.setString(3, blog.getUserName());
		pstmt.setString(4, blog.getJourneyName());
		pstmt.setString(5, blog.getMessage());
		pstmt.setDouble(6, blog.getLongitude());
		pstmt.setDouble(7, blog.getLatitude());
		pstmt.setDouble(8, blog.getElevation());

		System.out.println("photo length to store: " + blog.getPhotoLength());
		if (blog.getPhotoLength() != 0) {
			// check if user repository exists
			if (!UserRepositoryManager.isExistingRepository(blog.getUserName())) {
				throw new IOException("repository does not exists for user: "
						+ blog.getUserName());
			}
			byte[] rawPhoto = blog.getPhoto();

			// reconstruct photo as jpg
			BufferedImage originalJPEG = PhotoToolkit.decodeAsJPEG(rawPhoto);

			// get dimensions
			int[] dimension = new int[] { originalJPEG.getWidth(),
					originalJPEG.getHeight() };

			// create thumbnail version
			BufferedImage thumbnailJPEG = PhotoToolkit.getThumbnail(originalJPEG);

			// get photo repository path
			String path = UserRepositoryManager.getPhotoRepositoryPath(blog
					.getUserName());
			String pathOrig = path + blog.getTimestamp() + this.photoExtension;
			String pathThumb = path + this.thumbnailPrefix
					+ blog.getTimestamp() + this.photoExtension;
			String photoName = blog.getTimestamp() + this.photoExtension;

			// store jpg files in user repository
			if (originalJPEG != null && thumbnailJPEG != null
					&& PhotoToolkit.storeAsPhoto(pathOrig, originalJPEG)
					&& PhotoToolkit.storeAsPhoto(pathThumb, thumbnailJPEG)) {
				// store photo reference
				int photoId = this.storePhotoReference(photoName, blog.getPhotoLength(), dimension);
				pstmt.setInt(9, photoId);
			}
		} else {
			// no photo id
			pstmt.setInt(9,0);
		}
		pstmt.executeUpdate();
		pstmt.close();
		connection.close();
	}

	private int storePhotoReference(String name, int size, int[] dimension)
			throws SQLException {
		Connection connection = this.getConnection();

		String sql = "insert into photo (" + "id, name, size, width, height) "
				+ "values(?, ?, ?, ?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(sql);

		int photoId = this.getNextPhotoId();
		pstmt.setInt(1, photoId);
		pstmt.setString(2, name);
		pstmt.setInt(3, size);
		pstmt.setInt(4, dimension[0]);
		pstmt.setInt(5, dimension[1]);
		pstmt.executeUpdate();
		connection.close();
		return photoId;
	}

	private int getNextPhotoId() throws SQLException {
		Connection connection = this.getConnection();
		Statement stmt = connection.createStatement();
		ResultSet rsLastId = stmt.executeQuery("SELECT MAX(id) FROM photo");
		int id = 0;
		if(rsLastId.next()) {
			id = rsLastId.getInt(1) + 1;
		} else {
			id = 1;
		}
		System.out.println("next photo id: "+id);
		connection.close();
		return id;
	}

}
