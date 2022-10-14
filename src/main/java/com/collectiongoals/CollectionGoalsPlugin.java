package com.collectiongoals;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import net.runelite.api.WorldType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
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
    private static final String LOOT_TRACKER = "loottracker";
    private static final String DROPS_PREFIX = "drops_NPC_";
    private static final String COLLECTION_LOG_TEXT = "New item added to your collection log: ";
    private static final Pattern NUMBER_PATTERN = Pattern.compile("([0-9]+)");

    private static final Pattern ADVENTURE_LOG_TITLE_PATTERN = Pattern.compile("The Exploits of (.+)");
    private static final int ADVENTURE_LOG_COLLECTION_LOG_SELECTED_VARBIT_ID = 12061;


    private static final int COLLECTION_LOG_CONTAINER = 1;
    private static final int COLLECTION_LOG_DRAW_LIST_SCRIPT_ID = 2730;
    private static final int COLLECTION_LOG_DEFAULT_HIGHLIGHT = 901389;
    private static final int COLLECTION_LOG_ACTIVE_TAB_SPRITE_ID = 2283;


    private boolean isPohOwner = false;
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
            update();
        }
    }

    @Provides
    CollectionGoalsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CollectionGoalsConfig.class);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged)
    {
        if (configChanged.getGroup().equals(CONFIG_GROUP) || configChanged.getGroup().equals(KILLCOUNT))
        {
            update();
        }
    }


    public void update() {
        clientThread.invokeLater(() -> {
            SwingUtilities.invokeLater(() -> panel.updateProgressPanels());
        });
        dataManager.saveData();
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

    //from RL properties (unavailable on login screen)
    public int getKillcount(String dropSource)
    {
        Integer killCount = configManager.getRSProfileConfiguration(KILLCOUNT, dropSource.toLowerCase(), int.class);

        return killCount == null ? 0 : killCount;
    }

    //from user log data (potentially stale)
    public int getKillcountLogData(CollectionGoalsItem item) {
        if (item == null || item.getSources().size() != 1) {
            return -1;
        }
        Integer killCount = item.getUserLogData().get(0).getKillCount();
        return killCount == null ? 0 : killCount;
    }

    public int getGreatestKillcount(String boss, CollectionGoalsItem item) {
        return getKillcount(boss) >= getKillcountLogData(item) ? getKillcount(boss) : getKillcountLogData(item);
    }







    public float getProgressRelativeToDropRate(String itemName) {
        float percentComplete = 0f;

        CollectionGoalsItem baseItem = CollectionGoalsItems.getBaseItemByName(itemName);
        CollectionGoalsItem userItem = getUserItemByName(itemName);


        if (baseItem.getSources().size() == 1) {
            percentComplete = (float) (parseDropRate(baseItem.getSources().get(0).getRate()) * (float) getGreatestKillcount(baseItem.getSources().get(0).getName(), userItem));
        }
        else {
            for (CollectionGoalsSource source : baseItem.getSources()) {
                //todo: this math needs looking at as well for more than one source
                percentComplete += parseDropRate(source.getRate()) * (float) getGreatestKillcount(source.getName(), userItem);
            }
        }

        return percentComplete * 100f;
    }

    public float getDropChance(String itemName) {
        float percentComplete = 0f;

        CollectionGoalsItem baseItem = CollectionGoalsItems.getBaseItemByName(itemName);
        CollectionGoalsItem userItem = getUserItemByName(itemName);

        if (baseItem.getSources().size() > 1) {
            return 0; //TODO: figure out how this math works...
        }
        else {
            int kc = getGreatestKillcount(baseItem.getSources().get(0).getName(), userItem);
            if (kc==0) {
                return 0;
            }
            percentComplete = (float) Math.pow((1 - parseDropRate(baseItem.getSources().get(0).getRate())), kc);
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


    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded)
    {
        if (widgetLoaded.getGroupId() == WidgetID.ADVENTURE_LOG_ID)
        {
            Widget adventureLog = client.getWidget(WidgetInfo.ADVENTURE_LOG);
            if (adventureLog == null)
            {
                return;
            }

            // Children are rendered on tick after widget load. Invoke later to prevent null children on adventure log widget
            clientThread.invokeLater(() -> {
                Matcher adventureLogUser = ADVENTURE_LOG_TITLE_PATTERN.matcher(adventureLog.getChild(1).getText());
                if (adventureLogUser.find())
                {
                    isPohOwner = adventureLogUser.group(1).equals(client.getLocalPlayer().getName());
                }
            });
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired scriptPostFired)
    {
        if (scriptPostFired.getScriptId() == COLLECTION_LOG_DRAW_LIST_SCRIPT_ID)
        {
            clientThread.invokeLater(this::getEntry);
        }
    }




    /**
     * Load the current entry being viewed in the collection log
     * and get/update relevant information contained in the entry
     */
    private void getEntry()
    {
        if (!isValidWorldType())
        {
            return;
        }

        boolean openedFromAdventureLog = client.getVarbitValue(ADVENTURE_LOG_COLLECTION_LOG_SELECTED_VARBIT_ID) != 0;
        if (openedFromAdventureLog && !isPohOwner)
        {
            return;
        }








        String activeTabName = getActiveTabName();
        if (activeTabName == null)
        {
            return;
        }

        Widget entryHead = client.getWidget(WidgetInfo.COLLECTION_LOG_ENTRY_HEADER);

        if (entryHead == null)
        {
            return;
        }

        updateEntryItems(entryHead);

        update();
    }


    private boolean isValidWorldType()
    {
        List<WorldType> invalidTypes = ImmutableList.of(
                WorldType.DEADMAN,
                WorldType.NOSAVE_MODE,
                WorldType.SEASONAL,
                WorldType.TOURNAMENT_WORLD
        );

        for (WorldType worldType : invalidTypes)
        {
            if (client.getWorldType().contains(worldType))
            {
                return false;
            }
        }

        return true;
    }


    private String getActiveTabName()
    {
        Widget tabsWidget = client.getWidget(WidgetInfo.COLLECTION_LOG_TABS);
        if (tabsWidget == null)
        {
            return null;
        }

        Widget[] tabs = tabsWidget.getStaticChildren();
        for (Widget tab : tabs)
        {
            Widget subTab = tab.getChild(0);

            if (subTab.getSpriteId() == COLLECTION_LOG_ACTIVE_TAB_SPRITE_ID)
            {
                return tab.getName()
                        .split(">")[1]
                        .split("<")[0];
            }
        }

        return null;
    }





//TODO: use this to log items
    private void updateEntryItems(Widget categoryHead)
    {

        String entryTitle = categoryHead.getDynamicChildren()[0].getText();

        //todo - figure out how to update based on collection log message
        Widget itemsContainer = client.getWidget(WidgetInfo.COLLECTION_LOG_ENTRY_ITEMS);

        if (itemsContainer == null)
        {
            return;
        }

        int mainKillcount = getMainKillcountLog(categoryHead);
        int alternateKillcount = getAlternateKillcountLog(categoryHead);

        for (Widget widgetItem : itemsContainer.getDynamicChildren())
        {
            updateLogDataFromWidget(widgetItem, entryTitle, mainKillcount, alternateKillcount);
        }

    }




    private void updateLogDataFromWidget(Widget widgetItem, String entryTitle, int mainKillcount, int alternateKillcount) {

        int id = widgetItem.getItemId();
        String name = itemManager.getItemComposition(id).getName();
        int quantity = (widgetItem.getOpacity() == 0 ? widgetItem.getItemQuantity() : 0);

        for (int i=0; i<getItems().size(); i++) {
            if (getItems().get(i).getId() == id) {
                for (int j=0; j<getItems().get(i).getUserLogData().size(); j++) {
                    if (getItems().get(i).getUserLogData().get(j).getSource().equalsIgnoreCase(entryTitle)) {
                        getItems().get(i).getUserLogData().set(j, new CollectionGoalsLogItem(id, entryTitle, quantity, mainKillcount, alternateKillcount));
                        return;
                    }
                }
            }
        }
    }


//todo use this to update kc
    private int getKillcountLog(Widget categoryHead, int index)
    {
        Widget[] children = categoryHead.getDynamicChildren();
        if (children.length < 3)
        {
            return -1;
        }

        Widget[] killCountWidgets = Arrays.copyOfRange(children, 2, children.length);

        if (killCountWidgets.length < index+1) {
            return -1;
        }
        else {
            return Integer.parseInt(killCountWidgets[index].getText().split(": ")[1].split(">")[1].split("<")[0].replace(",", ""));
        }
    }

    private int getMainKillcountLog(Widget categoryHead) {
        return getKillcountLog(categoryHead, 0);
    }
    private int getAlternateKillcountLog(Widget categoryHead) {
        return getKillcountLog(categoryHead, 1);
    }
    private int getSecondAlternateKillcountLog(Widget categoryHead) {
        return getKillcountLog(categoryHead, 2);
    }


    public CollectionGoalsItem getUserItemByName(String name) {
        for (CollectionGoalsItem item : getItems()) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

public int getLootTrackerKills(String npc) {
    String lootTrackerJson = configManager.getRSProfileConfiguration(LOOT_TRACKER, DROPS_PREFIX + npc, String.class);

    if (lootTrackerJson == null || lootTrackerJson.equals("[]")) {
        return -1;
    }

    JsonObject body = gson.fromJson(lootTrackerJson, JsonObject.class);
    return body.get("kills").getAsInt();
}





}
