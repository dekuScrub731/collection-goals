package com.collectiongoals.panels;

import com.collectiongoals.CollectionGoalsPlugin;
import com.collectiongoals.utils.CollectionGoalsItem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.runelite.client.ui.ColorScheme;

public class CollectionGoalsResultPanel extends JPanel
{
	private static final Dimension IMAGE_SIZE = new Dimension(32, 32);

	CollectionGoalsResultPanel(CollectionGoalsPlugin plugin, CollectionGoalsItem item)
	{
		BorderLayout layout = new BorderLayout();
		layout.setHgap(5);
		setLayout(layout);
		setToolTipText(item.getName());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		Color background = getBackground();
		List<JPanel> panels = new ArrayList<>();
		panels.add(this);

		MouseAdapter itemPanelMouseListener = new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				plugin.addItem(item);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				for (JPanel panel : panels)
				{
					panel.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
				}
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				for (JPanel panel : panels)
				{
					panel.setBackground(background);
				}
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		};

		addMouseListener(itemPanelMouseListener);
		setBorder(new EmptyBorder(5, 5, 5, 0));

		// Image
		JLabel itemImage = new JLabel();
		itemImage.setPreferredSize(IMAGE_SIZE);
		if (plugin.getImage(item) != null)
		{
			plugin.getImage(item).addTo(itemImage);
		}
		add(itemImage, BorderLayout.LINE_START);

		// Item Details Panel
		JPanel rightPanel = new JPanel(new GridLayout(2, 1));
		panels.add(rightPanel);
		rightPanel.setBackground(background);

		// Item Name
		JLabel itemName = new JLabel();
		itemName.setForeground(Color.WHITE);
		itemName.setMaximumSize(new Dimension(0, 0));
		itemName.setPreferredSize(new Dimension(0, 0));
		itemName.setText(item.getName());
		rightPanel.add(itemName);

		// GE Price
		JLabel dropRateLabel = new JLabel();
		dropRateLabel.setText(item.getRateString());
		dropRateLabel.setForeground(ColorScheme.GRAND_EXCHANGE_PRICE);
		rightPanel.add(dropRateLabel);

		add(rightPanel, BorderLayout.CENTER);
	}
}
