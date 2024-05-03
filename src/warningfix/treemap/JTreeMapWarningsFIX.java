/*
 * ObjectLab, http://www.objectlab.co.uk/open is supporting JTreeMap.
 * 
 * Based in London, we are world leaders in the design and development 
 * of bespoke applications for the securities financing markets.
 * 
 * <a href="http://www.objectlab.co.uk/open">Click here to learn more</a>
 *           ___  _     _           _   _          _
 *          / _ \| |__ (_) ___  ___| |_| |    __ _| |__
 *         | | | | '_ \| |/ _ \/ __| __| |   / _` | '_ \
 *         | |_| | |_) | |  __/ (__| |_| |__| (_| | |_) |
 *          \___/|_.__// |\___|\___|\__|_____\__,_|_.__/
 *                   |__/
 *
 *                     www.ObjectLab.co.uk
 *
 * $Id: JTreeMapExample.java 145 2011-09-30 09:25:10Z jense128 $
 * 
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package warningfix.treemap;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultTreeModel;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

import net.sf.jtreemap.swing.JTreeMap;
import net.sf.jtreemap.swing.TreeMapNode;
import net.sf.jtreemap.swing.example.BuilderXML;
import net.sf.jtreemap.swing.example.DemoUtil;
import warningfix.Connection.Conexao;
import warningfix.GUI.TABs_GUI;

public class JTreeMapWarningsFIX extends JInternalFrame implements
		PropertyChangeListener {

	Statement stmt0;
	int buffer = 0;
	static Vector<Object> columnNames;
	public static Vector<Integer> maior;
	private static final int DEFAULT_FONT_SIZE = 16;
	private static final long serialVersionUID = 2813934810390001709L;
	private static final String EXIT = "Exit";
	private static String nodeselected = "";
	private final JTreeMap jTreeMap;
	private final Vector<String> typeProviders = new Vector<String>();
	private final Vector<String> meansProviders = new Vector<String>();
	private JTree treeView = new JTree();
	public TreeMapNode root;
	private DefaultTreeModel treeModel;
	private String program_analyzed = "";
	private String package_analyzed = "";
	private JTextField view_name = new JTextField();
	private String class_analyzed = "";
	private JPanel PaneCenter = new JPanel(new BorderLayout());
	private JButton earlier_view = new JButton();
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private JPanel panel_two_buttons = new JPanel();
	public String atual_view = "PROGRAM";
	public String type_view = "WARNINGS";
	private JPanel Panel_Program = new JPanel();
	private JTreeMapWarningsFIX all_packages_xml;
	private JTreeMapWarningsFIX all_class_xml;
	private TreeMapNode package_treemap_root;
	private TreeMapNode program_treemap_root;
	private TreeMapNode package_means_treemap_root;
	public TreeMapNode program_means_treemap_root;
	private MouseListener MouseListener, MouseListener2;
	protected TreeMapNode class_treemap_root;
	protected JScrollPane table;
	protected boolean mean_total_program;
	protected boolean mean_total_package;
	private JComboBox cmbTypeProvider;
	private JComboBox cmbMeansProvider;
	private String warnings_view = "TOTAL";

	/**
	 * Constructor
	 */
	// Construtor do nível de pacote
	// Construtor Nível de Programa
	public JTreeMapWarningsFIX(String name_program, String name_package,
			String name_class) {
		root = DemoUtil.buildDemoRoot();
		// pegar o tamanho da tela
		program_analyzed = name_program;
		package_analyzed = name_package;
		class_analyzed = name_class;
		jTreeMap = new JTreeMap(this.root, treeView);
		jTreeMap.setFont(new Font(null, Font.BOLD, DEFAULT_FONT_SIZE));
		jTreeMap.setBorder(BorderFactory
			.createEtchedBorder(EtchedBorder.LOWERED));
		
				// esconder a barra superior do JInternalFrame
		setRootPaneCheckingEnabled(false);
		jTreeMap.setPreferredSize(new Dimension(screenSize.width - 85,
				screenSize.height - 110));
		
				javax.swing.plaf.InternalFrameUI ifu = this.getUI();
		((javax.swing.plaf.basic.BasicInternalFrameUI) ifu).setNorthPane(null);
		TreeMapNode.setBorder(5);
		// init GUI
		try {
			initGUI();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	// Construtor Nível de Classe
	/**
	 * main
	 * 
	 * @param args
	 *            command line
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(final ActionEvent e) {
		// Action performed for the File Menu @see addMenu()

		final String command = e.getActionCommand();
		if (EXIT.equals(command)) {
			windowClosingEvent(null);
		}
	}

	public void setNewTreeMap(TreeMapNode TMW) {
		root = TMW;
		jTreeMap.setRoot(root);
		treeModel.setRoot(root);
		jTreeMap.revalidate();
		jTreeMap.repaint();
		jTreeMap.setColorProvider(new RedProvider(jTreeMap));
	}

	/**
	 * Set the xml file corresponding to the TreeMap.dtd
	 * 
	 * @param xmlFileName
	 *            xml file name
	 */
	public void setXmlFile(final String xmlFileName) {
		try {
			final BuilderXML bXml = new BuilderXML(xmlFileName);
			root = bXml.getRoot();
			jTreeMap.setRoot(this.root);
			treeModel.setRoot(this.root);
		} catch (final ParseException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(), "File error",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * Code to execute before closing the window
	 * 
	 * @param e
	 *            WindowEvent
	 */
	protected void windowClosingEvent(final WindowEvent e) {
		System.exit(0);
	}

	/**
	 * Add a splitPane with a treeview on the left and the JTreeMap on the right
	 */
	public void StrategiesQuantitiesofWarningsforClass() {
		File f = new File("programs_analyzeds/" + program_analyzed
				+ "/treemap/warnings/" + class_analyzed + "_warnings.xml");

		if (f.exists()) {
			setXmlFile(f.getAbsolutePath());
		} else {

			Document doc = new Document();
			Element root = new Element("root");
			Element label = new Element("label");
			label.addContent("" + class_analyzed);
			root.addContent(label);

			try {
				Conexao.open();
				String sql1 = "select beginline, quantidade_warning from quantity_tool_and_warnings_for_line A inner join arquivo B on (A.nameprogram=B.nomeprograma AND A.nameprogram='"
						+ program_analyzed
						+ "') AND (A.nameclass='"
						+ class_analyzed
						+ "' AND A.nameclass=B.nomearquivo) AND nomepacote='"
						+ package_analyzed + "'";
				Statement stmt7;
				stmt7 = Conexao.cn.createStatement();
				ResultSet rs7 = stmt7.executeQuery(sql1);
				while (rs7.next()) {
					Element branch = new Element("branch");
					Element leaf = new Element("leaf");
					Element label3 = null, weight = null, value = null;

					label3 = new Element("label");
					label3.addContent("" + rs7.getString(1));
					leaf.addContent(label3);
					weight = new Element("weight");
					weight.addContent("" + rs7.getString(2));

					value = new Element("value");
					value.addContent("" + rs7.getString(2));
					leaf.addContent(weight);
					leaf.addContent(value);

					branch.addContent(leaf);

					root.addContent(branch);

				}
				rs7.close();
				stmt7.close();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			doc.setRootElement(root);
			XMLOutputter xout = new XMLOutputter();
			OutputStream out;

			try {
				out = new FileOutputStream(f);
				xout.output(doc, out);
				out.close();
				Conexao.cn.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			setXmlFile(f.getAbsolutePath());
		}
	}

	public void StrategiesQuantitiesofSuspectionRateforPackage()
			throws NumberFormatException, SQLException, ClassNotFoundException {

		File f = new File("programs_analyzeds/" + program_analyzed
				+ "/treemap/suspection_rate/" + package_analyzed
				+ "_suspection.xml");
		if (f.exists()) {
			setXmlFile(f.getAbsolutePath());
		} else {
			Conexao.open();
			Statement sentenca = Conexao.cn.createStatement();
			String tamanho_matriz = "";

			ResultSet rs3 = sentenca
					.executeQuery(" SELECT count(distinct A.nameclass) FROM quantity_warnings_for_tool_for_line_eliminar_linhas_repetidas A inner join arquivo B on A.nameprogram=B.nomeprograma AND B.nomepacote='"
							+ package_analyzed
							+ "'  AND (A.nameclass=B.nomearquivo AND A.nameclass NOT LIKE '%.class' AND A.nameclass NOT LIKE '%jlint%' AND A.nameclass NOT LIKE '%.zip')");

			while (rs3.next()) {
				for (int i = 1; i <= 1; i++) {
					tamanho_matriz += rs3.getObject(i).toString();
					System.out.println("Tamanho da Matriz" + tamanho_matriz);

				}
			}
			rs3.close();

			double[] mean = new double[5];

			ResultSet rs4 = sentenca
					.executeQuery(" SELECT A.nameclass, sum(qnt_nv1), sum(qnt_nv2), sum(qnt_nv3), sum(qnt_nv4), sum(qnt_nv5) FROM quantity_warnings_for_tool_for_line_eliminar_linhas_repetidas A inner join arquivo B on A.nameprogram=B.nomeprograma AND B.nomepacote='"
							+ package_analyzed
							+ "' AND (A.nameclass=B.nomearquivo AND A.nameclass NOT LIKE '%.class' AND A.nameclass NOT LIKE '%jlint%' AND A.nameclass NOT LIKE '%.zip') group by A.nameclass");

			double[][] matriz_ = new double[Integer.parseInt(tamanho_matriz)][5];

			int j = 0;
			int soma_coluna_1 = 1;
			int soma_coluna_2 = 1;
			int soma_coluna_3 = 1;
			int soma_coluna_4 = 1;
			int soma_coluna_5 = 1;

			while (rs4.next()) {
				String part1 = "", part11[] = null;

				for (int i = 1; i <= 6; i++) {
					part1 += rs4.getObject(i) + "/";

					if (i == 6) {

						if (matriz_[j][0] != 0) {
							soma_coluna_1 += 1;

						}

						if (matriz_[j][1] != 0) {
							soma_coluna_2 += 1;

						}

						if (matriz_[j][2] != 0) {
							soma_coluna_3 += 1;

						}

						if (matriz_[j][3] != 0) {
							soma_coluna_4 += 1;

						}

						if (matriz_[j][4] != 0) {
							soma_coluna_5 += 1;

						}

						j++;
					}

				}
			}

			rs4.close();
			Document doc = new Document();
			Element root = new Element("root");
			Element label = new Element("label");
			label.addContent("" + package_analyzed);
			root.addContent(label);
			String sql1 = "select A.nameclass, sum(qnt_nv1), sum(qnt_nv2), sum(qnt_nv3), sum(qnt_nv4), sum(qnt_nv5)  from quantity_warnings_for_tool_for_line_eliminar_linhas_repetidas A inner join arquivo B on A.nameprogram=B.nomeprograma AND B.nomepacote='"
					+ package_analyzed
					+ "' AND (A.nameclass=B.nomearquivo AND A.nameclass NOT LIKE '%.class' AND A.nameclass NOT LIKE '%jlint%' AND A.nameclass NOT LIKE '%.zip') group by A.nameclass";
			Statement stmt2;
			stmt2 = Conexao.cn.createStatement();
			ResultSet rs = stmt2.executeQuery(sql1);
			double[] matriz_coeficientes = new double[Integer
					.parseInt(tamanho_matriz)];
			int i = 0;
			while (rs.next()) {
				double coeficiente = Integer.parseInt(rs.getString(2))
						/ soma_coluna_5 * 5 + Integer.parseInt(rs.getString(3))
						/ soma_coluna_4 * 4 + Integer.parseInt(rs.getString(4))
						/ soma_coluna_3 * 3 + Integer.parseInt(rs.getString(5))
						/ soma_coluna_2 * 2 + Integer.parseInt(rs.getString(6))
						/ soma_coluna_1 * 1;
				matriz_coeficientes[i] = coeficiente;
				System.out.println(matriz_coeficientes[i]);
				i++;
			}
			rs.close();
			stmt2.close();

			// System.out.println(selecionar_maior_coeficiente(matriz_coeficientes));

			int x = 0;
			Statement stmt;
			stmt = Conexao.cn.createStatement();
			ResultSet rs2 = stmt.executeQuery(sql1);
			double maior = selecionar_maior_coeficiente(matriz_coeficientes);
			System.out.println(maior);
			double valor;
			while (rs2.next()) {
				Element branch = new Element("branch");
				Element leaf = new Element("leaf");
				Element label3 = null, weight = null, value = null;
				label3 = new Element("label");

				label3.addContent("" + rs2.getString(1));
				leaf.addContent(label3);
				valor = matriz_coeficientes[x] / maior;
				weight = new Element("weight");
				weight.addContent("" + valor);
				value = new Element("value");
				value.addContent("" + valor);
				leaf.addContent(weight);
				leaf.addContent(value);
				branch.addContent(leaf);
				root.addContent(branch);

				x++;
			}
			rs2.close();
			stmt.close();
			doc.setRootElement(root);
			XMLOutputter xout = new XMLOutputter();
			OutputStream out;

			try {
				out = new FileOutputStream(f);
				xout.output(doc, out);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				Conexao.cn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			setXmlFile(f.getAbsolutePath());
		}
	}

	public void StrategiesQuantitiesofSuspectionRateforProgram()
			throws NumberFormatException, SQLException, ClassNotFoundException {

		File f = new File("programs_analyzeds/" + program_analyzed
				+ "/treemap/suspection_rate/" + program_analyzed
				+ "_suspection.xml");
		if (f.exists()) {
			setXmlFile(f.getAbsolutePath());
		} else {
			Conexao.open();
			Statement sentenca = Conexao.cn.createStatement();

			String tamanho_matriz = "";

			ResultSet rs3 = sentenca
					.executeQuery("select count(distinct nomepacote)from arquivo where nomeprograma='"
							+ program_analyzed
							+ "' AND nomepacote IS NOT NULL group by nomepacote");

			while (rs3.next()) {
				for (int i = 1; i <= 1; i++) {
					tamanho_matriz += rs3.getObject(i);
				}
			}
			rs3.close();

			ResultSet rs4 = sentenca
					.executeQuery(" SELECT B.nomepacote, sum(qnt_nv1), sum(qnt_nv2), sum(qnt_nv3), sum(qnt_nv4), sum(qnt_nv5) FROM quantity_warnings_for_tool_for_line_eliminar_linhas_repetidas A inner join arquivo B on (A.nameprogram='"
							+ program_analyzed
							+ "' AND A.nameprogram=B.nomeprograma) AND (nameclass NOT LIKE '%.class' AND nameclass NOT LIKE '%jlint%' AND nameclass NOT LIKE '%.zip') AND B.nomepacote IS NOT NULL group by B.nomepacote");

			double[][] matriz_ = new double[Integer.parseInt(tamanho_matriz)][5];

			int j = 0;
			int soma_coluna_1 = 1;
			int soma_coluna_2 = 1;
			int soma_coluna_3 = 1;
			int soma_coluna_4 = 1;
			int soma_coluna_5 = 1;

			while (rs4.next()) {
				String part1 = "", part11[] = null;

				for (int i = 1; i <= 6; i++) {
					part1 += rs4.getObject(i) + "/";

					if (i == 6) {

						// System.out.println(j);

						if (matriz_[j][0] != 0) {
							soma_coluna_1 += 1;

						}

						if (matriz_[j][1] != 0) {
							soma_coluna_2 += 1;

						}

						if (matriz_[j][2] != 0) {
							soma_coluna_3 += 1;

						}

						if (matriz_[j][3] != 0) {
							soma_coluna_4 += 1;

						}

						if (matriz_[j][4] != 0) {
							soma_coluna_5 += 1;

						}

						j++;
					}

				}

			}
			rs4.close();

			Document doc = new Document();
			Element root = new Element("root");
			Element label = new Element("label");
			label.addContent("" + program_analyzed);
			root.addContent(label);
			String sql1 = "select B.nomepacote, sum(qnt_nv1), sum(qnt_nv2), sum(qnt_nv3), sum(qnt_nv4), sum(qnt_nv5)  from quantity_warnings_for_tool_for_line_eliminar_linhas_repetidas A inner join arquivo B on (A.nameprogram='"
					+ program_analyzed
					+ "' AND A.nameprogram=B.nomeprograma)  AND   (nameclass NOT LIKE '%.class' AND nameclass NOT LIKE '%jlint%' AND nameclass NOT LIKE '%.zip') group by B.nomepacote";
			Statement stmt2;
			stmt2 = Conexao.cn.createStatement();
			ResultSet rs = stmt2.executeQuery(sql1);
			double[] matriz_coeficientes = new double[Integer
					.parseInt(tamanho_matriz)];
			int i = 0;
			while (rs.next()) {
				double coeficiente = Integer.parseInt(rs.getString(2))
						/ soma_coluna_5 * 5 + Integer.parseInt(rs.getString(3))
						/ soma_coluna_4 * 4 + Integer.parseInt(rs.getString(4))
						/ soma_coluna_3 * 3 + Integer.parseInt(rs.getString(5))
						/ soma_coluna_2 * 2 + Integer.parseInt(rs.getString(6))
						/ soma_coluna_1 * 1;
				matriz_coeficientes[i] = coeficiente;
				i++;
			}
			rs.close();
			stmt2.close();
			int x = 0;
			Statement stmt;
			stmt = Conexao.cn.createStatement();
			ResultSet rs2 = stmt.executeQuery(sql1);
			Double maior = selecionar_maior_coeficiente(matriz_coeficientes);
			Double valor;
			while (rs2.next()) {
				Element branch = new Element("branch");
				Element leaf = new Element("leaf");
				Element label3 = null, weight = null, value = null;
				label3 = new Element("label");

				label3.addContent("" + rs2.getString(1));
				leaf.addContent(label3);
				weight = new Element("weight");
				valor = matriz_coeficientes[x] / maior;
				weight.addContent("" + valor);
				value = new Element("value");
				value.addContent("" + valor);
				leaf.addContent(weight);
				leaf.addContent(value);
				branch.addContent(leaf);
				root.addContent(branch);

				x++;
			}
			rs2.close();
			stmt.close();
			doc.setRootElement(root);
			XMLOutputter xout = new XMLOutputter();
			OutputStream out;

			try {
				out = new FileOutputStream(f);
				xout.output(doc, out);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				Conexao.cn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			setXmlFile(f.getAbsolutePath());
		}
	}

	public void StrategiesQuantitiesofSuspectionRateforClass()
			throws NumberFormatException, SQLException, ClassNotFoundException {

		File f = new File("programs_analyzeds/" + program_analyzed
				+ "/treemap/suspection_rate/" + class_analyzed
				+ "_suspection.xml");

		if (f.exists()) {
			setXmlFile(f.getAbsolutePath());
		} else {

			Conexao.open();
			Statement sentenca = Conexao.cn.createStatement();

			String tamanho_matriz = "";

			ResultSet rs0 = sentenca
					.executeQuery(" SELECT count(*) FROM quantity_warnings_for_tool_for_line_eliminar_linhas_repetidas where nameprogram='"
							+ program_analyzed
							+ "'  AND ( nameclass='"
							+ class_analyzed
							+ "' AND nameclass NOT LIKE '%.class' AND nameclass NOT LIKE '%jlint%' AND nameclass NOT LIKE '%.zip')");

			while (rs0.next()) {
				for (int i = 1; i <= 1; i++) {
					tamanho_matriz += rs0.getObject(i);

				}
			}
			rs0.close();

			ResultSet rs1 = sentenca
					.executeQuery(" SELECT beginline, qnt_nv1, qnt_nv2, qnt_nv3, qnt_nv4, qnt_nv5 FROM quantity_warnings_for_tool_for_line_eliminar_linhas_repetidas where nameprogram='"
							+ program_analyzed
							+ "' AND (nameclass='"
							+ class_analyzed
							+ "' AND nameclass NOT LIKE '%.class' AND nameclass NOT LIKE '%jlint%' AND nameclass NOT LIKE '%.zip')");

			double[][] matriz_ = new double[Integer.parseInt(tamanho_matriz)][5];

			int j = 0;
			int soma_coluna_1 = 1;
			int soma_coluna_2 = 1;
			int soma_coluna_3 = 1;
			int soma_coluna_4 = 1;
			int soma_coluna_5 = 1;

			while (rs1.next()) {
				String part1 = "", part11[] = null;

				for (int i = 1; i <= 6; i++) {
					part1 += rs1.getObject(i) + "/";

					if (i == 6) {

						if (matriz_[j][0] != 0) {
							soma_coluna_1 += 1;

						}

						if (matriz_[j][1] != 0) {
							soma_coluna_2 += 1;

						}

						if (matriz_[j][2] != 0) {
							soma_coluna_3 += 1;

						}

						if (matriz_[j][3] != 0) {
							soma_coluna_4 += 1;

						}

						if (matriz_[j][4] != 0) {
							soma_coluna_5 += 1;

						}

						j++;
					}
				}
			}
			rs1.close();
			Document doc = new Document();
			Element root = new Element("root");
			Element label = new Element("label");
			label.addContent("" + class_analyzed);
			root.addContent(label);
			String sql1 = " SELECT beginline, qnt_nv1, qnt_nv2, qnt_nv3, qnt_nv4, qnt_nv5 FROM quantity_warnings_for_tool_for_line_eliminar_linhas_repetidas where nameprogram='"
					+ program_analyzed
					+ "' AND (nameclass='"
					+ class_analyzed
					+ "' AND nameclass NOT LIKE '%.class' AND nameclass NOT LIKE '%jlint%' AND nameclass NOT LIKE '%.zip')";
			Statement stmt2;
			stmt2 = Conexao.cn.createStatement();
			ResultSet rs3 = stmt2.executeQuery(sql1);
			double[] matriz_coeficientes = new double[Integer
					.parseInt(tamanho_matriz)];
			int i = 0;
			while (rs3.next()) {
				double coeficiente = Integer.parseInt(rs3.getString(2))
						/ soma_coluna_5 * 5
						+ Integer.parseInt(rs3.getString(3)) / soma_coluna_4
						* 4 + +Integer.parseInt(rs3.getString(4))
						/ soma_coluna_3 * 3
						+ Integer.parseInt(rs3.getString(5)) / soma_coluna_2
						* 2 + Integer.parseInt(rs3.getString(6))
						/ soma_coluna_1 * 1;
				matriz_coeficientes[i] = coeficiente;
				i++;
			}
			rs3.close();
			stmt2.close();
			// System.out.println("");
			int x = 0;
			Statement stmt;
			stmt = Conexao.cn.createStatement();
			ResultSet rs4 = stmt.executeQuery(sql1);

			double maior = selecionar_maior_coeficiente(matriz_coeficientes);
			double valor;
			while (rs4.next()) {
				Element branch = new Element("branch");
				Element leaf = new Element("leaf");
				Element label3 = null, weight = null, value = null;
				label3 = new Element("label");
				label3.addContent("" + rs4.getString(1));
				leaf.addContent(label3);
				weight = new Element("weight");
				valor = matriz_coeficientes[x] / maior;
				weight.addContent("" + valor);

				// System.out.println(selecionar_maior_coeficiente(matriz_coeficientes));
				value = new Element("value");
				value.addContent("" + valor);
				leaf.addContent(weight);
				leaf.addContent(value);
				branch.addContent(leaf);
				root.addContent(branch);
				x++;
			}
			rs4.close();
			stmt.close();

			doc.setRootElement(root);
			XMLOutputter xout = new XMLOutputter();
			OutputStream out;

			try {
				out = new FileOutputStream(f);
				xout.output(doc, out);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				Conexao.cn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			setXmlFile(f.getAbsolutePath());
		}
	}

	private static double selecionar_maior_coeficiente(
			double[] matriz_coeficientes) {
		double maior = 0;
		for (int i = 0; i < matriz_coeficientes.length; i++) {
			if (Math.abs(matriz_coeficientes[i]) > maior) {
				maior = matriz_coeficientes[i];
				System.out.println(maior);
			}

		}
		return maior;
	}

	public void StrategiesQuantitiesofToolsforClass() {
		File f = new File("programs_analyzeds/" + program_analyzed
				+ "/treemap/tools/" + class_analyzed + "_tools.xml");

		if (f.exists()) {
			setXmlFile(f.getAbsolutePath());
		} else {
			Document doc = new Document();
			// DocType dt= new DocType("root SYSTEM \"TreeMap.dtd\"");
			// doc.setDocType(dt);
			Element root = new Element("root");
			Element label = new Element("label");
			label.addContent("" + class_analyzed);
			root.addContent(label);

			try {
				Conexao.open();
				String sql1 = "select nameclass, nameprogram, beginline, quantidade_ferramentas from quantity_tool_and_warnings_for_line where nameprogram='"
						+ program_analyzed
						+ "' AND nameclass='"
						+ class_analyzed + "'";
				Statement stmt2;
				stmt2 = Conexao.cn.createStatement();
				ResultSet rs4 = stmt2.executeQuery(sql1);
				while (rs4.next()) {
					Element branch = new Element("branch");
					Element leaf = new Element("leaf");
					Element label3 = null, weight = null, value = null;

					label3 = new Element("label");
					label3.addContent("" + rs4.getString(3));
					leaf.addContent(label3);

					weight = new Element("weight");
					weight.addContent("" + rs4.getString(4));

					value = new Element("value");
					value.addContent("" + rs4.getString(4));
					leaf.addContent(weight);
					leaf.addContent(value);

					branch.addContent(leaf);

					root.addContent(branch);

				}
				rs4.close();
				stmt2.close();

			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			doc.setRootElement(root);
			XMLOutputter xout = new XMLOutputter();
			OutputStream out;

			try {
				out = new FileOutputStream(f);
				xout.output(doc, out);
				Conexao.cn.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			setXmlFile(f.getAbsolutePath());
		}

	}

	public String getATUALVIEW() {
		return atual_view;
	}

	public void setATUALVIEW(String new_atual_view) {
		atual_view = new_atual_view;
	}

	public String getWARNINGSVIEW() {
		return warnings_view;
	}

	public void seWARNINGSVIEW(String new_warnings_view) {
		warnings_view = new_warnings_view;
	}

	public String getTYPEVIEW() {
		return type_view;
	}

	public void setTYPEVIEW(String new_type_view) {
		type_view = new_type_view;
	}

	private void addPanelCenter(final Container parent) {

		if (PaneCenter.getComponentListeners() != null) {
			PaneCenter.setBorder(BorderFactory.createEmptyBorder());
			PaneCenter.validate();
			parent.validate();
			Panel_Program.add(jTreeMap);
			Panel_Program.setVisible(true);
			PaneCenter.add(Panel_Program, BorderLayout.CENTER);
			PaneCenter.revalidate();
			parent.add(PaneCenter, BorderLayout.CENTER);
			earlier_view.setEnabled(false);
		}

		treeModel = new DefaultTreeModel(root);
		treeView = new JTree(treeModel);
		jTreeMap.setTreeView(treeView);
		
		
		
		MouseListener = new MouseListener() {

			public void mouseClicked(MouseEvent e) {

				TreeMapNode dest = (TreeMapNode) JTreeMapWarningsFIX.this.treeView
						.getLastSelectedPathComponent();

				if (TABs_GUI.tabbedPane.getSelectedIndex() == 0) {

					if (getATUALVIEW().equals("PROGRAM")) {
						parent.validate();
						PaneCenter.validate();
						String name_package = dest.getLabel();
						package_analyzed = name_package;
						final JTreeMapWarningsFIX class_view_warnings = new JTreeMapWarningsFIX(
								program_analyzed, name_package, "");

						try {
							// pegar o nivel atual para recupera-lo
							// posteriormente
							class_view_warnings
									.StrategiesQuantitiesofWarningsforProgram();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						program_treemap_root = class_view_warnings.root;
						class_view_warnings
								.StrategiesQuantitiesofWarningsforPackage();
						setNewTreeMap(class_view_warnings.root);
						view_name.setText(dest.getLabel().toString()
								.toUpperCase());
						setNodeSelected(dest.getLabel().toString()
								.toUpperCase());
						PaneCenter.repaint();
						PaneCenter.revalidate();
						setATUALVIEW("PACKAGE");
						// habilitar o botão de subir nível
						earlier_view.setEnabled(true);
						// A quantidade total de avisos é o padrão
						JTreeMapWarningsFIX.this.repaint();
						setTYPEVIEW("WARNINGS");
						cmbTypeProvider.setSelectedIndex(0);
						seWARNINGSVIEW("TOTAL");
						cmbMeansProvider.setEnabled(true);
						cmbMeansProvider.setSelectedIndex(0);
						buffer = 1;
					} else {
						if (getATUALVIEW().equals("PACKAGE")) {
							String nameclass = dest.getLabel();
							package_treemap_root = JTreeMapWarningsFIX.this.root;
							final JTreeMapWarningsFIX class_view_warnings = new JTreeMapWarningsFIX(
									program_analyzed, package_analyzed,
									nameclass);
							class_view_warnings
									.StrategiesQuantitiesofWarningsforClass();
							view_name.setText(dest.getLabel().toString()
									.toUpperCase());
							setNodeSelected(dest.getLabel().toString()
									.toUpperCase());
							setNewTreeMap(class_view_warnings.root);
							class_analyzed = nameclass;
							jTreeMap.validate();
							jTreeMap.revalidate();
							PaneCenter.repaint();
							PaneCenter.revalidate();
							JTreeMapWarningsFIX.this.setATUALVIEW("CLASS");
							setTYPEVIEW("WARNINGS");
							cmbTypeProvider.setSelectedIndex(0);
							seWARNINGSVIEW("TOTAL");
							cmbMeansProvider.setEnabled(false);
							cmbMeansProvider.setSelectedIndex(0);
						} else {
							if (getATUALVIEW().equals("CLASS")) {
								final JTreeMapWarningsFIX class_view_warnings = new JTreeMapWarningsFIX(
										program_analyzed, package_analyzed,
										class_analyzed);

								// pegar o nivel atual para recupera-lo
								// posteriormente
								class_view_warnings
										.StrategiesQuantitiesofWarningsforClass();
								class_treemap_root = class_view_warnings.root;

								TreeMapNode dest2 = (TreeMapNode) JTreeMapWarningsFIX.this.treeView
										.getLastSelectedPathComponent();
								String linha = dest2.getLabel();

								try {
									setATUALVIEW("TABLE");
									cmbMeansProvider.setEnabled(false);
									cmbTypeProvider.setEnabled(false);
									jTreeMap.setVisible(false);
									table = createTABLE(class_analyzed, linha);
									PaneCenter.add(table,
											BorderLayout.PAGE_START);
								} catch (ClassNotFoundException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (SQLException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

							}

						}

					}

				}

			}

			public void mouseEntered(MouseEvent e) {			
				
			}

			public void mouseExited(MouseEvent e) {
				view_name.setText(getNodeSelected());	
			}

			
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		};

		jTreeMap.addMouseListener(MouseListener);

	}

	private JScrollPane createTABLE(String nameclass, String line)
			throws ClassNotFoundException, SQLException {
		String sql1 = "SELECT A.beginline, B.nome as tool_name, A.description, A.priority FROM warning A INNER JOIN ferramenta B ON A.tool=B.id AND A.nameprogram='"
				+ program_analyzed
				+ "' AND A.nameclass='"
				+ nameclass
				+ "' AND A.beginline='" + line + "' order by A.priority";

		// cria uma tabela dos avisos da linha clicada....
		JTable warnings = new JTable(Search(sql1));
		// pega os nomes das colunas
		JTableHeader header = warnings.getTableHeader();
		warnings.getColumn("description").setPreferredWidth(1010);
		warnings.revalidate();
		JPanel panelTABLE = new JPanel();
		panelTABLE.setLayout(new BorderLayout());
		panelTABLE.add(header, BorderLayout.NORTH);
		panelTABLE.add(warnings, BorderLayout.CENTER);
		warnings.getColumn("beginline").setPreferredWidth(maior.get(1));
		warnings.getColumn("tool_name").setPreferredWidth(maior.get(2));
		warnings.getColumn("description").setPreferredWidth(maior.get(3));
		System.out.println(maior.get(3));
		warnings.getColumn("priority").setPreferredWidth(maior.get(4));
		System.out.println(maior.get(4));
		JScrollPane scrollPane = new JScrollPane(panelTABLE);
		scrollPane.setVisible(true);
		return scrollPane;
	}

	public static DefaultTableModel Search(String sql1) throws SQLException,
			ClassNotFoundException {
		Statement stmt2;
		Conexao.open();
		stmt2 = Conexao.cn.createStatement();
		ResultSet rs4 = stmt2.executeQuery(sql1);
		ResultSetMetaData rsmd = rs4.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();

		// Pegar os nomes das colunas
		columnNames = new Vector<Object>();
		for (int column = 1; column <= numberOfColumns; column++) {
			columnNames.add(rsmd.getColumnName(column));

		}

		// preencher um vetor para pegar maiores valores de cada coluna
		maior = new Vector<Integer>();
		for (int x = 0; x < numberOfColumns; x++) {
			maior.add(x, 0);
		}

		// Pegar os valores
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		while (rs4.next()) {
			Vector<Object> vector = new Vector<Object>();
			for (int j = 1; j <= numberOfColumns; j++) {
				vector.add(rs4.getString(j));
				if (rs4.getString(j).length() > maior.get(j)) {
					maior.add(j, rs4.getString(j).length());
				}
			}
			data.add(vector);
		}

		return new DefaultTableModel(data, columnNames);
	}

	public void StrategiesQuantitiesofWarningsforPackage() {

		File f = new File("programs_analyzeds/" + program_analyzed
				+ "/treemap/warnings/" + package_analyzed + "_warnings.xml");

		if (f.exists()) {
			setXmlFile(f.getAbsolutePath());
		} else {
			Document doc = new Document();
			Element root = new Element("root");
			Element label = new Element("label");
			label.addContent("" + package_analyzed);
			root.addContent(label);
			try {
				Conexao.open();
				String sql1 = "select nameclass, count(*) from warning A inner join arquivo B on A.nameprogram=B.nomeprograma AND nomepacote='"
						+ package_analyzed
						+ "' AND (A.nameclass=B.nomearquivo AND nameclass NOT LIKE '%.class' AND nameclass NOT LIKE '%jlint%' AND nameclass NOT LIKE '%.zip') group by A.nameclass";

				Statement stmt3;
				stmt3 = Conexao.cn.createStatement();
				ResultSet rs3 = stmt3.executeQuery(sql1);

				while (rs3.next()) {
					Element branch = new Element("branch");
					Element leaf = new Element("leaf");
					Element label3 = null, weight = null, value = null;
					label3 = new Element("label");

					// cria todos xmls das classes
					if (buffer == 1) {
						all_class_xml = new JTreeMapWarningsFIX(
								program_analyzed, package_analyzed,
								rs3.getString(1));
						all_class_xml.StrategiesQuantitiesofWarningsforClass();
						all_class_xml
								.StrategiesQuantitiesofSuspectionRateforClass();
						all_class_xml.StrategiesQuantitiesofToolsforClass();
					}

					label3.addContent("" + rs3.getString(1));
					System.out.println(rs3.getString(1) + "\n");
					leaf.addContent(label3);
					weight = new Element("weight");
					weight.addContent("" + rs3.getString(2));
					value = new Element("value");
					value.addContent("" + rs3.getString(2));
					leaf.addContent(weight);
					leaf.addContent(value);
					branch.addContent(leaf);
					root.addContent(branch);
				}
				rs3.close();
				stmt3.close();

			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			doc.setRootElement(root);
			XMLOutputter xout = new XMLOutputter();
			OutputStream out;

			try {
				out = new FileOutputStream(f);
				xout.output(doc, out);
				out.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				Conexao.cn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			setXmlFile(f.getAbsolutePath());
		}
	}

	public void StrategiesQuantitiesofWarningsforProgram() throws SQLException,
			ClassNotFoundException {
		File f = new File("programs_analyzeds/" + program_analyzed
				+ "/treemap/warnings/" + program_analyzed + "_warnings.xml");

		if (f.exists()) {
			setXmlFile(f.getAbsolutePath());
		} else {
			Conexao.open();
			Document doc = new Document();
			Element root = new Element("root");
			Element label = new Element("label");
			label.addContent("" + program_analyzed);
			root.addContent(label);

			String sql0 = "select distinct nomepacote from arquivo where nomeprograma='"
					+ program_analyzed
					+ "' AND nomepacote IS NOT NULL group by nomepacote";
			Statement stmt0 = Conexao.cn.createStatement();
			ResultSet rs0 = stmt0.executeQuery(sql0);
			// armazenar nome dos pacotes

			while (rs0.next()) {

				String namepacote = rs0.getString(1);
				String sql1 = "select sum(quant_warnings) from quantity_warnings_for_package where nomepacote='"
						+ namepacote + "'";
				Statement stmt2;
				stmt2 = Conexao.cn.createStatement();
				ResultSet rs4 = stmt2.executeQuery(sql1);

				while (rs4.next()) {
					Element branch = new Element("branch");
					Element leaf = new Element("leaf");
					Element label3 = null, weight = null, value = null;
					label3 = new Element("label");
					label3.addContent("" + namepacote);
					leaf.addContent(label3);
					weight = new Element("weight");
					weight.addContent("" + rs4.getString(1));
					value = new Element("value");
					value.addContent("" + rs4.getString(1));
					leaf.addContent(weight);
					leaf.addContent(value);
					branch.addContent(leaf);
					root.addContent(branch);
				}

			}

			doc.setRootElement(root);
			XMLOutputter xout = new XMLOutputter();
			OutputStream out;

			try {
				out = new FileOutputStream(f);
				xout.output(doc, out);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				Conexao.cn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			setXmlFile(f.getAbsolutePath());
		}

	}

	public void StrategiesQuantitiesofWarningsforProgram_Means()
			throws SQLException, ClassNotFoundException {

		File f = new File("programs_analyzeds/" + program_analyzed
				+ "/treemap/warnings/" + program_analyzed
				+ "_means_warnings.xml");

		Conexao.open();
		if (f.exists()) {
			setXmlFile(f.getAbsolutePath());
		} else {
			Document doc = new Document();
			Element root = new Element("root");
			Element label = new Element("label");
			label.addContent("" + program_analyzed);
			root.addContent(label);
			String sql0 = "select distinct nomepacote from arquivo where nomeprograma='"
					+ program_analyzed
					+ "' AND nomepacote IS NOT NULL group by nomepacote";
			Statement stmt4;
			stmt4 = Conexao.cn.createStatement();
			ResultSet rs4 = stmt4.executeQuery(sql0);
			Statement stmt5;
			stmt5 = Conexao.cn.createStatement();
			// armazenar nome dos pacotes

			while (rs4.next()) {

				String namepacote = rs4.getString(1);
				String sql1 = "select nomepacote, sum(quant_warnings)/count(*) from quantity_warnings_for_package where nomepacote='"
						+ namepacote + "' group by nomepacote";

				ResultSet rs5 = stmt5.executeQuery(sql1);

				// all_packages_xml = new JTreeMapWarningsFIX(program_analyzed,
				// namepacote, "");
				// all_packages_xml
				// .StrategiesQuantitiesofWarningsforPackage_Means();

				while (rs5.next()) {
					Element branch = new Element("branch");
					Element leaf = new Element("leaf");
					Element label3 = null, weight = null, value = null;
					label3 = new Element("label");
					label3.addContent("" + namepacote);
					leaf.addContent(label3);
					weight = new Element("weight");
					weight.addContent("" + rs5.getString(2));
					value = new Element("value");
					value.addContent("" + rs5.getString(2));
					leaf.addContent(weight);
					leaf.addContent(value);
					branch.addContent(leaf);
					root.addContent(branch);
				}
				rs5.close();
			}
			rs4.close();
			stmt4.close();
			stmt5.close();

			doc.setRootElement(root);
			XMLOutputter xout = new XMLOutputter();
			OutputStream out;

			try {
				out = new FileOutputStream(f);
				xout.output(doc, out);
				out.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				Conexao.cn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			setXmlFile(f.getAbsolutePath());
		}

	}

	public void StrategiesQuantitiesofWarningsforPackage_Means() {

		File f = new File("programs_analyzeds/" + program_analyzed
				+ "/treemap/warnings/" + package_analyzed
				+ "_means_warnings.xml");

		if (f.exists()) {
			setXmlFile(f.getAbsolutePath());
		} else {
			Document doc = new Document();
			Element root = new Element("root");
			Element label = new Element("label");
			label.addContent("" + package_analyzed);
			root.addContent(label);
			try {
				Conexao.open();
				String sql1 = "select nameclass, sum(quantidade_warning)/count(*) from quantity_tool_and_warnings_for_line A inner join arquivo B on A.nameprogram=B.nomeprograma AND nomepacote='"
						+ package_analyzed
						+ "' AND (A.nameclass=B.nomearquivo AND nameclass NOT LIKE '%.class' AND nameclass NOT LIKE '%jlint%' AND nameclass NOT LIKE '%.zip') group by A.nameclass";
				Statement stmt6;
				stmt6 = Conexao.cn.createStatement();
				ResultSet rs6 = stmt6.executeQuery(sql1);

				while (rs6.next()) {
					Element branch = new Element("branch");
					Element leaf = new Element("leaf");
					Element label3 = null, weight = null, value = null;
					label3 = new Element("label");
					label3.addContent("" + rs6.getString(1));
					leaf.addContent(label3);
					weight = new Element("weight");
					weight.addContent("" + rs6.getString(2));
					value = new Element("value");
					value.addContent("" + rs6.getString(2));
					leaf.addContent(weight);
					leaf.addContent(value);
					branch.addContent(leaf);
					root.addContent(branch);
				}
				rs6.close();
				stmt6.close();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			doc.setRootElement(root);
			XMLOutputter xout = new XMLOutputter();
			OutputStream out;

			try {
				out = new FileOutputStream(f);
				xout.output(doc, out);
				out.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				Conexao.cn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			setXmlFile(f.getAbsolutePath());
		}
	}

	public void StrategiesQuantitiesofToolsforPackage() {

		File f = new File("programs_analyzeds/" + program_analyzed
				+ "/treemap/tools/" + package_analyzed + "_tools.xml");

		if (f.exists()) {
			setXmlFile(f.getAbsolutePath());
		} else {
			Document doc = new Document();
			Element root = new Element("root");
			Element label = new Element("label");
			label.addContent("" + package_analyzed);
			root.addContent(label);
			try {
				Conexao.open();
				String sql1 = "select nameclass, count(distinct tool) from warning A inner join arquivo B on A.nameprogram=B.nomeprograma AND nomepacote='"
						+ package_analyzed
						+ "' AND (A.nameclass=B.nomearquivo AND nameclass NOT LIKE '%.class' AND nameclass NOT LIKE '%jlint%' AND nameclass NOT LIKE '%.zip') group by A.nameclass";

				Statement stmt10;
				stmt10 = Conexao.cn.createStatement();
				ResultSet rs3 = stmt10.executeQuery(sql1);

				while (rs3.next()) {
					Element branch = new Element("branch");
					Element leaf = new Element("leaf");
					Element label3 = null, weight = null, value = null;
					label3 = new Element("label");

					all_class_xml = new JTreeMapWarningsFIX(program_analyzed,
							package_analyzed, rs3.getString(1));
					all_class_xml.StrategiesQuantitiesofToolsforClass();
					all_class_xml
							.StrategiesQuantitiesofSuspectionRateforClass();

					label3.addContent("" + rs3.getString(1));
					System.out.println(rs3.getString(1) + "\n");
					leaf.addContent(label3);
					weight = new Element("weight");
					weight.addContent("" + rs3.getString(2));
					value = new Element("value");
					value.addContent("" + rs3.getString(2));
					leaf.addContent(weight);
					leaf.addContent(value);
					branch.addContent(leaf);
					root.addContent(branch);
				}
				rs3.close();
				stmt10.close();

			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			doc.setRootElement(root);
			XMLOutputter xout = new XMLOutputter();
			OutputStream out;

			try {
				out = new FileOutputStream(f);
				xout.output(doc, out);
				out.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				Conexao.cn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			setXmlFile(f.getAbsolutePath());
		}

	}

	public void StrategiesQuantitiesofToolsforProgram() throws SQLException,
			ClassNotFoundException {

		File f = new File("programs_analyzeds/" + program_analyzed
				+ "/treemap/tools/" + program_analyzed + "_tools.xml");

		if (f.exists()) {
			setXmlFile(f.getAbsolutePath());
		} else {

			Document doc = new Document();
			Element root = new Element("root");
			Element label = new Element("label");
			label.addContent("" + program_analyzed);
			root.addContent(label);
			Conexao.open();
			String sql0 = "select distinct nomepacote from arquivo where nomeprograma='"
					+ program_analyzed
					+ "' AND nomepacote is NOT NULL group by nomepacote";
			Statement stmt9;
			stmt9 = Conexao.cn.createStatement();
			ResultSet rs1 = stmt9.executeQuery(sql0);
			Statement stmt8;
			stmt8 = Conexao.cn.createStatement();
			// armazenar nome dos pacotes

			while (rs1.next()) {
				String namepacote = rs1.getString(1);
				String sql1 = "select MAX(quant_tools) from quantity_tools_for_package where nomepacote='"
						+ namepacote + "' group by nomepacote";

				ResultSet rs2 = stmt8.executeQuery(sql1);

				// all_packages_xml = new JTreeMapWarningsFIX(program_analyzed,
				// namepacote, "");
				// all_packages_xml.StrategiesQuantitiesofToolsforPackage();
				// all_packages_xml
				// .StrategiesQuantitiesofSuspectionRateforPackage();

				while (rs2.next()) {
					Element branch = new Element("branch");
					Element leaf = new Element("leaf");
					Element label3 = null, weight = null, value = null;
					label3 = new Element("label");
					label3.addContent("" + namepacote);
					leaf.addContent(label3);
					weight = new Element("weight");
					System.out.println(rs2.getString(1) + "\n");
					weight.addContent("" + rs2.getString(1));
					value = new Element("value");
					value.addContent("" + rs2.getString(1));
					leaf.addContent(weight);
					leaf.addContent(value);
					branch.addContent(leaf);
					root.addContent(branch);
				}

				rs2.close();
			}

			doc.setRootElement(root);
			XMLOutputter xout = new XMLOutputter();
			OutputStream out;

			rs1.close();
			stmt9.close();
			stmt8.close();
			try {
				out = new FileOutputStream(f);
				xout.output(doc, out);
				out.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				Conexao.cn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			setXmlFile(f.getAbsolutePath());
		}
	}

	/**
	 * init the window
	 * 
	 * @throws Exception
	 */
	private void initGUI() throws Exception {
		addContainerListener(new java.awt.event.ContainerAdapter() {

			@SuppressWarnings("unused")
			public void containerClosing(final ContainerEvent e) {
				containerClosing(e);
			}
		});
		// Panel dos botões
		addPaneNorth(getContentPane());
		// Panel do JTreeMap
		addPanelCenter(getContentPane());
		jTreeMap.setColorProvider(new RedProvider(jTreeMap));
	}

	protected void createTypeProviders() {
		typeProviders.add("Quantities of Warnings");
		typeProviders.add("Quantities of Tools");
		typeProviders.add("Suspection Rate");
		for (int i = 0; i < typeProviders.size(); i++) {
			cmbTypeProvider.addItem(typeProviders.get(i));
		}
	}

	protected void createMeansProviders() {
		meansProviders.add("Total Warnings");
		meansProviders.add("Means Warnings");
		for (int i = 0; i < meansProviders.size(); i++) {
			cmbMeansProvider.addItem(meansProviders.get(i));
		}
	}

	private void addPaneNorth(final Container parent) {
		final JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		JPanel name_view = new JPanel();

		// nome do programa ou classe
		view_name.setVisible(true);
		view_name.setOpaque(false);
		view_name.setText(program_analyzed.toUpperCase());
		setNodeSelected(program_analyzed.toUpperCase());
		view_name.setEditable(false);
		Font newTextFieldFont = new Font(view_name.getFont().getName(),
				Font.BOLD, view_name.getFont().getSize() + 5);
		view_name.setFont(newTextFieldFont);
		name_view.add(view_name);
		northPanel.add(name_view, BorderLayout.WEST);

		cmbTypeProvider = new JComboBox();
		createTypeProviders();

		cmbTypeProvider.addActionListener(new ActionListener() {
		
			public void actionPerformed(final ActionEvent e) {
				if (JTreeMapWarningsFIX.this.cmbTypeProvider.getSelectedIndex() > -1) {
					try {
						updateTreeMap();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		cmbTypeProvider.setSelectedIndex(0);

		cmbMeansProvider = new JComboBox();
		createMeansProviders();

		cmbMeansProvider.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent e) {
				if (JTreeMapWarningsFIX.this.cmbMeansProvider
						.getSelectedIndex() > -1) {

					try {
						updateMeans();
					} catch (SQLException e1) { // TODO Auto-generated
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) { // TODO Auto-generated
						e1.printStackTrace();
					}

				}
			}
		});

		cmbMeansProvider.setSelectedIndex(0);

		earlier_view.setText("Earlier View");
		earlier_view.setEnabled(false);
		earlier_view.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				System.out.println(getATUALVIEW());

				if ((getATUALVIEW().equals("CLASS"))) {
					setNewTreeMap(package_treemap_root);
					view_name.setText(package_analyzed.toUpperCase());
					setNodeSelected(package_analyzed.toUpperCase());
					setATUALVIEW("PACKAGE");
					setTYPEVIEW("WARNINGS");
					seWARNINGSVIEW("TOTAL");
					cmbMeansProvider.setSelectedIndex(0);
					cmbTypeProvider.setSelectedIndex(0);
					cmbMeansProvider.setEnabled(true);
				} else {
					if ((getATUALVIEW().equals("PACKAGE"))) {
						setNewTreeMap(program_treemap_root);
						view_name.setText(program_analyzed.toUpperCase());
						setNodeSelected(program_analyzed.toUpperCase());
						setATUALVIEW("PROGRAM");
						earlier_view.setEnabled(false);
						setTYPEVIEW("WARNINGS");
						seWARNINGSVIEW("TOTAL");
						cmbMeansProvider.setSelectedIndex(0);
						cmbTypeProvider.setSelectedIndex(0);
						cmbMeansProvider.setEnabled(true);
					} else {
						// retorna a classe anterior
						setNewTreeMap(class_treemap_root);
						// seta visualização atual
						setATUALVIEW("CLASS");
						cmbTypeProvider.setEnabled(true);
						// torna jTreeMap visível
						jTreeMap.setVisible(true);
						// remove a tabela atual
						PaneCenter.remove(table);
						setTYPEVIEW("WARNINGS");
						seWARNINGSVIEW("TOTAL");
						cmbMeansProvider.setSelectedIndex(0);
						cmbTypeProvider.setEnabled(true);
						cmbTypeProvider.setSelectedIndex(0);
					}
				}
				PaneCenter.revalidate();
				PaneCenter.repaint();
				jTreeMap.revalidate();
				jTreeMap.repaint();

			}

		});

		JToolBar barra = new JToolBar();
		barra.add(cmbTypeProvider);
		barra.add(cmbMeansProvider);
		barra.add(earlier_view);
		barra.addSeparator();
		panel_two_buttons.add(barra);
		northPanel.add(panel_two_buttons, BorderLayout.EAST);
		parent.add(northPanel, BorderLayout.NORTH);

	}

	protected void updateTreeMap() throws SQLException, ClassNotFoundException {
		final String type = (String) cmbTypeProvider.getSelectedItem();
		System.out.println(type);
		// alterar em nível de programa
		if (getATUALVIEW().equals("PROGRAM")) {
			if (type.equals("Quantities of Tools")) {
				if (getTYPEVIEW().equals("TOOLS")) {
					// FAZ NADA
				} else {
					final JTreeMapWarningsFIX view_warnings = new JTreeMapWarningsFIX(
							program_analyzed, "", "");
					// gerando o treemap de quantidades de ferramentas nível de
					// programa
					view_warnings.StrategiesQuantitiesofToolsforProgram();
					// alterando o treeMap
					setNewTreeMap(view_warnings.root);
					// alterando a visualização atual
					cmbMeansProvider.setEnabled(false);
					setTYPEVIEW("TOOLS");
				}

			} else {
				if (type.equals("Quantities of Warnings")) {
					if (getTYPEVIEW().equals("WARNINGS")) {
						// FAZ NADA
					} else {
						final JTreeMapWarningsFIX view_warnings = new JTreeMapWarningsFIX(
								program_analyzed, "", "");
						view_warnings
								.StrategiesQuantitiesofWarningsforProgram();
						// recuperando o nó da árvore
						setNewTreeMap(view_warnings.root);
						cmbMeansProvider.setEnabled(true);
						cmbMeansProvider.setSelectedIndex(0);
						setTYPEVIEW("WARNINGS");
					}

				} else {
					if (type.equals("Suspection Rate")) {
						if (getTYPEVIEW().equals("SUSPECTION")) {
							// FAZ NADA
						} else {
							final JTreeMapWarningsFIX view_warnings = new JTreeMapWarningsFIX(
									program_analyzed, "", "");
							view_warnings
									.StrategiesQuantitiesofSuspectionRateforProgram();
							// recuperando o nó da árvore
							setNewTreeMap(view_warnings.root);
							cmbMeansProvider.setEnabled(false);
							setTYPEVIEW("SUSPECTION");
						}

					}
				}
			}

		} else {
			// alterar treemap em nível de pacote
			if (getATUALVIEW().equals("PACKAGE")) {

				if (type.equals("Quantities of Tools")) {
					if (getTYPEVIEW().equals("TOOLS")) {
						// FAZ NADA
					} else {
						final JTreeMapWarningsFIX view_warnings = new JTreeMapWarningsFIX(
								program_analyzed, package_analyzed, "");
						// gerando o treemap de quantidades de ferramentas nível
						// de programa
						view_warnings.StrategiesQuantitiesofToolsforPackage();
						// alterando o treeMap
						setNewTreeMap(view_warnings.root);
						// alterando a visualização atual
						cmbMeansProvider.setEnabled(false);
						setTYPEVIEW("TOOLS");
					}

				} else {
					if (type.equals("Quantities of Warnings")) {
						if (getTYPEVIEW().equals("WARNINGS")) {
							// FAZ NADA
						} else {
							final JTreeMapWarningsFIX view_warnings = new JTreeMapWarningsFIX(
									program_analyzed, package_analyzed, "");
							view_warnings
									.StrategiesQuantitiesofWarningsforPackage();
							// recuperando o nó da árvore
							setNewTreeMap(view_warnings.root);
							cmbMeansProvider.setEnabled(true);
							cmbMeansProvider.setSelectedIndex(0);
							setTYPEVIEW("WARNINGS");
						}

					} else {
						if (type.equals("Suspection Rate")) {

							if (getTYPEVIEW().equals("SUSPECTION")) {
								// FAZ NADA
							} else {
								final JTreeMapWarningsFIX view_warnings = new JTreeMapWarningsFIX(
										program_analyzed, package_analyzed,
										class_analyzed);
								view_warnings
										.StrategiesQuantitiesofSuspectionRateforPackage();
								// recuperando o nó da árvore
								setNewTreeMap(view_warnings.root);
								setTYPEVIEW("SUSPECTION");
								cmbMeansProvider.setEnabled(false);

							}

						}
					}
				}

			} else {

				if (getATUALVIEW().equals("CLASS")) {
					if (type.equals("Quantities of Tools")) {
						if (getTYPEVIEW().equals("TOOLS")) {
							// FAZ NADA
						} else {
							final JTreeMapWarningsFIX view_warnings = new JTreeMapWarningsFIX(
									program_analyzed, package_analyzed,
									class_analyzed);
							// gerando o treemap de quantidades de ferramentas
							// nível
							// de programa
							view_warnings.StrategiesQuantitiesofToolsforClass();
							// alterando o treeMap
							setNewTreeMap(view_warnings.root);
							// alterando a visualização atual
							setTYPEVIEW("TOOLS");
							cmbMeansProvider.setEnabled(false);
							// desabilitando

						}

					} else {
						if (type.equals("Quantities of Warnings")) {
							if (getTYPEVIEW().equals("WARNINGS")) {
								// FAZ NADA
							} else {
								final JTreeMapWarningsFIX view_warnings = new JTreeMapWarningsFIX(
										program_analyzed, package_analyzed,
										class_analyzed);
								view_warnings
										.StrategiesQuantitiesofWarningsforClass();
								// recuperando o nó da árvore
								setNewTreeMap(view_warnings.root);
								setTYPEVIEW("WARNINGS");
								cmbMeansProvider.setEnabled(true);
								cmbMeansProvider.setSelectedIndex(0);
								// habilitando botões de média e total de
								// warnings

							}

						} else {
							if (type.equals("Suspection Rate")) {
								if (getTYPEVIEW().equals("SUSPECTION")) {
									// FAZ NADA
								} else {
									final JTreeMapWarningsFIX view_warnings = new JTreeMapWarningsFIX(
											program_analyzed, package_analyzed,
											class_analyzed);
									view_warnings
											.StrategiesQuantitiesofSuspectionRateforClass();
									// recuperando o nó da árvore
									setNewTreeMap(view_warnings.root);
									setTYPEVIEW("SUSPECTION");
									cmbMeansProvider.setEnabled(false);

								}

							}
						}
					}

				}
			}
		}

		JTreeMapWarningsFIX.this.repaint();

	}

	protected void updateMeans() throws SQLException, ClassNotFoundException {
		final String type = (String) cmbMeansProvider.getSelectedItem();
		System.out.println(type);
		// alterar em nível de programa
		if (getATUALVIEW().equals("PROGRAM")) {

			if (type.equals("Means Warnings")) {
				if (getWARNINGSVIEW().equals("MEANS")) {
					// NÃO FAZ NADA
				} else {
					StrategiesQuantitiesofWarningsforProgram_Means();
					jTreeMap.setColorProvider(new RedProvider(jTreeMap));
					seWARNINGSVIEW("MEANS");
				}

			} else {

				if (type.equals("Total Warnings")) {
					if (getWARNINGSVIEW().equals("TOTAL")) {
						// NÃO FAZ NADA
					} else {
						StrategiesQuantitiesofWarningsforProgram();
						jTreeMap.setColorProvider(new RedProvider(jTreeMap));
						seWARNINGSVIEW("TOTAL");
					}
				}

			}

		} else {
			// alterar treemap em nível de pacote
			if (getATUALVIEW().equals("PACKAGE")) {

				if (type.equals("Means Warnings")) {
					if (getWARNINGSVIEW().equals("MEANS")) {
						// NÃO FAZ NADA
					} else {
						StrategiesQuantitiesofWarningsforPackage_Means();
						jTreeMap.setColorProvider(new RedProvider(jTreeMap));
						seWARNINGSVIEW("MEANS");
					}

				} else {

					if (type.equals("Total Warnings")) {
						if (getWARNINGSVIEW().equals("TOTAL")) {
							// NÃO FAZ NADA
						} else {
							StrategiesQuantitiesofWarningsforPackage();
							jTreeMap.setColorProvider(new RedProvider(jTreeMap));
							seWARNINGSVIEW("TOTAL");
						}
					}
				}
			}
		}

		JTreeMapWarningsFIX.this.repaint();

	}
	
	public String getNodeSelected(){
		return nodeselected;
	}
	
	public void setNodeSelected(String new_node){
		nodeselected=new_node;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		revalidate();
		repaint();
	}
}
