import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import redis.clients.jedis.Jedis;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/add")
public class Add extends HttpServlet {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String URL =
            "jdbc:mysql://120.48.0.79/bookmanager?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8";
    static final String USER = "root";
    static final String PASS = "!Tong0130";
    static final String SQL_ADD_NOTEPAD = "INSERT INTO book (id,name,price) values (?,?,?)";

    static Connection conn = null;
    static Jedis jedis = null;

    public void init() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(URL, USER, PASS);
            jedis = new Jedis("180.76.142.74");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String str = IOUtils.toString(request.getInputStream(), "UTF-8");
        Gson gson = new Gson();
        Book book = gson.fromJson(str, Book.class);

        Boolean delete = add(book);

        out.println(delete);

        out.flush();
        out.close();

    }

    private Boolean add( Book book) {
        Boolean flag = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(SQL_ADD_NOTEPAD);
            ps.setString(1,book.getId());
            ps.setString(2,book.getName());
            ps.setString(3,book.getPrice());

            int i = ps.executeUpdate();

            if (i > 0) {
                flag = true;
                jedis.del("booklist");
            } else {
                flag = false;
            }

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return flag;
    }

    class Book {
        private String id;
        private String name;
        private String price;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
}
