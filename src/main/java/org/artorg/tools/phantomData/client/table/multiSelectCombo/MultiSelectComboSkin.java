package org.artorg.tools.phantomData.client.table.multiSelectCombo;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

@SuppressWarnings("restriction")
public class MultiSelectComboSkin extends ComboBoxListViewSkin<ComboUiVO>
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