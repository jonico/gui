package com.collabnet.ccf.tfs.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.WorkbenchContentProvider;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.dialogs.CcfDialog;
import com.collabnet.ccf.dialogs.ExceptionDetailsErrorDialog;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.tfs.schemageneration.TFSLayoutExtractor;

public class CollectionProjectSelectionDialog extends CcfDialog {
	private Landscape landscape;
	private int type;
	private String title;
	private TFSLayoutExtractor tfsLayoutExtractor;
	private List<Collection> collections = new ArrayList<CollectionProjectSelectionDialog.Collection>();
	private List<Project> collectionProjects;
	private Map<String, List<Project>> projectMap = new HashMap<String, List<Project>>();
	
	private TreeViewer treeViewer;
	private Button okButton;
	
	private String collection;
	private String project;
	
	private Collection previouslySelectedDomain;
	private Project previouslySelectedProject;
	
	public static final int BROWSER_TYPE_DOMAIN = 0;
	public static final int BROWSER_TYPE_PROJECT = 1;

	public CollectionProjectSelectionDialog(Shell shell, Landscape landscape, String collection, String project, int type) {
		super(shell, "DomainProjectSelectionDialog");
		this.landscape = landscape;
		this.collection = collection;
		this.project = project;
		this.type = type;
	}
	
	protected Control createDialogArea(Composite parent) {
		getDomains();
		
		switch (type) {
		case BROWSER_TYPE_DOMAIN:
			title = "Select Collection";
			break;
		default:
			title = "Select Project";
			break;
		}		
		getShell().setText(title);
		
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		treeViewer = new TreeViewer(composite);
		treeViewer.setLabelProvider(new DomainProjectLabelProvider());
		treeViewer.setContentProvider(new DomainProjectSelectionContentProvider());
		treeViewer.setUseHashlookup(true);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL | GridData.GRAB_VERTICAL);
		layoutData.heightHint = 300;
		layoutData.widthHint = 300;
		treeViewer.getControl().setLayoutData(layoutData);
		treeViewer.setInput(this);
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (okButton != null) {
					okButton.setEnabled(canFinish());
				}
			}		
		});
		
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent e) {
                if (okButton.isEnabled()) okPressed();
            }
        });  
        
        if (previouslySelectedDomain != null) {
        	if (type == BROWSER_TYPE_PROJECT) {
        		treeViewer.expandToLevel(previouslySelectedDomain, TreeViewer.ALL_LEVELS);
        		treeViewer.reveal(previouslySelectedDomain);
        	} else {
        		treeViewer.setSelection(getSelection(previouslySelectedDomain));
        	}
        }
        
        if (type == BROWSER_TYPE_PROJECT && previouslySelectedProject != null) {
        	treeViewer.setSelection(getSelection(previouslySelectedProject));
        }
 
		return composite;
	}

	@SuppressWarnings("rawtypes")
	private ISelection getSelection(final Object object) {
		ISelection selection = new IStructuredSelection() {			
			public boolean isEmpty() {
				return false;
			}			
			@SuppressWarnings("unchecked")
			public List toList() {
				List list = new ArrayList();
				list.add(object);
				return list;
			}
			
			public Object[] toArray() {
				return toList().toArray();
			}			
			public int size() {
				return 1;
			}			
			public Iterator iterator() {
				return toList().iterator();
			}			
			public Object getFirstElement() {
				return object;
			}
		};
		return selection;
	}
	
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        Button button = super.createButton(parent, id, label, defaultButton);
		if (id == IDialogConstants.OK_ID) {
			okButton = button;
			if (type == BROWSER_TYPE_DOMAIN && previouslySelectedDomain == null) {
				okButton.setEnabled(false);
			}
			if (type == BROWSER_TYPE_PROJECT && previouslySelectedProject == null) {
				okButton.setEnabled(false);
			}
		}
        return button;
    }
	
	protected void okPressed() {
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		Object firstSelection = selection.getFirstElement();
		if (firstSelection instanceof Collection) {
			Collection selectedDomain = (Collection)firstSelection;
			collection = selectedDomain.toString();
		}
		if (firstSelection instanceof Project) {
			Project selectedProject = (Project)firstSelection;
			collection = selectedProject.getDomain();
			project = selectedProject.toString();
		}
		super.okPressed();
	}
	
	private boolean canFinish() {
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		if (selection.isEmpty()) return false;
		Object firstSelection = selection.getFirstElement();
		if (type == BROWSER_TYPE_PROJECT) return (firstSelection instanceof Project);
		else return (!(firstSelection instanceof Project));
	}
	
	public String getDomain() {
		return collection;
	}

	public String getProject() {
		return project;
	}

	private void getDomains() {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					tfsLayoutExtractor = new TFSLayoutExtractor();
					Properties properties;
					if (landscape.getType2().equals("QT")) {
						properties = landscape.getProperties2();
					} else {
						properties = landscape.getProperties1();
					}
					String url = properties.getProperty(Activator.PROPERTIES_TFS_URL, "");
					String user = properties.getProperty(Activator.PROPERTIES_TFS_USER, "");
					String password = Activator.decodePassword(properties.getProperty(
							Activator.PROPERTIES_TFS_PASSWORD, ""));
					tfsLayoutExtractor.setServerUrl(url);
					tfsLayoutExtractor.setUserName(user);
					tfsLayoutExtractor.setPassword(password);
					List<String> collectionList = tfsLayoutExtractor.getDomains();
					collections = new ArrayList<CollectionProjectSelectionDialog.Collection>();
					for (String tfsCollection : collectionList) {
						Collection addDomain = new Collection(tfsCollection);
						if (collection != null && collection.equals(tfsCollection)) {
							previouslySelectedDomain = addDomain;
						}
						collections.add(addDomain);
					}
				} catch (Exception e) {
					Activator.handleError(e);
					ExceptionDetailsErrorDialog.openError(getShell(), "Select Project", e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e));
				}
			}			
		});		
	}
	
	private List<Project> getProjects(final String domain) {
		collectionProjects = projectMap.get(domain);
		if (collectionProjects == null) {
			BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
				public void run() {
					try {
						List<String> projectList = tfsLayoutExtractor.getProjects(domain);
						if (projectList != null) {
							collectionProjects = new ArrayList<CollectionProjectSelectionDialog.Project>();
							for (String tfsProyect : projectList) {
								Project addProject = new Project(domain, tfsProyect);
								if (previouslySelectedDomain != null && domain.equals(previouslySelectedDomain.toString()) && project != null && project.equals(tfsProyect)) {
									previouslySelectedProject = addProject;
								}
								collectionProjects.add(addProject);
							}
							projectMap.put(domain, collectionProjects);
						}
					} catch (Exception e) {
						Activator.handleError(e);
						ExceptionDetailsErrorDialog.openError(getShell(), "Select Project", e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e));
					}
				}			
			});		
		}
		return collectionProjects;
	}
	
	class DomainProjectSelectionLabelProvider extends LabelProvider {
		// TODO icons?
		public Image getImage(Object element) {
			return null;
		}
		public String getText(Object element) {
			return super.getText(element);
		}
	}	
	
	class DomainProjectSelectionContentProvider extends WorkbenchContentProvider {
		public Object[] getChildren(Object element) {
			if (element instanceof CollectionProjectSelectionDialog) {
				return collections.toArray();
			}
			if (element instanceof Collection) {
				Collection domain = (Collection)element;
				collectionProjects = getProjects(domain.toString());
				if (collectionProjects != null) {
					return collectionProjects.toArray();
				}
			}
			return new Object[0];
		}
		public Object[] getElements(Object element) {
			return getChildren(element);
		}
		public boolean hasChildren(Object element) {
			if (element instanceof Collection) {
				return type == BROWSER_TYPE_PROJECT;
			}
			return false;
		}
	}
	
	class Collection {
		private String name;
		
		public Collection(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
		
	}
	
	class Project {
		private String domain;
		private String name;
		
		public Project(String domain, String name) {
			this.domain = domain;
			this.name = name;
		}

		public String getDomain() {
			return domain;
		}

		public String toString() {
			return name;
		}
		
	}
	
	class DomainProjectLabelProvider extends LabelProvider {
		
	}

}
