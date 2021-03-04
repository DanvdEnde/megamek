/*  
 * MegaMek - Copyright (C) 2021 - The MegaMek Team  
 *  
 * This program is free software; you can redistribute it and/or modify it under  
 * the terms of the GNU General Public License as published by the Free Software  
 * Foundation; either version 2 of the License, or (at your option) any later  
 * version.  
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT  
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS  
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more  
 * details.  
 */ 
package megamek.client.ui.swing.lobby;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import megamek.client.ui.swing.tooltip.UnitToolTip;
import megamek.client.ui.swing.util.UIUtil;
import megamek.common.*;
import megamek.common.force.*;
import megamek.common.icons.Camouflage;
import megamek.common.options.OptionsConstants;
import megamek.common.util.fileUtils.MegaMekFile;

/** A specialized renderer for the Mek Force tree. */
public class MekTreeForceRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = -2002064111324279609L;
    private final String UNKNOWN_UNIT = new MegaMekFile(Configuration.miscImagesDir(),
            "unknown_unit.gif").toString();

    private ChatLounge lobby;
    //    private final Color TRANSPARENT = new Color(250,250,250,0);
    private boolean isTopLevel;
    private boolean isSelected;
    private Color topLevelBG = UIUtil.uiGray();
    private Color treeBG = Color.BLACK;
    private Color selectionColor = Color.BLUE;
    private Entity entity;
    private IPlayer localPlayer;
    private JTree tree;
    private int row;

    static int counter = 0;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {

        isSelected = sel;
        IGame game = lobby.getClientgui().getClient().getGame();
        localPlayer = lobby.getClientgui().getClient().getLocalPlayer();
        treeBG = tree.getBackground();
        topLevelBG = UIUtil.alternateTableBGColor();
        isTopLevel = false;
        selectionColor = UIManager.getColor("Tree.selectionBackground");

        if (value instanceof Entity) {
            entity = (Entity) value;
            this.row = row; 
            IPlayer owner = entity.getOwner();

            setText(MekForceTreeCellFormatter.formatUnitCompact(entity, lobby));
            int size = UIUtil.scaleForGUI(40);
            if (lobby.isCompact()) {
                size = size / 2;
            }
            boolean showAsUnknown = owner.isEnemyOf(localPlayer)
                    && game.getOptions().booleanOption(OptionsConstants.BASE_BLIND_DROP);
            if (showAsUnknown) {
                setIcon(getToolkit().getImage(UNKNOWN_UNIT), size - 5);
            } else {
                Camouflage camo = entity.getCamouflageOrElse(entity.getOwner().getCamouflage());
                Image image = lobby.getClientgui().bv.getTilesetManager().loadPreviewImage(entity, camo, this);
                setIconTextGap(UIUtil.scaleForGUI(10));
                setIcon(image, size);
            }
        } else if (value instanceof Force) {
            entity = null;
            Force force = (Force) value;
            if (lobby.isCompact()) {
                setText(MekForceTreeCellFormatter.formatForceCompact(force, lobby));
            } else {
                setText(MekForceTreeCellFormatter.formatForceFull(force, lobby));
            }
            setIcon(null);
            isTopLevel = force.isTopLevel();
        }
        return this; 
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        if (entity == null) {
            return null;
        }
        Rectangle r = tree.getRowBounds(row);
        if (r != null && event.getPoint().x > r.getWidth() - UIUtil.scaleForGUI(50)) {
            return "<HTML>" + UnitToolTip.getEntityTipLobby(entity, localPlayer, null).toString();
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isTopLevel) {
            Color c = treeBG;
            if (isSelected) {
                c = selectionColor;
            }
            Graphics2D g2 = (Graphics2D) g;
            int x = UIUtil.scaleForGUI(300); 
            GradientPaint bg = new GradientPaint(x, 0, topLevelBG, x + 50, 50, c);
            g2.setPaint(bg);
            g.fillRect(0, 0, UIUtil.scaleForGUI(600), getBounds().height);
        }
        super.paintComponent(g);
    }


    @Override
    public Dimension getPreferredSize() {
        if (isTopLevel) {
            return new Dimension(UIUtil.scaleForGUI(600), super.getPreferredSize().height);
        }
        return super.getPreferredSize();
    }

    private void setIcon(Image image, int size) {
        setIcon(new ImageIcon(image.getScaledInstance(-1, size, Image.SCALE_SMOOTH)));
    }

    MekTreeForceRenderer(ChatLounge cl) {
        lobby = cl;
        tree = lobby.mekForceTree;
    }
}
