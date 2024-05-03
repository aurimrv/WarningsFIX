package br.ufg.inf.es.vv.hamurapi.htmlparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.jar.Attributes.Name;

public class Parser {

	BufferedReader in;
	Connection conn;
	Persistencia pers = new Persistencia();


	public static void main(String[] args) throws SQLException,
			ClassNotFoundException {
		new Parser().doit(args[0], args[1], args[2]);
	}

	public void doit(String s, String nomePrograma, String nomeClasse)
			throws SQLException, ClassNotFoundException {
		String x = null;
		String[] aux = nomeClasse.split("-");

		for (int i = 0; i < aux.length; i++) {
			String s2 = "";
			s2 += aux[i];
			if (i == aux.length - 1) {
				x = s2.replace(".java.html", "");
			}
		}

		String y = x.replace("./", "");
		String nameclass = y.replace("/", ".");

		try {
			in = new BufferedReader(new FileReader(s));
			String str, str2;

			conn = pers.getConnection();

			/* Verificando se o programa existe na tabela */

			String sql = "select * from programa where nomeprograma= '"
					+ nomePrograma + "'";

			Statement stmt = conn.createStatement();
			ResultSet rs1 = stmt.executeQuery(sql);

			if (!rs1.next()) {
				String sql2 = "insert into programa " + "(nomeprograma, "
						+ "descricaoprograma, " + "versaoprograma) "
						+ "values (?,?,?)";
				PreparedStatement stat = conn.prepareStatement(sql2);
				stat.setString(1, nomePrograma);
				stat.setString(2, "");
				stat.setString(3, "");
				stat.executeUpdate();
                                stat.close();

			}
                        rs1.close();

			/* Verificando se a classe existe na tabela */

			String sql3 = "select * from arquivo where nomeprograma='"
					+ nomePrograma + "' AND nomearquivo='" + nameclass + "'";

			ResultSet rs2 = stmt.executeQuery(sql3);

			/* Inserindo classe se não existir */

			if (!rs2.next()) {
				String sql4 = "insert into arquivo " + "(nomeprograma,"
						+ "nomepacote, " + "nomearquivo, " + "versaoprograma, "
						+ "descricaoarquivo, " + "localizacao, " + "fonte) "
						+ "values (?,?,?,?,?,?,?)";
				PreparedStatement stat2 = conn.prepareStatement(sql4);
				stat2.setString(1, nomePrograma);

				// pega o nome do pacote
				String namepackage = "";
				String temp=nameclass.replace(".", "@");
				String[] tokenizername = temp.split("@");
				System.out.println(tokenizername.length);
				if (tokenizername.length==1) {
					stat2.setString(2, "No Package");
				} else {
					for (int j = 0; j < tokenizername.length - 1; j++) {
						if (j == tokenizername.length - 2) {
							namepackage += tokenizername[j];
							stat2.setString(2, namepackage);
						} else {
							namepackage += tokenizername[j] + ".";
						}

					}
				}
				stat2.setString(3, nameclass);
				stat2.setString(4, "");
				stat2.setString(5, "");
				stat2.setString(6, "");
				stat2.setString(7, "");
				stat2.executeUpdate();
                                stat2.close();
			}

rs2.close();
			/* Verificando se a ferramenta existe na tabela */
			String sql5 = "select * from ferramenta where nome='hammurapi'";

			ResultSet rs3 = stmt.executeQuery(sql5);

			/* Inserindo classe se não existir */

			if (!rs3.next()) {
				String sql6 = "insert into ferramenta " + "(id,"
						+ "descricao, " + "linguagem, " + "nome," + "tipo,"
						+ "versao) " + "values (?,?,?,?,?,?)";
				PreparedStatement stat3 = conn.prepareStatement(sql6);
				stat3.setString(1, "HA0001");
				stat3.setString(2, "");
				stat3.setString(3, "java");
				stat3.setString(4, "hammurapi");
				stat3.setString(5, "codigo fonte");
				stat3.setString(6, "");
				stat3.executeUpdate();
                                stat3.close();
			}

rs3.close();

			while ((str = in.readLine()) != null) {
				if (str.contains("<B style=\"color:blue\">Violations</B>")) {
					while (!(str2 = in.readLine()).equals("</table>")) {
						// Elements valores = docJsoup.getElementsByTag("a");
						// for (int i = 0; i < valores.size(); i++) {
						// MontaString.put("", valores.get(i).text());
						// }
						if ((str2.contains("<tr class=\"standard\">"))
								|| (str2.contains("</tr>"))
								|| (str2.contains("<table border=\"0\" cellspacing=\"1\" cellpadding=\"3\" class=\"standard\">"))
								|| (str2.contains("<th class=\"standard\">Description</th>"))
								|| (str2.contains("<th class=\"standard\">#</th><th class=\"standard\">Line</th><th class=\"standard\">Column</th><th class=\"standard\">Name</th><th class=\"standard\">Severity</th>"))) {
							// conteudoAnalise = MontaString.toString();
							// Persistencia.persisteConteudo(tituloAnalise,
							// conteudoAnalise);
						} else {

							// <td align="right">19
							// <td align="right"><a href="#line_34">34</a>
							// <td align="right">2
							// <td NOWRAP="yes"><a
							// href="../../inspectors/inspector_ER-008.html">ER-008</a>
							// <td align="right">2
							// <td>Synchronize at the block level rather than
							// the method level

							String[] buffer = str2.split("</td>");
							String bufffer = " ";
							for (int i = 0; i < buffer.length; i++) {

								if (i != 0) {
									if (buffer[i]
											.startsWith("<td align=\"right\">")) {
										bufffer += buffer[i]
												.replace(
														"<td align=\"right\">",
														" O_O ");
										// System.out.println(bufffer + " ");
									} else {
										if (buffer[i]
												.startsWith("<td NOWRAP=\"yes\">")) {
											bufffer += buffer[i].replace(
													"<td NOWRAP=\"yes\">",
													" O_O ");
											// System.out.println(bufffer + " "
											// );
										} else {
											if (buffer[i].startsWith("<td>")) {
												bufffer += buffer[i].replace(
														"<td>", " O_O ");
												// System.out.println(bufffer +
												// " ");
											}
										}
									}
								} else {
									if (i == 0) {
										if (buffer[i]
												.startsWith("<td align=\"right\">")) {
											bufffer += buffer[i].replace(
													"<td align=\"right\">", "");
											// System.out.println(bufffer +
											// " ");
										} else {
											if (buffer[i]
													.startsWith("<td NOWRAP=\"yes\">")) {
												bufffer += buffer[i].replace(
														"<td NOWRAP=\"yes\">",
														"");
												// System.out.println(bufffer +
												// " " );
											} else {
												if (buffer[i]
														.startsWith("<td>")) {
													bufffer += buffer[i]
															.replace("<td>", "");
													// System.out.println(bufffer
													// + " ");
												}
											}
										}
									}
								}

								if (i == buffer.length - 1) {
									pers.persisteConteudo(bufffer,
											nomePrograma, nameclass);
								}

							}
						}

					}

				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
