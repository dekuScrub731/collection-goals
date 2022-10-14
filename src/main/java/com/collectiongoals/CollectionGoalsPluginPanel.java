package com.collectiongoals;

import com.google.inject.Inject;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.http.api.item.ItemPrice;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static com.collectiongoals.CollectionGoalsItems.ALL_ITEMS;


public class CollectionGoalsPluginPanel extends PluginPanel
{
	private static final String PROGRESS_PANEL = "PROGRESS_PANEL";
	private static final String SEARCH_PANEL = "SEARCH_PANEL";
	private static final String RESULTS_PANEL = "RESULTS_PANEL";
	private static final String ERROR_PANEL = "ERROR_PANEL";
	private static final String CONTAINS_ITEM_TITLE = "Info";
	private static final String CONTAINS_ITEM_MESSAGE = "This item is already being tracked.";
	private static final ImageIcon ADD_ICON;
	private static final ImageIcon ADD_HOVER_ICON;
	private static final ImageIcon CANCEL_ICON;
	private static final ImageIcon CANCEL_HOVER_ICON;
	private static final int MAX_SEARCH_ITEMS = 100;

	private final CollectionGoalsPlugin plugin;
	private CollectionGoalsConfig config;
	private final ClientThread clientThread;
	private final ItemManager itemManager;
	private final RuneLiteConfig runeLiteConfig;

	private final CardLayout centerCard = new CardLayout();
	private final CardLayout searchCard = new CardLayout();
	private final JPanel titlePanel = new JPanel(new BorderLayout());
	private final JLabel title = new JLabel();
	private final JLabel addItem = new JLabel(ADD_ICON);
	private final JLabel cancelItem = new JLabel(CANCEL_ICON);
	private final JPanel centerPanel = new JPanel(centerCard);
	private final JPanel progressPanel = new JPanel();
	private final JPanel completePanel = new JPanel();
	//private final JLabel value = new JLabel();
	private final JPanel searchPanel = new JPanel(new BorderLayout());
	private final JPanel searchCenterPanel = new JPanel(searchCard);
	private final JPanel searchResultsPanel = new JPanel();
	private final IconTextField searchBar = new IconTextField();
	private final PluginErrorPanel searchErrorPanel = new PluginErrorPanel();
	private final GridBagConstraints constraints = new GridBagConstraints();
	private final GridBagConstraints constraints2 = new GridBagConstraints();

	private final List<CollectionGoalsItem> searchItems = new ArrayList<>();

	static
	{
		final BufferedImage addImage = ImageUtil.loadImageResource(CollectionGoalsPluginPanel.class, "/add_icon.png");
		ADD_ICON = new ImageIcon(addImage);
		ADD_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(addImage, 0.53f));

		final BufferedImage cancelImage = ImageUtil.loadImageResource(CollectionGoalsPluginPanel.class, "/cancel_icon.png");
		CANCEL_ICON = new ImageIcon(cancelImage);
		CANCEL_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(cancelImage, 0.53f));
	}

	@Inject
	CollectionGoalsPluginPanel(CollectionGoalsPlugin plugin, CollectionGoalsConfig config, ClientThread clientThread, ItemManager itemManager, RuneLiteConfig runeLiteConfig)
	{
		super(false);
		this.plugin = plugin;
		this.config = config;
		this.clientThread = clientThread;
		this.itemManager = itemManager;
		this.runeLiteConfig = runeLiteConfig;

		setLayout(new BorderLayout());

		/* Container Panel (contains title panel and center panel) */
		JPanel container = new JPanel(new BorderLayout());
		container.setBorder(new EmptyBorder(10, 10, 10, 10));

		/* Title Panel */
		title.setText("Collection Goals");
		title.setForeground(Color.WHITE);
		title.setBorder(new EmptyBorder(0, 0, 10, 40));

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));

		/* Add Item Button */
		addItem.setToolTipText("Add an item from the Collection Log");
		addItem.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				switchToSearch();
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				addItem.setIcon(ADD_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				addItem.setIcon(ADD_ICON);
			}
		});
		actions.add(addItem);

		/* Cancel Button */
		cancelItem.setToolTipText("Cancel");
		cancelItem.setVisible(false);
		cancelItem.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				switchToProgress();
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				cancelItem.setIcon(CANCEL_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				cancelItem.setIcon(CANCEL_ICON);
			}
		});
		actions.add(cancelItem);

		titlePanel.add(title, BorderLayout.WEST);
		titlePanel.add(actions, BorderLayout.EAST);

		// Goals Panels (In Progress, and Complete)
		progressPanel.setLayout(new GridBagLayout());
		completePanel.setLayout(new GridBagLayout());

		// Panel Wrapper Constraints
		GridBagConstraints pWrapperConstraints = new GridBagConstraints();
		pWrapperConstraints.fill = GridBagConstraints.HORIZONTAL;
		pWrapperConstraints.anchor = GridBagConstraints.NORTH;
		pWrapperConstraints.gridwidth = 1;
		pWrapperConstraints.weightx = 1;
		pWrapperConstraints.gridx = 0;
		pWrapperConstraints.gridy = 0;

		// Panel Wrapper
		JPanel pWrapper = new JPanel(new GridBagLayout());
		pWrapper.add(progressPanel, pWrapperConstraints);
		pWrapperConstraints.gridy++;
		pWrapper.add(completePanel, pWrapperConstraints);

		// Progress Wrapper Container (needed to align to top for some reason)
		JPanel pWrapperContainer = new JPanel(new BorderLayout());
		pWrapperContainer.add(pWrapper, BorderLayout.NORTH);

		JScrollPane progressWrapper = new JScrollPane(pWrapperContainer);
		progressWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		progressWrapper.setBorder(new EmptyBorder(5, 0, 0, 0));
		progressWrapper.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
		progressWrapper.getVerticalScrollBar().setBorder(new EmptyBorder(5, 5, 0, 0));

		/* Search Results Panel */
		searchResultsPanel.setLayout(new GridBagLayout());

		JPanel sWrapper = new JPanel(new BorderLayout());
		sWrapper.add(searchResultsPanel, BorderLayout.NORTH);

		JScrollPane resultsWrapper = new JScrollPane(sWrapper);
		resultsWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		resultsWrapper.setBorder(new EmptyBorder(5, 0, 0, 0));
		resultsWrapper.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
		resultsWrapper.getVerticalScrollBar().setBorder(new EmptyBorder(5, 5, 0, 0));

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 1;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;

		/* Search Error Panel */
		searchErrorPanel.setContent("Collection Log Search",
			"Search for an item to select");

		JPanel errorWrapper = new JPanel(new BorderLayout());
		errorWrapper.add(searchErrorPanel, BorderLayout.NORTH);

		/* Search Center Panel (contains results and error panels) */
		searchCenterPanel.add(resultsWrapper, RESULTS_PANEL);
		searchCenterPanel.add(errorWrapper, ERROR_PANEL);
		searchCard.show(searchCenterPanel, ERROR_PANEL);

		/* Search Panel (contains search bar and search center panel) */
		searchBar.setIcon(IconTextField.Icon.SEARCH);
		searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 15, 30));
		searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		searchBar.addClearListener(this::searchForItems);
		searchBar.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					searchForItems();
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
			}
		});
		searchPanel.add(searchBar, BorderLayout.NORTH);
		searchPanel.add(searchCenterPanel, BorderLayout.CENTER);

		/* Center Panel (contains progress items/search items panel) */
		centerPanel.add(progressWrapper, PROGRESS_PANEL);
		centerPanel.add(searchPanel, SEARCH_PANEL);
		centerCard.show(centerPanel, PROGRESS_PANEL);

		container.add(titlePanel, BorderLayout.NORTH);
		container.add(centerPanel, BorderLayout.CENTER);
		add(container, BorderLayout.CENTER);
	}

	private void searchForItems()
	{
		searchResultsPanel.removeAll();
		if (searchBar.getText().isEmpty())
		{
			searchResultsPanel.removeAll();
			SwingUtilities.invokeLater(() -> searchResultsPanel.updateUI());
			return;
		}

		List<CollectionGoalsItem> results = search(searchBar.getText());

		if (results.isEmpty())
		{
			searchErrorPanel.setContent("No results found", "No items were found with that name, please try again");
			searchCard.show(searchCenterPanel, ERROR_PANEL);
			return;
		}

		clientThread.invokeLater(() -> processResults(results));
	}


	private void processResults(List<CollectionGoalsItem> results)
	{
		searchItems.clear();
		searchCard.show(searchCenterPanel, RESULTS_PANEL);

		int count = 0;

		// Add each result to items list
		for (CollectionGoalsItem item : results)
		{
			if (count++ > MAX_SEARCH_ITEMS)
			{
				break;
			}

			searchItems.add(item);
		}

		// Add each item in list to panel
		SwingUtilities.invokeLater(() ->
		{
			int index = 0;
			for (CollectionGoalsItem item : searchItems)
			{


				int itemId = item.getId();
				AsyncBufferedImage itemImage = itemManager.getImage(itemId);

				CollectionGoalsResultPanel panel = new CollectionGoalsResultPanel(plugin, item);

				if (index++ > 0)
				{
					JPanel marginWrapper = new JPanel(new BorderLayout());
					marginWrapper.setBorder(new EmptyBorder(5, 0, 0, 0));
					marginWrapper.add(panel, BorderLayout.NORTH);
					searchResultsPanel.add(marginWrapper, constraints);
				}
				else
				{
					searchResultsPanel.add(panel, constraints);
				}

				constraints.gridy++;
			}

			validate();
		});
	}

	public void updateProgressPanels()
	{
		progressPanel.removeAll();
		completePanel.removeAll();

		constraints.gridy++;

		for (CollectionGoalsItem item : plugin.getItems())
		{
			CollectionGoalsItemPanel panel = new CollectionGoalsItemPanel(plugin, config, item);


			if (item.isObtained())
			{
				JPanel marginWrapper = new JPanel(new BorderLayout());
				marginWrapper.setBorder(new EmptyBorder(5, 0, 0, 0));
				marginWrapper.add(panel, BorderLayout.NORTH);
				completePanel.add(marginWrapper, constraints);

			}
			else
			{
				JPanel marginWrapper = new JPanel(new BorderLayout());
				marginWrapper.setBorder(new EmptyBorder(5, 0, 0, 0));
				marginWrapper.add(panel, BorderLayout.NORTH);
				progressPanel.add(marginWrapper, constraints);
			}

			constraints.gridy++;
		}

		validate();
	}


	public void containsItemWarning()
	{
		JOptionPane.showConfirmDialog(this,
			CONTAINS_ITEM_MESSAGE, CONTAINS_ITEM_TITLE, JOptionPane.DEFAULT_OPTION);
	}

	public void switchToProgress()
	{
		cancelItem.setVisible(false);
		addItem.setVisible(true);
		centerCard.show(centerPanel, PROGRESS_PANEL);
	}

	private void switchToSearch()
	{
		addItem.setVisible(false);
		cancelItem.setVisible(true);
		centerCard.show(centerPanel, SEARCH_PANEL);
	}

	/**
	 * Search for collection log items based on item name
	 *
	 * @param itemName item name
	 * @return
	 */
	public List<CollectionGoalsItem> search(String itemName)
	{

		itemName = itemName.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();

		List<CollectionGoalsItem> result = new ArrayList<>();

		for (CollectionGoalsItem item : ALL_ITEMS)
		{
			final String name = item.getName();
			if (name.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase().contains(itemName))
			{
				result.add(item);
			}
		}
		return result;
	}

}
