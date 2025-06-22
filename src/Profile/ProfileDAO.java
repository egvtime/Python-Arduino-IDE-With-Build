package Profile;

import Profile.ArduinoProfile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class ProfileDAO {
    private static final String HOST     = "py-uno-python-arduino-ide.g.aivencloud.com";
    private static final int    PORT     = 10385;
    private static final String DATABASE = "ArduinoProfile";
    private static final String USER     = "avnadmin";
    private static final String PASSWORD = "AVNS_N54q6TR_jglwl9LZTCD";

    private String loadCACert() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/ca.pem")) {
            if (in == null) {
                throw new IOException("Resource ca.pem not found");
            }
            Path temp = Files.createTempFile("aiven-ca", ".pem");
            Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
            temp.toFile().deleteOnExit();
            return temp.toAbsolutePath().toString();
        }
    }

    private Connection getConnection() throws SQLException, IOException {
        String url = String.format("jdbc:postgresql://%s:%d/%s", HOST, PORT, DATABASE);
        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);
        props.setProperty("sslmode", "verify-full");
        props.setProperty("sslrootcert", loadCACert());
        return DriverManager.getConnection(url, props);
    }

    public int getHighestID() throws SQLException, IOException {
        String sql = "SELECT COALESCE(MAX(id),0) AS max_id FROM public.profiles";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("max_id") : 0;
        }
    }

    public boolean usernameExists(String username) throws SQLException, IOException {
        String sql = "SELECT 1 FROM public.profiles WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean saveProfile(ArduinoProfile profile) throws SQLException, IOException {
        if (usernameExists(profile.getUserName())) return false;
        String sql = "INSERT INTO public.profiles(id, username, password, picture, project_titles, project_infos, project_descriptions) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, profile.getId());
            ps.setString(2, profile.getUserName());
            ps.setString(3, profile.getPassword());
            byte[] picBytes = null;
            Image pic = profile.getProfilePicture();
            if (pic != null) {
                WritableImage wr = new WritableImage((int)pic.getWidth(), (int)pic.getHeight());
                new ImageView(pic).snapshot(new SnapshotParameters(), wr);
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(SwingFXUtils.fromFXImage(wr, null), "png", baos);
                    picBytes = baos.toByteArray();
                }
            }
            ps.setBytes(4, picBytes);
            Vector<ArduinoProfile.Project> projects = profile.getProjects();
            int n = projects.size();
            String[] titles = new String[n], infos = new String[n], descs = new String[n];
            for (int i = 0; i < n; i++) {
                ArduinoProfile.Project p = projects.get(i);
                titles[i] = p.getTitle(); infos[i] = p.getInfo(); descs[i] = p.getDescription();
            }
            ps.setArray(5, conn.createArrayOf("text", titles));
            ps.setArray(6, conn.createArrayOf("text", infos));
            ps.setArray(7, conn.createArrayOf("text", descs));
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateProfile(ArduinoProfile profile) throws SQLException, IOException {
        String existing = getUsernameById(profile.getId());
        if (!profile.getUserName().equals(existing) && usernameExists(profile.getUserName())) return false;
        String sql = "UPDATE public.profiles SET username=?, password=?, picture=?, project_titles=?, project_infos=?, project_descriptions=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, profile.getUserName());
            ps.setString(2, profile.getPassword());
            byte[] picBytes = null;
            Image pic = profile.getProfilePicture();
            if (pic != null) {
                WritableImage wr = new WritableImage((int)pic.getWidth(), (int)pic.getHeight());
                new ImageView(pic).snapshot(new SnapshotParameters(), wr);
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(SwingFXUtils.fromFXImage(wr, null), "png", baos);
                    picBytes = baos.toByteArray();
                }
            }
            ps.setBytes(3, picBytes);
            Vector<ArduinoProfile.Project> projects = profile.getProjects();
            int n = projects.size();
            String[] titles = new String[n], infos = new String[n], descs = new String[n];
            for (int i = 0; i < n; i++) {
                ArduinoProfile.Project p = projects.get(i);
                titles[i] = p.getTitle(); infos[i] = p.getInfo(); descs[i] = p.getDescription();
            }
            ps.setArray(4, conn.createArrayOf("text", titles));
            ps.setArray(5, conn.createArrayOf("text", infos));
            ps.setArray(6, conn.createArrayOf("text", descs));
            ps.setInt(7, profile.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public ArduinoProfile getProfileThroughUsername(String username) throws SQLException, IOException {
        String sql = "SELECT id, password, picture, project_titles, project_infos, project_descriptions FROM public.profiles WHERE username=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                int id = rs.getInt("id");
                String pwd = rs.getString("password");
                byte[] picBytes = rs.getBytes("picture");
                Image pic = (picBytes!=null && picBytes.length>0)? new Image(new ByteArrayInputStream(picBytes)) : null;
                String[] titles = (String[]) rs.getArray("project_titles").getArray();
                String[] infos  = (String[]) rs.getArray("project_infos").getArray();
                String[] descs  = (String[]) rs.getArray("project_descriptions").getArray();
                Vector<ArduinoProfile.Project> projects = new Vector<>();
                for (int i = 0; i < titles.length; i++) projects.add(new ArduinoProfile.Project(titles[i], infos[i], descs[i]));
                return new ArduinoProfile(id, username, pwd, projects, pic);
            }
        }
    }

    public ArduinoProfile getProfileThroughID(int id) throws SQLException, IOException {
        String sql = "SELECT username, password, picture, project_titles, project_infos, project_descriptions FROM public.profiles WHERE id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String user = rs.getString("username");
                String pwd = rs.getString("password");
                byte[] picBytes = rs.getBytes("picture");
                Image pic = (picBytes!=null && picBytes.length>0)? new Image(new ByteArrayInputStream(picBytes)) : null;
                String[] titles = (String[]) rs.getArray("project_titles").getArray();
                String[] infos  = (String[]) rs.getArray("project_infos").getArray();
                String[] descs  = (String[]) rs.getArray("project_descriptions").getArray();
                Vector<ArduinoProfile.Project> projects = new Vector<>();
                for (int i = 0; i < titles.length; i++) projects.add(new ArduinoProfile.Project(titles[i], infos[i], descs[i]));
                return new ArduinoProfile(id, user, pwd, projects, pic);
            }
        }
    }

    public boolean deleteProfile(int id) throws SQLException, IOException {
        String sql = "DELETE FROM public.profiles WHERE id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); return ps.executeUpdate()>0;
        }
    }

    public boolean removeProject(int profileId, String projectTitle) throws SQLException, IOException {
        ArduinoProfile prof = getProfileThroughID(profileId);
        if (prof == null) return false;
        Vector<ArduinoProfile.Project> projs = prof.getProjects();
        boolean rem = projs.removeIf(p->p.getTitle().equals(projectTitle));
        return rem && updateProfile(prof);
    }

    private String getUsernameById(int id) throws SQLException, IOException {
        String sql = "SELECT username FROM public.profiles WHERE id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()? rs.getString("username") : null;
            }
        }
    }
}
