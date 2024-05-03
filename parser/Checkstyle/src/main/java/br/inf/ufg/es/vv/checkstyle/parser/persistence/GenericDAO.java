/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufg.es.vv.checkstyle.parser.persistence;

import java.util.List;



/**
 *
 * @author Helios
 */
public class GenericDAO<T> {
	private EntityManager manager;
	private Class<T> classe;

	public GenericDAO(EntityManager manager, Class<T> classe) {
		this.manager = manager;
		this.classe = classe;
	}

	public void salva(T t) {
		this.manager.getTransaction().begin();
		this.manager.persist(t);
		this.manager.getTransaction().commit();
	}

	public void atualiza(T t) {
		this.manager.getTransaction().begin();
		this.manager.merge(t);
		this.manager.getTransaction().commit();
	}

	public T remove(T t) {
		this.manager.getTransaction().begin();
		Object ToRemove = this.manager.merge(t);
		this.manager.remove(ToRemove);
		this.manager.getTransaction().commit();
		return t;
	}

	public T buscaPorId(Long id) {
		return this.manager.find(classe, id);
	}

	public List<T> lista() {
		CriteriaBuilder cb = manager.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(classe);
		Root<T> root = cq.from(classe);
		cq.select(root);
		return manager.createQuery(cq).getResultList();
	}
}
