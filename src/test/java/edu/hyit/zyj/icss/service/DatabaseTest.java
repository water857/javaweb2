import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseTest {
    public static void main(String[] args) {
        try {
            // 连接数据库
            String dbUrl = "jdbc:mysql://localhost:3306/community_service?useSSL=false&serverTimezone=UTC";
            String dbUsername = "root";
            String dbPassword = "123456";
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

            // 执行查询
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM property_services";
            ResultSet resultSet = statement.executeQuery(query);

            // 打印查询结果
            while (resultSet.next()) {
                System.out.println("Service ID: " + resultSet.getInt("id"));
                System.out.println("Service Name: " + resultSet.getString("service_name"));
            }

            // 关闭连接
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
