package warningfix.GUI;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Random;

import javax.swing.*;

@SuppressWarnings("unused")
public class FirstWindow extends JPanel implements ActionListener,
		PropertyChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7805199293184605717L;
	private JProgressBar progressBar;
	private JTextArea taskOutput;
	private JScrollPane scrolltaskoutput;
	private static String program_name_zip;
	private Task task;
	private JPanel panel_first_window_top;
	private JButton analyze;
	private JButton run;
	private JPanel panel_first_window_bottom;
	private JFrame frame;
	private JTextField choose;
	private String name_tool_running;
	Process p1, p2, p3, p4, p5, p6, p7;
	private int quant_tools;
	private int progress = 0;
	private String program_name;

	class Task extends SwingWorker<Void, Void> {
		private Random random = new Random();

		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			int progress = 0;
			setProgress(0);
			program_name = program_name_zip.replace(".zip", "");
			quant_tools = 1;

			String stdOut = "", s, s2;
			try {
				Thread.sleep(random.nextInt(1000));
			} catch (InterruptedException ignore) {
			}
			while (quant_tools <= 7) {
				switch (quant_tools) {					
				
				case 1:
					try {
						p1 = Runtime.getRuntime().exec(
								"bash scripts/Run_Hammurapi.sh " + program_name
										+ " " + choose.getText());
						p1.waitFor();
						setNameToolRunning("Hammurapi");
						System.out.println(name_tool_running);
						setProgressGlobal(15);
						String stdIn = "";
						BufferedReader stdInput = new BufferedReader(
								new InputStreamReader(p1.getErrorStream()));
						while ((s = stdInput.readLine()) != null) {
							stdIn += s + "\n";
						}
						System.out.println("Saida Padrao Hammurapi: \n" + stdIn);
						p1.destroy();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				
				case 2:
					try {
						p2 = Runtime.getRuntime().exec(
								"bash scripts/Run_FindBugs.sh " + program_name);
						p2.waitFor(); // espera pelo processo terminar
						setNameToolRunning("Findbugs");
						System.out.println(name_tool_running);
						setProgressGlobal(15);
						String stdIn = "";
						BufferedReader stdInput = new BufferedReader(
								new InputStreamReader(p2.getErrorStream()));
						while ((s = stdInput.readLine()) != null) {
							stdIn += s + "\n";
						}
						System.out.println("Saida Padrao Findbugs: \n" + stdIn);
						p2.destroy();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				case 3:
					try {
						p3 = Runtime.getRuntime().exec(
								"bash scripts/Run_JCSC.sh " + program_name);
						p3.waitFor(); // espera pelo processo terminar
						setNameToolRunning("JCSC");
						System.out.println(name_tool_running);
						setProgressGlobal(15);
						String stdIn = "";
						BufferedReader stdInput = new BufferedReader(
								new InputStreamReader(p3.getErrorStream()));
						while ((s = stdInput.readLine()) != null) {
							stdIn += s + "\n";
						}
						System.out.println("Saida Padrao JCSC: \n" + stdIn);
						p3.destroy();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				case 4:
					try {
						p4 = Runtime.getRuntime().exec(
								"bash scripts/Run_CheckStyle.sh "
										+ program_name);
						p4.waitFor(); // espera pelo processo terminar
						setNameToolRunning("CheckStyle");
						System.out.println(name_tool_running);
						setProgressGlobal(15);
						String stdIn = "";
						BufferedReader stdInput = new BufferedReader(
								new InputStreamReader(p4.getErrorStream()));
						while ((s = stdInput.readLine()) != null) {
							stdIn += s + "\n";
						}
						System.out.println("Saida Padrao Checkstyle: \n" + stdIn);
						p4.destroy();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				case 5:
					try {
						p5 = Runtime.getRuntime().exec(
								"bash scripts/Run_JLint.sh " + program_name);
						p5.waitFor(); // espera pelo processo terminar
						setNameToolRunning("JLint");
						System.out.println(name_tool_running);
						setProgressGlobal(15);
						String stdIn = "";
						BufferedReader stdInput = new BufferedReader(
								new InputStreamReader(p5.getErrorStream()));
						while ((s = stdInput.readLine()) != null) {
							stdIn += s + "\n";
						}
						System.out.println("Saida Padrao Jlint: \n" + stdIn);

						p5.destroy();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				case 6:
					try {
						p6 = Runtime.getRuntime().exec(
								"bash scripts/Run_PMD.sh " + program_name);
						p6.waitFor(); // espera pelo processo terminar
						setNameToolRunning("PMD");
						System.out.println(name_tool_running);
						setProgressGlobal(15);
						String stdIn = "";
						BufferedReader stdInput = new BufferedReader(
								new InputStreamReader(p6.getErrorStream()));
						while ((s = stdInput.readLine()) != null) {
							stdIn += s + "\n";
						}
						System.out.println("Saida Padrao PMD: \n" + stdIn);
						p6.destroy();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				case 7:
					try {
						p7 = Runtime.getRuntime().exec(
								"bash scripts/Run_ESCJAVA.sh " + program_name);
						p7.waitFor(); // espera pelo processo terminar
						setNameToolRunning("ESCJava");
						System.out.println(name_tool_running);
						String stdIn = "";
						BufferedReader stdInput = new BufferedReader(
								new InputStreamReader(p7.getErrorStream()));
						while ((s = stdInput.readLine()) != null) {
							stdIn += s + "\n";
						}
						System.out.println("Saida Padrao ESCJava: \n" + stdIn);
						setProgressGlobal(10);
						p7.destroy();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				quant_tools++;
				setProgress(getProgressGlobal());
			}
			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			run.setEnabled(true);
			run.setCursor(null); // turn off the wait cursor
			taskOutput.append("Creating TreeMap....!\n");

			try {
				try {
					try {
						createTreeMap();
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public FirstWindow() {

		frame = new JFrame("WarningsFix");
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel_first_window_top = new JPanel();
		choose = new JTextField(40);
		// choose.setText("Procure um arquivo com os dados do programa que será anilisado!!!");
		panel_first_window_top.add(choose);

		analyze = new JButton("FIND");
		panel_first_window_top.add(analyze);
		analyze.addActionListener(new Action() {

			public void actionPerformed(ActionEvent e) {

				JFileChooser file = new JFileChooser();
				file.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int i = file.showSaveDialog(null);

				if (i == 1) {
					if (choose.getText().equals(null)) {
						choose.setText("");
					} else {
						choose.setText(choose.getText());
					}

				} else {
					File arquivo = file.getSelectedFile();
					choose.setText(arquivo.getPath());
					program_name_zip = arquivo.getName();
				}
			}

			public Object getValue(String key) {
				// TODO Auto-generated method stub
				return null;
			}

			public void putValue(String key, Object value) {
				// TODO Auto-generated method stub

			}

			public void setEnabled(boolean b) {
				// TODO Auto-generated method stub

			}

		
			public boolean isEnabled() {
				// TODO Auto-generated method stub
				return false;
			}

			
			public void addPropertyChangeListener(
					PropertyChangeListener listener) {
				// TODO Auto-generated method stub

			}

		
			public void removePropertyChangeListener(
					PropertyChangeListener listener) {
				// TODO Auto-generated method stub

			}

		});

		run = new JButton("START");
		run.setActionCommand("start");
		run.addActionListener(this);
		panel_first_window_top.add(run);

		panel_first_window_top.setVisible(true);
		frame.add(panel_first_window_top);

		panel_first_window_bottom = new JPanel(new BorderLayout());
		panel_first_window_bottom.setBorder(BorderFactory.createEmptyBorder(20,
				20, 20, 20));
		panel_first_window_bottom.add(panel_first_window_top,
				BorderLayout.NORTH);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		panel_first_window_bottom.add(progressBar, BorderLayout.CENTER);

		taskOutput = new JTextArea(5, 20);
		taskOutput.setMargin(new Insets(5, 5, 5, 5));
		taskOutput.setEditable(false);

		scrolltaskoutput = new JScrollPane(taskOutput);
		panel_first_window_bottom.add(scrolltaskoutput, BorderLayout.SOUTH);
		panel_first_window_bottom.setOpaque(true);
		frame.add(panel_first_window_bottom);
		frame.setResizable(false);
		frame.pack();

	}

	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				new FirstWindow();
			}
		});

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			progressBar.setValue((Integer) evt.getNewValue());
			taskOutput.append(String.format("Completed %d%% of task.\n",
					task.getProgress())
					+ "\n" + getNameToolRunning() + " finished execution \n\n");

		}
	}


	public void actionPerformed(ActionEvent arg0) {
		if (choose.getText().equals("")) {
			JOptionPane.showMessageDialog(frame,
					"Nenhum programa foi escolhido para a análise!!!");

		} else {
			run.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			// Instances of javax.swing.SwingWorker are not reusuable, so
			// we create new instances as needed.
			task = new Task();
			task.addPropertyChangeListener(this);
			task.execute();
		}

	}

	private void createTreeMap() throws PropertyVetoException,
			InterruptedException, NumberFormatException,
			ClassNotFoundException, SQLException {
		frame.setVisible(false);
		final TABs_GUI tabs = new TABs_GUI(program_name);
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				try {
					tabs.createAndShowGUI();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public String getNameToolRunning() {
		return name_tool_running;
	}

	public int getProgressGlobal() {
		return progress;
	}

	public void setProgressGlobal(int add) {
		progress += add;
	}

	public void setNameToolRunning(String new_tool) {
		name_tool_running = new_tool;
	}

}
