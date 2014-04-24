package validation;

import javax.swing.JFrame;

import org.junit.Test;

import validation.view.ModelValidationView;

public class ModelValidationViewTest {
    
    @Test
    public void todo() throws Exception {
        
    }
    
    public static void main(String args[]) {
        new ModelValidationViewTest().run();
    }

    private void run() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                
                ModelValidationView validationView = new ModelValidationView();
                frame.add(validationView);
                frame.setSize(500, 300);
                frame.setVisible(true);
            }
        });
    }

}
