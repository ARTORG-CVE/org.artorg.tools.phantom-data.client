package org.artorg.tools.phantomData.client.table.multiSelectCombo;

public class ComboUiVO
{
    private String itemName;
    private boolean isSelected;
 
    public ComboUiVO(String itemName, boolean isSelected)
    {
    super();
    this.itemName = itemName;
    this.isSelected = isSelected;
    }
 
    public String getItemName()
    {
    return itemName;
    }
 
    public void setItemName(String itemName)
    {
    this.itemName = itemName;
    }
 
    public boolean isSelected()
    {
    return isSelected;
    }
 
    public void setSelected(boolean isSelected)
    {
    this.isSelected = isSelected;
    }
 
}