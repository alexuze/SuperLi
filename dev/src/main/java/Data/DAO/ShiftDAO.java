package Data.DAO;


import Misc.Pair;
import Misc.TypeOfShift;
import Data.DTO.EmployeeDTO;
import Misc.Pair;
import Data.DTO.ShiftDTO;
import Data.Repository;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class ShiftDAO extends DAO<ShiftDTO> {

    public ShiftDAO() {
        this.tableName = "Shifts";
    }
    @Override
    public int insert(ShiftDTO Ob) {
        Connection conn = Repository.getInstance().connect();
        if (Ob == null) return 0;
        String toInsertShift = this.InsertStatement(Ob.fieldsToString());
        Statement s;
        try {
            s = conn.createStatement();
            s.executeUpdate(toInsertShift);
            int resConstraints = insertToShiftConstraints(Ob );
            int resEmpInShift = insertToEmployeeInShift(Ob);
            if (resConstraints + resEmpInShift == 2) {
                return 1;
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
        finally {
            Repository.getInstance().closeConn(conn);
        }
    }
    private int insertToShiftConstraints(ShiftDTO Ob) {
        Connection conn = Repository.getInstance().connect();
        if (Ob == null) return 0;
        try {
            for (String type : Ob.getConstraintsMap().keySet()) {
                String toInsertConstraints = String.format("INSERT INTO %s \n" +
                        "VALUES %s;", "ShiftConstraints", Ob.getConstraint(type));

                Statement s;

                    s = conn.createStatement();
                    s.executeUpdate(toInsertConstraints);


            }
        } catch (Exception e) {
            return 0;
        }
        finally {
            Repository.getInstance().closeConn(conn);
        }



        return 1;
    }
    private int insertToEmployeeInShift(ShiftDTO Ob) {
        Connection conn = Repository.getInstance().connect();
        if (Ob == null) return 0;
        try {
            for (int index = 0; index < Ob.getNumberOfEmpInShift(); index++) {
                String toInsert = String.format("INSERT INTO %s \n" +
                        "VALUES %s;", "EmployeesInShift", Ob.getEmployees(index));
                Statement s;

                    s = conn.createStatement();
                    s.executeUpdate(toInsert);

            }
        } catch (Exception e) {
            return 0;
        }
        finally {
            Repository.getInstance().closeConn(conn);
        }

        return 1;
    }

    @Override

    public int update(ShiftDTO updatedOb)//not allowed to change ID
    {
        Connection conn = Repository.getInstance().connect();
        if(updatedOb == null) return 0;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String updateString = String.format("UPDATE %s" +
                        " SET  Date= \"%s\" " +
                        ", TypeOfShift=\"%s\", IsSealed=%s" +
                        " WHERE ID == %s ;",
                tableName,formatter.format(updatedOb.date),updatedOb.type,
                updatedOb.isSealed ? 1 : 0, updatedOb.shiftId);
        Statement s;
        try {
            s = conn.createStatement();
            return s.executeUpdate(updateString);
        }
        catch (Exception e ){
            return 0;
        }
        finally {
            Repository.getInstance().closeConn(conn);
        }

    }

    public int getShiftIdByDateAndType(Date date, String type)
    {
        Connection conn = Repository.getInstance().connect();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String updateString = String.format("select ID From Shifts where Date = \"%s\" AND TypeOfShift==\"%s\"",formatter.format(date),type);
        ResultSet rs=null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(updateString);
            if(rs==null)
            {
                return -1;
            }
            int x =  rs.getInt(1);
            return x;

        }
        catch (Exception e ){
            return -1;
        }
        finally {
            Repository.getInstance().closeConn(conn);
        }

    }
    public int updateConstraint(Date date,String typeOfShift, String TypeOfEmployee, int amount)
    {
        int ShiftID = getShiftIdByDateAndType(date, typeOfShift);
        Connection conn = Repository.getInstance().connect();
        if(TypeOfEmployee == null || ShiftID < 0 || amount<0) return 0;
        String updateString = String.format("UPDATE %s" +
                        " SET  \"ShiftID\"= %s " +
                        ", \"TypeOfEmployee\"=\"%s\",  \"Amount\"=%s" +
                        " WHERE \"ShiftID\" == %s ;",
                "ShiftConstraints", ShiftID,TypeOfEmployee,amount, ShiftID);
        Statement s;
        try {
            s = conn.createStatement();
            return  s.executeUpdate(updateString);
        }
        catch (Exception e ){
            return 0;
        }
        finally {
            Repository.getInstance().closeConn(conn);
        }

    }

    public int addEmployeeToShift(String EmployeeID,int ShiftID ,String RoleInShift)
    {
        Connection conn = Repository.getInstance().connect();
        String updateString;
        if(EmployeeID == null || ShiftID < 0 || RoleInShift == null) return 0;
        updateString= String.format("INSERT INTO %s \n" +
                "VALUES (\"%s\",%s,\"%s\",%s);", "EmployeesInShift", EmployeeID, ShiftID,RoleInShift, null);
        Statement s;
        try
        {
            s = conn.createStatement();
            return s.executeUpdate(updateString);
        }
        catch (Exception e ){
            return 0;
        }
        finally {
            Repository.getInstance().closeConn(conn);
        }

    }
    public int addDriverToShift(String EmployeeID,int ShiftID ,String RoleInShift)
    {
        Connection conn = Repository.getInstance().connect();
        String updateString;
        if(EmployeeID == null || ShiftID < 0 || RoleInShift == null) return 0;
        updateString= String.format("INSERT INTO %s \n" +
                "VALUES (%s,%s,\"%s\",\"%s\");", "EmployeesInShift",null, ShiftID,RoleInShift,  EmployeeID);
        Statement s;
        try
        {
            s = conn.createStatement();
            return s.executeUpdate(updateString);
        }
        catch (Exception e ){
            return 0;
        }
        finally {
            Repository.getInstance().closeConn(conn);
        }

    }


    public int removeEmployeeFromShift( String EmployeeID,int ShiftID,String RoleInShift)
    {
        Connection conn = Repository.getInstance().connect();
        String updateString;
        if(EmployeeID == null || ShiftID < 0 || RoleInShift==null) return 0;
        updateString= String.format("DELETE FROM %s \n" +
                "WHERE %s=\"%s\" AND %s=%s AND %s=\"%s\";", "EmployeesInShift", "EmployeeID", EmployeeID,"ShiftID" ,ShiftID, "RoleInShift", RoleInShift);
        Statement s;
        try
        {
            s = conn.createStatement();
            return s.executeUpdate(updateString);
        }
        catch (Exception e )
        {
            return 0;
        }
        finally {
            Repository.getInstance().closeConn(conn);
        }

    }
    public int removeDriverFromShift( String EmployeeID,int ShiftID,String RoleInShift)
    {
        Connection conn = Repository.getInstance().connect();
        String updateString;
        if(EmployeeID == null || ShiftID < 0 || RoleInShift==null) return 0;
        updateString= String.format("DELETE FROM %s \n" +
                "WHERE %s=\"%s\" AND %s=%s AND %s=\"%s\";", "EmployeesInShift", "DriverID", EmployeeID,"ShiftID" ,ShiftID, "RoleInShift", RoleInShift);
        Statement s;
        try
        {
            s = conn.createStatement();
            return s.executeUpdate(updateString);
        }
        catch (Exception e )
        {
            return 0;
        }
        finally {
            Repository.getInstance().closeConn(conn);
        }

    }
    public int addConstraints(int ShiftID,String TypeOfEmployee, int amount)
    {
        Connection conn = Repository.getInstance().connect();
        String updateString;
        if(TypeOfEmployee == null || ShiftID < 0 || amount<0) return 0;
        updateString= String.format("INSERT INTO %s \n" +
                "VALUES (%s,\"%s\",%s);", "ShiftConstraints", ShiftID, TypeOfEmployee,amount);
        Statement s;
        try
        {
            s = conn.createStatement();
            return s.executeUpdate(updateString);
        }
        catch (Exception e )
        {
            return 0;
        }
        finally
        {
            Repository.getInstance().closeConn(conn);
        }

    }

    public int removeConstraints(int ShiftID,String TypeOfEmployee)
    {
        Connection conn = Repository.getInstance().connect();
        String updateString;
        if(TypeOfEmployee == null || ShiftID < 0 ) return 0;
        updateString= String.format("DELETE FROM %s \n" +
                "WHERE %s=%s AND %s=\"%s\";", "ShiftConstraints", "ShiftID", ShiftID,"TypeOfEmployee" ,TypeOfEmployee);
        Statement s;
        try
        {
            s = conn.createStatement();
            return s.executeUpdate(updateString);
        }
        catch (Exception e )
        {
            return 0;
        }
        finally
        {
            Repository.getInstance().closeConn(conn);
        }

    }



    @Override

    public ShiftDTO makeDTO(ResultSet RS)
    {
        ShiftDTO output ;
        try {
            int id = RS.getInt(1);
            Map<String, Integer> constraints = getconstraintsList(id);
            if (constraints == null) {
                return null;
            }
            List<Pair<String/*empId*/, String/*typeOfEmployee*/>> currentShiftEmployees = getcurrentShiftEmployeesList(RS.getInt(1)/*id*/);
            if (currentShiftEmployees == null) {
                return null;
            }
            /*
             public ShiftDTO(Integer Id,String type, Date date)
             */
            output = new ShiftDTO(/*id*/RS.getInt(1),/*type*/RS.getString(3),/*date*/new SimpleDateFormat("dd/MM/yyyy").parse(RS.getString(2)), RS.getInt(4), constraints, currentShiftEmployees);
        } catch (Exception e) {
            output = null;
        }
        return output;
    }


    public int removeShift(int ShiftID)
    {
        Connection conn = Repository.getInstance().connect();
        String updateString;
        if(ShiftID < 0 ) return 0;
        updateString= String.format("DELETE FROM %s \n" +
                "WHERE %s=%s;", "Shifts", "ID", ShiftID);
        Statement s;
        try
        {
            s = conn.createStatement();
            return s.executeUpdate(updateString);
        }
        catch (Exception e )
        {
            return 0;
        }
        finally
        {
            Repository.getInstance().closeConn(conn);
        }
    }

    private List<Pair<String, String>> getcurrentShiftEmployeesList(int shiftId) {
        List<Pair<String, String>> ans =new LinkedList<>();
        Connection conn = Repository.getInstance().connect();
        ResultSet rs=getWithInt("EmployeesInShift","ShiftID", shiftId, conn  );
        try
        {
            while (rs.next())
            {
                Pair<String, String> p;
                if(rs.getString(1) != null) //Employee is not a driver
                    p = new Pair<>(rs.getString(1),rs.getString(3)/*type of employee*/);
                else //driver
                    p = new Pair<>(rs.getString(4),rs.getString(3)/*type of employee*/);
                ans.add(p);
            }
        } catch (Exception e) {
            return null;
        } finally {
            Repository.getInstance().closeConn(conn);
        }
        return ans;
    }

    private Map<String, Integer> getconstraintsList(int shiftId) {
        Connection conn = Repository.getInstance().connect();
        Map<String, Integer> ans= new HashMap<>();
        ResultSet rs=getWithInt("ShiftConstraints","ShiftID", shiftId,  conn);
        try
        {
            while (rs.next())
            {
                ans.put(rs.getString(2)/*type of employee*/,rs.getInt(3)/*amount*/);

            }
        } catch (Exception e) {
            return null;
        } finally{
            Repository.getInstance().closeConn(conn);
        }
        return ans;
    }



}
