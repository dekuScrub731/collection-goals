package com.collectiongoals;

import com.google.gson.Gson;
import com.google.inject.Provides;

import javax.inject.Inject;
import javax.swing.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.hiscore.HiscoreClient;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
        name = "Collection Goals"
)
public class CollectionGoalsPlugin extends Plugin {

	public static final String CONFIG_GROUP = "collectiongoals";
	private static final String PLUGIN_NAME = "Collection Goals";
	private static final String ICON_IMAGE = "/panel_icon.png";
    private static final String KILLCOUNT = "killcount";
    private static final String COLLECTION_LOG_TEXT = "New item added to your collection log: ";
    private static final Pattern NUMBER_PATTERN = Pattern.compile("([0-9]+)");

    private int shamanCount = 0;

    @Getter
    @Setter
    private List<CollectionGoalsItem> items = new ArrayList<>();

    @Inject
    private CollectionGoalsDataManager dataManager;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ConfigManager configManager;

    @Inject
    private Gson gson;

    @Inject
    private HiscoreClient hiscoreClient;

    @Inject
    private CollectionGoalsConfig config;

    @Inject
    private ClientToolbar clientToolbar;

    private CollectionGoalsPluginPanel panel;
    private NavigationButton navButton;


    @Override
    protected void startUp() throws Exception {
        panel = injector.getInstance(CollectionGoalsPluginPanel.class);

        final BufferedImage icon = ImageUtil.loadImageResource(CollectionGoalsPlugin.class, ICON_IMAGE);

        navButton = NavigationButton.builder()
                .tooltip(PLUGIN_NAME)
                .icon(icon)
                .priority(9)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);



/*
        // Initialize by looking up user; we can iterate on chat message
        //TODO: store to variables
        final HiscoreResult result = hiscoreClient.lookup(client.getLocalPlayer().getName());
        result.getSkill(HiscoreSkill.CLUE_SCROLL_BEGINNER);
        result.getSkill(HiscoreSkill.CLUE_SCROLL_EASY);
        result.getSkill(HiscoreSkill.CLUE_SCROLL_MEDIUM);
        result.getSkill(HiscoreSkill.CLUE_SCROLL_HARD);
        result.getSkill(HiscoreSkill.CLUE_SCROLL_ELITE);
        result.getSkill(HiscoreSkill.CLUE_SCROLL_MASTER);
        result.getSkill(HiscoreSkill.CLUE_SCROLL_ALL);
        */





		this.dataManager = new CollectionGoalsDataManager(this, configManager, itemManager, gson);

		clientThread.invokeLater(() ->
		{
			dataManager.loadData();
            SwingUtilities.invokeLater(() -> panel.updateProgressPanels());
		});




    }

    @Override
    protected void shutDown() throws Exception {
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {

            //log.info(String.valueOf(getKillcount("Zamorakian spear")) + " kills");




            log.info("Percent Complete = " + getProgressRelativeToDropRate("Zamorakian spear"));
            //client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);

            clientThread.invokeLater(() ->
            {
                SwingUtilities.invokeLater(() -> panel.updateProgressPanels());
            });






        }
    }

    @Provides
    CollectionGoalsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CollectionGoalsConfig.class);
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged)
    {
        if (configChanged.getGroup().equals(CONFIG_GROUP))
        {
            clientThread.invokeLater(() -> {
                SwingUtilities.invokeLater(() -> panel.updateProgressPanels());
            });
        }

        if (configChanged.getGroup().equals(KILLCOUNT))
        {
            log.info("Killcount changed for " + configChanged.getKey() + ": " + configChanged.getNewValue());
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() != ChatMessageType.GAMEMESSAGE
                && event.getType() != ChatMessageType.SPAM
                && event.getType() != ChatMessageType.TRADE
                && event.getType() != ChatMessageType.FRIENDSCHATNOTIFICATION)
        {
            return;
        }

        String chatMessage = event.getMessage();

        /*

        if (chatMessage.contains("You have completed") && chatMessage.contains("Treasure"))
        {
            Matcher m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage));
            if (m.find())
            {
                clueNumber = Integer.valueOf(m.group());
                clueType = chatMessage.substring(chatMessage.lastIndexOf(m.group()) + m.group().length() + 1, chatMessage.indexOf("Treasure") - 1);
                return;
            }
        }

        if (chatMessage.startsWith("Your Barrows chest count is"))
        {
            Matcher m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage));
            if (m.find())
            {
                killType = KillType.BARROWS;
                killCountNumber = Integer.valueOf(m.group());
                return;
            }
        }

        if (chatMessage.startsWith("Your completed Chambers of Xeric count is:"))
        {
            Matcher m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage));
            if (m.find())
            {
                killType = KillType.COX;
                killCountNumber = Integer.valueOf(m.group());
                return;
            }
        }

        if (chatMessage.startsWith("Your completed Chambers of Xeric Challenge Mode count is:"))
        {
            Matcher m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage));
            if (m.find())
            {
                killType = KillType.COX_CM;
                killCountNumber = Integer.valueOf(m.group());
                return;
            }
        }

        if (chatMessage.startsWith("Your completed Theatre of Blood"))
        {
            Matcher m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage));
            if (m.find())
            {
                killType = chatMessage.contains("Hard Mode") ? KillType.TOB_HM : (chatMessage.contains("Story Mode") ? KillType.TOB_SM : KillType.TOB);
                killCountNumber = Integer.valueOf(m.group());
                return;
            }
        }

        if (chatMessage.startsWith("Your completed Tombs of Amascut"))
        {
            Matcher m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage));
            if (m.find())
            {
                killType = chatMessage.contains("Expert Mode") ? KillType.TOA_EXPERT_MODE :
                        chatMessage.contains("Entry Mode") ? KillType.TOA_ENTRY_MODE :
                                KillType.TOA;
                killCountNumber = Integer.valueOf(m.group());
                return;
            }
        }

        if (config.screenshotPet() && PET_MESSAGES.stream().anyMatch(chatMessage::contains))
        {
            String fileName = "Pet";
            takeScreenshot(fileName, SD_PETS);
        }



        if (chatMessage.equals(CHEST_LOOTED_MESSAGE) && config.screenshotRewards())
        {
            final int regionID = client.getLocalPlayer().getWorldLocation().getRegionID();
            String eventName = CHEST_LOOT_EVENTS.get(regionID);
            if (eventName != null)
            {
                takeScreenshot(eventName, SD_CHEST_LOOT);
            }
        }

        if (config.screenshotValuableDrop())
        {
            Matcher m = VALUABLE_DROP_PATTERN.matcher(chatMessage);
            if (m.matches())
            {
                int valuableDropValue = Integer.parseInt(m.group(2).replaceAll(",", ""));
                if (valuableDropValue >= config.valuableDropThreshold())
                {
                    String valuableDropName = m.group(1);
                    String fileName = "Valuable drop " + valuableDropName;
                    takeScreenshot(fileName, SD_VALUABLE_DROPS);
                }
            }
        }

        if (config.screenshotUntradeableDrop() && !isInsideGauntlet())
        {
            Matcher m = UNTRADEABLE_DROP_PATTERN.matcher(chatMessage);
            if (m.matches())
            {
                String untradeableDropName = m.group(1);
                String fileName = "Untradeable drop " + untradeableDropName;
                takeScreenshot(fileName, SD_UNTRADEABLE_DROPS);
            }
        }

        if (config.screenshotDuels())
        {
            Matcher m = DUEL_END_PATTERN.matcher(chatMessage);
            if (m.find())
            {
                String result = m.group(1);
                String count = m.group(2).replace(",", "");
                String fileName = "Duel " + result + " (" + count + ")";
                takeScreenshot(fileName, SD_DUELS);
            }
        }

        if (config.screenshotCollectionLogEntries() && chatMessage.startsWith(COLLECTION_LOG_TEXT) && client.getVarbitValue(Varbits.COLLECTION_LOG_NOTIFICATION) == 1)
        {
            String entry = Text.removeTags(chatMessage).substring(COLLECTION_LOG_TEXT.length());
            String fileName = "Collection log (" + entry + ")";
            takeScreenshot(fileName, SD_COLLECTION_LOG);
        }

        if (chatMessage.contains("combat task") && config.screenshotCombatAchievements() && client.getVarbitValue(Varbits.COMBAT_ACHIEVEMENTS_POPUP) == 1)
        {
            String fileName = parseCombatAchievementWidget(chatMessage);
            if (!fileName.isEmpty())
            {
                takeScreenshot(fileName, SD_COMBAT_ACHIEVEMENTS);
            }
        }
        */
    }

    public int getKillcount(String boss)
    {
        Integer killCount = configManager.getRSProfileConfiguration(KILLCOUNT, boss.toLowerCase(), int.class);
        //log.info("Killcount for " + boss + " = " + killCount);
        return killCount == null ? 0 : killCount;
    }

    public float getProgressRelativeToDropRate(String itemName) {
        float percentComplete = 0f;

        CollectionGoalsItem item = CollectionGoalsItems.getItemByName(itemName);
        for (CollectionGoalsSource source : item.getSources()) {
            //log.info(String.valueOf(source.getRate()));
            //todo: this math needs looking at as well
            percentComplete += parseDropRate(source.getRate()) * (float) getKillcount(source.getName());
        }

        return percentComplete * 100f;
    }

    public float getDropChance(String itemName) {
        float percentComplete = 0f;
        CollectionGoalsItem item = CollectionGoalsItems.getItemByName(itemName);
        if (item.getSources().size() > 1) {
            return 0; //TODO: figure out how this math works...
        }
        else {
            int kc = getKillcount(item.getSources().get(0).getName());
            if (kc==0) {
                return 0;
            }
            percentComplete = (float) Math.pow((1 - parseDropRate(item.getSources().get(0).getRate())), kc);
        }
        return (1-percentComplete) * 100f;
    }

    public void addItem(CollectionGoalsItem item)
    {
        clientThread.invokeLater(() ->
        {
            if (!containsItem(item))
            {
                items.add(item);
                dataManager.saveData();
                SwingUtilities.invokeLater(() ->
                {
                    panel.switchToProgress();
                    panel.updateProgressPanels();
                });
            }
            else
            {
                SwingUtilities.invokeLater(() -> panel.containsItemWarning());
            }
        });
    }

    public void removeItem(CollectionGoalsItem item)
    {
        clientThread.invokeLater(() -> {
            items.remove(item);
            dataManager.saveData();
            SwingUtilities.invokeLater(() -> panel.updateProgressPanels());
        });
    }


    private boolean containsItem(CollectionGoalsItem newItem)
    {
        for (CollectionGoalsItem item : items) {
            if (item.getId() == newItem.getId()) {
                return true;
            }
        }
        return false;
    }


    public AsyncBufferedImage getImage (CollectionGoalsItem item) {
        return itemManager.getImage(item.getId());
    }



    double parseDropRate(String ratio) {
        if (ratio.contains("/")) {
            String[] rat = ratio.split("/");
            return Double.parseDouble(rat[0]) / Double.parseDouble(rat[1]);
        } else {
            return Double.parseDouble(ratio);
        }
    }



    // when collection log is open, check to see what we have as goals, and update

    //log warning if no data inferred?









}
