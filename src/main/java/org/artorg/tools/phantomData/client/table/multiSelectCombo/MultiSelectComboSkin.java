package org.artorg.tools.phantomData.client.table.multiSelectCombo;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

import javafx.scene.Node;

@SuppressWarnings("restriction")
public class MultiSelectComboSkin extends ComboBoxListViewSkin<Node>
{
 
    public MultiSelectComboSkin(MultiSelectCombo comboBox)
    {
    super(comboBox);
 
    }
 
    protected boolean isHideOnClickEnabled()
    {
    return false;
    }
 
}