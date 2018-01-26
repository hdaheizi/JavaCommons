package top.daheizi.commons.test;

import java.sql.*;

public class TestSql {
    public static void main(String[] args) {

        String url="jdbc:mysql://localhost:2092/test";
        String name="root";
        String password="zhaodi2092";
        String check="select password from users where name in (select name from students where class = ?)";
        
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn=DriverManager.getConnection(url,name,password);
            PreparedStatement stmt = conn.prepareStatement(check);
            
            ResultSet rs=null;
            for(int i=1;i<=2;i++){
                stmt.setString(1,"class"+i);
                rs = stmt.executeQuery();
                
                System.out.println("class"+i+"i");
                while (rs.next()) {
                    System.out.println(rs.getString("password"));
                  }
            }
            

            stmt.close();
            conn.close();

            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
    }

}
