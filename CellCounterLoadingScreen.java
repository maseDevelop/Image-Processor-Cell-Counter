//Name: Connor Jones, Mason Elliott
//ID: 1351782, 1347257

import java.awt.*;
import javax.swing.*;


public class CellCounterLoadingScreen
{

    private JFrame frame = null;
    
    public CellCounterLoadingScreen() throws Exception
    {
        SwingUtilities.invokeLater(new Runnable()
        {
        public void run()
        {
            frame = new JFrame("Cell Counter");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            //Loading in Gif 
            JLabel loadingLabel = new JLabel(new ImageIcon("LoadingGif1.gif"));
            //Placing component on Frame
            frame.add(loadingLabel, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
        });
    }

    //Closes the Loading Screen
    public void closeLoading() {
        frame.dispose();
    }

}


