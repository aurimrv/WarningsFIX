/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufg.es.vv.checkstyle.parser.persistence.util;

/**
 *
 * @author Helios
 */
public class JpaUtil {
    private static JpaUtil jpa;
	private EntityManager manager;
	private EntityManagerFactory factory;

	private JpaUtil() {
		this.factory = Persistence.createEntityManagerFactory("checkstyle");
	}

	public static JpaUtil getInstance() {
		if (jpa == null) {
			jpa = new JpaUtil();
		}
		return jpa;
	}

	public EntityManager getEntityManager() {
		if (this.manager == null) {
			this.manager = factory.createEntityManager();
		}
		return manager;
	}
}
