package ee.ut.cs.rum.database.util;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import ee.ut.cs.rum.database.domain.SystemParameter;
import ee.ut.cs.rum.database.domain.enums.SystemParametersEnum;
import ee.ut.cs.rum.database.internal.Activator;
import ee.ut.cs.rum.database.util.exceptions.SystemParameterNotSetException;

public final class SystemParameterAccess {
	
	private SystemParameterAccess() {
	}
	
	public static String getSystemParameterValue(SystemParametersEnum systemParameterName) throws SystemParameterNotSetException {
		EntityManagerFactory emf = Activator.getEmf();
		EntityManager em = emf.createEntityManager();
		
		String queryString = "Select sp from SystemParameter sp where sp.name = '" + systemParameterName.toString() + "'";
		TypedQuery<SystemParameter> query = em.createQuery(queryString, SystemParameter.class);
		SystemParameter systemParameter = query.getSingleResult();
		String systemParameterValue = systemParameter.getValue();
		
		if (systemParameterValue==null || systemParameterValue.isEmpty()) {
			throw new SystemParameterNotSetException(systemParameterName);
		}
		return systemParameterValue;
	}
	
	public static List<SystemParameter> getSystemParametersDataFromDb() {
		EntityManagerFactory emf = Activator.getEmf();
		EntityManager em = emf.createEntityManager();
		
		String queryString = "Select sp from SystemParameter sp order by sp.id";
		TypedQuery<SystemParameter> query = em.createQuery(queryString, SystemParameter.class);
		List<SystemParameter> systemParameters = query.getResultList();
		
		return systemParameters;
	}
	
	public static boolean updateParameterValue(SystemParameter updatedSystemParameter) {
		//TODO: Review updating process
		boolean setValueSuccess = false;
		SystemParametersEnum systemParameterName = SystemParametersEnum.valueOf(updatedSystemParameter.getName());
		SystemParameter systemParameter = SystemParameterAccess.getSystemParameterDataFromDb(systemParameterName);
		if (systemParameter!=null) {
			systemParameter.setValue(updatedSystemParameter.getValue());
			systemParameter.setLastModifiedBy(updatedSystemParameter.getLastModifiedBy());
			systemParameter.setLastModifiedAt(updatedSystemParameter.getLastModifiedAt());
			EntityManagerFactory emf = Activator.getEmf();
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			em.merge(systemParameter);
			try {
				em.getTransaction().commit();
				setValueSuccess = true;
				Activator.getLogger().info("Modified systemParameter: " + systemParameter.toString());
			} catch (Exception e) {
				Activator.getLogger().info("Failed modifiying systemParameter: " + systemParameter.toString());
			} finally {
				em.close();
			}
		} else {
			Activator.getLogger().info("Can not get system parameter with name: " + systemParameterName);
		}
		return setValueSuccess;
	}
	
	public static void addSystemParameterDataToDb(SystemParameter systemParameter) {
		SystemParameter existingSystemParameter = SystemParameterAccess.getSystemParameterDataFromDb(SystemParametersEnum.valueOf(systemParameter.getName()));
		
		if (existingSystemParameter==null) {
			EntityManagerFactory emf = Activator.getEmf();
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			em.persist(systemParameter);
			try {
				em.getTransaction().commit();
				Activator.getLogger().info("Added systemParameter: " + systemParameter.toString());
			} catch (Exception e) {
				Activator.getLogger().info("Failed commiting systemParameter: " + systemParameter.toString());
			} finally {
				em.close();
			}
		} else {
			Activator.getLogger().info("Skipped adding existing systemParameter: " + systemParameter.toString());
		}
	}
	
	private static SystemParameter getSystemParameterDataFromDb(SystemParametersEnum systemParameterName) {
		SystemParameter systemParameter = null;
		EntityManagerFactory emf = Activator.getEmf();
		EntityManager em = emf.createEntityManager();
		
		String queryString = "Select sp from SystemParameter sp where sp.name = '" + systemParameterName.toString() + "'";
		TypedQuery<SystemParameter> query = em.createQuery(queryString, SystemParameter.class);
		
		try {
			systemParameter = query.getSingleResult();
		} catch (Exception e) {
			Activator.getLogger().info("Failed querying systemparameter with name: " + systemParameterName);
		}
		return systemParameter;
	}
}
