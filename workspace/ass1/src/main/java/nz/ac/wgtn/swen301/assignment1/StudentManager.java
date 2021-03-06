package main.java.nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.studentdb.*;
import java.sql.*;
import java.util.*;

/**
 * A student managers providing basic CRUD operations for instances of Student, and a read operation for instances of Degree.
 * @author jens dietrich
 */
public class StudentManager {
	// Credit: https://www.tutorialspoint.com/jdbc/jdbc-sample-code.htm
	
    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND THE APPLICATION CAN CONNECT TO IT WITH JDBC
    static {
        StudentDB.init();
    }
    // DO NOT REMOVE BLOCK ENDS HERE

    // Database Credentials
	private final static String JDBC_URL = "jdbc:derby:memory:studentdb";
    private static Connection conn = null;
    private static Statement stmt = null;
    
    //Database in map format
	private static Map<String, Degree> MAP_DEGREES = new HashMap<>();
    private static Map<String, Student> MAP_STUDENTS = new HashMap<>();
    
    //Little helper memory
    private static Queue<Integer> VACANT_ID_LIST = new ArrayDeque<>();
    
    // THE FOLLOWING METHODS MUST IMPLEMENTED :
    /**
     * Return a student instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id
     * @return
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_readStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student readStudent(String id) throws NoSuchRecordException {
    	retrieveData();
    	if(!MAP_STUDENTS.containsKey(id)) throw new NoSuchRecordException();
    	
        return MAP_STUDENTS.get(id);
    }

    /**
     * Return a degree instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id
     * @return
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_readDegree (followed by optional numbers if multiple tests are used)
     */
    public static Degree readDegree(String id) throws NoSuchRecordException {
    	retrieveData();
    	if(!MAP_DEGREES.containsKey(id)) throw new NoSuchRecordException();
    	
        return MAP_DEGREES.get(id);
    }

    /**
     * Delete a student instance from the database.
     * I.e., after this, trying to read a student with this id will result in a NoSuchRecordException.
     * @param student
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_delete
     */
    public static void delete(Student student) throws NoSuchRecordException {
    	retrieveData();
    	if(!MAP_STUDENTS.containsValue(student)) throw new NoSuchRecordException();
    	
    	//Retrieve info
    	String id = student.getId();
    	
    	//Delete Student			Credit: https://www.boraji.com/jdbc-delete-record-example
    	try {
    		String sql = "DELETE from Students where id='" + id + "'";
    		stmt.executeUpdate(sql);
    		System.out.println("Student deletion successful");
    	} catch(SQLException e) {
    		System.out.println("Failed to delete student");
    		e.printStackTrace();
    	}
    	
    	//Remember vacant ID slot
    	int vacantid = Integer.parseInt(id.replaceAll("[^\\d]", ""));
    	VACANT_ID_LIST.add(vacantid);
    }

    /**
     * Update (synchronize) a student instance with the database.
     * The id will not be changed, but the values for first names or degree in the database might be changed by this operation.
     * After executing this command, the attribute values of the object and the respective database value are consistent.
     * Note that names and first names can only be max 1o characters long.
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param student
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_update (followed by optional numbers if multiple tests are used)
     */
    public static void update(Student student) throws NoSuchRecordException {
    	retrieveData();
    	if(!MAP_STUDENTS.keySet().contains(student.getId())) throw new NoSuchRecordException();
    	
    	//Retrieve info
    	String id = student.getId();
    	String firstName = student.getFirstName();
    	String name = student.getName();
    	Degree degree = student.getDegree();
    	
    	//Update Student			Credit: https://www.boraji.com/jdbc-update-record-example
    	try {
    		String sql = "UPDATE Students set first_name='" + firstName + "' where id='" + id + "'";
    		stmt.executeUpdate(sql);
    		sql = "UPDATE Students set name='" + name + "' where id='" + id + "'";
    		stmt.executeUpdate(sql);
    		sql = "UPDATE Students set degree='" + degree.getId() + "' where id='" + id + "'";
    		stmt.executeUpdate(sql);
    		
    		System.out.println("Student update successful");
    		
    	} catch(SQLException e) {
    		System.out.println("Failed to update student");
    		e.printStackTrace();
    	}
    }


    /**
     * Create a new student with the values provided, and save it to the database.
     * The student must have a new id that is not been used by any other Student instance or STUDENTS record (row).
     * Note that names and first names can only be max 1o characters long.
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param name
     * @param firstName
     * @param degree
     * @return a freshly created student instance
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_createStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student createStudent(String name,String firstName,Degree degree) {
    	retrieveData();
        //First, find available ID.
    	String ID;
        if(!VACANT_ID_LIST.isEmpty()) {
    		ID = "id" + VACANT_ID_LIST.poll();
    	} else {
    		ID = "id" + MAP_STUDENTS.size();
    	}
        
        //Create Student
        Student s = new Student(ID, name, firstName, degree);
        
        //Save to database		Credit: https://www.tutorialspoint.com/jdbc/jdbc-insert-records.htm
        try {
    		String sql = "INSERT INTO Students "
    				+ "VALUES ('" + ID + "','" + name + "','" + firstName + "','" + degree.getId() + "')";
    		stmt.executeUpdate(sql);
    		System.out.println("Student creation successful");
    		
    	} catch(SQLException e) {
    		System.out.println("Failed to create student");
    		e.printStackTrace();
    		return null;
    	}
        
        return s;
    }

    /**
     * Get all student ids currently being used in the database.
     * @return
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_getAllStudentIds (followed by optional numbers if multiple tests are used)
     */
    public static Collection<String> getAllStudentIds() {
    	retrieveData();
    	Collection<String> returnThis = new ArrayList<String>();
    	
    	//credit: https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap
    	for (String key : MAP_STUDENTS.keySet()) {
    	    returnThis.add(key);
    	}
    	
        return returnThis;
    }

    /**
     * Get all degree ids currently being used in the database.
     * @return
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_getAllDegreeIds (followed by optional numbers if multiple tests are used)
     */
    public static Iterable<String> getAllDegreeIds() {
    	retrieveData();
    	Collection<String> returnThis = new ArrayList<String>();
    	
    	//credit: https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap
    	for (String key : MAP_DEGREES.keySet()) {
    	    returnThis.add(key);
    	}
    	
        return returnThis;
    }
    
    /*===============[HELPER METHODS]===============*/
    
    /**
     * Open database
     */
    public static void openConnection() {
    	try {
    		//Connecting
    		System.out.println("Connecting to database...");
    		conn = DriverManager.getConnection(JDBC_URL);
    		
    		//Prep statement
    		Statement stmt = conn.createStatement();
    		
    	} catch(SQLException se) {
    		System.out.println("SQLException: StudentManager > openConnection()");
    		se.printStackTrace();
    	} catch(Exception e) {
    		System.out.println("Exception: StudentManager > openConnection()");
    		e.printStackTrace();
    	}
    }
    
    /**
     * Prep database this class
     */
    public static void retrieveData() {
    	MAP_DEGREES.clear();
    	MAP_STUDENTS.clear();
    	
    	//FIRST: Degrees
    	try {
    		stmt = conn.createStatement();
    		String sql = "SELECT id, name FROM Degrees";
    		ResultSet rs = stmt.executeQuery(sql);
    		
    		while(rs.next()) {
    			String id = rs.getString("id");
    			String name = rs.getString("name");
    			
    			Degree d = new Degree(id, name);
    			MAP_DEGREES.put(id, d);
    		}
    	} catch (SQLException e) {
    		System.out.println("Failure to prep MAP_DEGREES");
    		e.printStackTrace();
    	}
    	
    	//SECOND: Students
    	try {
    		stmt = conn.createStatement();
	    	String sql = "SELECT id, first_name, name, degree FROM Students";
	    	ResultSet rs = stmt.executeQuery(sql);
	    	
	    	while(rs.next()) {
	    		//Getting data
	    		String id = rs.getString("id");
	    		String firstName = rs.getString("first_name");
	    		String name = rs.getString("name");
	    		
	    		String degreeID = rs.getString("degree");
	    		Degree degree = MAP_DEGREES.get(degreeID);
	    		
	    		//Constructing instance of student
	    		Student s = new Student(id, firstName, name, degree);
	    		
	    		MAP_STUDENTS.put(id, s);
	    	}
		} catch (SQLException e) {
    		System.out.println("Failure to prep MAP_STUDENTS");
			e.printStackTrace();
		}
    }
    
    /**
     * Close database
     */
    public static void closeConnection() {
    	try {
    		System.out.println("Disconnecting from database...");
            if(conn!=null) {
                conn.close();
        		System.out.println("Connection successfully closed!");
            } else {
            	System.out.println("No established connection");
            }
         } catch(SQLException se) {
        	 System.out.println("SQLException: StudentManager > closeConnection()");
            se.printStackTrace();
         }
    }


}
