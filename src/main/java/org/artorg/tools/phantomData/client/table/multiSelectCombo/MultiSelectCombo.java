package org.artorg.tools.phantomData.client.table.multiSelectCombo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
 
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
 
public class MultiSelectCombo extends ComboBox<ComboUiVO>
{
 
    public static String SELECT_ALL = "Select All";
    public static String CONTROLS = "CONTROLS";
    private List<String> listExclusion = Arrays.asList(SELECT_ALL, CONTROLS);
    private int COMBO_SIZE;
 
    private List<ComboUiVO> listProvider;
    private List<String> listSelected;
 
    private MultiSelectCombo cmb = this;
    private BooleanProperty disableProperty;
 
    private boolean isSelectedAll;
 
    // Style.
    private static final String STYLE_BORDER_FILTER_PRESENT = "-fx-border-color:  #FF0000";
    private Image imgNormal, imgFilter;
 
    public MultiSelectCombo()
    {
    }
 
    /********************************************** Constructor **************************************************/
    public MultiSelectCombo(List<ComboUiVO> listProvider, Image imgNormal, Image imgFilter)
    {
 
    init(listProvider);
    // SELECT_ALL_INITIALLY:
    selectUnselectAll(true);
    disableProperty = new SimpleBooleanProperty(false);
    this.imgFilter = imgFilter;
    this.imgNormal = imgNormal;
 
    //
    storeImages();
    }
 
    /************************************************ Init *******************************************************/
    private void init(List<ComboUiVO> listProvider)
    {
    listSelected = new ArrayList<String>();
 
    // Additional check boxes.
    listProvider.add(0, new ComboUiVO(SELECT_ALL, false));
    listProvider.add(new ComboUiVO(CONTROLS, false));
 
    // Provider.
    this.listProvider = listProvider;
    setItems(FXCollections.observableArrayList(this.listProvider));
 
    // Button cell.
    setButtonCell(buttonCell);
 
    // Cell Factory.
    setCellFactory(cb);
 
    // Actual size of the combo excluding the extra fields.
    // (Select All,Controls)
    COMBO_SIZE = listProvider.size() - listExclusion.size();
    }
 
    /******************************************************* Provider *************************************************/
    public void setListProvider(List<ComboUiVO> listProvider)
    {
    init(listProvider); // It will set COMBO_SIZE also.
    setListSelected(); // It will set listSelected.
 
    // All items are selected.
    if (COMBO_SIZE == listSelected.size())
    {
        setIsSelectedAll(true);
        updateProvider(true);
    }
    else
    {
        setIsSelectedAll(false);
        updateProvider();
    }
    }
 
    private void setListSelected()
    {
    for (ComboUiVO uiVO : listProvider)
    {
        if (uiVO.isSelected())
        {
        if (!listExclusion.contains(uiVO.getItemName()))
        {
            listSelected.add(uiVO.getItemName());
        }
        }
    }
    }
 
    /******************************************
     * Selected Items
     **********************************************/
    public List<String> getListSelected()
    {
    return listSelected;
    }
 
    public List<ComboUiVO> getListProvider()
    {
    return listProvider;
    }
 
    @Override
    protected Skin<?> createDefaultSkin()
    {
    return new MultiSelectComboSkin(this);
    }
 
    /******************************************
     * Button Cell
     ***********************************************************/
    private ListCell<ComboUiVO> buttonCell = new ListCell<ComboUiVO>()
    {
    protected void updateItem(ComboUiVO item, boolean empty)
    {
        super.updateItem(item, empty);
        setText("");
    }
    };
    /******************************************
     * Call Back Factory
     ******************************************************/
    private final Callback<ListView<ComboUiVO>, ListCell<ComboUiVO>> cb = new Callback<ListView<ComboUiVO>, ListCell<ComboUiVO>>()
    {
 
    @Override
    public ListCell<ComboUiVO> call(ListView<ComboUiVO> param)
    {
        ListCell<ComboUiVO> cell = new ListCell<ComboUiVO>()
        {
        @Override
        protected void updateItem(ComboUiVO item, boolean empty)
        {
            super.updateItem(item, empty);
            if (!empty && item != null)
            {
            if (item.getItemName().equalsIgnoreCase(CONTROLS))
            {
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_LEFT);
                hBox.setPadding(new Insets(2, 2, 2, 2));
                hBox.setSpacing(10);
 
                Button btnOK = new Button("OK");
                btnOK.addEventHandler(ActionEvent.ACTION, onButtonClick);
                btnOK.disableProperty().bind(disableProperty);
                Button btnCancel = new Button("Cancel");
                btnCancel.addEventHandler(ActionEvent.ACTION, onButtonClick);
                hBox.getChildren().addAll(btnOK, btnCancel);
                setGraphic(hBox);
 
            }
            else
            {
                CheckBox cb = new CheckBox(item.getItemName());
                cb.setSelected(item.isSelected());
                cb.addEventHandler(MouseEvent.MOUSE_CLICKED, onCheckBoxClick);
                setGraphic(cb);
            }
            }
            else
            {
            setGraphic(null);
            }
        }
        };
        return cell;
    }
    };
 
    /*******************************************
     * Control Button Click
     *************************************************/
    private EventHandler<Event> onButtonClick = new EventHandler<Event>()
    {
 
    @Override
    public void handle(Event event)
    {
        Button btn = (Button) event.getSource();
        MultiSelectComboEvent e = null;
        if (btn.getText().equalsIgnoreCase("Ok"))
        {
        e = new MultiSelectComboEvent(cmb, MultiSelectComboEvent.EVENT_OK);
        }
        else
        {
        e = new MultiSelectComboEvent(cmb, MultiSelectComboEvent.EVENT_CANCEL);
        }
        fireEvent(e);
    }
    };
    /**************************************************
     * Combo Check
     ****************************************************/
    private EventHandler<MouseEvent> onCheckBoxClick = new EventHandler<MouseEvent>()
    {
 
    @Override
    public void handle(MouseEvent event)
    {
        CheckBox chk = (CheckBox) event.getSource();
        String itemName = chk.getText();
 
        if (itemName.equalsIgnoreCase(SELECT_ALL))
        {
        setIsSelectedAll(chk.isSelected());
        selectUnselectAll(chk.isSelected());
        }
        else
        {
        if (getItems().size() == listExclusion.size())
        {
            selectUnselectAll(chk.isSelected());
        }
        else
        {
            if (chk.isSelected())
            {
            if (!listSelected.contains(itemName))
            {
                listSelected.add(itemName);
                updateProvider(itemName, true);
 
            }
            }
            else
            {
            if (listSelected.contains(itemName))
            {
                listSelected.remove(itemName);
                updateProvider(itemName, false);
            }
            }
            if (COMBO_SIZE == listSelected.size())
            {
            setIsSelectedAll(true);
            selectUnselectAll(chk.isSelected());
            }
            else
            {
            setIsSelectedAll(false);
            }
        }
        }
        enableDisableButton();
        // changeComboColor();
        flipImages();
    }
    };
 
    /******************************************************
     * Enable/Disable Controls
     *******************************************/
    private void enableDisableButton()
    {
    if (getListSelected().size() == 0)
    {
        disableProperty.setValue(true);
    }
    else
    {
        disableProperty.setValue(false);
    }
    }
 
    /******************************************************
     * Change Combo Border Color
     ************************************************/
    @Deprecated
    private void changeComboColor()
    {
    if (getIsSelectedAll() == false)
    {
        cmb.setStyle(STYLE_BORDER_FILTER_PRESENT);
    }
    else
    {
        cmb.setStyle(null);
    }
    }
 
    /***************************************************
     * Select/UnSelect All
     ***********************************************************/
    private void selectUnselectAll(boolean check)
    {
    if (check)
    {
        for (ComboUiVO uiVO : (List<ComboUiVO>) getItems())
        {
        if (!uiVO.getItemName().equalsIgnoreCase(SELECT_ALL) && !uiVO.getItemName().equalsIgnoreCase(CONTROLS) && !listSelected.contains(uiVO.getItemName()))
        {
            listSelected.add(uiVO.getItemName());
        }
        }
    }
    else
    {
        listSelected = new ArrayList<String>();
    }
    updateProvider(check);
    }
 
    /**************************************************
     * Update Provider
     ****************************************************/
    private void updateProvider(String itemName, boolean isSelected)
    {
    // UPDATE_SELECTED_ITEM:
    for (ComboUiVO uiVO : (List<ComboUiVO>) getItems())
    {
        if (uiVO.getItemName().equalsIgnoreCase(itemName))
        {
        uiVO.setSelected(isSelected);
        break;
        }
    }
    // UPDATE_ONLY_SELECT_ALL:
    updateProvider();
    }
 
    private void updateProvider()
    {
    List<ComboUiVO> listTemp = new ArrayList<ComboUiVO>();
    for (ComboUiVO uiVO : getItems())
    {
        if (uiVO.getItemName().equalsIgnoreCase(SELECT_ALL))
        {
        uiVO.setSelected(false);
        }
        listTemp.add(uiVO);
    }
    getItems().clear();
    setItems(FXCollections.observableArrayList(listTemp));
    }
 
    private void updateProvider(boolean isSelected)
    {
    List<ComboUiVO> listTemp = new ArrayList<ComboUiVO>();
    for (ComboUiVO uiVO : getItems())
    {
        uiVO.setSelected(isSelected);
        listTemp.add(uiVO);
    }
    getItems().clear();
    setItems(FXCollections.observableArrayList(listTemp));
    }
 
    /***************************************************
     * Is Selected All
     **********************************************/
    public Boolean getIsSelectedAll()
    {
    return isSelectedAll;
    }
 
    public void setIsSelectedAll(Boolean isSelectedAll)
    {
    this.isSelectedAll = isSelectedAll;
    }
 
    /**************************************************** Store ***************************************************/
    private void storeImages()
    {
    // Runs only once to store the initial values.
    Platform.runLater(new Runnable()
    {
 
        @Override
        public void run()
        {
        StackPane sPane = (StackPane) lookup(".arrow-button");
        if (sPane != null)
        {
            sPane.getChildren().clear();
            ImageView imgView = new ImageView(imgNormal);
            sPane.getChildren().add(imgView);
        }
 
        }
    });
 
    }
 
    /*************************************************
     * Flip Images
     ***************************************************/
    private void flipImages()
    {
    Platform.runLater(new Runnable()
    {
 
        @Override
        public void run()
        {
        StackPane sPane = (StackPane) lookup(".arrow-button");
        sPane.getChildren().clear();
 
        // Set the image.
        ImageView imgView = null;
        if (cmb.getIsSelectedAll())
        {
            imgView = new ImageView(imgNormal);
        }
        else
        {
            imgView = new ImageView(imgFilter);
        }
        sPane.getChildren().add(imgView);
        }
    });
    }
}