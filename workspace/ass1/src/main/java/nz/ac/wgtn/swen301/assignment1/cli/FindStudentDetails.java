package main.java.nz.ac.wgtn.swen301.assignment1.cli;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import main.java.nz.ac.wgtn.swen301.assignment1.StudentManager;
import nz.ac.wgtn.swen301.studentdb.Degree;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import nz.ac.wgtn.swen301.studentdb.StudentDB;

public class FindStudentDetails {

    @Before
    public void init() {
        StudentDB.init();
    }

    // Open and Close connections.		credit: glimpse of the code from a buddy of mine, it sounded like a cool idea.
    @BeforeClass
    public static void openConnection() { StudentManager.openConnection(); }
    
    @AfterClass
    public static void closeConnection() { StudentManager.closeConnection(); }
    
    // THE FOLLOWING METHOD MUST IMPLEMENTED
    /**
     * Executable: the user will provide a student id as single argument, and if the details are found,
     * the respective details are printed to the console.
     * E.g. a user could invoke this by running "java -cp <someclasspath> nz.ac.wgtn.swen301.assignment1.cli.FindStudentDetails id42"
     * @param arg
     */
    public static void main (String[] arg) {
    	if(arg.length == 0) {
    		System.out.println("ERROR: Too few arguements!");
    		System.out.println("Please parse a student id as an argument (EG: \"id42\")");
    	} else if (arg.length > 1) {
    		System.out.println("ERROR: Too many arguements!");
    		System.out.println("Please parse a SINGULAR student id as an argument (EG: \"id42\")");
    	} else {
			try {
	    		//Declare details
	        	String id = arg[0];
	        	String name;
	        	String firstName;
	        	Degree degree;
	        	
	        	Student student;
	        	
	        	//Finding details
				student = StudentManager.readStudent(id);
	        	name = student.getName();
	        	firstName = student.getFirstName();
	        	degree = student.getDegree();
	        	
	        	//Now print!
	        	System.out.println("ID:         " + id);
	        	System.out.println("First Name: " + id);
	        	System.out.println("Last Name:  " + id);
	        	System.out.println("Degree:     " + degree.getName());
	        	
			} catch (NoSuchRecordException e) {
				e.printStackTrace();
			}
        	
    	}
    }
}
