/*
 * MegaMek - Copyright (C) 2000-2003 Ben Mazur (bmazur@sev.org)
 * MegaMek - Copyright (C) 2020 - The MegaMek Team  
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */

package megamek.client.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;

import megamek.client.Client;
import megamek.client.ui.Messages;
import megamek.common.IPlayer;
import megamek.common.Team;

public class PlayerListDialog extends JDialog {

    private static final long serialVersionUID = 7270469195373150106L;

    private JList<String> playerList = new JList<String>(
            new DefaultListModel<String>());

    private Client client;

    public PlayerListDialog(JFrame parent, Client client) {
        super(parent, Messages.getString("PlayerListDialog.title"), false); //$NON-NLS-1$
        this.client = client;
        
        add(playerList, BorderLayout.CENTER);
        add(Box.createHorizontalStrut(20), BorderLayout.LINE_START);
        add(Box.createHorizontalStrut(20), BorderLayout.LINE_END);
        add(new JButton(new CloseAction(this)), BorderLayout.PAGE_END);

        refreshPlayerList();
        setMinimumSize(new Dimension(300,260));
        pack();
        setResizable(false);
        setLocation(parent.getLocation().x + (parent.getSize().width / 2) 
                - (getSize().width / 2),
                parent.getLocation().y + (parent.getSize().height / 2) 
                - (getSize().height / 2));
        
    }

    public static void refreshPlayerList(JList<String> playerList, 
            Client client) {
        refreshPlayerList(playerList, client, false);
    }

    /**
     * Refreshes the player list component with information from the game
     * object.
     */
    public static void refreshPlayerList(JList<String> playerList,
            Client client, boolean displayTeam) {
        ((DefaultListModel<String>) playerList.getModel()).removeAllElements();
        for (IPlayer player : client.getGame().getPlayersVector()) {
            StringBuffer playerDisplay = new StringBuffer(player.getName());

            // Append team information
            if (displayTeam) {
                Team team = client.getGame().getTeamForPlayer(player);
                if (team != null) {
                    if (team.getId() == IPlayer.TEAM_NONE) {
                        playerDisplay.append(Messages
                                .getString("PlayerListDialog.NoTeam"));
                    } else {
                        playerDisplay.append(Messages
                                .getString("PlayerListDialog.Team", team.getId()));
                    }
                } else {
                    playerDisplay.append(Messages
                            .getString("PlayerListDialog.TeamLess"));
                }
            }

            if (player.isGhost()) {
                playerDisplay.append(Messages
                        .getString("PlayerListDialog.player_ghost")); //$NON-NLS-1$
            } else if (player.isObserver()) {
                playerDisplay.append(Messages
                        .getString("PlayerListDialog.player_observer")); //$NON-NLS-1$
            } else if (player.isDone()) {
                playerDisplay.append(Messages
                        .getString("PlayerListDialog.player_done")); //$NON-NLS-1$
            }
            ((DefaultListModel<String>) playerList.getModel())
                    .addElement(playerDisplay.toString());
        }
    }

    public void refreshPlayerList() {
        refreshPlayerList(playerList, client, true);
        pack();
    }
}
