//Name: Connor Jones, Mason Elliott
//ID: 1351782, 1347257

import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class CellCounterGUI
{

  public CellCounterGUI(ArrayList<BufferedImage> imageList, ArrayList<String> stringList, int cellCount) throws Exception
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        JFrame frame = new JFrame("Cell Counter");
        frame.setSize(660,610);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Initial image being display is the final image
        ImageIcon imageIcon = new ImageIcon(imageList.get(imageList.size()-1));
        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);
        

        //Coutner label
        JLabel counterLabel = new JLabel("Cell Count: " + cellCount);
        counterLabel.setFont(new Font("", Font.PLAIN, 20));
        

        //Creating a list element to store Pipeline functions in 
        DefaultListModel list1 = new DefaultListModel<String>();
        for (String string : stringList) {
          list1.addElement(string);
        }

         //Turning List to a Component that can be displayed on screen
        JList list = new JList<>(list1);
       
        //Putting components on the frame
        frame.add(jLabel, BorderLayout.CENTER);
        frame.getContentPane().add(counterLabel, BorderLayout.PAGE_END);
        frame.getContentPane().add(list, BorderLayout.LINE_START);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //Setting up event listener (When element in list is select changes the image displayed)
        list.addListSelectionListener(new ListSelectionListener(){
        
            @Override
            public void valueChanged(ListSelectionEvent e) {
                imageIcon.setImage(imageList.get(list.getSelectedIndex()));
                jLabel.setIcon(imageIcon);
                jLabel.repaint();

            }
        });
      }
    });
    
  }

}


