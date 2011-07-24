/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */
package net.sourceforge.fullsync.ui;

import java.util.Date;
import java.util.HashMap;

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileListChangeListener;
import net.sourceforge.fullsync.ProfileManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class NiceListViewProfileListComposite extends ProfileListComposite implements ProfileListChangeListener {
	class ContentComposite extends Composite {
		private Profile profile;

		private Label labelSource;
		private Label labelDestination;
		private Label labelLastUpdate;
		private Label labelNextUpdate;
		private ToolBar toolbar;

		public ContentComposite(Composite parent, int style) {
			super(parent, style);
			initGui();
		}

		public void initGui() {
			GridLayout layout = new GridLayout(2, false);
			layout.marginHeight = 1;
			layout.marginWidth = 1;
			layout.verticalSpacing = 2;
			layout.horizontalSpacing = 20;
			this.setLayout(layout);

			labelSource = new Label(this, SWT.NULL);
			labelSource.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
			labelSource.setSize(300, 16);

			toolbar = new ToolBar(this, SWT.FLAT);
			GridData d = new GridData(GridData.END, GridData.CENTER, true, false);
			d.verticalSpan = 4;
			toolbar.setLayoutData(d);

			ToolItem t = new ToolItem(toolbar, SWT.PUSH);
			t.setImage(imageRun);
			t.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					handler.runProfile(profile, true);
				}
			});

			t = new ToolItem(toolbar, SWT.PUSH);
			t.setImage(imageRunNonInter);
			t.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					handler.runProfile(profile, false);
				}
			});

			t = new ToolItem(toolbar, SWT.PUSH);
			t.setImage(imageEdit);
			t.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					handler.editProfile(profile);
				}
			});

			t = new ToolItem(toolbar, SWT.PUSH);
			t.setImage(imageDelete);
			t.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					handler.deleteProfile(profile);
				}
			});

			labelDestination = new Label(this, SWT.NULL);
			labelDestination.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
			labelDestination.setSize(200, 16);
			labelLastUpdate = new Label(this, SWT.NULL);
			labelLastUpdate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
			labelLastUpdate.setSize(300, 16);
			labelNextUpdate = new Label(this, SWT.NULL);
			labelNextUpdate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
			labelNextUpdate.setSize(200, 16);
		}

		public void updateComponent() {
			new Date().getTime();
			labelSource.setText(Messages.getString("NiceListViewProfileListComposite.Source") + ": " + profile.getSource()); //$NON-NLS-1$ //$NON-NLS-2$
			labelDestination.setText(Messages.getString("NiceListViewProfileListComposite.Destination") + ": " + profile.getDestination()); //$NON-NLS-1$ //$NON-NLS-2$
			labelLastUpdate.setText(Messages.getString("NiceListViewProfileListComposite.LastUpdate") + ": " + profile.getLastUpdateText()); //$NON-NLS-1$ //$NON-NLS-2$
			labelNextUpdate.setText(Messages.getString("NiceListViewProfileListComposite.NextUpdate") + ": " + profile.getNextUpdateText()); //$NON-NLS-1$ //$NON-NLS-2$
			layout();
		}

		public void setProfile(Profile profile) {
			this.profile = profile;
			updateComponent();
		}

		public Profile getProfile() {
			return profile;
		}
	}

	private ScrolledComposite scrollPane;
	private NiceListView profileList;
	private HashMap<Profile, NiceListViewItem> profilesToItems;

	private ProfileManager profileManager;
	private ProfileListControlHandler handler;

	private Image imageProfileDefault;
	private Image imageProfileScheduled;
	private Image imageProfileError;
	private Image imageProfileErrorScheduled;
	private Image imageRun;
	private Image imageRunNonInter;
	private Image imageEdit;
	private Image imageDelete;

	public NiceListViewProfileListComposite(Composite parent, int style) {
		super(parent, style);
		loadImages();
		initGui();
	}

	private void initGui() {
		scrollPane = new ScrolledComposite(this, SWT.BORDER | SWT.V_SCROLL);
		profileList = new NiceListView(scrollPane, SWT.NULL);
		scrollPane.setExpandHorizontal(true);
		scrollPane.setExpandVertical(false);
		scrollPane.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		scrollPane.setAlwaysShowScrollBars(true);
		scrollPane.setContent(profileList);
		scrollPane.getVerticalBar().setIncrement(20);
		profileList.pack();
		this.setLayout(new FillLayout());
		this.layout();
	}

	private void loadImages() {
		imageProfileDefault = GuiController.getInstance().getImage("Profile_Default.png"); //$NON-NLS-1$
		imageProfileScheduled = GuiController.getInstance().getImage("Profile_Default_Scheduled.png"); //$NON-NLS-1$
		imageProfileError = GuiController.getInstance().getImage("Profile_Default_Error.png"); //$NON-NLS-1$
		imageProfileErrorScheduled = GuiController.getInstance().getImage("Profile_Default_Error_Scheduled.png"); //$NON-NLS-1$

		imageRun = GuiController.getInstance().getImage("Profile_Run.png"); //$NON-NLS-1$
		imageRunNonInter = GuiController.getInstance().getImage("Profile_Run_Non_Inter.png"); //$NON-NLS-1$
		imageEdit = GuiController.getInstance().getImage("Profile_Edit.png"); //$NON-NLS-1$
		imageDelete = GuiController.getInstance().getImage("Profile_Delete.png"); //$NON-NLS-1$
	}

	@Override
	public void dispose() {
		profileManager.removeProfilesChangeListener(this);

		super.dispose();
	}

	private void updateItem(NiceListViewItem item, Profile profile) {
		if (item.getImage() != null) {
			item.getImage().dispose();
		}
		if (profile.isEnabled() && (profile.getSchedule() != null)) {
			// scheduled
			if (profile.getLastErrorLevel() > 0) {
				item.setImage(new Image(getDisplay(), imageProfileErrorScheduled, SWT.IMAGE_COPY));
			}
			else {
				item.setImage(new Image(getDisplay(), imageProfileScheduled, SWT.IMAGE_COPY));
			}
		}
		else {
			// not scheduled
			if (profile.getLastErrorLevel() > 0) {
				item.setImage(new Image(getDisplay(), imageProfileError, SWT.IMAGE_COPY));
			}
			else {
				item.setImage(new Image(getDisplay(), imageProfileDefault, SWT.IMAGE_COPY));
			}
		}

		item.setText(profile.getName());

		if (profile.getLastErrorLevel() > 0) {
			item.setStatusText(profile.getLastErrorString());
		}
		else {
			String desc = profile.getDescription();
			if ((desc != null) && !desc.equals("")) {
				item.setStatusText(desc);
			}
			else if (profile.isEnabled() && (profile.getSchedule() != null)) {
				item.setStatusText(profile.getNextUpdateText());
			}
			else {
				item.setStatusText(""); //$NON-NLS-1$
			}
		}
	}

	private void populateProfileList() {
		if (getProfileManager() != null) {
			profilesToItems = new HashMap<Profile, NiceListViewItem>();
			setItemsMenu(null);
			profileList.clear();
			for (Profile p : getProfileManager().getProfiles()) {
				NiceListViewItem item = new NiceListViewItem(profileList, SWT.NULL);
				ContentComposite content = new ContentComposite(item, SWT.NULL);
				content.setProfile(p);
				item.setContent(content);
				item.setMenu(getMenu());
				item.setHandler(handler);
				item.setProfile(p);
				updateItem(item, p);

				profilesToItems.put(p, item);
			}
			profileList.pack();
		}
	}

	@Override
	public Profile getSelectedProfile() {
		ContentComposite content = (ContentComposite) profileList.getSelectedContent();
		if (content != null) {
			return content.getProfile();
		}
		else {
			return null;
		}
	}

	@Override
	public void setProfileManager(ProfileManager profileManager) {
		if (this.profileManager != null) {
			profileManager.removeProfilesChangeListener(this);

		}
		this.profileManager = profileManager;
		if (this.profileManager != null) {
			profileManager.addProfilesChangeListener(this);
		}
		populateProfileList();
	}

	@Override
	public ProfileManager getProfileManager() {
		return profileManager;
	}

	@Override
	public ProfileListControlHandler getHandler() {
		return handler;
	}

	@Override
	public void setHandler(ProfileListControlHandler handler) {
		this.handler = handler;
	}

	public void setItemsMenu(Menu menu) {
		Control[] items = profileList.getChildren();
		for (Control item : items) {
			item.setMenu(menu);
		}
	}

	@Override
	public void setMenu(Menu menu) {
		setItemsMenu(menu);
		super.setMenu(menu);
	}

	@Override
	public void profileListChanged() {
		getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				populateProfileList();
			}
		});
	}

	@Override
	public void profileChanged(final Profile p) {
		getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				Object composite = profilesToItems.get(p);
				if (composite == null) {
					populateProfileList();
				}
				else {
					NiceListViewItem item = (NiceListViewItem) composite;
					ContentComposite content = (ContentComposite) item.getContent();
					updateItem(item, content.getProfile());
					content.updateComponent();
				}
			}
		});
	}
}
