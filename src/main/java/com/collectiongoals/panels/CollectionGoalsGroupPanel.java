package com.collectiongoals.panels;

import com.collectiongoals.CollectionGoalsConfig;
import com.collectiongoals.CollectionGoalsPlugin;
import com.collectiongoals.utils.CollectionGoalsItem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

public class CollectionGoalsGroupPanel extends JPanel
{
	GridBagConstraints constraints = new GridBagConstraints();
	private static final int TITLE_PADDING = 5;


	CollectionGoalsGroupPanel(CollectionGoalsPlugin plugin, CollectionGoalsConfig config, String groupName, List<CollectionGoalsItem> items)
	{
		setLayout(new GridBagLayout());
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 1;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;


		JPanel logTitle = new JPanel();
		logTitle.setLayout(new BoxLayout(logTitle, BoxLayout.X_AXIS));
		logTitle.setBorder(new EmptyBorder(7, 7, 7, 7));
		logTitle.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		logTitle.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 12, 30));


		JLabel newTitle = new JLabel();
		newTitle.setText(groupName);
		newTitle.setForeground(Color.WHITE);
		newTitle.setMinimumSize(new Dimension(1, newTitle.getPreferredSize().height));

		logTitle.add(newTitle);

		//logTitle.add(Box.createRigidArea(new Dimension(TITLE_PADDING, 0)));
		//logTitle.add(Box.createHorizontalGlue());
		//logTitle.add(Box.createRigidArea(new Dimension(TITLE_PADDING, 0)));


		add(logTitle);
		constraints.gridy++;

		for (CollectionGoalsItem item : items)
		{
			CollectionGoalsItemPanel panel = new CollectionGoalsItemPanel(plugin, config, item);

			JPanel marginWrapper = new JPanel(new BorderLayout());
			marginWrapper.setBorder(new EmptyBorder(2, 0, 0, 0));
			marginWrapper.add(panel, BorderLayout.NORTH);
			add(marginWrapper, constraints);
			constraints.gridy++;

		}

		validate();


	}


}
