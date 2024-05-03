package warningfix.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;

import warningfix.treemap.JTreeMapWarningsFIX;

public class TABs_GUI extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2360681668180402923L;
	private static String programname;
	public static JTabbedPane tabbedPane;

	public TABs_GUI(String programa_name) throws PropertyVetoException,
			NumberFormatException, ClassNotFoundException, SQLException {
		super(new GridLayout(1, 1));
		programname = programa_name;
		tabbedPane = new JTabbedPane();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		tabbedPane.setPreferredSize(screenSize);
		final JTreeMapWarningsFIX first_tab = new JTreeMapWarningsFIX(
				programname, "", "");
		first_tab.StrategiesQuantitiesofWarningsforProgram();
		tabbedPane.addTab("Home", null, first_tab, "Home");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		// criar aba de geração de relatório
		String sql1 = "SELECT A.nameclass, A.beginline, B.nome as tool_name, A.description, A.priority FROM warning A INNER JOIN ferramenta B ON A.tool=B.id AND A.nameprogram='"
				+ programa_name
				+ "' AND (A.nameclass NOT LIKE '%.class' AND A.nameclass NOT LIKE '%jlint%' AND A.nameclass NOT LIKE '%.zip') order by A.nameclass, A.beginline";
		JTable warnings = new JTable(JTreeMapWarningsFIX.Search(sql1));
		// pega os nomes das colunas
		JTableHeader header = warnings.getTableHeader();
		warnings.getColumn("nameclass").setMinWidth(
				JTreeMapWarningsFIX.maior.get(1));
		warnings.getColumn("tool_name").setMinWidth(
				JTreeMapWarningsFIX.maior.get(2));
		warnings.getColumn("description").setMinWidth(
				JTreeMapWarningsFIX.maior.get(3));
		warnings.getColumn("priority").setMinWidth(
				JTreeMapWarningsFIX.maior.get(4));
		warnings.revalidate();
		JPanel panelTABLE = new JPanel();
		panelTABLE.setLayout(new BorderLayout());
		panelTABLE.add(header, BorderLayout.NORTH);
		panelTABLE.add(warnings, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane(panelTABLE);
		scrollPane.setVisible(true);
		tabbedPane.addTab("Report", null, scrollPane, "Report");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_2);

		add(tabbedPane);
		// The following line enables to use scrolling tabs.
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

	}

	protected JComponent makeTextPanel(String text) {
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel(text);
		filler.setHorizontalAlignment(SwingConstants.CENTER);
		panel.setLayout(new GridLayout(1, 1));
		panel.add(filler);
		return panel;
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = TABs_GUI.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws NumberFormatException
	 */

	void createAndShowGUI() throws NumberFormatException,
			ClassNotFoundException, SQLException {
		// Create and set up the window.
		JFrame frame = new JFrame(
				"WARNINGFIX, Recommendation System for Priorization of Warnings in Diferents Program´s Level");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		try {
			frame.add(new TABs_GUI(programname), BorderLayout.CENTER);
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

}
