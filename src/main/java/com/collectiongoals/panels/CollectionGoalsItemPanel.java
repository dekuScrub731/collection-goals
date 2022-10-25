
package com.collectiongoals.panels;

import com.collectiongoals.CollectionGoalsConfig;
import static com.collectiongoals.CollectionGoalsConfig.progressMethod.DROP_CHANCE;
import com.collectiongoals.CollectionGoalsPlugin;
import com.collectiongoals.utils.CollectionGoalsItem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

public class CollectionGoalsItemPanel extends JPanel
{

	private static final String DELETE_TITLE = "Warning";
	private static final String DELETE_MESSAGE = "Are you sure you want to delete this progress item?";
	private static final ImageIcon DELETE_ICON;
	private static final ImageIcon DELETE_HOVER_ICON;
	private static final Dimension IMAGE_SIZE = new Dimension(32, 32);

	private float percent;
	private float progressPercent;
	private Color barColor;
	private String percentText;

	static
	{
		final BufferedImage deleteImage = ImageUtil.loadImageResource(CollectionGoalsPluginPanel.class, "/delete_icon.png");
		DELETE_ICON = new ImageIcon(deleteImage);
		DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(deleteImage, 0.53f));
	}

	CollectionGoalsItemPanel(CollectionGoalsPlugin plugin, CollectionGoalsConfig config, CollectionGoalsItem item)
	{
		barColor = config.underRateColor();

		BorderLayout layout = new BorderLayout();
		layout.setHgap(5);
		setLayout(layout);
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
		JPanel rightPanel = new JPanel(new GridLayout(4, 1));
		rightPanel.setBackground(new Color(0, 0, 0, 0));

		// Item Name
		JLabel itemName = new JLabel();
		itemName.setForeground(Color.WHITE);
		itemName.setMaximumSize(new Dimension(0, 0));
		itemName.setPreferredSize(new Dimension(0, 0));
		itemName.setText(item.getName());
		rightPanel.add(itemName);


		// Source
		JLabel sourceInfo = new JLabel();

		// Determine percent progress relative to drop rate first
		// This allows for color coding to come into play
		percent = plugin.getProgressRelativeToDropRate(item.getName());
		percentText = String.format("%.2f", percent) + "% of Drop Rate";
		progressPercent = percent;
		if (progressPercent >= 100)
		{
			progressPercent = 100;
		}

		//Logic for bar color (based on progress toward drop rate)
		if (percent >= 200)
		{
			barColor = config.twiceRateColor();
		}
		else if (percent >= 100)
		{
			barColor = config.overRateColor();
		}

		//Only replace if the config dictates
		if (config.progressMethod().equals(DROP_CHANCE))
		{
			percent = plugin.getDropChance(item.getName());
			percentText = String.format("%.1f", percent) + "% Drop Chance";
			progressPercent = percent;

			if (progressPercent >= 100)
			{
				progressPercent = 100;
			}
		}

		String sourceName = item.getSources().get(0).getName();
		int kc = plugin.getGreatestKillcount(sourceName, item);

		String killNoun = "kills";
		if (item.getSources().get(0).getName().contains("Clue Scroll")) {
			killNoun = "clues";
		}

		String killInfo = " (" + kc + " " + killNoun + ")";

		if (item.isObtained())
		{
			percent = 100;
			percentText = "Complete";
			progressPercent = percent;
			barColor = config.completeColor();
			killInfo = "";
		}

		if (item.getSources().size() > 1)
		{
			sourceName = "";
			killInfo = "";
		}

		// Drop Rate
		JLabel dropRate = new JLabel();
		dropRate.setText(item.getRateString() + killInfo);
		dropRate.setForeground(ColorScheme.GRAND_EXCHANGE_PRICE);
		rightPanel.add(dropRate);


		sourceInfo.setText(sourceName);
		rightPanel.add(sourceInfo);

		// Progress
		JLabel progressLabel = new JLabel();
		progressLabel.setText(percentText);
		progressLabel.setForeground(Color.WHITE);
		rightPanel.add(progressLabel);

		// Remove Button
		JPanel deletePanel = new JPanel(new BorderLayout());
		deletePanel.setBackground(new Color(0, 0, 0, 0));

		JLabel deleteItem = new JLabel(DELETE_ICON);
		deleteItem.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (deleteConfirm())
				{
					plugin.removeItem(item);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				deleteItem.setIcon(DELETE_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				deleteItem.setIcon(DELETE_ICON);
			}
		});
		deletePanel.add(deleteItem, BorderLayout.NORTH);
		deletePanel.setOpaque(false);

		add(rightPanel, BorderLayout.CENTER);
		add(deletePanel, BorderLayout.EAST);
	}

	private boolean deleteConfirm()
	{
		int confirm = JOptionPane.showConfirmDialog(this,
			DELETE_MESSAGE, DELETE_TITLE, JOptionPane.YES_NO_OPTION);

		return confirm == JOptionPane.YES_NO_OPTION;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		g.setColor(barColor);

		float barPercent = this.getWidth() * progressPercent / 100;
		int barWidth = (int) barPercent;
		g.fillRect(0, 0, barWidth, this.getHeight());

		if (barWidth != this.getWidth())
		{
			g.setColor(ColorScheme.DARKER_GRAY_COLOR);
			g.fillRect(barWidth, 0, this.getWidth() - barWidth, this.getHeight());
		}
	}
}
