package springboot.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import springboot.domain.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// 리스트 1-6 독립된 SimpleConnectionMaker를 사용하게 만든 UserDao

public class UserDao {
     private ConnectionMaker connectionMaker;
     private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

//    public UserDao(){
//        connectionMaker = new DConnectionMaker(); // 앗! 그런데 여기에는 클래스 이름이 나오네
//    }

    //수정자 메소드 DI 방식
//    public void setConnectionMaker(ConnectionMaker connectionMaker) {
//        this.connectionMaker = connectionMaker;
//    }

//    public UserDao(ConnectionMaker connectionMaker){
//        this.connectionMaker = connectionMaker;
//    }

    public void add(User user) throws SQLException, ClassNotFoundException {
     //   Connection c = connectionMaker.makeConnection();
        Connection c = dataSource.getConnection();
        PreparedStatement ps = c.prepareStatement(
                "insert into users(id, name, password) values(?,?,?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();

        ps.close();
        c.close();
    }

    public User get(String id) throws SQLException, ClassNotFoundException {
        Connection c = dataSource.getConnection();
//        Connection c = connectionMaker.makeConnection();

        PreparedStatement ps = c
                .prepareStatement("select * from users where id = ?");

        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        User user = null;
        if(rs.next()){
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if(user == null) throw new EmptyResultDataAccessException(1);

        return user;
    }

    public void deleteAll() throws SQLException{
        Connection c = null;
        PreparedStatement ps = null;

        try {
            // 예외가 발생할 가능성이 있는 코드를 모두 try 블록으로 묶어준다.
            c = dataSource.getConnection();
            ps = c.prepareStatement("delete from users");
            ps.executeUpdate();
        }catch (SQLException e){
            // 예외가 발생했을 때 부가적인 작업을 해줄 수 있도록 catch 블록을 둔다.
            // 아직은 예외를 다시 메소드 밖으로 던지는 것밖에 없다.
            throw e;
        } finally {
            // finally이므로 try 블록에서 예외가 발생했을 때나 안 했을 때나 모두 실행된다.
            if(ps != null){
                try{
                    ps.close();
                } catch (SQLException e){

                }
            }
            if(c != null){
                try{
                    c.close();
                } catch (SQLException e){

                }
            }
        }

        ps.close();
        c.close();
    }

    public int getCount() throws SQLException{
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = dataSource.getConnection();
            ps = c.prepareStatement("select count(*) from users");
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e){
            throw e;
        } finally {
            if(rs != null){
                try{
                    rs.close();
                } catch (SQLException e){

                }
            }
            if(ps != null){
                try{
                    ps.close();
                } catch (SQLException e){

                }
            }
            if(c != null){
                try{
                    c.close();
                } catch (SQLException e){

                }
            }
        }


        rs.close();
        ps.close();
        c.close();
        return count;
    }




    
}