package com.collabnet.ccf.tfs.schemageneration;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.tfs.core.TFSConfigurationServer;
import com.microsoft.tfs.core.TFSTeamProjectCollection;
import com.microsoft.tfs.core.clients.framework.catalog.CatalogQueryOptions;
import com.microsoft.tfs.core.clients.framework.catalog.CatalogResource;
import com.microsoft.tfs.core.clients.framework.catalog.CatalogResourceTypes;
import com.microsoft.tfs.core.clients.workitem.WorkItem;
import com.microsoft.tfs.core.clients.workitem.project.Project;
import com.microsoft.tfs.core.clients.workitem.project.ProjectCollection;
import com.microsoft.tfs.core.clients.workitem.query.WorkItemCollection;
import com.microsoft.tfs.core.clients.workitem.wittype.WorkItemType;
import com.microsoft.tfs.core.clients.workitem.wittype.WorkItemTypeCollection;
import com.microsoft.tfs.core.util.GUID;


public class TFSConnection {

	private static String SERVER_URL = ""; //"http://bvdevtfs2010:8080/tfs";
//	public static String COLLECTION_URL = ""; //"http://bvdevtfs2010:8080/tfs/";
	private static String USERNAME = ""; //"user_tfs";
	private static String DOMAIN = ""; //"B-VISION";
	private static String PASSWORD = ""; //"M4r4d0n4";

	public TFSConnection(String serverUrl, String userName, String password) {
		
		SERVER_URL = serverUrl;
		String nameAndDomain[] = userName.split("\\\\");
		USERNAME = nameAndDomain[1];
		DOMAIN = nameAndDomain[0];
		PASSWORD = password;
	}
	
	/**
	 * This method answers the collections names
	 * 
	 * @return
	 */
	public List<String> getCollectionsNames (){
		
		List<String> collectionsNames = new ArrayList<String>();
		TFSConfigurationServer configurationServer = null;
		
		try {
			configurationServer =
		    new TFSConfigurationServer(
		        SERVER_URL,
		        USERNAME,
		        DOMAIN,
		        PASSWORD,
		    	null,
		        "",
		        "");
		
			GUID[] resourceTypes = new GUID[] {
			        CatalogResourceTypes.PROJECT_COLLECTION
			};
			
			CatalogResource[] catalogueResources = configurationServer.getCatalogService().queryResourcesByType(resourceTypes, CatalogQueryOptions.NONE); 
			
			for (int i = 0; i < catalogueResources.length; i++){
				collectionsNames.add(catalogueResources[i].getDisplayName());
			}
			
		} finally {
			configurationServer.close();
		}
		
		return collectionsNames;
	}
	
	/**
	 * This method answers the projects names
	 * 
	 * @param proyectCollectionName
	 * @return
	 */
	public List<String> getProyectsNames(String proyectCollectionName){
		
		List<String> proyectsNames = new ArrayList<String>();
		TFSTeamProjectCollection tpc = null;
		
		try {
			tpc =
				new TFSTeamProjectCollection(
						SERVER_URL + "/"+ proyectCollectionName,
						USERNAME,
						DOMAIN,
						PASSWORD,
						null,
						"",
						"");
	
			ProjectCollection proyects = tpc.getWorkItemClient().getProjects();
			
			for (Project project : proyects) {
				proyectsNames.add(project.getName());
			}
			
		} finally {
			tpc.close();
		}
		
		return proyectsNames;
	}

	public List<String> getWorkItemsTypesNames(String collectionName, String projectName) {

		List<String> workItemTypesNames = new ArrayList<String>();
		
		TFSTeamProjectCollection tpc = null;
		
		try {
			tpc =
				new TFSTeamProjectCollection(
						SERVER_URL + "/"+ collectionName,
						USERNAME,
						DOMAIN,
						PASSWORD,
						null,
						"",
						"");
		
			Project project = tpc.getWorkItemClient().getProjects().get(projectName);
			WorkItemTypeCollection workItemsCollectionTypes = project.getWorkItemTypes();
			for (int i = 0; i < workItemsCollectionTypes.getTypes().length; i++){
				WorkItemType type = workItemsCollectionTypes.getTypes()[i];
				workItemTypesNames.add(type.getName());
			}
		} 
		finally {                    
			tpc.close();
		}
		
		return workItemTypesNames;
	}
}
