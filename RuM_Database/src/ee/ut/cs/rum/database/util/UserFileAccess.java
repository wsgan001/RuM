package ee.ut.cs.rum.database.util;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import ee.ut.cs.rum.database.domain.UserFile;
import ee.ut.cs.rum.database.internal.Activator;

public final class UserFileAccess {
	
	private UserFileAccess() {
	}
	
	//TODO: Add overview of all user files to the UI
	public static List<UserFile> getUserFilesDataFromDb() {
		EntityManagerFactory emf = Activator.getEmf();
		EntityManager em = emf.createEntityManager();
		
		String queryString = "Select uf from UserFile uf order by uf.id";
		TypedQuery<UserFile> query = em.createQuery(queryString, UserFile.class);
		List<UserFile> userFiles = query.getResultList();
		
		return userFiles;
	}
	
	public static List<UserFile> getWorkspaceUserFilesDataFromDb(Long workspaceId) {
		EntityManagerFactory emf = Activator.getEmf();
		EntityManager em = emf.createEntityManager();
		
		String queryString = "Select uf from UserFile uf where uf.workspaceId = " + workspaceId + " order by uf.id";
		TypedQuery<UserFile> query = em.createQuery(queryString, UserFile.class);
		List<UserFile> userFiles = query.getResultList();
		
		return userFiles;
	}
	
	public static UserFile getUserFileDataFromDb(Long userFileId) {
		EntityManagerFactory emf = Activator.getEmf();
		EntityManager em = emf.createEntityManager();
		
		UserFile userFile = em.find(UserFile.class, userFileId);
		return userFile;
	}
}