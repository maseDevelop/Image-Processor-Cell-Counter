import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class CellCounterGUI
{
    BufferedImage image = null;
    BufferedImage image1 = null;


  public CellCounterGUI(ArrayList<BufferedImage> imageList, ArrayList<String> stringList, int cellCount) throws Exception
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        JFrame frame = new JFrame("Cell Counter");
        frame.setSize(650,600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        //Image
        ImageIcon imageIcon = new ImageIcon(imageList.get(imageList.size()-1));
        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);
        frame.add(jLabel, BorderLayout.CENTER);

        //Coutner label
        JLabel counterLabel = new JLabel("Cell Count: " + cellCount);
        counterLabel.setFont(new Font("", Font.PLAIN, 20));
        frame.add(counterLabel, BorderLayout.PAGE_END);


        DefaultListModel list1 = new DefaultListModel<String>();
        
        for (String string : stringList) {
          list1.addElement(string);
        }

        JList list = new JList<>(list1);

        frame.add(list, BorderLayout.LINE_START);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        //Setting up event listener
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