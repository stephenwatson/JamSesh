import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;


public class ApplettMain extends JApplet{

	 public void init() {
	        //Execute a job on the event-dispatching thread; creating this applet's GUI.
	        try {
	            SwingUtilities.invokeAndWait(new Runnable() {
	                public void run() {
	                   
	                }
	            });
	        } catch (Exception e) {
	            System.err.println("createGUI didn't complete successfully");
	        }
	    }
	
}
