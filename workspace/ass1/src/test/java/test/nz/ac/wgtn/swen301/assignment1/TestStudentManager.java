package test.java.test.nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.studentdb.Degree;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import nz.ac.wgtn.swen301.studentdb.StudentDB;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import main.java.nz.ac.wgtn.swen301.assignment1.StudentManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.*;



public class TestStudentManager {
    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND IN ITS INITIAL STATE BEFORE EACH TEST RUNS
    @Before
    public void init() {
        StudentDB.init();
    }
    // DO NOT REMOVE BLOCK ENDS HERE
    
    // Open and Close connections.		credit: glimpse of the code from a buddy of mine, it sounded like a cool idea.
    @BeforeClass
    public static void openConnection() { StudentManager.openConnection(); }
    
    @AfterClass
    public static void closeConnection() { StudentManager.closeConnection(); }
    
    // TESTS
    @Test
    public void test_readStudent() throws Exception {
    	String id = "id" + ((int) ((Math.random() * (9999 - 0)) + 0));
    	
    	//Test Successful case
        Student student = StudentManager.readStudent(id);
        assertNotNull(student);
        assertTrue(student.getId().equals(id));
        
        //Test Unsuccessful case
        boolean errorThrown = false;
        
        try { StudentManager.readStudent("coughing sounds"); } 
        catch (Exception e) { if(e.getClass().equals(NoSuchRecordException.class)) errorThrown = true; }
        
        assertTrue(errorThrown);
    }
    
    @Test
    public void test_readDegree() throws Exception {
    	String id = "deg" + ((int) ((Math.random() * (9 - 0)) + 0));
    	
    	//Test Successful case
        Degree degree = StudentManager.readDegree(id);
        assertNotNull(degree);
        assertTrue(degree.getId().equals(id));
        
        //Test Unsuccessful case
        boolean errorThrown = false;
        
        try { StudentManager.readDegree("coughing sounds"); } 
        catch (Exception e) { if(e.getClass().equals(NoSuchRecordException.class)) errorThrown = true; }
        
        assertTrue(errorThrown);
    }
    
    @Test
    public void test_delete() throws Exception {
    	String id = "id" + ((int) ((Math.random() * (9999 - 0)) + 0));
    	
    	//Test Successful case
        Student student = StudentManager.readStudent(id);
        StudentManager.delete(student);

        boolean errorThrown = false;
        try { StudentManager.readStudent(id).getName(); } 
        catch (Exception e) { 
        	if(e.getClass().equals(NoSuchRecordException.class)) errorThrown = true; 
        }
        assertTrue(errorThrown);
        
        
        //Test Unsuccessful case
        errorThrown = false;
        try { StudentManager.delete(student); }
        catch (Exception e) { if(e.getClass().equals(NoSuchRecordException.class)) errorThrown = true; }
        assertTrue(errorThrown);
        
        //Re-add student
        StudentManager.createStudent(student.getName(), student.getFirstName(), student.getDegree());
    }
    
    @Test
    public void test_update() throws Exception {
    	String id = "id" + ((int) ((Math.random() * (9999 - 0)) + 0));
    	
    	//Test Successful case
    	Student student = StudentManager.readStudent(id);
    	String oldName = student.getName();
        student.setName("Captain b");
        StudentManager.update(student);
    	
        String newName = StudentManager.readStudent(id).getName();
    	assertTrue(!newName.equals(oldName));
        
        //Test Unsuccessful case
    	Student imposterStudent = new Student("12", "J.Seinfeld", "Jerry", new Degree());
    	
        boolean errorThrown = false;
        try { StudentManager.update(imposterStudent); }
        catch (Exception e) { if(e.getClass().equals(NoSuchRecordException.class)) errorThrown = true; }
        assertTrue(errorThrown);
    }
    
    @Test
    public void test_createStudent() throws Exception {
    	String id = "id" + ((int) ((Math.random() * (9999 - 0)) + 0));
    	
    	//Test Successful case
        Student student = StudentManager.readStudent(id);
        StudentManager.delete(student);
        
        student = StudentManager.createStudent(student.getName(), student.getFirstName(), student.getDegree());
        id = student.getId();
        
        assertNotNull(StudentManager.readStudent(id));
        assertEquals(StudentManager.readStudent(id).getName(), student.getName());
    }
    
    @Test
    public void test_getAllStudentIds() {
    	Collection<String> listOfIds = StudentManager.getAllStudentIds();
    	
    	assertNotNull(listOfIds);
    }
    
    @Test
    public void test_getAllDegreeIds() {
    	List<String> expected = new ArrayList<String>();
    	for(int i = 0; i < 9; i++) {
    		expected.add("deg" + i);
    	}
    	
    	List<String> results = (List<String>) StudentManager.getAllDegreeIds();
    	
    	assertNotNull(results);
    	assertTrue(results.containsAll(expected));
    }
    
    @Test
    public void testPerformance() throws Exception {
    	boolean failure = false;

        long time0=System.currentTimeMillis();
    	for(int i = 0; i < 1000; i++) {
        	String id = "id" + ((int) ((Math.random() * (9999 - 0)) + 0));
    		StudentManager.readStudent(id);
    	}
        long time1=System.currentTimeMillis();
        
        float result = time1-time0;
        
        System.out.println("Time: " + result + "ms");
    	assertTrue(result < 1000);
    }
}
